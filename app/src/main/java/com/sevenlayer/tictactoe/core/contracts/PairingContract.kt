package com.sevenlayer.tictactoe.core.contracts

import android.app.Activity
import android.content.Context

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
interface PairingContract {
    interface View {
        fun setPresenter(presenter: Presenter)
        fun provideContext(): Context
        fun provideActivity(): Activity
    }

    interface Presenter {
        fun onDestroy()
        fun moveAlong()
        fun openBTSettings()
    }
}