package com.sevenlayer.tictactoe.core.presenters

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.activities.LobbyActivity
import com.sevenlayer.tictactoe.core.connection.BTConnection
import com.sevenlayer.tictactoe.core.contracts.DeviceListContract

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
class DeviceListPresenter(private var view: DeviceListContract.View?): DeviceListContract.Presenter {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onDestroy() {
        view = null
    }

    override fun fetchDevices() {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        val bondedDevices: MutableList<BluetoothDevice>? = ArrayList()

        pairedDevices?.forEach {
            if (it.bondState == BluetoothDevice.BOND_BONDED) {
                bondedDevices?.add(it)
            }
        }

        view?.onFetchDevices(bondedDevices)
    }

    override fun setDeviceAndMoveAlong(device: BluetoothDevice) {
        view?.let {
            BTConnection.setDevice(device)
            it.provideContext().startActivity(Intent(it.provideContext(), LobbyActivity::class.java))
            it.provideActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            it.provideActivity().finish()
        }
    }
}