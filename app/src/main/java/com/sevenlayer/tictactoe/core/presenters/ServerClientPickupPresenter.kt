package com.sevenlayer.tictactoe.core.presenters

import com.sevenlayer.tictactoe.core.connection.BluetoothConnectionHandler
import com.sevenlayer.tictactoe.core.contracts.ServerClientPickupContract
import com.sevenlayer.tictactoe.core.game.Game
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
class ServerClientPickupPresenter(private val view: ServerClientPickupContract.View): ServerClientPickupContract.Presenter {
  private val compositeDisposable: CompositeDisposable = CompositeDisposable()

  override fun onDestroy() {
    compositeDisposable.clear()
  }

  override fun continueAsServer() {
    view.startLoading()
    Game.getInstance().setPlayerType(true)

    runBlocking {
      launch {
        compositeDisposable.add(
            BluetoothConnectionHandler.getInstance()
                .connectionStatusObservable
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ status ->
                  if (status == BluetoothConnectionHandler.BluetoothConnectionStatus.CONNECTED) {
                    view.stopLoading()
                    view.continueToLobby()
                    return@subscribe
                  }

                  if (status == BluetoothConnectionHandler.BluetoothConnectionStatus.FAILED) {
                    view.stopLoading()
                    view.onConnectionFailed()
                  }
                }, {t ->
                  Timber.e(t)
                  view.stopLoading()
                  view.onConnectionFailed()
                })
        )

        BluetoothConnectionHandler.getInstance().initServer()
      }
    }
  }

  override fun continueAsClient() {
    view.startLoading()
    Game.getInstance().setPlayerType(false)
    view.stopLoading()

    view.continueToDeviceList()
  }
}