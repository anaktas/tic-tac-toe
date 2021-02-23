package com.sevenlayer.tictactoe.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.core.contracts.PairingContract
import com.sevenlayer.tictactoe.core.presenters.PairingPresenter

/**
 * The pairing rationale screen activity.
 *
 * @author Anastasios Daris <t.daris@7linternational.com>
 */
class PairingActivity : AppCompatActivity(), PairingContract.View {
    private lateinit var message: TextView
    private lateinit var continueButton: Button
    private lateinit var btSettings: Button

    private lateinit var presenter: PairingContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pairing)

        setPresenter(PairingPresenter(this))

        message = findViewById(R.id.message)
        continueButton = findViewById(R.id.continue_btn)
        btSettings = findViewById(R.id.bt_settings)

        continueButton.setOnClickListener {
            presenter.moveAlong()
        }

        btSettings.setOnClickListener {
            presenter.openBTSettings()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, LaunchActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun setPresenter(presenter: PairingContract.Presenter) {
        this.presenter = presenter
    }

    override fun provideContext(): Context = this

    override fun provideActivity(): Activity = this
}