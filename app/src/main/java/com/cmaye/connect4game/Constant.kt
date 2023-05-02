package com.cmaye.connect4game

import android.graphics.Color

object Constant {
    val whiteColor = Color.rgb(255, 255, 255)
    val redColor = Color.rgb(220, 25, 25)
    val yellowColor = Color.rgb(232, 189, 48)

    val columnCount = 9
    val rowCount = 9

    enum class Player {
        RedPlayer, YellowPlayer, None
    }


}
