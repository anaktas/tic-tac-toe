package com.sevenlayer.tictactoe.core.presenters

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.sevenlayer.tictactoe.core.contracts.DeviceListContract

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
class DeviceListPresenter(private val view: DeviceListContract.View): DeviceListContract.Presenter {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onDestroy() {

    }

    override fun fetchDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        val bondedDevices: MutableList<BluetoothDevice>? = ArrayList()

        pairedDevices?.forEach {
            if (it.bondState == BluetoothDevice.BOND_BONDED) {
                bondedDevices?.add(it)
            }
        }

        view.onFetchDevices(bondedDevices)
    }
}