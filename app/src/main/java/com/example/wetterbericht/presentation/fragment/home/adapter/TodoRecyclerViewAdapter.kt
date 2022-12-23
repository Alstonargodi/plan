package com.example.wetterbericht.presentation.fragment.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import android.annotation.SuppressLint
import android.util.Log
import com.example.wetterbericht.databinding.ItemcvTodoBinding
import com.example.wetterbericht.data.local.entity.dailytask.TodoLocal
import com.example.wetterbericht.presentation.componen.TaskTitleView

class TodoRecyclerViewAdapter(
        private val data: List<TodoLocal>,
        val onChecked: (TodoLocal,Boolean) -> Unit
    ) : RecyclerView.Adapter<TodoRecyclerViewAdapter.ViewHolder>() {

    private lateinit var detailCallback : DetailCallback

    fun detailOnItemCallback(callback : DetailCallback){
        this.detailCallback = callback
    }

    class ViewHolder(var binding : ItemcvTodoBinding): RecyclerView.ViewHolder(binding.root){
        private lateinit var detailTodo : TodoLocal
        //TODO 9
        fun bind(item : TodoLocal){
            detailTodo = item
            binding.tvtoxoCardName.text = item.title
            binding.tvtoxoCardName.setTextColor(item.levelColor)

            //TODO 10
            when{
                item.isComplete->{
                    binding.tvtoxoCardName.state = TaskTitleView.DONE
                    binding.checkboxtask.isChecked = true
                }
                item.dateDueMillis < System.currentTimeMillis()->{
                    binding.tvtoxoCardName.state = TaskTitleView.OVERDUE
                    binding.checkboxtask.isChecked = false
                }
                else->{
                    binding.tvtoxoCardName.state = TaskTitleView.NORMAL
                    binding.checkboxtask.isChecked = false
                }
            }
        }
        fun getData(): TodoLocal = detailTodo
    }

    //TODO 8
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemcvTodoBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val item = data[position]
            holder.bind(item)

            holder.binding.layTodo.setOnClickListener {
                detailCallback.detailCallBack(item)
            }

            holder.binding.checkboxtask.setOnClickListener {
                onChecked(item,!item.isComplete)
            }
        }catch (e : Exception){
            Log.d("task",e.toString())
        }

    }

    override fun getItemCount(): Int = data.size

    interface DetailCallback{
        fun detailCallBack(data : TodoLocal)
    }
}