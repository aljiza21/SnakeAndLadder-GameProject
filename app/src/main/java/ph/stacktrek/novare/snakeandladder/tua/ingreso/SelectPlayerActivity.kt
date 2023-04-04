package ph.stacktrek.novare.snakeandladder.tua.ingreso

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import ph.stacktrek.novare.snakeandladder.tua.ingreso.databinding.ActivityDialoguePlayersBinding
import ph.stacktrek.novare.snakeandladder.tua.ingreso.databinding.ActivitySelectPlayerBinding

class SelectPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectPlayerBinding
    private lateinit var playerNames: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectPlayer2.setOnClickListener {
            showPlayersDialogue(2).show()
        }
        binding.selectPlayer3.setOnClickListener {
            showPlayersDialogue(3).show()
        }
        binding.selectPlayer4.setOnClickListener {
            showPlayersDialogue(4).show()
        }
    }

    fun showPlayersDialogue(players: Int): Dialog {
        return this.let {
            val builder = AlertDialog.Builder(it)
            val activityDialoguePlayersBinding: ActivityDialoguePlayersBinding =
                ActivityDialoguePlayersBinding.inflate(it.layoutInflater)

            val playerNames = mutableListOf<String>()

            if (players == 2) {
                activityDialoguePlayersBinding.playerThreeLabel.visibility = View.GONE
                activityDialoguePlayersBinding.playerThreeInput.visibility = View.GONE
                activityDialoguePlayersBinding.playerFourLabel.visibility = View.GONE
                activityDialoguePlayersBinding.playerFourInput.visibility = View.GONE
            }
            if (players == 3) {
                activityDialoguePlayersBinding.playerFourLabel.visibility = View.GONE
                activityDialoguePlayersBinding.playerFourInput.visibility = View.GONE
            }

            with(builder) {
                setPositiveButton("START", DialogInterface.OnClickListener { dialog, id ->
                    val goToGame = Intent(applicationContext, GameBoardActivity::class.java)

                    playerNames.add(activityDialoguePlayersBinding.playerOneInput.text.toString())
                    playerNames.add(activityDialoguePlayersBinding.playerTwoInput.text.toString())

                    if (players > 2) {
                        playerNames.add(activityDialoguePlayersBinding.playerThreeInput.text.toString())
                    }
                    if (players > 3) {
                        playerNames.add(activityDialoguePlayersBinding.playerFourInput.text.toString())
                    }

                    goToGame.putExtra("playerCount", playerNames.size)
                    goToGame.putStringArrayListExtra("playerNames", ArrayList(playerNames))
                    startActivity(goToGame)
                    finish()
                })
                setNegativeButton("CANCEL",DialogInterface.OnClickListener { dialog, id ->

                })
                setView(activityDialoguePlayersBinding.root)

                create()
            }
        }
    }
}