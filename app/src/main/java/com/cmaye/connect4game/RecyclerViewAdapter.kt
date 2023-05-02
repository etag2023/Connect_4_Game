package com.cmaye.connect4game

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.cmaye.connect4game.databinding.GridItemBinding
import com.cmaye.connect4game.model.Coin

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val coinItemCallback = object : ItemCallback<Coin>() {
        override fun areItemsTheSame(oldItem: Coin, newItem: Coin) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Coin, newItem: Coin) = oldItem == newItem
    }

    var currentPlayer = Constant.Player.RedPlayer


    val differ = AsyncListDiffer(this, coinItemCallback)

    fun getCurrentItemList() = differ.currentList

    var totalRowColumnCount: Int = Constant.rowCount * Constant.columnCount

    inner class ViewHolder(val binding: GridItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: Coin) {
            with(binding) {


                when (item.player) {
                    Constant.Player.RedPlayer -> {
                        imgIcon.setBackgroundColor(Constant.redColor);
                    }
                    Constant.Player.YellowPlayer -> {
                        imgIcon.setBackgroundColor(Constant.yellowColor);
                    }
                    Constant.Player.None -> {
                        imgIcon.setBackgroundColor(Constant.whiteColor)

                    }
                }
                cardView.setOnClickListener {

                    if (item.player != Constant.Player.None) {
                        return@setOnClickListener
                    }

                    if (item.id in 1..Constant.columnCount) {
                        getCurrentItemList()[adapterPosition].player = currentPlayer
                        val isWin = checkWin(currentPlayer)
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
                            val isWin = checkWin(currentPlayer)
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
        }

        currentPlayer = Constant.Player.None
        toggleCurrentPlayer()
        notifyDataSetChanged()
    }

    fun checkWin(player: Constant.Player): Boolean {

        val myPlayer = getCurrentItemList().filter { it.player == player }

        val ids = myPlayer.map { it.id }

        val horizontal = horizontal(ids)
        val vertical = vertical(ids)
        if (horizontal) {
            Log.e("My player win :", "$player : WIN")
            return true
        }
        if (vertical) {
            Log.e("My player win :", "$player : WIN")
            return true
        }

//        Log.e("My Player Name", player.toString())
//        Log.e(" my ID", ids.toString())
        return false
    }


    fun horizontal(ids: List<Int>): Boolean {

        for (row in 1..Constant.rowCount) {
            var id = row * Constant.columnCount
            //      3       *   7 = 21

            for (col in 1..id - 3) {
//                Log.e("first id", col.toString())
//                Log.e("second id", (col + 1).toString())
//                Log.e("third id", (col + 2).toString())
//                Log.e("fourth id", (col + 3).toString())
                val firstCoil = ids.find {
                    it == col
                }
                val secondCoil = ids.find {
                    it == col + 1
                }
                val thirdCoil = ids.find {

                    it == col + 2
                }
                val fourthCoil = ids.find {

                    it == col + 3
                }
                val isWinPlayer =
                    firstCoil != null && secondCoil != null && thirdCoil != null && fourthCoil != null
                if (isWinPlayer) {
                    return true
                }
                id += 1
            }
        }
        return false
    }

    fun vertical(ids: List<Int>) : Boolean {

        for (column in 1..Constant.columnCount) {
            Log.e("data",column.toString())
            var totalRowCheckID = column * Constant.rowCount
            //                 9 =    1   * 9
            for (rowItem in 1..totalRowCheckID - 3) {
                val firstCoil = ids.find {
                    it == rowItem
                }

                val secondCoil = ids.find {
                    it == rowItem + Constant.columnCount
                }
                val thirdCoil = ids.find {

                    it == rowItem + (2 * Constant.columnCount)
                }
                val fourthCoil = ids.find {

                    it == rowItem + (3 * Constant.columnCount)
                }
//                Log.e("first",firstCoil.toString())
//                Log.e("second",firstCoil.toString())
//                Log.e("third",firstCoil.toString())
//                Log.e("fouth",firstCoil.toString())
                val isWinPlayer =
                    firstCoil != null && secondCoil != null && thirdCoil != null && fourthCoil != null
                if (isWinPlayer) {
                    return true
                }
                totalRowCheckID += 1
            }
        }
        return false
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
