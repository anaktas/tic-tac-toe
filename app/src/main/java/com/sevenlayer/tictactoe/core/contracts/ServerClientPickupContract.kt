package com.sevenlayer.tictactoe.core.contracts

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
     * Continues to the lobby screen.
     */
    fun continueToLobby()

    /**
     * Displays a toast with a message to the user about the connection failure.
     */
    fun onConnectionFailed()

    /**
     * Continues to the bonded devices list screen.
     */
    fun continueToDeviceList()
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