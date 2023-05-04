package com.cmaye.connect4game

import android.provider.SyncStateContract.Columns
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
                if (item.isWinner) {
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
                } else {
                    when (item.player) {
                        Constant.Player.RedPlayer -> {
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


                    Log.e("index", adapterPosition.toString())

                    if (item.player != Constant.Player.None) {
                        return@setOnClickListener
                    }

                    // last row item
                    if (item.id in ((Constant.rowCount * Constant.columnCount) - 6)..(Constant.rowCount * Constant.columnCount)) {
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

                    val previousItemId = item.id + Constant.columnCount
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

        val horizontal = horizontal(ids)
        val vertical = vertical(ids)
        var diagonal = diagonal(ids)
        var reverseDiagonal = reverseDiagonal(ids)

        notifyDataSetChanged()


        if (vertical || horizontal || diagonal || reverseDiagonal) {
            Log.e(
                "My player win :",
                "$player : WIN IN  D"
            )
            return true
        }
        return false
    }

    private fun horizontal(ids: List<Int>): Boolean {
        var isWin = false

        // check rows for a win
        for (row in 0 until Constant.rowCount) {
            for (col in 1..Constant.columnCount - 3) {

                if (ids.contains(getId(row, col)) &&
                    ids.contains(getId(row, col + 1)) &&
                    ids.contains(getId(row, col + 2)) &&
                    ids.contains(getId(row, col + 3))
                ) {

                    var idList = listOf<Int>(
                        getId(row, col),
                        getId(row, col + 1),
                        getId(row, col + 2),
                        getId(row, col + 3)
                    )
                    insertWinnerData(
                        idList,
                        true,
                        currentPlayer)
                    isWin = true
                }
            }
        }

        return isWin
    }


    fun vertical(ids: List<Int>): Boolean {
        var isWin = false

        // check columns for a win
        for (col in 1 .. Constant.columnCount) {
            for (row in 0 until Constant.rowCount - 3) {
//                2* 7 =14 +0 =14
//               3* 7 =21 +0 =8
//               4* 7 =28 +0 =15
//               5* 7 =35 +0 =22


                (row * Constant.columnCount) + col
                if (ids.contains(getId(row, col)) &&
                    ids.contains(getId(row + 1, col)) &&
                    ids.contains(getId(row + 2, col)) &&
                    ids.contains(getId(row + 3, col))
                ) {

                    var idList = listOf<Int>(
                        getId(row, col),
                        getId(row + 1, col),
                        getId(row + 2, col),
                        getId(row + 3, col)
                    )
                    insertWinnerData(
                        idList,
                        true,
                        currentPlayer)
                    isWin = true
                }
            }
        }

        return isWin
    }

    fun diagonal(ids: List<Int>): Boolean {
        var isWin = false

        for (row in 3 until Constant.rowCount) {
            for (col in 1..Constant.columnCount - 3) {

                // 22,16,10,4
                // 6 => 39,33

                //5 * 7 = 35+4 =39
                (row * Constant.columnCount) + col

                //                   5 * 7 = 35+4 =39
                //val firstCoin =    (row * Constant.columnCount) + col
                //                  4 * 7 = 14+5 =19
                //val sCoin =    getId(row - 1, col + 1)
//                                  3* 7 = 7+6 =13
                //val tCoin =   getId(row - 2, col + 2)
//                                  2* 7 = 0+7=7
                //val fCoin =    getId(row - 3, col + 3)


                if (ids.contains(getId(row, col)) &&
                    ids.contains(getId(row - 1, col + 1)) &&
                    ids.contains(getId(row - 2, col + 2)) &&
                    ids.contains(getId(row - 3, col + 3))
                ) {
                    var idList = listOf<Int>(
                        getId(row, col),
                        getId(row - 1, col + 1),
                        getId(row - 2, col + 2),
                        getId(row - 3, col + 3)
                    )
                    insertWinnerData(
                        idList,
                        true,
                        currentPlayer
                    )
                    isWin = true
                }
            }
        }

        return isWin
    }

    fun reverseDiagonal(ids: List<Int>): Boolean {
        var isWin = false

        for (row in 3 until Constant.rowCount) {
            for (col in Constant.columnCount downTo Constant.columnCount - 3) {

                //3,7  => 21 + 7  = 28
                //3,7  => 14 + 6  = 20
                //3,7  => 7 + 5  = 12
                //3,7  => 0 + 4  = 4


                //3,6  => 21 + 6  = 27
                //3,6  => 14 + 5  = 19
                //3,6  => 7 + 4= 11
                //3,6  => 0 + 3 = 3


                //3,5  => 21 + 5  = 26
                //3,5  => 14 + 4  = 18
                //3,5  => 7 + 3 = 10
                //3,5  => 0 + 2  = 2

                //3,4  => 21 + 4  = 25
                //3,4  => 14 + 3  = 17
                //3,4  => 7 + 2 = 9
                //3,4  => 0 + 1  = 1


                //5,5  => 35 + 5  = 41
                //5,5  => 28 + 4  = 32
                //5,5  => 21 + 3 = 24
                //5,5  => 14 + 2  = 16


                //5,4  => 42 + 4  = 46
                //5,4  => 35 + 3  = 38
                //5,4  => 28 + 2 = 30
                //5,4  => 21 + 1  = 22



                if (ids.contains(getId(row, col)) &&
                    ids.contains(getId(row - 1, col - 1)) &&
                    ids.contains(getId(row - 2, col - 2)) &&
                    ids.contains(getId(row - 3, col - 3))
                ) {
                    var idList = listOf<Int>(
                        getId(row, col),
                        getId(row - 1, col - 1),
                        getId(row - 2, col - 2),
                        getId(row - 3, col - 3)
                    )
                    insertWinnerData(
                        idList,
                        true,
                        currentPlayer)
                    isWin = true
                }
            }
        }

        return isWin
    }

    fun getId(row: Int, col: Int): Int {
        return (row * Constant.columnCount) + col
    }

    private fun insertWinnerData(ids: List<Int>, isWinner: Boolean, color: Constant.Player) {
        ids.forEach { id ->
            getCurrentItemList().find { it.id == id }?.isWinner = isWinner
            getCurrentItemList().find { it.id == id }?.player = color
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
