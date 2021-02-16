package com.sevenlayer.tictactoe.core.contracts

import android.content.Context
import android.content.Intent

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
interface LaunchContract {
  interface View {
    fun setPresenter(presenter: Presenter)
    fun onBTNotSupported()
    fun onEnableBT(intent: Intent)
    fun provideContext(): Context
    fun moveAlong()
  }

  interface Presenter {
    fun onDestroy()
    fun enableBT()
  }
}