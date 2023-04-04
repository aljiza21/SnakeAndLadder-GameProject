package ph.stacktrek.novare.snakeandladder.tua.ingreso

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

class GameBoardActivity : AppCompatActivity() {

    private val snakes = mapOf(
        16 to 6,
        47 to 26,
        49 to 11,
        56 to 53,
        62 to 19,
        64 to 60,
        87 to 24,
        93 to 73,
        98 to 76)
    private val ladders = mapOf(
        1 to 38,
        4 to 14,
        9 to 31,
        21 to 42,
        28 to 84,
        36 to 44,
        51 to 67,
        71 to 91,
        80 to 100)

    private var playerPositions = arrayOf(0, 0, 0, 0)
    private var currentPlayer = 0
    private var totalPlayers = 2
    private lateinit var playerNames: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_board)
        initBoard()

        // Get the player information from the intent extras
        totalPlayers = intent.getIntExtra("playerCount", 2) // Default value is 2
        playerNames = intent.getStringArrayListExtra("playerNames") ?: mutableListOf("Player 1", "Player 2")
        playerPositions = Array(totalPlayers) { 0 }

        val playerPositionTextView = findViewById<TextView>(R.id.playerPosition)
        val diceImageView = findViewById<ImageView>(R.id.diceImageView)
        val rollDiceButton = findViewById<Button>(R.id.rollDiceButton)

        updatePlayerPositionTextView(playerPositionTextView)
//------------------------roll dice-----------------------//
        rollDiceButton.setOnClickListener {
            val diceRoll = Random.nextInt(1, 7)
            val diceImageResource = when (diceRoll) {
                1 -> R.drawable.dice1
                2 -> R.drawable.dice2
                3 -> R.drawable.dice3
                4 -> R.drawable.dice4
                5 -> R.drawable.dice5
                else -> R.drawable.dice6
            }
            diceImageView.setImageResource(diceImageResource)

            val currentPlayerPosition = playerPositions[currentPlayer]
            var newPosition = currentPlayerPosition + diceRoll

            if (newPosition <= 100) {
                newPosition = snakes[newPosition] ?: ladders[newPosition] ?: newPosition
                playerPositions[currentPlayer] = newPosition
                updatePlayerPositionTextView(playerPositionTextView)
                initBoard()

                if (newPosition == 100) {
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle("Congratulations")
                    alertDialogBuilder.setMessage("${playerNames[currentPlayer]} wins!")
                    alertDialogBuilder.setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, which -> finish() })

                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()

                    playerPositionTextView.text = "${playerNames[currentPlayer]} wins!"
                    rollDiceButton.isEnabled = false

                    // Store the winner's name in SharedPreference
                    val sharedPreferences = getSharedPreferences("winners", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    val winnersJson = sharedPreferences.getString("winners", "[]") ?: "[]"
                    val winnersList = Gson().fromJson(winnersJson, object : TypeToken<MutableList<String>>() {}.type) as MutableList<String>
                    winnersList.add(playerNames[currentPlayer])

                    if (winnersList.size > 5) {
                        winnersList.removeAt(0)
                    }

                    val updatedWinnersJson = Gson().toJson(winnersList)
                    editor.putString("winners", updatedWinnersJson)
                    editor.apply()

                } else {
                    playerPositions[currentPlayer] = newPosition
                    currentPlayer = (currentPlayer + 1) % totalPlayers
                    updatePlayerPositionTextView(playerPositionTextView)
                }
            }

        }
    }

    //---------------------- update text
    private fun updatePlayerPositionTextView(playerPositionTextView: TextView) {
        val positionsText = StringBuilder()
        for (i in 0 until totalPlayers) {
            positionsText.append("${playerNames[i]} is on square ${playerPositions[i]}\n")
        }
        playerPositionTextView.text = positionsText.toString().trimEnd()
    }

    //----------------------------board layout------------
    private fun initBoard() {
        val tableLayout = findViewById<TableLayout>(R.id.board)

        // Remove all existing TableRows from the TableLayout (if any)
        tableLayout.removeAllViews()

        var position = 100

        for (row in 0 until 10) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT,
                1f
            )

            for (col in 0 until 10) {
                val button = Button(this)
                button.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1f
                ).apply {
                    setMargins(2, 2, 2, 2)
                }

                button.text = position.toString()

                // Set the button color based on the player position
                when {
                    playerPositions.any { it == position } -> {
                        val playerIndex = playerPositions.indexOfFirst { it == position }
                        val playerColor = when (playerIndex) {
                            0 -> Color.BLUE
                            1 -> Color.RED
                            2 -> Color.GREEN
                            else -> Color.MAGENTA
                        }
                        button.setBackgroundColor(playerColor)
                    }
                    snakes.containsKey(position) -> button.setBackgroundColor(Color.parseColor("#F6D24B")) // Color for snakes
                    ladders.containsKey(position) -> button.setBackgroundColor(Color.parseColor("#FF5BA5")) // Color for ladders
                    else -> button.setBackgroundColor(Color.parseColor("#B1CCE4"))
                }

                // Add the button to the TableRow
                if (row % 2 == 0) {
                    tableRow.addView(button)
                } else {
                    tableRow.addView(button, 0)
                }

                position--
            }

            // Add the TableRow to the TableLayout
            tableLayout.addView(tableRow)
        }
    }


}