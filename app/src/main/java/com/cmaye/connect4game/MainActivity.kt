package com.cmaye.connect4game

import android.animation.ObjectAnimator
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.cmaye.connect4game.databinding.ActivityMainBinding
import com.cmaye.connect4game.model.Coin

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: TestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setUpRecycler()
        createBoard()
        binding.imgIconRed.setOnClickListener { imageView ->
//            animateImageView(binding.imgIconRed)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.reset, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reset -> {
                mAdapter.resetGame()
                true
            }
            else -> false
        }
    }


    private fun setUpRecycler() {
        mAdapter = TestAdapter()
        mAdapter.setOnClickListener(object : TestAdapter.OnClickListener {

            override fun isCheck(currentPlayer: String, list: MutableList<Coin>) {
                binding.currentPlayer.text = "Current Player : $currentPlayer"
                if (list.filter { it.player != Constant.Player.None }.size == (Constant.columnCount * Constant.rowCount)) {
                    gameOver()
                }

//                animateImageView(imageView)
            }

            override fun showToast(msg: String) {
                Toast.makeText(this@MainActivity, msg.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun showWinPlayer(msg: String) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("The win player")
                builder.setMessage(msg)
                builder.setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                    mAdapter.resetGame()
                }

                builder.setCancelable(false)
                val dialog = builder.create()
                dialog.show()
            }

        })
        binding.recyclerView.layoutManager = GridLayoutManager(this, Constant.columnCount)
        binding.recyclerView.adapter = mAdapter
    }


    private fun createBoard() {
        val list = mutableListOf<Coin>()
        var id = 1
        for (row in 1..Constant.rowCount) {
            for (column in 1..Constant.columnCount) {
//                val id = totalCount
                list.add(Coin(id, Constant.Player.None))
                id += 1
            }
        }

//
//        for (id in 1..totalCount) {
//            list.add(Coin(id, Constant.Player.None))
//        }
//        mAdapter.differ.submitList(list.reversed())

        mAdapter.differ.submitList(list)

    }


    private fun gameOver() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over!")
        builder.setMessage("Your play game is Game Over!!")
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
            mAdapter.resetGame()
        }

        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }


    private fun gameWinner(player: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Connect 4 Game")
        builder.setMessage("$player is Winner!")
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }

        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }

    private fun animateImageView(imageView: ImageView) {
        // get the height of the screen
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        // create an ObjectAnimator to animate the translationY property
        val animator = ObjectAnimator.ofFloat(
            imageView, "translationY", -imageView.height.toFloat(), screenHeight.toFloat()
        )

        // set the duration of the animation
        animator.duration = 1000

        // start the animation
        animator.start()
    }
}