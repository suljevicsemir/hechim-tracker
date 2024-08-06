package com.example.hechimtracker

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.hechimtracker.databinding.FragmentDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.CancelableCallback
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.UUID


class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView
    private val args by navArgs<DetailFragmentArgs>()

    private fun getImageFile(bitmap: Bitmap): File {

        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.png")
        val stream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,stream)
        stream.flush()
        stream.close()
        return file

    }

    val snapshotReadyCallback : GoogleMap.SnapshotReadyCallback = GoogleMap.SnapshotReadyCallback { selectedScreenShot ->
        if(selectedScreenShot != null) {
            //getImageFile(selectedScreenShot)
            binding.mapPreview.setImageBitmap(selectedScreenShot)
            println("SELECTED SCREEN SHOT NOT NULL")
        }

    }

    val onMapLoadedCallback : GoogleMap.OnMapLoadedCallback = GoogleMap.OnMapLoadedCallback {

        val bc = LatLngBounds.Builder()
        for (item in args.workout.points) {
//            latlngHistory.add(item)
            bc.include(item.toLatLng())
        }

        bc.include(args.workout.points.first().toLatLng())
        bc.include(args.workout.points.last().toLatLng())


        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bc.build(),
                binding.workoutMap.width,
                binding.workoutMap.height,
                180
            ),
            1000,
            object : CancelableCallback {
                override fun onCancel() {
                    TODO("Not yet implemented")
                }

                override fun onFinish() {
                    googleMap.snapshot(snapshotReadyCallback)

                }

            },

        )



    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        mapView = binding.workoutMap
        mapView.onCreate(savedInstanceState)
        mapView.onResume()


        binding.button.setOnClickListener {
            googleMap.snapshot(snapshotReadyCallback)
        }



        lifecycleScope.launch {
            mapView.getMapAsync { map ->
                googleMap = map



                val firstPoint = LatLng(args.workout.points.first().latitude!!, args.workout.points.first().longitude!!)
                val lastPoint = LatLng(args.workout.points.last().latitude!!, args.workout.points.last().longitude!!)

                googleMap.addMarker(
                    MarkerOptions().position(firstPoint).icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                )
                googleMap.addMarker(
                    MarkerOptions().position(lastPoint).icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                    )
                )
                googleMap.addPolyline(PolylineOptions().addAll(
                    args.workout.points.map {
                        LatLng(it.latitude!!, it.longitude!!)
                    }
                ).color(requireContext().resources.getColor(R.color.white))
                )

                val bc = LatLngBounds.Builder()
                for (item in args.workout.points) {
//            latlngHistory.add(item)
                    bc.include(item.toLatLng())
                }

                bc.include(args.workout.points.first().toLatLng())
                bc.include(args.workout.points.last().toLatLng())


                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bc.build(),
                        binding.workoutMap.width,
                        binding.workoutMap.height,
                        180
                    ),
                    object : CancelableCallback {
                        override fun onCancel() {
                            TODO("Not yet implemented")
                        }

                        override fun onFinish() {
                            googleMap.snapshot(snapshotReadyCallback)

                        }

                    },

                    )
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                ))
                //googleMap.setOnMapLoadedCallback(onMapLoadedCallback)






                val width = binding.root.height
                val height = binding.workoutMap.height

            }
        }







        return binding.root
    }


}