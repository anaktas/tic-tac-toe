package com.sevenlayer.tictactoe.activities

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.adapters.BTAdapter
import com.sevenlayer.tictactoe.core.connection.BTConnection
import com.sevenlayer.tictactoe.core.contracts.DeviceListContract
import com.sevenlayer.tictactoe.core.game.GameInstance
import com.sevenlayer.tictactoe.core.presenters.DeviceListPresenter

class DeviceListActivity : AppCompatActivity(), DeviceListContract.View {
    private lateinit var list: RecyclerView

    private lateinit var presenter: DeviceListContract.Presenter
    private lateinit var adapter: BTAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        list = findViewById(R.id.device_list)

        setPresenter(DeviceListPresenter(this))
    }

    override fun onBackPressed() {
        BTConnection.die()
        GameInstance.die()
        startActivity(Intent(this, ServerClientPickupActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    override fun onResume() {
        super.onResume()

        presenter.fetchDevices()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun setPresenter(presenter: DeviceListContract.Presenter) {
        this.presenter = presenter
    }

    override fun onFetchDevices(pairedDevices: MutableList<BluetoothDevice>?) {
        pairedDevices?.let { deviceList ->
            list.invalidate()

            adapter = BTAdapter(deviceList) { device ->
                setDeviceAndMoveAlong(device)
            }

            list.adapter = adapter
            linearLayoutManager = LinearLayoutManager(this)
            list.layoutManager = linearLayoutManager
        }
    }

    /**
     * Sets the server BT device in the connection instance and moves
     * along to the lobby screen.
     */
    private fun setDeviceAndMoveAlong(device: BluetoothDevice) {
        BTConnection.setDevice(device)

        startActivity(Intent(this, LobbyActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}