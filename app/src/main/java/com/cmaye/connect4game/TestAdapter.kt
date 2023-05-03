package com.cmaye.connect4game

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.cmaye.connect4game.databinding.GridItemBinding
import com.cmaye.connect4game.model.Coin

class TestAdapter : RecyclerView.Adapter<TestAdapter.ViewHolder>() {

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
                Log.e("winner",item.toString())

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

        Log.e("dataALL",getCurrentItemList().toString())
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

                getCurrentItemList().find { it.id  == middleCoil}?.isWinner = true
                getCurrentItemList().find { it.id  == right1Coil}?.isWinner = true
                getCurrentItemList().find { it.id  == right2Coil}?.isWinner = true
                getCurrentItemList().find { it.id  == right3Coil}?.isWinner = true
                getCurrentItemList().find { it.id  == middleCoil}?.player = if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer
                getCurrentItemList().find { it.id  == right1Coil}?.player = if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer
                getCurrentItemList().find { it.id  == right2Coil}?.player = if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer
                getCurrentItemList().find { it.id  == right3Coil}?.player = if (currentPlayer ==  Constant.Player.RedPlayer) Constant.Player.RedPlayer else Constant.Player.YellowPlayer
                notifyDataSetChanged()
                return true
            }
        }


//
//        val start = (row * Constant.columnCount)  + column
//        //start  14  = (2 * 7)
//
//        //          13       (14 + 7 = 22)  - 4
//        for (i in start..(start + Constant.columnCount) - 4) {
//            val firstCoil = ids.find { it == i }
//            val secondCoil = ids.find { it == i + 1 }
//            val thirdCoil = ids.find { it == i + 2 }
//            val fourthCoil = ids.find { it == i + 3 }
//
//            if (firstCoil != null && secondCoil != null && thirdCoil != null && fourthCoil != null) {
//                return true
//            }
//        }

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
//        Log.e("first", firstCoil.toString())
//        Log.e("second", secondCoil.toString())
//        Log.e("third", thirdCoil.toString())
//        Log.e("fouth", fourthCoil.toString())

        val isWinPlayer =
            firstCoil != null && secondCoil != null && thirdCoil != null && fourthCoil != null
        if (isWinPlayer) {
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
        val bottomLeftCoil3 = ids.find { id <= startData && it == (id - (3 * Constant.columnCount)) + 3 }
        val bottomLeftCoil2 = ids.find { id <= startData && it == (id - (2 * Constant.columnCount)) + 2 }
        val bottomLeftCoil1 = ids.find { id <= startData && it == (id - Constant.columnCount) + 1 }

        //                                      26
        val bottomLeftMiddleCoil = ids.find { it == id }
        //                                  (7 + 7) - 1
        val topRightCoil1 = ids.find { id >= endData && it == (id + Constant.columnCount) - 1 }
        val topRightCoil2 = ids.find { id >= endData && it == (id + (2 * Constant.columnCount)) - 2 }
        val topRightCoil3 = ids.find { id >= endData && it == (id + (3 * Constant.columnCount)) - 3 }




        Log.e("firstLeft", bottomLeftCoil1.toString())
        Log.e("secondCoilLeft", bottomLeftCoil2.toString())
        Log.e("thirdCoilLeft", bottomLeftCoil3.toString())
        Log.e("firstCoil", bottomLeftMiddleCoil.toString())
        Log.e("secondCoil", topRightCoil1.toString())
        Log.e("thirdCoil", topRightCoil2.toString())
        Log.e("fourthCoil", topRightCoil3.toString())


        // bottom right to top left
        val bottomRightCoil3 = ids.find { it == (id - (3 * Constant.columnCount)) - 3 }
        val bottomRightCoil2 = ids.find { it == (id - (2 * Constant.columnCount)) - 2 }
        val bottomRightCoil1 = ids.find { it == (id - Constant.columnCount) - 1 }

        val bottomRight = ids.find { it == id }
        //                                  (7 + 7) - 1
        val topLeftCoil1 = ids.find { it == (id + Constant.columnCount) + 1 }
        val topLeftCoil2 = ids.find { it == (id + (2 * Constant.columnCount)) + 2 }
        val topLeftCoil3 = ids.find { it == (id + (3 * Constant.columnCount)) + 3 }


        if ((bottomRightCoil3 != null && bottomRightCoil2 != null && bottomRightCoil1 != null && bottomRight != null)
            || (bottomRightCoil2 != null && bottomRightCoil1 != null && bottomRight != null && topLeftCoil1 != null)
            || (bottomRightCoil1 != null && bottomRight != null && topLeftCoil1 != null && topLeftCoil2 != null)
            || (bottomRight != null && topLeftCoil1 != null && topLeftCoil2 != null && topLeftCoil3 != null)
        ) {
            return true
        }


        if ((bottomLeftCoil3 != null && bottomLeftCoil2 != null && bottomLeftCoil1 != null && bottomLeftMiddleCoil != null)
            || (bottomLeftCoil2 != null && bottomLeftCoil1 != null && bottomLeftMiddleCoil != null && topRightCoil1 != null)
            || (bottomLeftCoil1 != null && bottomLeftMiddleCoil != null && topRightCoil1 != null && topRightCoil2 != null)
            || (bottomLeftMiddleCoil != null && topRightCoil1 != null && topRightCoil2 != null && topRightCoil3 != null)
        ) {
            return true
        }

        return false // no diagonal win found
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
