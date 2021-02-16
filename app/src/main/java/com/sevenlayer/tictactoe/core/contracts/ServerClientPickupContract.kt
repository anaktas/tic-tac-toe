package com.sevenlayer.tictactoe.core.contracts

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
interface ServerClientPickupContract {
  interface View {
    fun setPresenter(presenter: Presenter)
    fun startLoading()
    fun stopLoading()
    fun continueToLobby()
    fun onConnectionFailed()
    fun continueToDeviceList()
  }

  interface Presenter {
    fun onDestroy()
    fun continueAsServer()
    fun continueAsClient()
  }
}