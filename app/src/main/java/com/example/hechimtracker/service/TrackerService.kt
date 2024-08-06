package com.example.hechimtracker.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.example.hechimtracker.MainActivity
import com.example.hechimtracker.R
import com.example.hechimtracker.model.database.Point
import com.example.hechimtracker.model.database.Workout
import com.example.hechimtracker.model.database.WorkoutEndUpdate

import com.example.hechimtracker.model.repo.WorkoutRepo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class TrackerService: Service(), SensorEventListener {

    @Inject
    lateinit var workoutRepo: WorkoutRepo
    @Inject
    lateinit var trackerHelper: TrackerHelper

    private val STOP_WORKOUT = "STOP_WORKOUT"
    private val PAUSE_WORKOUT = "PAUSE_WORKOUT"
    private val RESUME_WORKOUT = "RESUME_WORKOUT"

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    private var sensorManager: SensorManager? = null
    private var builder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val stopIntent = Intent(this, TrackerService::class.java)
        stopIntent.action = STOP_WORKOUT
        val pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val pauseIntent = Intent(this, TrackerService::class.java)
        pauseIntent.action = PAUSE_WORKOUT
        val pendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE)

        val resumeIntent = Intent(this, TrackerService::class.java)
        resumeIntent.action = RESUME_WORKOUT
        val pendingResumeIntent = PendingIntent.getService(this, 0, resumeIntent, PendingIntent.FLAG_IMMUTABLE)

        //check if intent action should stop the workout
        if(trackerHelper.intentActionEquals(STOP_WORKOUT, intent)) {
            return stopWorkout()
        }
        //check if intent action should pause the workout
        else if(trackerHelper.intentActionEquals(PAUSE_WORKOUT, intent)) {
            return pauseWorkout(
                pendingStopIntent = pendingStopIntent,
                pendingResumeIntent = pendingResumeIntent
            )
        }
        //check if intent action should resume the workout
        else if(trackerHelper.intentActionEquals(RESUME_WORKOUT, intent)) {
            return resumeWorkout(
                pendingStopIntent = pendingStopIntent,
                pendingPauseIntent = pendingPauseIntent
            )
        }
        //start workout
        else {
            val notificationIntent = Intent(this, MainActivity::class.java)


            builder = NotificationCompat.Builder(this, "RideArrival")
                .setSmallIcon(R.drawable.ic_notification)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1))
                .addAction(R.drawable.ic_pause, "Pause", pendingPauseIntent)
                .addAction(R.drawable.ic_stop, "Stop", pendingStopIntent)
                .setColor(ContextCompat.getColor(this, R.color.orange_400))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE))

            trackerHelper.workoutId = 0
            insertWorkout()

            startForeground(1, builder!!.build())
            startLocationTracking()
            setupSensors()
            setupTimer()

            return START_NOT_STICKY
        }

    }

    private fun insertWorkout() {
        scope.launch {
            val id = workoutRepo.insertWorkout(
                Workout(0, startTime = LocalDate.now().toString())
            )
            println("value of inserted workout is: $id")
            trackerHelper.workoutId = id
        }
    }
    private fun insertPoint(point: Point) {
        scope.launch {
            workoutRepo.insertPoint(point)
        }
    }


    //invoked by user pressing the pause action button in notification
    private fun pauseWorkout(
        pendingStopIntent: PendingIntent,
        pendingResumeIntent: PendingIntent
    ):Int{
        //change action buttons, we now turn pause button in start button,
        //stop stays the same

        builder!!.clearActions()
            .addAction(R.drawable.ic_start, "Resume", pendingResumeIntent)
            .addAction(R.drawable.ic_stop, "Stop", pendingStopIntent)

        updateNotification()
        trackerHelper.settings.paused = true

        return START_NOT_STICKY
    }

    private fun endWorkout() {
        scope.launch {
            workoutRepo.updateEndWorkout(
                workoutId = trackerHelper.workoutId,
                endTime = LocalDate.now().toString(),
                distance = trackerHelper.settings.distance,
                duration = trackerHelper.settings.time
            )
        }

    }

    private fun stopWorkout():Int{
        endWorkout()
        mainHandler.removeMessages(0)
        mGpsLocationClient?.removeUpdates(locationListener)
        mGpsLocationClient = null
        sensorManager?.unregisterListener(this)
        stopSelf()
        notificationManager!!.cancel(1)
        return START_NOT_STICKY
    }

    private fun resumeWorkout(
        pendingPauseIntent: PendingIntent,
        pendingStopIntent: PendingIntent
    ):Int{
        builder!!.clearActions()
            .addAction(R.drawable.ic_pause, "Pause", pendingPauseIntent)
            .addAction(R.drawable.ic_stop, "Stop", pendingStopIntent)
        trackerHelper.settings.paused = false
        updateNotification()
        return START_NOT_STICKY
    }

    private fun setupTimer() {
        mainHandler.post(object : Runnable {
            override fun run() {
                updateNotification()
                trackerHelper.settings.time++
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    private fun setupSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if(stepSensor == null) {
            println("no step sensor for this device")
            return
        }
        sensorManager?.registerListener(
            this,
            stepSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }


    private fun updateNotification() {
        if(builder == null || notificationManager == null || trackerHelper.settings.paused) {
            return
        }
        val titleContent = "%.2f".format(trackerHelper.settings.distance / 1000) + " km"
        builder!!.setContentTitle(HtmlCompat.fromHtml(
            "<font color=\"" +
                    ContextCompat.getColor(this, R.color.orange_400) +
                    "\">" + titleContent + "</font>",
            HtmlCompat.FROM_HTML_MODE_LEGACY))

        builder!!.setContentText(trackerHelper.formatDurationTime(trackerHelper.settings.time) + " Steps ${trackerHelper.settings.workoutSteps - trackerHelper.settings.totalSteps}")

        notificationManager!!.notify(1, builder!!.build())
    }
    var mGpsLocationClient: LocationManager? = null
    val mainHandler = Handler(Looper.getMainLooper())

    @SuppressLint("MissingPermission")
    fun startLocationTracking() {

        mGpsLocationClient = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager

        mGpsLocationClient!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            3000,
            10.0f,
            locationListener
        )


    }

    private val locationListener = android.location.LocationListener { it -> //handle location change
        if(trackerHelper.settings.initialPosition.provider!!.isEmpty()) {
            trackerHelper.settings.initialPosition.latitude = it.latitude
            trackerHelper.settings.initialPosition.longitude = it.longitude
            trackerHelper.settings.initialPosition.speed = it.speed
            trackerHelper.settings.initialPosition.altitude = it.altitude
            trackerHelper.settings.initialPosition.provider = "updated"
        }
        else {
            if(it.accuracy <= 11) {
                if(!trackerHelper.settings.paused) {
                    trackerHelper.settings.distance += trackerHelper.settings.initialPosition.distanceTo(it)
                    println("INSERTING POINT")
                    insertPoint(
                        Point(
                            workoutId = trackerHelper.workoutId,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            speed = it.speed,
                        )
                    )
                }

                trackerHelper.settings.initialPosition = it
            }
        }
        updateNotification()
    }



    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event == null) {
            return
        }

        if(trackerHelper.settings.totalSteps == -1) {
            println("total steps are -1, setting to ${event.values[0].toInt()}")
            trackerHelper.settings.totalSteps = event.values[0].toInt()
        }
        trackerHelper.settings.workoutSteps = event.values[0].toInt()
        updateNotification()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}