package com.sevenlayer.tictactoe.core.game

import android.text.TextUtils
import com.sevenlayer.tictactoe.core.connection.BTConnection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

/**
 * The game instance object.
 *
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
object GameInstance {
  private const val SERVER_PLAYER = 0
  private const val CLIENT_PLAYER = 1
  private const val SERVER_MARKER = 1
  private const val CLIENT_MARKER = 2

  private val compositeDisposable: CompositeDisposable = CompositeDisposable()

  private var player: Int = 0
  private var serverScore: Int = 0
  private var clientScore: Int = 0
  private var turn: Int = 0

  private val board = Array(3) { IntArray(3) }

  private val boardObservable = PublishSubject.create<Array<IntArray>>()
  private val winnerObservable = PublishSubject.create<Int>()

  init {
    for (i in 0..2) {
      for (j in 0..2) {
        board[i][j] = 0
      }
    }

    // The first player is always the server player
    turn = SERVER_PLAYER
  }

  fun getBoardObservable(): Observable<Array<IntArray>> = boardObservable
  fun getWinnerObservable(): Observable<Int> = winnerObservable

  fun getServerScore(): Int = serverScore
  fun getClientScore(): Int = clientScore

  fun getScore(): String {
    return if (isServer()) {
      "$serverScore - $clientScore"
    } else {
      "$clientScore - $serverScore"
    }
  }

  fun startListening() {
    addDisposable(BTConnection
        .getMessageObservable()
        .observeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe({ message: String ->
          if (!TextUtils.isEmpty(message)) {
            val entries = message.split(",".toRegex()).toTypedArray()
            if (entries != null && entries.isNotEmpty()) {
              val row = entries[0].toInt()
              val col = entries[1].toInt()
              val playerType = entries[1].toInt()

              // If the server player played, then it's the
              // clients turn
              turn = if (playerType == SERVER_PLAYER) {
                CLIENT_PLAYER
              } else { // otherwise it's the servers turn.
                SERVER_PLAYER
              }
              updateBoard(row, col, playerType)
            } else {
              // The server had send a reset message
              clearBoard()
            }
          }
        }) { t: Throwable? -> Timber.e(t) })
  }

  fun updateBoard(row: Int, col: Int, player: Int) {
    if (player == SERVER_PLAYER) {
      board[row][col] = SERVER_MARKER
    } else {
      board[row][col] = CLIENT_MARKER
    }

    boardObservable.onNext(board)
    evaluateGame()
  }

  fun evaluateGame(): Boolean {
    // Row evaluation
    if (board[0][0] == board[0][1] && board[0][1] == board[0][2]) {
      // I.e.
      // [1, 1, 1]
      // [0, 0, 0]
      // [0, 0, 0]
      if (board[0][0] == SERVER_MARKER || board[0][0] == CLIENT_MARKER) {
        if (isServer()) {
          serverScore++
        } else {
          clientScore++
        }
        winnerObservable.onNext(player)
        return true
      }
    }

    if (board[1][0] == board[1][1] && board[1][1] == board[1][2]) {
      // I.e.
      // [0, 0, 0]
      // [1, 1, 1]
      // [0, 0, 0]
      if (board[1][0] == SERVER_MARKER || board[1][0] == CLIENT_MARKER) {
        if (isServer()) {
          serverScore++
        } else {
          clientScore++
        }
        winnerObservable.onNext(player)
        return true
      }
    }

    if (board[2][0] == board[2][1] && board[2][1] == board[2][2]) {
      // I.e.
      // [0, 0, 0]
      // [0, 0, 0]
      // [1, 1, 1]
      if (board[2][0] == SERVER_MARKER || board[2][0] == CLIENT_MARKER) {
        if (isServer()) {
          serverScore++
        } else {
          clientScore++
        }
        winnerObservable.onNext(player)
        return true
      }
    }

    // Column evaluation
    if (board[0][0] == board[1][0] && board[1][0] == board[2][0]) {
      // I.e.
      // [1, 0, 0]
      // [1, 0, 0]
      // [1, 0, 0]
      if (board[0][0] == SERVER_MARKER || board[0][0] == CLIENT_MARKER) {
        if (isServer()) {
          serverScore++
        } else {
          clientScore++
        }
        winnerObservable.onNext(player)
        return true
      }
    }

    if (board[0][1] == board[1][1] && board[1][1] == board[2][1]) {
      // I.e.
      // [0, 1, 0]
      // [0, 1, 0]
      // [0, 1, 0]
      if (board[0][1] == SERVER_MARKER || board[0][1] == CLIENT_MARKER) {
        if (isServer()) {
          serverScore++
        } else {
          clientScore++
        }
        winnerObservable.onNext(player)
        return true
      }
    }

    if (board[0][2] == board[1][2] && board[1][2] == board[2][2]) {
      // I.e.
      // [0, 0, 1]
      // [0, 0, 1]
      // [0, 0, 1]
      if (board[0][2] == SERVER_MARKER || board[0][2] == CLIENT_MARKER) {
        if (isServer()) {
          serverScore++
        } else {
          clientScore++
        }
        winnerObservable.onNext(player)
        return true
      }
    }

    // Diagonal evaluation
    if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
      // I.e.
      // [1, 0, 0]
      // [0, 1, 0]
      // [0, 0, 1]
      if (board[0][0] == SERVER_MARKER || board[0][0] == CLIENT_MARKER) {
        if (isServer()) {
          serverScore++
        } else {
          clientScore++
        }
        winnerObservable.onNext(player)
        return true
      }
    }

    // Diagonal evaluation
    if (board[0][2] == board[1][1] && board[1][1] == board[0][2]) {
      // I.e.
      // [0, 0, 1]
      // [0, 1, 0]
      // [1, 0, 0]
      if (board[0][2] == SERVER_MARKER || board[0][2] == CLIENT_MARKER) {
        if (isServer()) {
          serverScore++
        } else {
          clientScore++
        }
        winnerObservable.onNext(player)
        return true
      }
    }

    return false
  }

  fun makeMovement(row: Int, col: Int) {
    if (isServer() && turn != SERVER_PLAYER) return
    if (!isServer() && turn != CLIENT_PLAYER) return

    turn = if (isServer()) {
      CLIENT_PLAYER
    } else {
      SERVER_PLAYER
    }

    if (!evaluateGame()) {
      val message = "$row,$col,$player"
      BTConnection.send(message)
    }
  }

  fun isServer(): Boolean = (player == SERVER_PLAYER)

  fun clearBoard() {
    for (i in 0..2) {
      for (j in 0..2) {
        board[i][j] = 0
      }
    }
  }

  fun setPlayerType(isServer: Boolean) {
    player = if (isServer) {
      SERVER_PLAYER
    } else {
      CLIENT_PLAYER
    }
  }

  fun die() {
    compositeDisposable.clear()
    clearBoard()
    player = 0
    turn = 0
    serverScore = 0
    clientScore = 0
  }

  private fun addDisposable(disposable: Disposable) {
    compositeDisposable.add(disposable)
  }
}