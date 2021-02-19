package com.sevenlayer.tictactoe.core.contracts

/**
 * The MVP contract of the lobby screen.
 *
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
interface LobbyContract {
    /**
     * The view interface of the lobby contract.
     */
    interface View {
        /**
         * Sets the presenter of the view.
         */
        fun setPresenter(presenter: Presenter)

        /**
         * Starts a loading progress dialog.
         */
        fun startLoading()

        /**
         * Stops the loading progress dialog.
         */
        fun stopLoading()

        /**
         * Handles the successful connection.
         */
        fun onConnected()

        /**
         * Displays an error message in case of a connection failure.
         */
        fun onFailure(message: String?)
    }

    /**
     * The presenter interface of the lobby contract.
     */
    interface Presenter {
        /**
         * Destroys and clears the presenter.
         */
        fun onDestroy()

        /**
         * Starts the connection process.
         */
        fun startConnection()
    }
}