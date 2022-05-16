package com.thunder.thunderplane.level_page

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thunder.thunderplane.R
import com.thunder.thunderplane.bean.LevelData
import com.thunder.thunderplane.tool.Tool

class LevelAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList = mutableListOf<LevelData>()

    companion object{
        const val LEFT_PLANET = 0
        const val RIGHT_PLANET = 1
    }

    fun setNewData(dataList : MutableList<LevelData>){
        this.dataList.clear()
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        if (position % 2 == 0){
            return LEFT_PLANET
        }
        return RIGHT_PLANET
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == LEFT_PLANET){
            return LeftPlanetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.left_planet_layout,parent,false))
        }

        return LeftPlanetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.right_planet_layout,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is LeftPlanetViewHolder){
            holder.setData(dataList[position])
        }


    }

    override fun getItemCount(): Int = dataList.size


    private class LeftPlanetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon : ImageView = itemView.findViewById(R.id.galaxy_icon)
        private val tvTitle : TextView = itemView.findViewById(R.id.galaxy_title)

        fun setData(data : LevelData){
            ivIcon.setImageResource(data.imageId)
            tvTitle.text = data.title
            Tool.startRotate(ivIcon,360f)
        }
    }


}