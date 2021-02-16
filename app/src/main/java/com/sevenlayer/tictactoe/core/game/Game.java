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
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
public class Game {
    private final static int SERVER_PLAYER = 0;
    private final static int CLIENT_PLAYER = 1;

    private final static int SERVER_MARKER = 1;
    private final static int CLIENT_MARKER = 2;

    private static Game sInstance;
    private CompositeDisposable compositeDisposable;

    private int player;

    private int serverScore;
    private int clientScore;

    private final int[][] board = new int[3][3];

    private final PublishSubject<int[][]> boardObservable = PublishSubject.create();
    private final PublishSubject<Integer> winnerObservable = PublishSubject.create();

    private Game() {
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

    public Observable<int[][]> getBoardUpdates() {
        return boardObservable;
    }

    public Observable<Integer> getWinnerObservable() {
        return winnerObservable;
    }

    public int getServerScore() {
        return serverScore;
    }

    public int getClientScore() {
        return clientScore;
    }

    public String getScore() {
        if (isServer()) {
            return serverScore + " - " + clientScore;
        } else {
            return clientScore + " - " + serverScore;
        }
    }

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

                            updateBoard(row, col, playerType);
                        } else {
                            // The server had send a reset message
                            clearBoard();
                        }
                    }
                }, Timber::e));
    }

    public void updateBoard(int row, int col, int player) {
        if (isServer()) {
            board[row][col] = SERVER_MARKER;
        } else {
            board[row][col] = CLIENT_MARKER;
        }

        boardObservable.onNext(board);

        evaluateGame();
    }

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

        // Column evaluation
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

    public void setPlayerType(boolean isServer) {
        if (isServer) {
            player = SERVER_PLAYER;
        } else {
            player = CLIENT_PLAYER;
        }
    }

    public boolean isServer() {
        return player == SERVER_PLAYER;
    }

    public void makeMovement(int row, int col) {
        if (!evaluateGame()) {
            String message = row + "," + col + "," + player;
            BluetoothConnectionHandler.getInstance().send(message);
        }
    }

    public void clearBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }
    }

    private void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    public void die() {
        compositeDisposable.clear();
        compositeDisposable = null;
        sInstance = null;
    }
}
