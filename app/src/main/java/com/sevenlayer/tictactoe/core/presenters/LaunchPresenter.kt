package com.sevenlayer.tictactoe.core.presenters

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import com.sevenlayer.tictactoe.core.contracts.LaunchContract
import timber.log.Timber

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
class LaunchPresenter(private val view: LaunchContract.View): LaunchContract.Presenter {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onDestroy() {

    }

    override fun enableBT() {
        Timber.d("enableBT()")
        if (bluetoothAdapter == null) {
            view.onBTNotSupported()
        } else {
            if (!bluetoothAdapter.isEnabled ) {
                view.onEnableBT(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            } else {
                view.moveAlong()
            }
        }
    }
}