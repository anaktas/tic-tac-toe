package com.sevenlayer.tictactoe.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.core.contracts.ServerClientPickupContract
import com.sevenlayer.tictactoe.core.game.Game
import com.sevenlayer.tictactoe.core.presenters.ServerClientPickupPresenter

/**
 * The pick up screen for the server or client player activity.
 *
 * @author Anastasios Daris <t.daris@7linternational.com>
 */
class ServerClientPickupActivity : AppCompatActivity(), ServerClientPickupContract.View {
  private lateinit var continueAsServer: Button
  private lateinit var continueAsClient: Button

  private lateinit var presenter: ServerClientPickupContract.Presenter

  private lateinit var progress: ProgressDialog

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_server_client_pickup)

    setPresenter(ServerClientPickupPresenter(this))

    continueAsServer = findViewById(R.id.continue_as_server)
    continueAsClient = findViewById(R.id.continue_as_client)

    continueAsServer.setOnClickListener {
      presenter.continueAsServer()
    }

    continueAsClient.setOnClickListener {

    }

    progress = ProgressDialog(this)
    progress.setTitle("Connecting")
    progress.setMessage("Please wait...")
  }

  override fun onDestroy() {
    presenter.onDestroy()
    super.onDestroy()
  }

  override fun setPresenter(presenter: ServerClientPickupContract.Presenter) {
    this.presenter = presenter
  }

  override fun startLoading() {
    progress.show()
  }

  override fun stopLoading() {
    if (progress.isShowing) {
      progress.dismiss()
    }
  }

  override fun continueToLobby() {

  }

  override fun onConnectionFailed() {
    Toast.makeText(this, "Something went wrong while trying to establish a BT connection.", Toast.LENGTH_LONG).show()
  }

  override fun continueToDeviceList() {

  }
}