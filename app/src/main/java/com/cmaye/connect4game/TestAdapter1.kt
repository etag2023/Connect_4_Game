package com.cmaye.connect4game

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.cmaye.connect4game.databinding.GridItemBinding
import com.cmaye.connect4game.model.Coin

class TestAdapter1 : RecyclerView.Adapter<TestAdapter1.ViewHolder>() {

    private val coinItemCallback = object : ItemCallback<Coin>() {
        override fun areItemsTheSame(oldItem: Coin, newItem: Coin) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Coin, newItem: Coin) = oldItem == newItem
    }
    var currentPlayer = Constant.Player.RedPlayer


    val differ = AsyncListDiffer(this, coinItemCallback)

    fun getCurrentItemList() = differ.currentList

    inner class ViewHolder(val binding: GridItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: Coin) {
            with(binding) {
                if (item.isWinner)
                {
                    when (item.player) {
                        Constant.Player.RedPlayer -> {
                            imgIcon.setBackgroundResource(R.drawable.winner_red_color)
                        }
                        Constant.Player.YellowPlayer -> {
                            imgIcon.setBackgroundResource(R.drawable.winner_yellow_color)
                        }
                        Constant.Player.None -> {
                            imgIcon.setBackgroundColor(Constant.whiteColor)

                        }
                    }
                }else{
                    when (item.player) {
                        Constant.Player.RedPlayer -> {
//                        imgIcon.setBackgroundResource(R.drawable.winner_red_color)
                            imgIcon.setBackgroundColor(Constant.redColor)
                        }
                        Constant.Player.YellowPlayer -> {
                            imgIcon.setBackgroundColor(Constant.yellowColor)
                        }
                        Constant.Player.None -> {
                            imgIcon.setBackgroundColor(Constant.whiteColor)

                        }
                    }
                }

                cardView.setOnClickListener {



                    Log.e("index",adapterPosition.toString())

                    if (item.player != Constant.Player.None) {
                        return@setOnClickListener
                    }

                    if (item.id in 1..Constant.columnCount) {
                        getCurrentItemList()[adapterPosition].player = currentPlayer
                        val isWin =
                            checkWin(getCurrentItemList()[adapterPosition].id, currentPlayer)
                        if (isWin) {
                            Log.e("MyplayerWin", "True")
                            mClickListener?.showWinPlayer(currentPlayer.toString())
                        }
                        toggleCurrentPlayer()
                        notifyItemChanged(adapterPosition)
                        return@setOnClickListener
                    }
//
//                      9-7=2           8
//                    if (item.id in Constant.columnCount.plus(1)..totalRowColumnCount) {
//                        if (getCurrentItemList()[adapterPosition.plus(Constant.columnCount)].player != Constant.Player.None) {
//                            getCurrentItemList()[adapterPosition].player = currentPlayer
//                            toggleCurrentPlayer()
//                            notifyItemChanged(adapterPosition)
//                        }
//                    }

                    val previousItemId = item.id - Constant.columnCount
                    val previousItem = getCurrentItemList().find { it.id == previousItemId }

                    if (previousItem != null) {
                        if (previousItem.player != Constant.Player.None) {
                            getCurrentItemList()[adapterPosition].player = currentPlayer
                            val isWin =
                                checkWin(getCurrentItemList()[adapterPosition].id, currentPlayer)
                            if (isWin) {
                                Log.e("MyplayerWin", "True")
                                mClickListener?.showWinPlayer(currentPlayer.toString())
                            }
                            toggleCurrentPlayer()
                            notifyItemChanged(adapterPosition)
                        }
                    }
                }
            }
        }
    }

    fun toggleCurrentPlayer() {
        currentPlayer = when (currentPlayer) {

            Constant.Player.RedPlayer -> {
                Log.e("Change to Yellow Player", "TRUE")
                mClickListener?.isCheck("Yellow Player", getCurrentItemList())
                Constant.Player.YellowPlayer
            }

            Constant.Player.YellowPlayer -> {
                Log.e("Change to Yellow Player", "False")
                mClickListener?.isCheck("Red Player", getCurrentItemList())
                Constant.Player.RedPlayer
            }

            Constant.Player.None -> {
                Log.e("Change to Yellow Player", "FALSe")
                mClickListener?.isCheck("Red Player", getCurrentItemList())
                Constant.Player.RedPlayer
            }
        }
    }

    fun resetGame() {
        currentPlayer = Constant.Player.RedPlayer
        getCurrentItemList().forEach {
            it.player = Constant.Player.None
            it.isWinner = false
        }

        currentPlayer = Constant.Player.None
        toggleCurrentPlayer()
        notifyDataSetChanged()
    }

    fun checkWin(id: Int, player: Constant.Player): Boolean {

        val myPlayer = getCurrentItemList().filter { it.player == player }

        val ids = myPlayer.map { it.id }

        val horizontal = horizontal(id, ids)
        val vertical = vertical(id, ids)
        var diagonal = diagonal(id, ids)
        if (horizontal || vertical || diagonal) {
            Log.e(
                "My player win :",
                "$player : WIN IN ${if (horizontal) "H" else if (vertical) "V" else "D"}"
            )
            return true
        }

//        Log.e("My Player Name", player.toString())
//        Log.e(" my ID", ids.toString())
        return false
    }

    fun horizontal(id: Int, ids: List<Int>): Boolean {
        //       1  = 7 / 7
        val row = id / Constant.columnCount
        //       1 =7  % 7
        val column = id % Constant.columnCount

        var endData = 0
        if (column > 0) {
            endData = (row + 1) * Constant.columnCount
        } else {
            // 7   =  1  * 7
            endData = row * Constant.columnCount
        }

        var startData = 0

        if (column > 0) {
            startData = (row * Constant.columnCount) + 1
        } else {
            // 7
            startData = ((row - 1) * Constant.columnCount) + 1
        }
//                  1               7-3=4
        for (i in startData..endData - 3) {

            val middleCoil = ids.find { it in startData..endData && it == i }
            val right1Coil = ids.find { it in startData..endData && it == i + 1 }
            val right2Coil = ids.find { it in startData..endData && it == i + 2 }
            val right3Coil = ids.find { it in startData..endData &&  it == i + 3 }

            if (middleCoil != null && right1Coil != null && right2Coil != null && right3Coil != null) {
                Log.e("all",getCurrentItemList().toString())
                var idList = listOf<Int>(middleCoil,right1Coil,right2Coil,right3Coil)
                insertWinnerData(idList,true,if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer)
                notifyDataSetChanged()
                return true
            }
        }
        return false // no horizontal win found
    }


    fun vertical(id: Int, ids: List<Int>): Boolean {
        //          23
        val firstCoil = ids.find {
            it == id
        }
        val secondCoil = ids.find {
            //      23 - 7 = 16
            it == id - Constant.columnCount
        }
        val thirdCoil = ids.find {
            // 23 - (2* 7 = 14) = 9
            it == id - (2 * Constant.columnCount)
        }
        val fourthCoil = ids.find {
            // 23 - (3* 7 = 21) = 2
            it == id - (3 * Constant.columnCount)
        }
        if(firstCoil != null && secondCoil != null && thirdCoil != null && fourthCoil != null)
        {
            var idList = listOf<Int>(firstCoil,secondCoil,thirdCoil,fourthCoil)
            insertWinnerData(idList,true,if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer)
            notifyDataSetChanged()
            return true
        }
        return false
    }

    fun diagonal(id: Int, ids: List<Int>): Boolean {


        //       1  = 7 / 7
        val row = id / Constant.columnCount
        //       1 =7  % 7
        val column = id % Constant.columnCount

        var endData = 0
        if (column > 0) {
            endData = (row + 1) * Constant.columnCount
        } else {
            // 7   =  1  * 7
            endData = row * Constant.columnCount
        }

        var startData = 0

        if (column > 0) {
            startData = (row * Constant.columnCount) + 1
        } else {
            // 7
            startData = ((row - 1) * Constant.columnCount) + 1
        }




        // bottom left to top right
        //                                      26
//        val bottomLeftCoil3 = ids.find { it >= (startData - (3 * Constant.columnCount)) && it == (id - (3 * Constant.columnCount)) + 3 }
//        val bottomLeftCoil2 = ids.find { it >= (startData - (2 * Constant.columnCount)) && it == (id - (2 * Constant.columnCount)) + 2 }
//        val bottomLeftCoil1 = ids.find { it >= (startData -  Constant.columnCount) && it == (id - Constant.columnCount) + 1 }


        var bottomLeftCoil3 = ids.find { it == (id - (3 * Constant.columnCount)) + 3 }
        var bottomLeftCoil2 = ids.find { it == (id - (2 * Constant.columnCount)) + 2 }
        var bottomLeftCoil1 = ids.find { it == (id - Constant.columnCount) + 1 }

        //                                      26
        var bottomLeftMiddleCoil = ids.find { it == id }
        //                                  (7 + 7) - 1
        var topRightCoil1 = ids.find {  it == (id + Constant.columnCount) - 1 }
        var topRightCoil2 = ids.find {  it == (id + (2 * Constant.columnCount)) - 2 }
        var topRightCoil3 = ids.find {  it == (id + (3 * Constant.columnCount)) - 3 }



        when(bottomLeftMiddleCoil)
        {
             endData -> {
                 bottomLeftCoil3 =  null
                 bottomLeftCoil2 =  null
                 bottomLeftCoil1 =  null
             }
            endData.minus(1) ->{
                bottomLeftCoil3 = null
                bottomLeftCoil2 = null
            }
            endData.minus(2) -> {
                bottomLeftCoil3 = null
            }

            startData -> {
                topRightCoil3 = null
                topRightCoil2 = null
                topRightCoil1 = null
            }

            startData.minus(1) -> {
                topRightCoil3 = null
                topRightCoil2 = null
            }

            startData.minus(2) -> {
                topRightCoil3 = null
            }
        }


        Log.e("bottemLeft3",bottomLeftCoil3.toString())
        Log.e("bottemLeft2",bottomLeftCoil2.toString())
        Log.e("bottemLeft1",bottomLeftCoil1.toString())
        Log.e("bottomLeftMiddleCoil",bottomLeftMiddleCoil.toString())
        Log.e("topRightCoil1",topRightCoil1.toString())
        Log.e("topRightCoil2",topRightCoil2.toString())
        Log.e("topRightCoil3",topRightCoil3.toString())


        // bottom right to top left
        var bottomRightCoil3 = ids.find { it == (id - (3 * Constant.columnCount)) - 3 }
        var bottomRightCoil2 = ids.find { it == (id - (2 * Constant.columnCount)) - 2 }
        var bottomRightCoil1 = ids.find { it == (id - Constant.columnCount) - 1 }

        var bottomRight = ids.find { it == id }
        //                                  (7 + 7) - 1
        var topLeftCoil1 = ids.find { it == (id + Constant.columnCount) + 1 }
        var topLeftCoil2 = ids.find { it == (id + (2 * Constant.columnCount)) + 2 }
        var topLeftCoil3 = ids.find { it == (id + (3 * Constant.columnCount)) + 3 }


        when(bottomLeftMiddleCoil)
        {
            startData -> {
                bottomRightCoil3 = null
                bottomRightCoil2 = null
                bottomRightCoil1 = null
            }

            startData.plus(1) -> {
                bottomRightCoil3 = null
                bottomRightCoil2 = null
            }

            startData.plus(2) -> {
                bottomRightCoil3 = null
            }

            endData -> {
                topLeftCoil3 =  null
                topLeftCoil2 =  null
                topLeftCoil3 =  null
            }
            endData.plus(1) ->{
                topLeftCoil3 =  null
                topLeftCoil2 =  null
            }
            endData.plus(2) -> {
                topLeftCoil3 =  null
                topLeftCoil2 =  null
            }
        }


        if (bottomRightCoil3 != null && bottomRightCoil2 != null && bottomRightCoil1 != null && bottomRight != null)
            {
                var idList = listOf<Int>(bottomRightCoil3,bottomRightCoil2,bottomRightCoil1,bottomRight)
                insertWinnerData(idList,true,if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer)
                notifyDataSetChanged()
                return true
            }
        else if (bottomRightCoil2 != null && bottomRightCoil1 != null && bottomRight != null && topLeftCoil1 != null) {
            var idList = listOf<Int>(bottomRightCoil2,bottomRightCoil1,bottomRight,topLeftCoil1)
            insertWinnerData(idList,true,if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer)
            notifyDataSetChanged()
            return true
        }
        else if(bottomRightCoil1 != null && bottomRight != null && topLeftCoil1 != null && topLeftCoil2 != null) {
            var idList = listOf<Int>(bottomRightCoil1,bottomRight,topLeftCoil1,topLeftCoil2)
            insertWinnerData(idList,true,if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer)
            notifyDataSetChanged()
            return true
        }
        else if (bottomRight != null && topLeftCoil1 != null && topLeftCoil2 != null && topLeftCoil3 != null)
        {
            var idList = listOf<Int>(bottomRight,topLeftCoil1,topLeftCoil2,topLeftCoil3)
            insertWinnerData(idList,true,if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer)
            notifyDataSetChanged()
            return true
        }


        else if (bottomLeftCoil3 != null && bottomLeftCoil2 != null && bottomLeftCoil1 != null && bottomLeftMiddleCoil != null){
            var idList = listOf<Int>(bottomLeftCoil3,bottomLeftCoil2,bottomLeftCoil1,bottomLeftMiddleCoil)
            insertWinnerData(idList,true,if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer)
            notifyDataSetChanged()
            return true
        }
        else if(bottomLeftCoil2 != null && bottomLeftCoil1 != null && bottomLeftMiddleCoil != null && topRightCoil1 != null){
            var idList = listOf<Int>(bottomLeftCoil2,bottomLeftCoil1,bottomLeftMiddleCoil,topRightCoil1)
            insertWinnerData(idList,true,if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer)
            notifyDataSetChanged()
            return true
        }
        else if(bottomLeftCoil1 != null && bottomLeftMiddleCoil != null && topRightCoil1 != null && topRightCoil2 != null){
            var idList = listOf<Int>(bottomLeftCoil1,bottomLeftMiddleCoil,topRightCoil1,topRightCoil2)
            insertWinnerData(idList,true,if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer)
            notifyDataSetChanged()
            return true
        }
        else if(bottomLeftMiddleCoil != null && topRightCoil1 != null && topRightCoil2 != null && topRightCoil3 != null) {
            var idList = listOf<Int>(bottomLeftMiddleCoil,topRightCoil1,topRightCoil2,topRightCoil3)
            insertWinnerData(idList,true,if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer)
            notifyDataSetChanged()
            return true
        }

        else {
            return false
        }
    }

//    fun diagonal(id: Int, ids: List<Int>): Boolean {
//
//        // 4
//        val row = id / Constant.columnCount
//        //         4 / 7 = 0
//        val column = id % Constant.columnCount
//        //            4 %   7 = 4
//
//        // 7                (0   *  7 = 0)  + 1 =  4
//        for (i in 0..((row * Constant.columnCount) + column) step (Constant.columnCount)) {
//            //                              7
//            val firstCoil = ids.find { it == i }
//            //                                (7 + 7) - 1 = 13
//            val secondCoil = ids.find { it == i + Constant.columnCount - 1 }
//            //                                      (7 + 14) - 1 = 20
//            val thirdCoil = ids.find { it == i + 2 * (Constant.columnCount - 1) }
//            //                                        (7 +21) - 1 = 27
//            val fourthCoil = ids.find { it == i + 3 * (Constant.columnCount - 1) }
//
//            if (firstCoil != null && secondCoil != null && thirdCoil != null && fourthCoil != null) {
//                return true
//            }
//        }
//
//        // bottom right to top left
//        val startDownward = id - (row * Constant.columnCount - column)
//        //    =  1 - (0  * 7  - 1 )
//        for (i in startDownward..startDownward + (row * Constant.columnCount - column) step (Constant.columnCount)) {
//            val firstCoil = ids.find { it == i }
//            val secondCoil = ids.find { it == i + Constant.columnCount + 1 }
//            val thirdCoil = ids.find { it == i + 2 * (Constant.columnCount + 1) }
//            val fourthCoil = ids.find { it == i + 3 * (Constant.columnCount + 1) }
//
//            if (firstCoil != null && secondCoil != null && thirdCoil != null && fourthCoil != null) {
//                return true
//            }
//        }
//
//        return false // no diagonal win found
//    }




    private fun insertWinnerData(ids : List<Int>,isWinner : Boolean, color : Constant.Player)
    {
        ids.forEach {id ->
            getCurrentItemList().find { it.id == id }?.isWinner = isWinner
            getCurrentItemList().find { it.id  == id}?.player = color
        }



    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GridItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getCurrentItemList()[position]
        holder.bindView(item)
    }

    override fun getItemCount(): Int = getCurrentItemList().size

    interface OnClickListener {
        fun isCheck(currentPlayer: String, list: MutableList<Coin>)

        fun showToast(msg: String)

        fun showWinPlayer(msg: String)
    }

    var mClickListener: OnClickListener? = null
    fun setOnClickListener(clickListener: OnClickListener) {
        mClickListener = clickListener
    }

}
