package com.sevenlayer.tictactoe.core.contracts

import android.bluetooth.BluetoothDevice

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
interface DeviceListContract {
    interface View {
        fun setPresenter(presenter: Presenter)
        fun onFetchDevices(pairedDevices: MutableList<BluetoothDevice>?)
    }

    interface Presenter {
        fun onDestroy()
        fun fetchDevices()
    }
}