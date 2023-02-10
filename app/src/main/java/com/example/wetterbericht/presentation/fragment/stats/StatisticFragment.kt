package com.example.wetterbericht.presentation.fragment.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.wetterbericht.databinding.FragmentStatisticBinding
import com.example.wetterbericht.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat

class StatisticFragment : Fragment() {

    private lateinit var binding : FragmentStatisticBinding

    private val viewModel : StatisticFragmentViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext())
    }
    private var dateList = mutableListOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticBinding.inflate(layoutInflater)

        viewModel.readTodoTask().observe(viewLifecycleOwner){ data->
            binding.tvTotalActivites.setText(data.size.toString())
            data.forEach { data->
                dateList.add(data.dateDueMillis)
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val dateMax = dateFormat.format(dateList.maxOf { it })
            val dateMin = dateFormat.format(dateList.minOf { it })
            binding.tvStatDaterange.text = "from $dateMin until $dateMax"

        }

        return binding.root
    }

}