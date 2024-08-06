package com.example.hechimtracker

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.hechimtracker.adapters.list_adapter.WorkoutListAdapter
import com.example.hechimtracker.databinding.FragmentListBinding
import com.example.hechimtracker.model.database.WorkoutPoint
import com.example.hechimtracker.model.local.Resource
import com.example.hechimtracker.service.TrackerService
import com.example.hechimtracker.view_model.DatabaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    private val databaseViewModel: DatabaseViewModel by activityViewModels()
    private lateinit var binding: FragmentListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        lifecycleScope.launch {

            databaseViewModel.getWorkouts()
            databaseViewModel.list.collectLatest {
                if(it is Resource.Loading) {
                    println("resource is loading")
                    return@collectLatest
                }



                println("id list: ${it.data!!.size}")
                val adapter = WorkoutListAdapter(
                    it.data,
                    itemClickListener = object : WorkoutListAdapter.ItemClickListener {
                        //list item on click
                        override fun onItemClick(item: WorkoutPoint) {
                            val action = ListFragmentDirections.actionListFragmentToDetailFragment(workout = item)
                            findNavController().navigate(action)

                        }
                    }
                )

                val recyclerView = binding.workoutList
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(activity)
            }

        }

        binding.listButton.setOnClickListener {
            startService()
        }

        return binding.root


    }
    private fun startService() {
        val serviceIntent = Intent(requireContext(), TrackerService::class.java)
        serviceIntent.action = "START"

        requireContext().startService(serviceIntent)
    }


}