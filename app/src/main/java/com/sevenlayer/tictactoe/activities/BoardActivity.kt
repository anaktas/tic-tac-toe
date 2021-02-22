package com.sevenlayer.tictactoe.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sevenlayer.tictactoe.R
import com.sevenlayer.tictactoe.core.connection.BTConnection
import com.sevenlayer.tictactoe.core.contracts.BoardContract
import com.sevenlayer.tictactoe.core.game.GameInstance
import com.sevenlayer.tictactoe.core.presenters.BoardPresenter
import org.w3c.dom.Text
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
//        boardHolder[0][0] = zero_zero
//        boardHolder[0][1] = zero_one
//        boardHolder[0][2] = zero_two

        val zeroRow = arrayOf(zero_zero, zero_one, zero_two)
        boardHolder += zeroRow

//        boardHolder[1][0] = one_zero
//        boardHolder[1][1] = one_one
//        boardHolder[1][2] = one_two

        val firstRow = arrayOf(one_zero, one_one, one_two)
        boardHolder += firstRow

//        boardHolder[2][0] = two_zero
//        boardHolder[2][1] = two_one
//        boardHolder[2][2] = two_two

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
            score.text = if (GameInstance.isServer()) {
                "X: $scr :O"
            } else {
                "0: $scr :X"
            }
        }
    }

    override fun updateBoard(board: Array<IntArray>) {
        runOnUiThread {
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == 0) boardHolder[i][j].text = ""
                    if (board[i][j] == 1) boardHolder[i][j].text = "X"
                    if (board[i][j] == 2) boardHolder[i][j].text = ")"
                    when(board[i][j]) {
                        0 -> boardHolder[i][j].text = ""
                        1 -> boardHolder[i][j].text = "X"
                        2 -> boardHolder[i][j].text = "O"
                    }
                }
            }
        }
    }
}