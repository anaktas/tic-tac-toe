package com.sevenlayer.tictactoe.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.core.contracts.ServerClientPickupContract
import com.sevenlayer.tictactoe.core.presenters.ServerClientPickupPresenter
import timber.log.Timber

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
            presenter.continueAsClient()
        }

        progress = ProgressDialog(this)
        progress.setTitle("Connecting")
        progress.setMessage("Please wait...")
    }

    override fun onBackPressed() {
        startActivity(Intent(this, PairingActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun setPresenter(presenter: ServerClientPickupContract.Presenter) {
        this.presenter = presenter
    }

    override fun startLoading() {
        Timber.d("startLoading()")
        progress.show()
//        Toast.makeText(
//            this,
//            "Please wait. We will transition to the next screen automatically...",
//            Toast.LENGTH_SHORT).show()
    }

    override fun stopLoading() {
        Timber.d("stopLoading()")
        if (progress.isShowing) {
            Timber.d("Dismissing")
            progress.dismiss()
        }
    }

    override fun continueToLobby() {
        Timber.d("Continue to lobby screen")
        startActivity(Intent(this, LobbyActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    override fun onConnectionFailed() {
        Toast.makeText(this, "Something went wrong while trying to establish a BT connection.", Toast.LENGTH_LONG).show()
    }

    override fun continueToDeviceList() {
        startActivity(Intent(this, DeviceListActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}