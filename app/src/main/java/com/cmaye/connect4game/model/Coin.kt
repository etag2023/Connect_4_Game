package com.cmaye.connect4game.model

import com.cmaye.connect4game.Constant

data class Coin(
    var id: Int,
    var player: Constant.Player = Constant.Player.None,
    var isWinner : Boolean  = false
)
