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
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
public class BluetoothConnectionHandler {
    private final static UUID uuid = UUID.fromString("00000000-0000-1000-8000-00805F9B34FA");
    private static BluetoothConnectionHandler sInstance;

    private BluetoothServerSocket serverSocket;
    private BluetoothSocket clientSocket;
    private final BluetoothAdapter bluetoothAdapter;

    private InputStream inputStream;
    private OutputStream outputStream;

    private final PublishSubject<String> messageObservable = PublishSubject.create();
    private final PublishSubject<BluetoothConnectionStatus> connectionStatusObservable = PublishSubject.create();

    private BluetoothConnectionHandler() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public synchronized static BluetoothConnectionHandler getInstance() {
        if (sInstance == null) sInstance = new BluetoothConnectionHandler();

        return sInstance;
    }

    public Observable<String> getMessageObservable() {
        return messageObservable;
    }

    public Observable<BluetoothConnectionStatus> getConnectionStatusObservable() {
        return connectionStatusObservable;
    }

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
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                        byte[] buffer = new byte[256];
                        DataInputStream dataInputStream = new DataInputStream(inputStream);

                        int bytes = dataInputStream.read(buffer);
                        String message = new String(buffer, 0, bytes);

                        messageObservable.onNext(message);
                    } catch (IOException e) {
                        Timber.e(e);
                    }

                    break;
                }
            }
        });
    }

    public void initClient(BluetoothDevice device) {
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

    public void startClient() {
        Executor executor = AsyncFactory.generateExecutor();
        executor.execute(() -> {
            bluetoothAdapter.cancelDiscovery();

            try {
                clientSocket.connect();

                inputStream = clientSocket.getInputStream();
                outputStream = clientSocket.getOutputStream();
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

    public void stopClient() {
        try {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    public void send(String message) {
        try {
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    public void die() {
        stopServer();
        stopClient();
        sInstance = null;
    }

    public enum BluetoothConnectionStatus {
        CONNECTED,
        FAILED
    }
}
