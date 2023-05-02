package com.cmaye.connect4game

import com.cmaye.connect4game.model.Coin

class ConnectFourGame() {
    val list = mutableListOf<Coin>()

    init {
        createBoard()
    }

    private fun createBoard() {
        var id = 1
        for (row in 1..Constant.rowCount) {
            for (column in 1..Constant.columnCount) {
                list.add(Coin(id, Constant.Player.None))
                id += 1
            }
        }
    }


    fun checkWin() {

    }


    fun horizontal() {

    }

    fun vertical() {

    }

}