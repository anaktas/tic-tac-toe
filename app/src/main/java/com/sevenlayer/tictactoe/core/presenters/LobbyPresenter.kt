package com.sevenlayer.tictactoe.core.presenters

import com.sevenlayer.tictactoe.core.connection.BTConnection
import com.sevenlayer.tictactoe.core.contracts.LobbyContract
import com.sevenlayer.tictactoe.core.game.GameInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
class LobbyPresenter(private val view: LobbyContract.View) : LobbyContract.Presenter {
  private val compositeDisposable: CompositeDisposable = CompositeDisposable()

  override fun onDestroy() {
      compositeDisposable.clear()
  }

  override fun startConnection() {
      view.startLoading()

      GameInstance.startListening()

      if (GameInstance.isServer()) {
          startListeningForServerConnectionStatus()
          BTConnection.startServer()
      } else {
          startListeningForClientConnectionStatus()
          BTConnection.startClient()
      }
  }

  private fun startListeningForClientConnectionStatus() {
      runBlocking {
          launch {
              compositeDisposable.add(
                  BTConnection
                      .getConnectionStatusObservable()
                      .observeOn(Schedulers.io())
                      .subscribeOn(AndroidSchedulers.mainThread())
                      .subscribe({ state ->
                          view.stopLoading()

                          if (state == BTConnection.BTConnectionStatus.CONNECTED) {
                              view.onConnected()
                          } else {
                              view.onFailure("Failed to connect")
                          }
                      }, { t ->
                          Timber.e(t)
                          view.stopLoading()
                          view.onFailure(t.message)
                      })
              )
          }
      }
  }

  private fun startListeningForServerConnectionStatus() {
      runBlocking {
          launch {
              compositeDisposable.add(
                  BTConnection
                      .getClientConnectionObservable()
                      .observeOn(Schedulers.io())
                      .subscribeOn(AndroidSchedulers.mainThread())
                      .subscribe({ clientConnected ->
                          view.stopLoading()

                          if (clientConnected) {
                              view.onConnected()
                          } else {
                              view.onFailure("A client was not connected at reasonable time.")
                          }
                      }, { t ->
                          Timber.e(t)
                          view.stopLoading()
                          view.onFailure(t.message)
                      })
              )
          }
      }
  }
}