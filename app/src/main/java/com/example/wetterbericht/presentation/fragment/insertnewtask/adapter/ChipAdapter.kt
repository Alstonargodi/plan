package com.example.wetterbericht.presentation.fragment.insertnewtask.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wetterbericht.R
import com.example.wetterbericht.data.local.ChipAlarm
import kotlinx.android.synthetic.main.chip_card.view.*

class ChipAdapter: RecyclerView.Adapter<ChipAdapter.ViewHolder>() {
    private var datalist = emptyList<ChipAlarm>()
    private lateinit var timeCallback : timeCallBack

    fun onTimeCallback(timeCallBack: timeCallBack){
        this.timeCallback = timeCallBack
    }

    class ViewHolder(view : View): RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chip_card,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]
        holder.itemView.tv_chip_title.text = item.name

        holder.itemView.tv_chip_title.setOnClickListener {
            holder.itemView.tv_chip_title.setTextColor(Color.BLACK)
            timeCallback.timeCallBack(item.time)
        }
    }

    override fun getItemCount(): Int = datalist.size

    fun setData(data : List<ChipAlarm>){
        datalist = data
        notifyDataSetChanged()
    }

    interface timeCallBack{
        fun timeCallBack(time : String)
    }
}