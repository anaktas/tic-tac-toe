package com.sevenlayer.tictactoe.core.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * The launch screen MVP contract.
 *
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
interface LaunchContract {
    /**
     * The view interface of the contract.
     */
    interface View {
        /**
         * Set's the view's presenter.
         */
        fun setPresenter(presenter: Presenter)

        /**
         * Displays a message to the user in case that
         * the device does not support BT.
         */
        fun onBTNotSupported()

        /**
         * Provides a context reference to the presenter.
         */
        fun provideContext(): Context

        /**
         * Provides an activity reference to the presenter.
         */
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
         * Enables the BT.
         */
        fun enableBT()
    }
}