package com.sevenlayer.tictactoe.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.core.connection.BTConnection
import com.sevenlayer.tictactoe.core.contracts.LobbyContract
import com.sevenlayer.tictactoe.core.game.GameInstance
import com.sevenlayer.tictactoe.core.presenters.LobbyPresenter

class LobbyActivity : AppCompatActivity(), LobbyContract.View {
    private lateinit var statusMessage: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var continueButton: Button

    private lateinit var presenter: LobbyContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        setPresenter(LobbyPresenter(this))

        statusMessage = findViewById(R.id.status_message)
        progressBar = findViewById(R.id.progress_bar)
        continueButton = findViewById(R.id.continue_btn)

        continueButton.visibility = View.GONE

        continueButton.setOnClickListener {
            presenter.moveToBoardScreen()
        }

        statusMessage.text = if (GameInstance.isServer()) {
            getString(R.string.waiting_guest)
        } else {
            getString(R.string.waiting_host)
        }

        progressBar.visibility = View.GONE

        presenter.startConnection()
    }

    override fun onBackPressed() {
        BTConnection.die()
        GameInstance.die()
        startActivity(Intent(this, ServerClientPickupActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun setPresenter(presenter: LobbyContract.Presenter) {
        this.presenter = presenter
    }

    override fun startLoading() {
        runOnUiThread {
            progressBar.visibility = View.VISIBLE
        }
    }

    override fun stopLoading() {
        runOnUiThread {
            progressBar.visibility = View.GONE
        }
    }

    override fun onConnected() {
        runOnUiThread {
            continueButton.visibility = View.VISIBLE
            statusMessage.text = getString(R.string.continue_playing)
        }
    }

    override fun onFailure(message: String?) {
        runOnUiThread {
            message?.let {
                statusMessage.text = it
            }
        }
    }

    override fun provideContext(): Context = this

    override fun provideActivity(): Activity = this
}