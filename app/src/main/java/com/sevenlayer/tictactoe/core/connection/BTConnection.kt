package com.sevenlayer.tictactoe.core.connection

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

/**
 * This object is gonna handle the BT connection and the sockets.
 *
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
object BTConnection {
    /**
     * The BT service UUID.
     */
    private val uuid: UUID = UUID.fromString("00000000-0000-1000-8000-00805F9B34FA")

    /**
     * Constant timeout for waiting a client connection.
     */
    private const val TIMEOUT: Int = 240

    /**
     * The BT adapter.
     */
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    /**
     * The message observable for the incoming messages.
     */
    private val messageObservable: PublishSubject<String> = PublishSubject.create()

    /**
     * The BT connection status observable.
     */
    private val connectionStatusObservable: PublishSubject<BTConnectionStatus> = PublishSubject.create()

    /**
     * Client socket connection observable.
     */
    private val clientConnectionStatusObservable: PublishSubject<Boolean> = PublishSubject.create()

    /**
     * The server starting flag.
     */
    private var serverStarted: Boolean = false

    /**
     * The client starting flag.
     */
    private var clientStarted: Boolean = false

    /**
     * The server socket.
     */
    private var serverSocket: BluetoothServerSocket? = null

    /**
     * The client socket.
     */
    private var clientSocket: BluetoothSocket? = null

    /**
     * The server BT device object/
     */
    private var btDevice: BluetoothDevice? = null

    /**
     * The incoming {@link InputStream}.
     */
    private var inputStream: InputStream? = null

    /**
     * The outgoing {@link OutputStream}.
     */
    private var outputStream: OutputStream? = null

    /**
     * The client coroutine job.
     */
    private var clientJob: Job? = null

    /**
     * The server coroutine job.
     */
    private var serverJob: Job? = null

    /**
     * The listening coroutin job.
     */
    private var listeningJob: Job? = null

    /**
     * Returns the message observable.
     */
    fun getMessageObservable(): Observable<String> = messageObservable

    /**
     * Returns the connection status observable.
     */
    fun getConnectionStatusObservable(): Observable<BTConnectionStatus> = connectionStatusObservable

    /**
     * Returns the client socket connection status observable.
     */
    fun getClientConnectionObservable(): Observable<Boolean> = clientConnectionStatusObservable

    /**
     * Set's the server device.
     */
    fun setDevice(device: BluetoothDevice) {
        btDevice = device
    }

    /**
     * Initializes the BT server and creates the corresponding socket.
     */
    fun initServer() {
        CoroutineScope(Dispatchers.IO).launch {
            Timber.d("initServer()")
            var tmp: BluetoothServerSocket? = null

            try {
                Timber.d("Acquiring the BT server socket")
                tmp = bluetoothAdapter?.listenUsingRfcommWithServiceRecord("TicTacToeServer", uuid)
                // looks weird, but it works
                delay(1000)
                connectionStatusObservable.onNext(BTConnectionStatus.CONNECTED)
            } catch (e: IOException) {
                delay(1000)
                connectionStatusObservable.onNext(BTConnectionStatus.FAILED)
                Timber.d(e)
                Timber.e(e)
            }

            serverSocket = tmp
        }.start()
    }

    /**
     * Starts the BT server which is listening for incoming client sockets.
     */
    fun startServer() {
        Timber.d("startServer()")
        if (serverStarted) return

        serverStarted = true

        serverJob?.cancel()
        serverJob = CoroutineScope(Dispatchers.IO).launch {
            var socket: BluetoothSocket? = null
            var timeout = 0

            while (true) {
                Timber.d("Looping: $timeout")
                // The host will not wait indefinitely, but instead he wil wait up to two minutes
                if (timeout == TIMEOUT) {
                    Timber.d("Break the loop due to timeout")
                    delay(1000)
                    clientConnectionStatusObservable.onNext(false)
                    break
                }

                try {
                    if (serverSocket != null) {
                        Timber.d("Accepting the client socket")
                        socket = serverSocket!!.accept()
                        Timber.d("Move over")
                    } else {
                        serverStarted = false
                    }
                } catch (e: IOException) {
                    serverStarted = false
                    Timber.e(e)
                    Timber.d(e)
                    clientConnectionStatusObservable.onNext(false)
                    break
                }

                if (socket != null) {
                    Timber.d("Client socket acquired")
                    try {
                        clientSocket = socket
                        inputStream = socket.inputStream
                        outputStream = socket.outputStream

                        startListening()

                        serverSocket?.close()
                    } catch (e: IOException) {
                        serverStarted = false
                        Timber.e(e)
                    }

                    clientConnectionStatusObservable.onNext(true)

                    break
                }

                timeout++
                delay(500)
            }
        }

        serverJob?.start()
    }

    /**
     * Performs the client socket connection.
     */
    fun startClient() {
        if (clientStarted) return

        clientStarted = true
        var tmp: BluetoothSocket? = null

        clientJob?.cancel()
        clientJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                tmp = btDevice?.createRfcommSocketToServiceRecord(uuid)
                delay(1000)
                connectionStatusObservable.onNext(BTConnectionStatus.CONNECTED)
            } catch (e: IOException) {
                clientStarted = false
                delay(1000)
                connectionStatusObservable.onNext(BTConnectionStatus.FAILED)
                Timber.e(e)
            }

            clientSocket = tmp


            bluetoothAdapter?.cancelDiscovery()

            try {
                if (clientSocket != null) {
                    clientSocket?.let {
                        it.connect()

                        inputStream = it.inputStream
                        outputStream = it.outputStream

                        startListening()
                    }
                } else {
                    clientStarted = false
                }
            } catch (e: IOException) {
                clientStarted = false
                Timber.e(e)

                try {
                    clientSocket?.close()
                } catch (exc: IOException) {
                    Timber.e(e)
                }
            }
        }

        clientJob?.start()
    }

    /**
     * Starts listening for incoming messages.
     */
    private fun startListening() {
        listeningJob?.cancel()
        listeningJob = CoroutineScope(Dispatchers.IO).launch {
            inputStream?.let {
                while (true) {
                    try {
                        val buffer = ByteArray(1024)
                        val dataInputStream = DataInputStream(it)

                        val bytes = dataInputStream.read(buffer)
                        val message = String(buffer, 0, bytes)

                        delay(300)
                        messageObservable.onNext(message)

                        delay(300)
                    } catch (e: IOException) {
                        Timber.e(e)
                    }
                }
            }
        }

        listeningJob?.start()
    }

    /**
     * Stops the server socket and streams.
     */
    private fun stopServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                inputStream?.close()

                outputStream?.let {
                    it.flush()
                    it.close()
                }

                serverSocket?.close()
            } catch (e: IOException) {
                Timber.e(e)
            }
        }.start()
    }

    /**
     * Stops the client socket and streams.
     */
    private fun stopClient() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                inputStream?.close()

                outputStream?.let {
                    it.flush()
                    it.close()
                }

                clientSocket?.close()
            } catch (e: IOException) {
                Timber.e(e)
            }
        }.start()
    }

    /**
     * Sends a message to the BT device.
     */
    fun send(message: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                message?.let {
                    outputStream?.write(it.toByteArray())
                }
            } catch (e: IOException) {
                Timber.e(e)
            }
        }.start()
    }

    /**
     * Clears the instance.
     */
    fun die() {
        stopServer()
        stopClient()
        serverStarted = false
        clientStarted = false

        serverJob?.cancel()
        clientJob?.cancel()
        listeningJob?.cancel()

        serverSocket = null
        clientSocket = null
        btDevice = null
        inputStream = null
        outputStream = null
        clientJob = null
        serverJob = null
        listeningJob = null
    }

    enum class BTConnectionStatus {
        CONNECTED,
        FAILED
    }
}