package com.sevenlayer.tictactoe.activities

import android.content.Intent
import android.os.Bundle
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_pairing)

    message = findViewById(R.id.message)
    continueButton = findViewById(R.id.continue_btn)

    continueButton.setOnClickListener {
      moveAlong()
    }
  }

  /**
   * Moves along to the next screen which is the ServerClientPickupActivity.
   */
  private fun moveAlong() {
    startActivity(Intent(this, ServerClientPickupActivity::class.java))
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
  }
}