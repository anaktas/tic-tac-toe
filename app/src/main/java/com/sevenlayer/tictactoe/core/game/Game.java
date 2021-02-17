package com.sevenlayer.tictactoe.core.game;

import android.text.TextUtils;

import com.sevenlayer.tictactoe.core.connection.BluetoothConnectionHandler;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

/**
 * The game instance.
 *
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
public class Game {
    /**
     * The server player indicator.
     */
    private final static int SERVER_PLAYER = 0;

    /**
     * The client player indicator.
     */
    private final static int CLIENT_PLAYER = 1;

    /**
     * The server player board marker.
     */
    private final static int SERVER_MARKER = 1;

    /**
     * The client player board marker.
     */
    private final static int CLIENT_MARKER = 2;

    /**
     * The {@link Game} instance.
     */
    private static Game sInstance;

    /**
     * The disposable list holder.
     */
    private CompositeDisposable compositeDisposable;

    /**
     * The player type.
     */
    private int player;

    /**
     * The server player score.
     */
    private int serverScore;

    /**
     * The client player score.
     */
    private int clientScore;

    /**
     * The abstract game board.
     */
    private final int[][] board = new int[3][3];

    private int turn;

    /**
     * The board update observable.
     */
    private final PublishSubject<int[][]> boardObservable = PublishSubject.create();

    /**
     * The winner observable.
     */
    private final PublishSubject<Integer> winnerObservable = PublishSubject.create();

    private Game() {
        // Initializing the board with zeros.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }

        compositeDisposable = new CompositeDisposable();
    }

    public synchronized static Game getInstance() {
        if (sInstance == null) sInstance = new Game();

        return sInstance;
    }

    /**
     * Returns the game board observable.
     *
     * @return {@link Observable}
     */
    public Observable<int[][]> getBoardUpdates() {
        return boardObservable;
    }

    /**
     * Returns the winner observable.
     *
     * @return {@link Observable<Integer>}
     */
    public Observable<Integer> getWinnerObservable() {
        return winnerObservable;
    }

    /**
     * Returns the server player score.
     *
     * @return the server player score
     */
    public int getServerScore() {
        return serverScore;
    }

    /**
     * REturns the client player score.
     *
     * @return the client player score
     */
    public int getClientScore() {
        return clientScore;
    }

    /**
     * Returns a refined game score with the score depicted as:
     * Current User - Opponent.
     *
     * @return the game score
     */
    public String getScore() {
        if (isServer()) {
            return serverScore + " - " + clientScore;
        } else {
            return clientScore + " - " + serverScore;
        }
    }

    /**
     * Starts listening for messages from the BT connection.
     */
    public void startListening() {
        addDisposable(BluetoothConnectionHandler.getInstance()
                .getMessageObservable()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    if (!TextUtils.isEmpty(message)) {
                        String[] entries = message.split(",");
                        if (entries != null && entries.length > 0) {
                            int row = Integer.parseInt(entries[0]);
                            int col = Integer.parseInt(entries[1]);
                            int playerType = Integer.parseInt(entries[1]);

                            // If the server player played, then it's the
                            // clients turn
                            if (playerType == SERVER_PLAYER) {
                                turn = CLIENT_PLAYER;
                            } else { // otherwise it's the servers turn.
                                turn = SERVER_PLAYER;
                            }

                            updateBoard(row, col, playerType);
                        } else {
                            // The server had send a reset message
                            clearBoard();
                        }
                    }
                }, Timber::e));
    }

    /**
     * Updates the local game board.
     *
     * @param row the tapped row
     * @param col the tapped column
     * @param player the player type
     *               (either {@link Game#SERVER_PLAYER} or {@link Game#CLIENT_PLAYER})
     */
    public void updateBoard(int row, int col, int player) {
        if (player == Game.SERVER_PLAYER) {
            board[row][col] = SERVER_MARKER;
        } else {
            board[row][col] = CLIENT_MARKER;
        }

        boardObservable.onNext(board);

        evaluateGame();
    }

    /**
     * Evaluates the current board and returns a flag which
     * indicated if the game has finished or not. It sends
     * also a message to the winner observable.
     *
     * @return true if game has finished
     */
    public boolean evaluateGame() {
        // Row evaluation
        if (board[0][0] == board[0][1] && board[0][1] == board[0][2]) {
            // I.e.
            // [1, 1, 1]
            // [0, 0, 0]
            // [0, 0, 0]
            if (board[0][0] == SERVER_MARKER || board[0][0] == CLIENT_MARKER) {
                if (isServer()) {
                    serverScore++;
                } else {
                    clientScore++;
                }
                winnerObservable.onNext(player);
                return true;
            }
        }

        if (board[1][0] == board[1][1] && board[1][1] == board[1][2]) {
            // I.e.
            // [0, 0, 0]
            // [1, 1, 1]
            // [0, 0, 0]
            if (board[1][0] == SERVER_MARKER || board[1][0] == CLIENT_MARKER) {
                if (isServer()) {
                    serverScore++;
                } else {
                    clientScore++;
                }
                winnerObservable.onNext(player);
                return true;
            }
        }

        if (board[2][0] == board[2][1] && board[2][1] == board[2][2]) {
            // I.e.
            // [0, 0, 0]
            // [0, 0, 0]
            // [1, 1, 1]
            if (board[2][0] == SERVER_MARKER || board[2][0] == CLIENT_MARKER) {
                if (isServer()) {
                    serverScore++;
                } else {
                    clientScore++;
                }
                winnerObservable.onNext(player);
                return true;
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
                    serverScore++;
                } else {
                    clientScore++;
                }
                winnerObservable.onNext(player);
                return true;
            }
        }

        if (board[0][1] == board[1][1] && board[1][1] == board[2][1]) {
            // I.e.
            // [0, 1, 0]
            // [0, 1, 0]
            // [0, 1, 0]
            if (board[0][1] == SERVER_MARKER || board[0][1] == CLIENT_MARKER) {
                if (isServer()) {
                    serverScore++;
                } else {
                    clientScore++;
                }
                winnerObservable.onNext(player);
                return true;
            }
        }

        if (board[0][2] == board[1][2] && board[1][2] == board[2][2]) {
            // I.e.
            // [0, 0, 1]
            // [0, 0, 1]
            // [0, 0, 1]
            if (board[0][2] == SERVER_MARKER || board[0][2] == CLIENT_MARKER) {
                if (isServer()) {
                    serverScore++;
                } else {
                    clientScore++;
                }
                winnerObservable.onNext(player);
                return true;
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
                    serverScore++;
                } else {
                    clientScore++;
                }
                winnerObservable.onNext(player);
                return true;
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
                    serverScore++;
                } else {
                    clientScore++;
                }
                winnerObservable.onNext(player);
                return true;
            }
        }

        return false;
    }

    /**
     * Set's the current player type.
     *
     * @param isServer whether the type is server player or not
     */
    public void setPlayerType(boolean isServer) {
        if (isServer) {
            player = SERVER_PLAYER;
        } else {
            player = CLIENT_PLAYER;
        }
    }

    /**
     * Checks if the current player is a server player.
     *
     * @return true if current player is player
     */
    public boolean isServer() {
        return player == SERVER_PLAYER;
    }

    /**
     * Broadcasts the player movement.
     *
     * @param row the tapped row
     * @param col the tapped column
     */
    public void makeMovement(int row, int col) {
        // If the user is a server player and it's not his turn, abort
        if (isServer() && turn != SERVER_PLAYER) return;
        // If the user is a client player and it's not his turn, abort
        if (!isServer() && turn != CLIENT_PLAYER) return;

        // If the player is a server player
        // the next move is the client's
        if (isServer()) {
            turn = CLIENT_PLAYER;
        } else { // otherwise the next move is the server's
            turn = SERVER_PLAYER;
        }

        if (!evaluateGame()) {
            String message = row + "," + col + "," + player;
            BluetoothConnectionHandler.getInstance().send(message);
        }
    }

    /**
     * Clears the board by zeroing the board array.
     */
    public void clearBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }
    }

    /**
     * Adds a disposable to the disposable list.
     *
     * @param disposable {@link Disposable}
     */
    private void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    /**
     * Clears the entire instance.
     */
    public void die() {
        compositeDisposable.clear();
        compositeDisposable = null;
        sInstance = null;
    }
}
