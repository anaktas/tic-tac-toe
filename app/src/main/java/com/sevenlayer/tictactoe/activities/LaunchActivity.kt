package com.sevenlayer.tictactoe.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.core.contracts.LaunchContract
import com.sevenlayer.tictactoe.core.presenters.LaunchPresenter

/**
 * The launch screen activity.
 *
 * @author Anastasios Daris <t.daris@7linternational.com>
 */
class LaunchActivity : AppCompatActivity(), LaunchContract.View {
    private lateinit var presenter: LaunchContract.Presenter
    private lateinit var message: TextView
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        setPresenter(LaunchPresenter(this))

        message = findViewById(R.id.message)
        continueButton = findViewById(R.id.continue_btn)
        continueButton.visibility = View.VISIBLE

        continueButton.setOnClickListener {
            presenter.enableBT()
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun setPresenter(presenter: LaunchContract.Presenter) {
        this.presenter = presenter
    }

    override fun onBTNotSupported() {
        Toast.makeText(this, "We are sorry, but your device does not support bluetooth.", Toast.LENGTH_LONG).show()
    }

    override fun provideContext(): Context = this

    override fun provideActivity(): Activity = this
}