package com.sevenlayer.tictactoe.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.core.contracts.LaunchContract
import com.sevenlayer.tictactoe.core.presenters.LaunchPresenter
import timber.log.Timber

/**
 * The launch screen activity.
 *
 * @author Anastasios Daris <t.daris@7linternational.com>
 */
class LaunchActivity : AppCompatActivity(), LaunchContract.View {
    private val PERMISSION: Int = 4556
    private val REQUEST_ENABLE_BT: Int = 4557
    private lateinit var presenter: LaunchContract.Presenter
    private lateinit var message: TextView
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        setPresenter(LaunchPresenter(this))

        message = findViewById(R.id.message)
        continueButton = findViewById(R.id.continue_btn)
        continueButton.visibility = View.GONE

        continueButton.setOnClickListener {
            presenter.enableBT()
        }
    }

    override fun onResume() {
        checkPermissions()
        super.onResume()
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

    override fun onEnableBT(intent: Intent) {
        Timber.d("onEnableBT()")
        startActivityForResult(intent, REQUEST_ENABLE_BT)
    }

    override fun provideContext(): Context = this

    override fun moveAlong() {
        Timber.d("moveAlong()")
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }

        startActivity(discoverableIntent)

        startActivity(Intent(this, PairingActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    continueButton.visibility = View.VISIBLE
                } else {
                    continueButton.visibility = View.GONE
                    Toast.makeText(this, "Please go to the app settings and allow the necessary permissions", Toast.LENGTH_LONG).show()
                }

                return
            }
        }
    }

    /**
     * Checks if the app has the necessary permissions granted.
     */
    private fun checkPermissions() {
        Timber.d("checkPermissions()")

        val isFineLocationNotGranted = ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        val isCoarseLocationNotGranted = ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

        val arePermissionsNotGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val isBackgroundLocationNotGranted = ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
            isBackgroundLocationNotGranted && isFineLocationNotGranted && isCoarseLocationNotGranted
        } else {
            isFineLocationNotGranted && isCoarseLocationNotGranted
        }

        val permissionArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        if (arePermissionsNotGranted) {
            ActivityCompat.requestPermissions(
                this,
                permissionArray,
                PERMISSION)
        } else {
            continueButton.visibility = View.VISIBLE
        }
    }
}