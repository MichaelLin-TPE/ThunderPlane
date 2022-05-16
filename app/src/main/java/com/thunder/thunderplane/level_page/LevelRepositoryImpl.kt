package com.thunder.thunderplane.level_page

import com.thunder.thunderplane.R
import com.thunder.thunderplane.base.MyApplication
import com.thunder.thunderplane.bean.LevelData

class LevelRepositoryImpl : LevelRepository{

    override fun getLevelDataList(): MutableList<LevelData> {
        val dataList = mutableListOf<LevelData>()
        dataList.add(LevelData(R.drawable.galaxy1,MyApplication.instance.applicationContext.getString(R.string.first_level),1))
        dataList.add(LevelData(R.drawable.galaxy2,MyApplication.instance.applicationContext.getString(R.string.second_level),2))
        dataList.add(LevelData(R.drawable.galaxy3,MyApplication.instance.applicationContext.getString(R.string.third_level),3))
        dataList.add(LevelData(R.drawable.galaxy4,MyApplication.instance.applicationContext.getString(R.string.fourth_level),4))
        dataList.add(LevelData(R.drawable.galaxy5,MyApplication.instance.applicationContext.getString(R.string.fifth_level),5))
        dataList.add(LevelData(R.drawable.galaxy6,MyApplication.instance.applicationContext.getString(R.string.sixth_level),6))
        dataList.add(LevelData(R.drawable.galaxy7,MyApplication.instance.applicationContext.getString(R.string.seventh_level),7))
        dataList.add(LevelData(R.drawable.galaxy8,MyApplication.instance.applicationContext.getString(R.string.eighth_level),8))
        dataList.add(LevelData(R.drawable.galaxy9,MyApplication.instance.applicationContext.getString(R.string.night_level),9))
        dataList.add(LevelData(R.drawable.galaxy10,MyApplication.instance.applicationContext.getString(R.string.ten_level),10))
        return dataList
    }


}