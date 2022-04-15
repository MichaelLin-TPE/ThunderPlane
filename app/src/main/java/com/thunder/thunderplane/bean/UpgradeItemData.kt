package com.thunder.thunderplane.bean

import android.view.View

data class UpgradeItemData(val updateItem: View, var isRight: Boolean, var isTop : Boolean){
    val bulletList = ArrayList<BulletData>()
}
