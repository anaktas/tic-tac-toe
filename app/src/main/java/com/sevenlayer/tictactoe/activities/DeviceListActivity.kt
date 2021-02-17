package com.sevenlayer.tictactoe.activities

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.adapters.BTAdapter
import com.sevenlayer.tictactoe.core.contracts.DeviceListContract
import com.sevenlayer.tictactoe.core.presenters.DeviceListPresenter
import timber.log.Timber

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

  override fun onResume() {
    super.onResume()

    presenter.fetchDevices()
  }

  override fun setPresenter(presenter: DeviceListContract.Presenter) {
    this.presenter = presenter
  }

  override fun onFetchDevices(pairedDevices: MutableList<BluetoothDevice>?) {
    pairedDevices?.let { deviceList ->
      list.invalidate()

      adapter = BTAdapter(deviceList) { device ->
        Log.e("DEVICE","$device")
      }

      list.adapter = adapter
      linearLayoutManager = LinearLayoutManager(this)
      list.layoutManager = linearLayoutManager
    }
  }
}