package com.sevenlayer.tictactoe.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.core.connection.BTConnection
import com.sevenlayer.tictactoe.core.contracts.BoardContract
import com.sevenlayer.tictactoe.core.game.GameInstance
import com.sevenlayer.tictactoe.core.presenters.BoardPresenter
import timber.log.Timber

class BoardActivity : AppCompatActivity(), BoardContract.View {
    private lateinit var score: TextView

    private lateinit var zero_zero: TextView
    private lateinit var zero_one: TextView
    private lateinit var zero_two: TextView

    private lateinit var one_zero: TextView
    private lateinit var one_one: TextView
    private lateinit var one_two: TextView

    private lateinit var two_zero: TextView
    private lateinit var two_one: TextView
    private lateinit var two_two: TextView

    private lateinit var reset: Button

    private var boardHolder = arrayOf<Array<TextView>>()

    private lateinit var presenter: BoardContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        setPresenter(BoardPresenter(this))

        score = findViewById(R.id.score)

        zero_zero = findViewById(R.id.zero_zero)
        zero_one = findViewById(R.id.zero_one)
        zero_two = findViewById(R.id.zero_two)

        one_zero = findViewById(R.id.one_zero)
        one_one = findViewById(R.id.one_one)
        one_two = findViewById(R.id.one_two)

        two_zero = findViewById(R.id.two_zero)
        two_one = findViewById(R.id.two_one)
        two_two = findViewById(R.id.two_two)

        reset = findViewById(R.id.reset)

        reset.visibility = if (GameInstance.isServer()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        reset.setOnClickListener {
            GameInstance.resetBoard()
        }

        initBoard()
        updateScore(GameInstance.getScore())

        presenter.startPlaying()
    }

    override fun onBackPressed() {
        BTConnection.die()
        GameInstance.die()
        startActivity(Intent(this, ServerClientPickupActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun initBoard() {
        val zeroRow = arrayOf(zero_zero, zero_one, zero_two)
        boardHolder += zeroRow

        val firstRow = arrayOf(one_zero, one_one, one_two)
        boardHolder += firstRow

        val secondRow = arrayOf(two_zero, two_one, two_two)
        boardHolder += secondRow

        initMechanism()
    }

    private fun initMechanism() {
        for (i in 0..2) {
            for (j in 0..2) {
                boardHolder[i][j].setOnClickListener {
                    move(i, j, boardHolder[i][j])
                }
            }
        }
    }

    private fun move(row: Int, col: Int, txt: TextView?) {
        Timber.d("Can move: ${GameInstance.canMove(row, col)}")
        if (GameInstance.canMove(row, col)) {
            txt?.text = if (GameInstance.isServer()) {
                "X"
            } else {
                "O"
            }

            // Send the move to the server
            presenter.move(row, col)
            updateScore(GameInstance.getScore())
        }
    }

    override fun setPresenter(presenter: BoardContract.Presenter) {
        this.presenter = presenter
    }

    override fun updateScore(scr: String) {
        runOnUiThread {
            score.text = scr
        }
    }

    override fun updateBoard(board: Array<IntArray>) {
        runOnUiThread {
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == 0) boardHolder[i][j].text = ""
                    if (board[i][j] == 1) boardHolder[i][j].text = "X"
                    if (board[i][j] == 2) boardHolder[i][j].text = "O"
                }
            }
        }
    }
}