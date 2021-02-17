package com.sevenlayer.tictactoe.adapters

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.sevenlayer.tictactoe.R

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
class BTAdapter(private val deviceList: MutableList<BluetoothDevice>, private val listener: ((BluetoothDevice) -> Unit)?): RecyclerView.Adapter<BTAdapter.ViewHolder>() {

  class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val deviceButton: Button = view.findViewById(R.id.device)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.layout_list_item_bt_device, parent, false)

    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.deviceButton.text = deviceList[position].name

    holder.deviceButton.setOnClickListener {
      listener?.invoke(deviceList[position])
    }
  }

  override fun getItemCount() = deviceList.size
}