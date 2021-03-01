package com.sevenlayer.tictactoe.core.contracts

import android.app.Activity
import android.content.Context

/**
 * The MVP contract of the server/client pickup screen.
 *
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
interface ServerClientPickupContract {
    /**
     * The view interface of the contract.
     */
    interface View {
        /**
         * Sets the presenter of the view.
         */
        fun setPresenter(presenter: Presenter)

        /**
         * Displays the progress dialog modal.
         */
        fun startLoading()

        /**
         * Dismisses the progress dialog modal.
         */
        fun stopLoading()

        /**
         * Displays a toast with a message to the user about the connection failure.
         */
        fun onConnectionFailed()

        fun provideContext(): Context

        fun provideActivity(): Activity
    }

    /**
     * The presenter interface of the contract.
     */
    interface Presenter {
        /**
         * Destroys the presenter.
         */
        fun onDestroy()

        /**
         * Starts the BT server and continues to the next screen.
         */
        fun continueAsServer()

        /**
         * Sets the type of the player as client and moves to the next screen.
         */
        fun continueAsClient()
    }
}