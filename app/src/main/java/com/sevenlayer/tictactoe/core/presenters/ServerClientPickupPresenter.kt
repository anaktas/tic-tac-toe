package com.sevenlayer.tictactoe.core.presenters

import android.content.Intent
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.activities.DeviceListActivity
import com.sevenlayer.tictactoe.activities.LobbyActivity
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
class ServerClientPickupPresenter(private var view: ServerClientPickupContract.View?): ServerClientPickupContract.Presenter {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
        view = null
    }

    override fun continueAsServer() {
        Timber.d("continueAsServer()")
        view?.startLoading()
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
                            view?.stopLoading()

                            if (status == BTConnection.BTConnectionStatus.CONNECTED) {
                                view?.let {
                                    it.provideContext().startActivity(Intent(it.provideContext(), LobbyActivity::class.java))
                                    it.provideActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                    it.provideActivity().finish()
                                }
                            }

                            if (status == BTConnection.BTConnectionStatus.FAILED) {
                                view?.onConnectionFailed()
                            }
                        }, { t ->
                            Timber.e(t)
                            view?.stopLoading()
                            view?.onConnectionFailed()
                        })
                )
            }
        }
    }

    override fun continueAsClient() {
        view?.let {
            it.startLoading()
            GameInstance.setPlayerType(false)
            it.stopLoading()

            it.provideContext().startActivity(Intent(it.provideContext(), DeviceListActivity::class.java))
            it.provideActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            it.provideActivity().finish()
        }
    }
}