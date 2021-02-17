package com.sevenlayer.tictactoe.core.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.sevenlayer.tictactoe.factories.AsyncFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

/**
 * This singleton instance is gonna handle the BT connection and the sockets.
 *
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
public class BluetoothConnectionHandler {
    /**
     * The BT service UUID.
     */
    private final static UUID uuid = UUID.fromString("00000000-0000-1000-8000-00805F9B34FA");

    /**
     * The BT connection handler instance.
     */
    private static BluetoothConnectionHandler sInstance;

    /**
     * The server socket.
     */
    private BluetoothServerSocket serverSocket;

    /**
     * The client socket.
     */
    private BluetoothSocket clientSocket;

    /**
     * The BT adapter.
     */
    private final BluetoothAdapter bluetoothAdapter;

    private BluetoothDevice device;

    /**
     * The incoming {@link InputStream}.
     */
    private InputStream inputStream;

    /**
     * The outgoing {@link OutputStream}.
     */
    private OutputStream outputStream;

    /**
     * The message observable for the incoming messages.
     */
    private final PublishSubject<String> messageObservable = PublishSubject.create();

    /**
     * The BT connection status observable.
     */
    private final PublishSubject<BluetoothConnectionStatus> connectionStatusObservable = PublishSubject.create();

    private BluetoothConnectionHandler() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public synchronized static BluetoothConnectionHandler getInstance() {
        if (sInstance == null) sInstance = new BluetoothConnectionHandler();

        return sInstance;
    }

    /**
     * Returns the message observable.
     *
     * @return {@link Observable<String>}
     */
    public Observable<String> getMessageObservable() {
        return messageObservable;
    }

    /**
     * Returns the connections status observable.
     *
     * @return {@link Observable<BluetoothConnectionStatus>}
     */
    public Observable<BluetoothConnectionStatus> getConnectionStatusObservable() {
        return connectionStatusObservable;
    }

    /**
     * Set's the {@link BluetoothDevice} for the client.
     *
     * @param device a {@link BluetoothDevice}
     */
    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    /**
     * Initializes the BT server and creates the corresponding socket.
     */
    public void initServer() {
        BluetoothServerSocket tmp = null;

        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("TicTacToeServer", uuid);
            connectionStatusObservable.onNext(BluetoothConnectionStatus.CONNECTED);
        } catch (IOException e) {
            connectionStatusObservable.onNext(BluetoothConnectionStatus.FAILED);
            Timber.e(e);
        }

        serverSocket = tmp;
    }

    /**
     * Starts the BT server which is listening for incoming client sockets.
     */
    public void startServer() {
        Executor executor = AsyncFactory.generateExecutor();
        executor.execute(() -> {
            BluetoothSocket socket = null;

            while (true) {
                try {
                    if (serverSocket != null) {
                        socket = serverSocket.accept();
                    }
                } catch (IOException e) {
                    Timber.e(e);
                }

                if (socket != null) {
                    try {
                        clientSocket = socket;
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();

                        startListening();

                        serverSocket.close();
                    } catch (IOException e) {
                        Timber.e(e);
                    }

                    break;
                }
            }
        });
    }

    /**
     * Initializes the BT client and the corresponding socket.
     */
    public void initClient() {
        BluetoothSocket tmp = null;

        try {
            tmp = device.createRfcommSocketToServiceRecord(uuid);
            connectionStatusObservable.onNext(BluetoothConnectionStatus.CONNECTED);
        } catch (IOException e) {
            connectionStatusObservable.onNext(BluetoothConnectionStatus.FAILED);
            Timber.e(e);
        }

        clientSocket = tmp;
    }

    /**
     * Performs the client socket connection.
     */
    public void startClient() {
        Executor executor = AsyncFactory.generateExecutor();
        executor.execute(() -> {
            bluetoothAdapter.cancelDiscovery();

            try {
                if (clientSocket != null) {
                    clientSocket.connect();

                    inputStream = clientSocket.getInputStream();
                    outputStream = clientSocket.getOutputStream();

                    startListening();
                }
            } catch (IOException e) {
                Timber.e(e);

                try {
                    clientSocket.close();
                } catch (IOException exc) {
                    Timber.e(exc);
                }
            }
        });
    }

    public void startListening() {
        new Thread(() -> {
            if (inputStream != null) {
                while (true) {
                    try {
                        byte[] buffer = new byte[1024];
                        DataInputStream dataInputStream = new DataInputStream(inputStream);

                        int bytes = dataInputStream.read(buffer);
                        String message = new String(buffer, 0, bytes);

                        messageObservable.onNext(message);

                        // Slowing down the loop
                        Thread.sleep(300);
                    } catch (IOException | InterruptedException e) {
                        Timber.e(e);
                    }
                }
            }
        }).start();
    }

    /**
     * Stops the server by closing the corresponding socket and the streams.
     */
    public void stopServer() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    /**
     * Stops the client by closing the corresponding socket and the streams.
     */
    public void stopClient() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message the gaming movement message
     */
    public void send(String message) {
        try {
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    /**
     * Clears the entire instance.
     */
    public void die() {
        stopServer();
        stopClient();
        sInstance = null;
    }

    /**
     * A custom connections status enumeration.
     */
    public enum BluetoothConnectionStatus {
        CONNECTED,
        FAILED
    }
}
