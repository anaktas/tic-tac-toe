package com.sevenlayer.tictactoe.core.contracts

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
interface BoardContract {
    interface View {
        fun setPresenter(presenter: Presenter)
        fun updateBoard(board: Array<IntArray>)
        fun updateScore(scr: String)
    }

    interface Presenter {
        fun onDestroy()
        fun startPlaying()
        fun move(row: Int, col: Int)
    }
}