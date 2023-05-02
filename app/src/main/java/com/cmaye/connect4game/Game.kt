package com.cmaye.connect4game

class Game {
    private var currentPlayer = PLAYER_ONE
    private val board = Array(6) { IntArray(7) }

    fun getCurrentPlayer() = currentPlayer

    fun getBoard() = board

    fun dropDisc(column: Int): Boolean {
        for (row in 5 downTo 0) {
            if (board[row][column] == EMPTY_CELL) {
                board[row][column] = currentPlayer
                currentPlayer = if (currentPlayer == PLAYER_ONE) PLAYER_TWO else PLAYER_ONE
                return true
            }
        }
        return false
    }

    fun checkWinner(): Int {
        // check rows
        for (row in 0 until 6) {
            for (col in 0..3) {
                if (board[row][col] != EMPTY_CELL &&
                    board[row][col] == board[row][col + 1] &&
                    board[row][col] == board[row][col + 2] &&
                    board[row][col] == board[row][col + 3]) {
                    return board[row][col]
                }
            }
        }

        // check columns
        for (col in 0 until 7) {
            for (row in 0..2) {
                if (board[row][col] != EMPTY_CELL &&
                    board[row][col] == board[row + 1][col] &&
                    board[row][col] == board[row + 2][col] &&
                    board[row][col] == board[row + 3][col]) {
                    return board[row][col]
                }
            }
        }

        // check diagonals (top-left to bottom-right)
        for (row in 0..2) {
            for (col in 0..3) {
                if (board[row][col] != EMPTY_CELL &&
                    board[row][col] == board[row + 1][col + 1] &&
                    board[row][col] == board[row + 2][col + 2] &&
                    board[row][col] == board[row + 3][col + 3]) {
                    return board[row][col]
                }
            }
        }

        // check diagonals (top-right to bottom-left)
        for (row in 0..2) {
            for (col in 3 until 7) {
                if (board[row][col] != EMPTY_CELL &&
                    board[row][col] == board[row + 1][col - 1] &&
                    board[row][col] == board[row + 2][col - 2] &&
                    board[row][col] == board[row + 3][col - 3]) {
                    return board[row][col]
                }
            }
        }

        // no winner yet
        return NO_WINNER
    }

    companion object {
        const val EMPTY_CELL = 0
        const val PLAYER_ONE = 1
        const val PLAYER_TWO = 2
        const val NO_WINNER = -1
    }
}