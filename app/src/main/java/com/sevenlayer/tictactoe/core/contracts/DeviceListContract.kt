package com.sevenlayer.tictactoe.core.contracts

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
interface DeviceListContract {
    interface View {
        fun setPresenter(presenter: Presenter)
        fun onFetchDevices(pairedDevices: MutableList<BluetoothDevice>?)
        fun provideContext(): Context
        fun provideActivity(): Activity
    }

    interface Presenter {
        fun onDestroy()
        fun fetchDevices()

        /**
         * Sets the server BT device in the connection instance and moves
         * along to the lobby screen.
         */
        fun setDeviceAndMoveAlong(device: BluetoothDevice)
    }
}