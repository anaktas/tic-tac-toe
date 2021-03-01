package com.sevenlayer.tictactoe.core.presenters

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.activities.PairingActivity
import com.sevenlayer.tictactoe.core.contracts.LaunchContract
import timber.log.Timber

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
class LaunchPresenter(private var view: LaunchContract.View?): LaunchContract.Presenter {
    private val REQUEST_ENABLE_BT: Int = 4557
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    override fun onDestroy() {
        view = null
    }

    override fun enableBT() {
        Timber.d("enableBT()")
        view?.let {
            if (bluetoothAdapter == null) {
                it.onBTNotSupported()
            } else {
                if (!bluetoothAdapter.isEnabled ) {
                    it.provideActivity().startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT)
                } else {
                    val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                        putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                    }

                    it.provideContext().startActivity(discoverableIntent)

                    it.provideContext().startActivity(Intent(it.provideContext(), PairingActivity::class.java))
                    it.provideActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
            }
        }
    }
}