package com.cmaye.connect4game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.cmaye.connect4game.databinding.ActivityMainBinding
import com.cmaye.connect4game.databinding.TestBinding


class TestActivity : AppCompatActivity() {
    private lateinit var binding : TestBinding
    private lateinit var game : Game
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        for (col in 0 until 7) {
            binding.cell00.tag = col
            binding.cell00.setOnClickListener(::onCellClick)
        }

        game = Game()

    }


    private fun updateUI(game: Game) {
        val board = game.getBoard()

        for (row in 0 until 6) {
            for (col in 0 until 7) {
                when (board[row][col]) {
                    Game.PLAYER_ONE -> binding.cell00.setImageResource(R.drawable.circle_svgrepo_com_green)
                    Game.PLAYER_TWO -> binding.cell00.setImageResource(R.drawable.circle_svgrepo_com_red)
                    else -> binding.cell00.setImageResource(R.drawable.ic_launcher_background)
                }
            }
        }
    }

    private fun onCellClick(view: View) {
        if (view !is ImageView) return

        val col = view.tag as Int
        if (!game.dropDisc(col)) return

        val winner = game.checkWinner()
        if (winner != Game.NO_WINNER) {
            // show a message and reset the game
            val message = if (winner == Game.PLAYER_ONE) "Player One wins!" else "Player Two wins!"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            game = Game()
        }

        updateUI(game)
    }



}