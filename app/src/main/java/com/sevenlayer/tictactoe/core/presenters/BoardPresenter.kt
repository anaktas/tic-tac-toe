package com.sevenlayer.tictactoe.core.presenters

import com.sevenlayer.tictactoe.core.contracts.BoardContract
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
class BoardPresenter(private val view: BoardContract.View) : BoardContract.Presenter {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.clear()
    }

    override fun startPlaying() {
        Timber.d("startPlaying()")
        runBlocking {
            launch {
                compositeDisposable.add(
                    GameInstance
                        .getBoardObservable()
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe({ board ->
                            Timber.d("Received board: $board")
                            view.updateBoard(board)
                            view.updateScore(GameInstance.getScore())
                        }, { t ->
                            Timber.e(t)
                        })
                )
            }
        }
    }

    override fun move(row: Int, col: Int) {
        Timber.d("move()")
        GameInstance.makeMovement(row, col)
        GameInstance.evaluateGame()
    }
}