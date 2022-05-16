package com.thunder.thunderplane.level_page

import com.thunder.thunderplane.bean.LevelData

interface LevelRepository {

    fun getLevelDataList():MutableList<LevelData>

}