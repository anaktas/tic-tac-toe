package com.sevenlayer.tictactoe.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sevenlayer.tictactoe.R

/**
 * The pairing rationale screen activity.
 *
 * @author Anastasios Daris <t.daris@7linternational.com>
 */
class PairingActivity : AppCompatActivity() {
    private lateinit var message: TextView
    private lateinit var continueButton: Button
    private lateinit var btSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pairing)

        message = findViewById(R.id.message)
        continueButton = findViewById(R.id.continue_btn)
        btSettings = findViewById(R.id.bt_settings)

        continueButton.setOnClickListener {
            moveAlong()
        }

        btSettings.setOnClickListener {
            openBTSettings()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, LaunchActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    /**
     * Moves along to the next screen which is the ServerClientPickupActivity.
     */
    private fun moveAlong() {
        startActivity(Intent(this, ServerClientPickupActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    /**
     * Opens the device's BT settings.
     */
    private fun openBTSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_BLUETOOTH_SETTINGS
        startActivity(intent)
    }
}