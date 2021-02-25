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
    /**
     * The server player constant type.
     */
    private const val SERVER_PLAYER = 0

    /**
     * The client player constant type.
     */
    private const val CLIENT_PLAYER = 1

    /**
     * The server player board marker.
     */
    private const val SERVER_MARKER = 1

    /**
     * The client player board marker.
     */
    private const val CLIENT_MARKER = 2

    /**
     * The disposable container.
     */
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    /**
     * The player type variable.
     */
    private var player: Int = 0

    /**
     * The server score.
     */
    private var serverScore: Int = 0

    /**
     * The client score.
     */
    private var clientScore: Int = 0

    /**
     * The game turn placeholder/
     */
    private var turn: Int = 0

    /**
     * The board array.
     */
    private val board = Array(3) { IntArray(3) }

    private var gameEnded: Boolean = false

    /**
     * The board change/update observable.
     */
    private val boardObservable = PublishSubject.create<Array<IntArray>>()

    /**
     * The winner observable.
     */
    private val winnerObservable = PublishSubject.create<Int>()

    init {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j] = 0
            }
        }

        // The first player is always the server player
        turn = SERVER_PLAYER
        gameEnded = false
    }

    /**
     * Returns the board status observable.
     */
    fun getBoardObservable(): Observable<Array<IntArray>> = boardObservable

    /**
     * Returns the winner status observable.
     */
    fun getWinnerObservable(): Observable<Int> = winnerObservable

    /**
     * Returns the server player score.
     */
    fun getServerScore(): Int = serverScore

    /**
     * Returns the client player score.
     */
    fun getClientScore(): Int = clientScore

    /**
     * Returns the score board string according to who is the current player.
     */
    fun getScore(): String {
        return if (isServer()) {
            "$serverScore - $clientScore"
        } else {
            "$clientScore - $serverScore"
        }
    }

    /**
     * Starts listening the BT input stream.
     */
    fun startListening() {
        addDisposable(BTConnection
            .getMessageObservable()
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({ message: String ->
                if (!TextUtils.isEmpty(message)) {
                    Timber.d("Message: $message")
                    if (message == "reset") {
                        // The server had send a reset message
                        clearBoard()
                        gameEnded = false
                        turn = SERVER_PLAYER
                        boardObservable.onNext(board)
                    } else {
                        val entries = message.split(",".toRegex()).toTypedArray()
                        if (entries != null && entries.isNotEmpty()) {
                            val row = entries[0].toInt()
                            val col = entries[1].toInt()
                            val playerType = entries[1].toInt()

                            // If the server player played, then it's the
                            // clients turn
                            turn = if (turn == SERVER_PLAYER) {
                                CLIENT_PLAYER
                            } else { // otherwise it's the servers turn.
                                SERVER_PLAYER
                            }

                            updateBoard(row, col, playerType)
                        }
                    }
                }
            }) { t: Throwable? -> Timber.e(t) })
    }

    /**
     * Updates the game board and notifies the observers.
     */
    private fun updateBoard(row: Int, col: Int, player: Int) {
        if (player == SERVER_PLAYER) {
            board[row][col] = SERVER_MARKER
        } else {
            board[row][col] = CLIENT_MARKER
        }

        printBoard()
        boardObservable.onNext(board)
        evaluateGame()
    }

    /**
     * Evaluates the game status and notifies the observers.
     */
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
                gameEnded = true
                winnerObservable.onNext(player)
                clearBoard()
                boardObservable.onNext(board)
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
                gameEnded = true
                winnerObservable.onNext(player)
                clearBoard()
                boardObservable.onNext(board)
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
                gameEnded = true
                winnerObservable.onNext(player)
                clearBoard()
                boardObservable.onNext(board)
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
                gameEnded = true
                winnerObservable.onNext(player)
                clearBoard()
                boardObservable.onNext(board)
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
                gameEnded = true
                winnerObservable.onNext(player)
                clearBoard()
                boardObservable.onNext(board)
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
                gameEnded = true
                winnerObservable.onNext(player)
                clearBoard()
                boardObservable.onNext(board)
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
                gameEnded = true
                winnerObservable.onNext(player)
                clearBoard()
                boardObservable.onNext(board)
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
                gameEnded = true
                winnerObservable.onNext(player)
                clearBoard()
                boardObservable.onNext(board)
                return true
            }
        }

        return false
    }

    /**
     * Makes a movement and sends the move to the other side.
     */
    fun makeMovement(row: Int, col: Int) {
        Timber.d("Movement: $row, $col, $player")
        if (isServer() && turn != SERVER_PLAYER) return
        if (!isServer() && turn != CLIENT_PLAYER) return
        if (gameEnded) return

        // Just a precaution measure in order to avoid
        // an attempt to change a tile which is already
        // filled.
        if (board[row][col] != 0) return

        turn = if (isServer()) {
            CLIENT_PLAYER
        } else {
            SERVER_PLAYER
        }

        board[row][col] = if (isServer()) {
            SERVER_MARKER
        } else {
            CLIENT_MARKER
        }

        val message = "$row,$col,$player"
        BTConnection.send(message)

        evaluateGame()
    }

    /**
     * Returns true if the current player is a server player.
     */
    fun isServer(): Boolean = (player == SERVER_PLAYER)

    fun canMove(row: Int, col: Int): Boolean {
        val boardValue = board[row][col]
        if (isServer() && turn != SERVER_PLAYER) return false
        if (!isServer() && turn != CLIENT_PLAYER) return false

        if (boardValue != 0) return false

        return true
    }

    private fun clearBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j] = 0
            }
        }
    }

    fun resetBoard() {
        gameEnded = false
        turn = SERVER_PLAYER;
        clearBoard()
        boardObservable.onNext(board)
        BTConnection.send("reset")
    }

    /**
     * Sets the current player type. If true, the player type is a server player.
     */
    fun setPlayerType(isServer: Boolean) {
        player = if (isServer) {
            SERVER_PLAYER
        } else {
            CLIENT_PLAYER
        }
    }

    /**
     * Clears the instance.
     */
    fun die() {
        compositeDisposable.clear()
        clearBoard()
        player = 0
        turn = 0
        serverScore = 0
        clientScore = 0
    }

    /**
     * Adds a disposable to the composite disposable.
     */
    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun printBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                val boarValue = board[i][j]
                Timber.d("board[$i][$j] = $boarValue")
            }
        }
    }
}