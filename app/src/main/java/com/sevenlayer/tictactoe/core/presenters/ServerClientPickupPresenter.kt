package com.sevenlayer.tictactoe.core.presenters

import com.sevenlayer.tictactoe.core.connection.BTConnection
import com.sevenlayer.tictactoe.core.contracts.ServerClientPickupContract
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
class ServerClientPickupPresenter(private val view: ServerClientPickupContract.View): ServerClientPickupContract.Presenter {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    override fun continueAsServer() {
        Timber.d("continueAsServer()")
        view.startLoading()
        GameInstance.setPlayerType(true)

        runBlocking {
            launch {
                Timber.d("Launching the blocking coroutine")

                compositeDisposable.add(
                    BTConnection
                        .getConnectionStatusObservable()
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            Timber.d("Initializing server")
                            BTConnection.initServer()
                        }
                        .subscribe({ status ->
                            Timber.d("Received status: $status")
                            view.stopLoading()

                            if (status == BTConnection.BTConnectionStatus.CONNECTED) {
                                view.continueToLobby()
                            }

                            if (status == BTConnection.BTConnectionStatus.FAILED) {
                                view.onConnectionFailed()
                            }
                        }, { t ->
                            Timber.e(t)
                            view.stopLoading()
                            view.onConnectionFailed()
                        })
                )

                //BTConnection.initServer()
            }
        }
    }

    override fun continueAsClient() {
        view.startLoading()
        GameInstance.setPlayerType(false)
        view.stopLoading()

        view.continueToDeviceList()
    }
}