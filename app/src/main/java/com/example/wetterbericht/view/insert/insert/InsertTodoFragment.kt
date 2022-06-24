package com.example.wetterbericht.view.insert.insert

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetterbericht.databinding.FragmentInsertTodoBinding
import com.example.wetterbericht.model.local.TodoLocal
import com.example.wetterbericht.model.local.TodoSubTask
import com.example.wetterbericht.view.home.adapter.SubTaskAdapter
import com.example.wetterbericht.view.insert.adapter.ChipAdapter
import com.example.wetterbericht.view.insert.dialog.InsertAlarmChipFragment
import com.example.wetterbericht.view.insert.dialog.InsertTagFragment
import com.example.wetterbericht.util.AlarmReceiver
import com.example.wetterbericht.util.AlarmReceiver.Companion.type_one_time
import com.example.wetterbericht.viewmodel.local.LocalViewModel
import com.example.wetterbericht.viewmodel.utils.obtainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import kotlin.properties.Delegates
import kotlin.streams.asSequence


class InsertTodoFragment : Fragment() {
    private lateinit var binding : FragmentInsertTodoBinding
    private lateinit var localViewModel: LocalViewModel
    private var taskList = arrayListOf<TodoSubTask>()
    private val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private var userId = Random().ints(10, 0, source.length)
        .asSequence()
        .map(source::get)
        .joinToString("")


    private lateinit var alarm : String
    private var leveColour by Delegates.notNull<Int>()
    private lateinit var alarmReceiver : AlarmReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsertTodoBinding.inflate(layoutInflater)
        localViewModel = obtainViewModel(requireActivity())
        alarmReceiver = AlarmReceiver()

        readChipAlarm()


        binding.btnAddchipalarm.setOnClickListener {
            val dialog = InsertAlarmChipFragment()
            val sFragment= requireActivity().supportFragmentManager
            dialog.show(sFragment,"dialog")
            dialog.onTimeCallback(object : InsertAlarmChipFragment.timeCallBack{
                override fun timeCallBack(time: String) {
                    deadlineTime(time)
                }
            })
        }

        binding.addtag.setOnClickListener {
            val dialog = InsertTagFragment()
            val sFragment= requireActivity().supportFragmentManager
            dialog.show(sFragment,"dialog")
            dialog.onTagCallBack(object : InsertTagFragment.onTagCallback{
                override fun tagCallBack(name: String, color: Int) {
                    binding.addtag.text = "#$name"
                    binding.addtag.setTextColor(Color.WHITE)
                    binding.addtag.setBackgroundColor(color)
                    leveColour = color

                }
            })
        }


        binding.btnAddsubtask.setOnClickListener {
            val task = binding.inserttodoSubtask.text.toString()
            val temp = TodoSubTask(
                0,
                task,
                false,
                userId
            )
            taskList.add(temp)
            lifecycleScope.launch {
                readSubtask()
            }
        }

        binding.btnaddtodo.setOnClickListener {
            insertTodo()
        }

        return binding.root
    }

    private fun readChipAlarm(){
        val adapter = ChipAdapter()
        val sideRv = binding.rvChip
        sideRv.adapter = adapter
        sideRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        localViewModel.readAlarmChip()
        localViewModel.responseAlarmChip.observe(viewLifecycleOwner){
            adapter.setData(it)
        }

        adapter.onTimeCallback(object : ChipAdapter.timeCallBack{
            override fun timeCallBack(time: String) {
               deadlineTime(time)
            }
        })
    }

    private fun deadlineTime(time : String){
        binding.etDeadlineTodo.apply {
            visibility = View.VISIBLE
            text = "Deadline set at $time"
            alarm = time
        }
    }

    private fun readSubtask(){
        val adapter = SubTaskAdapter()
        val recView = binding.rvSubtask
        recView.adapter = adapter
        recView.layoutManager = LinearLayoutManager(requireContext())

        adapter.submitList(taskList)
    }


    private fun insertTodo(){
        val name = binding.inserttodoName.text.toString()
        val description = binding.inserttodoDescription.text.toString()
        val levelTitle = binding.addtag.text.toString()

        val tempData = TodoLocal(
            userId,
            name,
            description,
            levelTitle,
            leveColour,
            getDate(),
            alarm,
            false
        )

        taskList.forEach {
            val tempSubtask = TodoSubTask(
                it.id,
                it.title,
                it.isComplete,
                it.todoId
            )
            localViewModel.insertSubtask(tempSubtask)
            Log.d("insert todo",tempSubtask.toString())
        }
        Log.d("insert todo",tempData.toString())
        localViewModel.insertTodoLocal(tempData)

        lifecycleScope.launch {
            setAlarm(name)
        }
    }

    private suspend fun setAlarm(name : String){
        delay(2000L)
        localViewModel.readTodo(name)
        localViewModel.responseTodoSearch.observe(viewLifecycleOwner){
            showToast("alarm set to ${it[0].timeDeadline}")
            val alarmId= (1..200).random()
            alarmReceiver.setOneAlarm(
                requireContext(),
                type_one_time,
                it[0].dateDeadline,
                it[0].timeDeadline,
                it[0].title,
                alarmId
            )
        }
    }

    private fun getDate(): String{
        val formatter = LocalDate.now()
        return formatter.toString()
    }

    companion object{
        const val time_key = "time_picker"
    }

    private fun showToast(title : String){
        Toast.makeText(requireContext(),title,Toast.LENGTH_SHORT).show()
    }

}