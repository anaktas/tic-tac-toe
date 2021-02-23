package com.sevenlayer.tictactoe.core.presenters

import android.content.Intent
import android.provider.Settings
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.activities.ServerClientPickupActivity
import com.sevenlayer.tictactoe.core.contracts.PairingContract

/**
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
class PairingPresenter(private var view: PairingContract.View?): PairingContract.Presenter {
    override fun onDestroy() {
        view = null
    }

    override fun moveAlong() {
        view?.let {
            it.provideContext().startActivity(Intent(it.provideContext(), ServerClientPickupActivity::class.java))
            it.provideActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    override fun openBTSettings() {
        view?.let {
            val intent = Intent()
            intent.action = Settings.ACTION_BLUETOOTH_SETTINGS
            it.provideContext().startActivity(intent)
        }
    }
}