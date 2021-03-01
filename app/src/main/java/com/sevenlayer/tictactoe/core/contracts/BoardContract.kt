package com.sevenlayer.tictactoe.core.contracts

/**
 * The MVP contract of the board screen.
 *
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
interface BoardContract {
    /**
     * The view interface of the MVP contract.
     */
    interface View {
        /**
         * Sets the presenter of the view.
         */
        fun setPresenter(presenter: Presenter)

        /**
         * Updates the board with the received movements.
         */
        fun updateBoard(board: Array<IntArray>)

        /**
         * Updates the game score.
         */
        fun updateScore(scr: String)
    }

    /**
     * The presenter interface of the MVP contract.
     */
    interface Presenter {
        /**
         * Destroys the presenter and clears its composite disposable.
         */
        fun onDestroy()

        /**
         * Starts the game play.
         */
        fun startPlaying()

        /**
         * Makes a movement of the player.
         */
        fun move(row: Int, col: Int)
    }
}