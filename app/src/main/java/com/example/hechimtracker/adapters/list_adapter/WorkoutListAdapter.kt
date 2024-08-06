package com.example.hechimtracker.adapters.list_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hechimtracker.databinding.ListItemBinding
import com.example.hechimtracker.model.database.WorkoutPoint

//class BusStopAdapter(
//    private val onItemClicked: (WorkoutPoint) -> Unit
//) : ListAdapter<WorkoutPoint, BusStopAdapter.BusStopViewHolder>(DiffCallback) {
//
//    companion object {
//        private val DiffCallback = object : DiffUtil.ItemCallback<WorkoutPoint>() {
//            override fun areItemsTheSame(oldItem: WorkoutPoint, newItem: WorkoutPoint): Boolean {
//                return oldItem.workout.workoutId == newItem.workout.workoutId
//            }
//
//            override fun areContentsTheSame(oldItem: WorkoutPoint, newItem: WorkoutPoint): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusStopViewHolder {
//        val viewHolder = BusStopViewHolder(
//            ListItemBinding.inflate(
//                LayoutInflater.from( parent.context),
//                parent,
//                false
//            )
//        )
//        viewHolder.itemView.setOnClickListener {
//            val position = viewHolder.adapterPosition
//            onItemClicked(getItem(position))
//        }
//        return viewHolder
//    }
//
//    override fun onBindViewHolder(holder: BusStopViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    class BusStopViewHolder(
//        private var binding: ListItemBinding
//    ): RecyclerView.ViewHolder(binding.root) {
//        fun bind(workoutPoint: WorkoutPoint) {
//            binding.count.text = workoutPoint.points.size.toString()
//            binding.startTime.text = workoutPoint.workout.startTime
//            binding.distance.text = workoutPoint.workout.distance?.toInt().toString() + " m"
//            binding.duration.text = "${workoutPoint.workout.duration.toString()} s"
//        }
//    }
//}

class WorkoutListAdapter(
    var items: List<WorkoutPoint>,
    val itemClickListener: ItemClickListener
): RecyclerView.Adapter<WorkoutListAdapter.AppOptionsViewHolder>(
) {
    inner class AppOptionsViewHolder(
        val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppOptionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater, parent, false)
        return AppOptionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppOptionsViewHolder, position: Int) {
        holder.binding.apply {
            count.text = items[position].points.size.toString()
            startTime.text = items[position].workout.startTime
            distance.text = items[position].workout.distance?.toInt().toString() + " m"
            duration.text = "${items[position].workout.duration.toString()} s"
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface ItemClickListener {
        fun onItemClick(item: WorkoutPoint): Unit
    }



}