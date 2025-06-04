package com.praveen.bookmyplay.games.chess

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.praveen.bookmyplay.R
import com.praveen.bookmyplay.util.PieceSymbols
import kotlin.math.min

class ChessGameActivity : AppCompatActivity() {

    private lateinit var chessBoard: GridLayout
    private lateinit var btnFlip: Button
    private lateinit var turnText: TextView
    private lateinit var whiteTimerView: TextView
    private lateinit var blackTimerView: TextView

    private var currentMoveNumber = 1
    private var whiteMoveNotation = ""
    private var blackMoveNotation = ""

    private var gameEnded = false
    private val moveHistory = mutableListOf<String>()

    private val board = Array(8) { Array<Int?>(8) { null } }
    private var selectedRow: Int? = null
    private var selectedCol: Int? = null
    private var whiteTurn = true
    private var flipped = false
    private val possibleMoves = mutableListOf<Pair<Int, Int>>()

    private lateinit var whiteTimer: CountDownTimer
    private lateinit var blackTimer: CountDownTimer
    private var whiteTimeLeft = 5 * 60 * 1000L
    private var blackTimeLeft = 5 * 60 * 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess_game)

        chessBoard = findViewById(R.id.chessBoard)
        btnFlip = findViewById(R.id.btnFlip)
        turnText = findViewById(R.id.turnText)
        whiteTimerView = findViewById(R.id.whiteTimer)
        blackTimerView = findViewById(R.id.blackTimer)

        initBoard()
        renderBoard()
        updateTurnText()
        startWhiteTimer()

        btnFlip.setOnClickListener {
            flipped = !flipped
            renderBoard()
        }
    }

    private fun initBoard() {
        for (row in 0..7) {
            for (col in 0..7) {
                board[row][col] = null
            }
        }

        // Pawns
        for (col in 0..7) {
            board[1][col] = PieceSymbols.BLACK_PAWN
            board[6][col] = PieceSymbols.WHITE_PAWN
        }

        // Rooks
        board[0][0] = PieceSymbols.BLACK_ROOK; board[0][7] = PieceSymbols.BLACK_ROOK
        board[7][0] = PieceSymbols.WHITE_ROOK; board[7][7] = PieceSymbols.WHITE_ROOK

        // Knights
        board[0][1] = PieceSymbols.BLACK_KNIGHT; board[0][6] = PieceSymbols.BLACK_KNIGHT
        board[7][1] = PieceSymbols.WHITE_KNIGHT; board[7][6] = PieceSymbols.WHITE_KNIGHT

        // Bishops
        board[0][2] = PieceSymbols.BLACK_BISHOP; board[0][5] = PieceSymbols.BLACK_BISHOP
        board[7][2] = PieceSymbols.WHITE_BISHOP; board[7][5] = PieceSymbols.WHITE_BISHOP

        // Queens and Kings
        board[0][3] = PieceSymbols.BLACK_QUEEN
        board[0][4] = PieceSymbols.BLACK_KING
        board[7][3] = PieceSymbols.WHITE_QUEEN
        board[7][4] = PieceSymbols.WHITE_KING
    }

    private fun getMoveNotation(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int, capturedPiece: Int?): String {
        val piece = board[toRow][toCol] ?: return ""

        // For pawn moves
        if (piece == PieceSymbols.WHITE_PAWN || piece == PieceSymbols.BLACK_PAWN) {
            val file = ('a' + toCol).toString()
            return if (capturedPiece != null) "${('a' + fromCol)}x$file" else file
        }

        // For other pieces
        val pieceChar = when (piece) {
            PieceSymbols.WHITE_KING, PieceSymbols.BLACK_KING -> "K"
            PieceSymbols.WHITE_QUEEN, PieceSymbols.BLACK_QUEEN -> "Q"
            PieceSymbols.WHITE_ROOK, PieceSymbols.BLACK_ROOK -> "R"
            PieceSymbols.WHITE_BISHOP, PieceSymbols.BLACK_BISHOP -> "B"
            PieceSymbols.WHITE_KNIGHT, PieceSymbols.BLACK_KNIGHT -> "N"
            else -> ""
        }
        val file = ('a' + toCol).toString()
        val rank = (8 - toRow).toString()
        return "$pieceChar$file$rank"
    }

    private fun updateHistoryView() {
        val historyView = findViewById<TextView>(R.id.historyTextView)
        // Show incomplete move if white has moved but black hasn't yet
        val currentMove = if (whiteMoveNotation.isNotEmpty() && blackMoveNotation.isEmpty()) {
            "$currentMoveNumber.$whiteMoveNotation"
        } else {
            ""
        }
        val historyText = moveHistory.joinToString("\n") + if (currentMove.isNotEmpty()) "\n$currentMove" else ""
        historyView.text = historyText
    }

    private fun renderBoard() {
        chessBoard.removeAllViews()

        // Get screen dimensions minus padding
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels - (16 * displayMetrics.density).toInt() // 8dp padding on each side
        val availableHeight = (displayMetrics.heightPixels * 0.6).toInt() // Use 60% of screen height for board

        // Calculate cell size to fit both width and height
        val cellSize = min(screenWidth / 8, availableHeight / 8)

        for (i in 0..7) {
            val row = if (flipped) 7 - i else i
            for (j in 0..7) {
                val col = if (flipped) 7 - j else j

                val cell = ImageView(this)
                val params = GridLayout.LayoutParams().apply {
                    width = cellSize
                    height = cellSize
                    rowSpec = GridLayout.spec(i)
                    columnSpec = GridLayout.spec(j)
                }
                cell.layoutParams = params

                // Rest of your cell rendering code...
                val isLightSquare = (row + col) % 2 == 0
                cell.setBackgroundColor(
                    if (isLightSquare) Color.parseColor("#EEEED2") else Color.parseColor("#769656")
                )

                if (selectedRow == row && selectedCol == col) {
                    cell.setBackgroundColor(Color.YELLOW)
                }

                val pieceResId = board[row][col]
                if (pieceResId != null) {
                    cell.setImageResource(pieceResId)
                } else {
                    cell.setImageDrawable(null)
                }

                cell.setOnClickListener {
                    onCellClicked(row, col)
                }

                chessBoard.addView(cell)

                if (possibleMoves.contains(Pair(row, col))) {
                    val dotView = ImageView(this)
                    dotView.setImageResource(R.drawable.possible_move_dot)
                    val dotParams = GridLayout.LayoutParams().apply {
                        width = cellSize
                        height = cellSize
                        rowSpec = GridLayout.spec(i)
                        columnSpec = GridLayout.spec(j)
                    }
                    dotView.layoutParams = dotParams
                    dotView.scaleType = ImageView.ScaleType.CENTER
                    chessBoard.addView(dotView)
                }
            }
        }
        updateTurnText()
    }

    private fun onCellClicked(row: Int, col: Int) {
        val pieceResId = board[row][col]

        if (selectedRow == null || selectedCol == null) {
            if (pieceResId != null && isCurrentPlayersPiece(pieceResId)) {
                selectedRow = row
                selectedCol = col
                possibleMoves.clear()
                possibleMoves.addAll(getValidMoves(row, col))
                renderBoard()
            } else {
              //  Toast.makeText(this, "Select your own piece", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val fromRow = selectedRow!!
        val fromCol = selectedCol!!

        if (possibleMoves.contains(Pair(row, col))) {
            // Make the move
            val capturedPiece = board[row][col]
            board[row][col] = board[fromRow][fromCol]
            board[fromRow][fromCol] = null

            val moveNotation = getMoveNotation(fromRow, fromCol, row, col, capturedPiece)
            val timeUsed = formatTimeShort(if (whiteTurn) whiteTimeLeft else blackTimeLeft)
            if (whiteTurn) {
                // White's move (first part of the pair)
                whiteMoveNotation = "$moveNotation($timeUsed)"
            } else {
                // Black's move (second part of the pair)
                blackMoveNotation = "$moveNotation($timeUsed)"
                // Add the complete move pair to history
                moveHistory.add("$currentMoveNumber.$whiteMoveNotation $blackMoveNotation")
                currentMoveNumber++
                whiteMoveNotation = ""
                blackMoveNotation = ""
            }

          //  moveHistory.add("${if (whiteTurn) "White" else "Black"}: $moveNotation (${formatTime(if (whiteTurn) whiteTimeLeft else blackTimeLeft)})")
            updateHistoryView()

            // First render the board with the updated move
            selectedRow = null
            selectedCol = null
            possibleMoves.clear()
            renderBoard()

            // Switch turns
            whiteTurn = !whiteTurn

            // Check for game end after rendering the move
            if (!hasAnyValidMove()) {
                gameEnded = true
                if (::whiteTimer.isInitialized) whiteTimer.cancel()
                if (::blackTimer.isInitialized) blackTimer.cancel()

                val winner = if (whiteTurn) "Black" else "White"
                val isCheck = isKingInCheck(whiteTurn)
                val result = if (isCheck) "$winner wins by checkmate!" else "Game ended in stalemate!"

                // Add to history
                moveHistory.add(result)
                updateHistoryView()

                chessBoard.post {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }
                return
            }

            // Start timer for the next player
            if (whiteTurn) startWhiteTimer() else startBlackTimer()
        } else {
            selectedRow = null
            selectedCol = null
            possibleMoves.clear()
            renderBoard()
        }
    }


    private fun getValidMoves(row: Int, col: Int): List<Pair<Int, Int>> {
        val piece = board[row][col] ?: return emptyList()
        val moves = mutableListOf<Pair<Int, Int>>()

        // First get all possible moves without considering king safety
        val rawMoves = getRawMoves(row, col)

        // For each possible move, check if it would leave king in check
        for ((targetRow, targetCol) in rawMoves) {
            // Skip moves that capture own pieces
            if (board[targetRow][targetCol]?.let { isCurrentPlayersPiece(it) } == true) {
                continue
            }

            // Simulate the move
            val originalPiece = board[targetRow][targetCol]
            board[targetRow][targetCol] = board[row][col]
            board[row][col] = null

            // Check if king is safe after this move
            val isKingSafe = !isKingInCheck(whiteTurn)

            // Undo the simulated move
            board[row][col] = board[targetRow][targetCol]
            board[targetRow][targetCol] = originalPiece

            // Only add move if it doesn't leave king in check
            if (isKingSafe) {
                moves.add(targetRow to targetCol)
            }
        }

        return moves
    }

    private fun formatTimeShort(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun getRawMoves(row: Int, col: Int): List<Pair<Int, Int>> {
        val piece = board[row][col] ?: return emptyList()
        val moves = mutableListOf<Pair<Int, Int>>()

        fun isInBounds(r: Int, c: Int) = r in 0..7 && c in 0..7
        fun isEmpty(r: Int, c: Int) = board[r][c] == null
        fun isOpponent(r: Int, c: Int) = board[r][c]?.let { !isCurrentPlayersPiece(it) } ?: false

        when (piece) {
            PieceSymbols.WHITE_PAWN -> {
                if (isInBounds(row - 1, col) && isEmpty(row - 1, col)) moves.add(row - 1 to col)
                if (isInBounds(row - 1, col - 1) && isOpponent(row - 1, col - 1)) moves.add(row - 1 to col - 1)
                if (isInBounds(row - 1, col + 1) && isOpponent(row - 1, col + 1)) moves.add(row - 1 to col + 1)
                if (row == 6 && isEmpty(row - 1, col) && isEmpty(row - 2, col)) moves.add(row - 2 to col)
            }
            PieceSymbols.BLACK_PAWN -> {
                if (isInBounds(row + 1, col) && isEmpty(row + 1, col)) moves.add(row + 1 to col)
                if (isInBounds(row + 1, col - 1) && isOpponent(row + 1, col - 1)) moves.add(row + 1 to col - 1)
                if (isInBounds(row + 1, col + 1) && isOpponent(row + 1, col + 1)) moves.add(row + 1 to col + 1)
                if (row == 1 && isEmpty(row + 1, col) && isEmpty(row + 2, col)) moves.add(row + 2 to col)
            }
            PieceSymbols.WHITE_ROOK, PieceSymbols.BLACK_ROOK -> {
                val directions = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
                for ((dr, dc) in directions) {
                    var r = row + dr
                    var c = col + dc
                    while (isInBounds(r, c)) {
                        if (isEmpty(r, c)) {
                            moves.add(r to c)
                        } else {
                            if (isOpponent(r, c)) moves.add(r to c)
                            break
                        }
                        r += dr
                        c += dc
                    }
                }
            }
            PieceSymbols.WHITE_BISHOP, PieceSymbols.BLACK_BISHOP -> {
                val directions = listOf(1 to 1, 1 to -1, -1 to 1, -1 to -1)
                for ((dr, dc) in directions) {
                    var r = row + dr
                    var c = col + dc
                    while (isInBounds(r, c)) {
                        if (isEmpty(r, c)) {
                            moves.add(r to c)
                        } else {
                            if (isOpponent(r, c)) moves.add(r to c)
                            break
                        }
                        r += dr
                        c += dc
                    }
                }
            }
            PieceSymbols.WHITE_QUEEN, PieceSymbols.BLACK_QUEEN -> {
                val directions = listOf(
                    1 to 0, -1 to 0, 0 to 1, 0 to -1,
                    1 to 1, 1 to -1, -1 to 1, -1 to -1
                )
                for ((dr, dc) in directions) {
                    var r = row + dr
                    var c = col + dc
                    while (isInBounds(r, c)) {
                        if (isEmpty(r, c)) {
                            moves.add(r to c)
                        } else {
                            if (isOpponent(r, c)) moves.add(r to c)
                            break
                        }
                        r += dr
                        c += dc
                    }
                }
            }
            PieceSymbols.WHITE_KNIGHT, PieceSymbols.BLACK_KNIGHT -> {
                val knightMoves = listOf(
                    -2 to -1, -2 to 1, -1 to -2, -1 to 2,
                    1 to -2, 1 to 2, 2 to -1, 2 to 1
                )
                for ((dr, dc) in knightMoves) {
                    val r = row + dr
                    val c = col + dc
                    if (isInBounds(r, c) && (board[r][c]?.let { !isCurrentPlayersPiece(it) } ?: true)) {
                        moves.add(r to c)
                    }
                }
            }
            PieceSymbols.WHITE_KING, PieceSymbols.BLACK_KING -> {
                val kingMoves = listOf(
                    -1 to -1, -1 to 0, -1 to 1,
                    0 to -1, 0 to 1,
                    1 to -1, 1 to 0, 1 to 1
                )
                for ((dr, dc) in kingMoves) {
                    val r = row + dr
                    val c = col + dc
                    if (isInBounds(r, c) && (board[r][c]?.let { !isCurrentPlayersPiece(it) } ?: true)) {
                        moves.add(r to c)
                    }
                }
            }
        }
        return moves
    }

    private fun isKingInCheck(forWhite: Boolean): Boolean {
        val kingPiece = if (forWhite) PieceSymbols.WHITE_KING else PieceSymbols.BLACK_KING
        var kingRow = -1
        var kingCol = -1

        // Find the king's position
        outer@ for (row in 0..7) {
            for (col in 0..7) {
                if (board[row][col] == kingPiece) {
                    kingRow = row
                    kingCol = col
                    break@outer
                }
            }
        }

        return if (kingRow != -1 && kingCol != -1) {
            isSquareUnderAttack(kingRow, kingCol, !forWhite)
        } else {
            false
        }
    }

    private fun isSquareUnderAttack(targetRow: Int, targetCol: Int, byWhite: Boolean): Boolean {
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = board[row][col] ?: continue
                val isWhitePiece = isWhitePiece(piece)
                if (isWhitePiece != byWhite) continue

                val originalTurn = whiteTurn
                whiteTurn = isWhitePiece
                val moves = getRawMoves(row, col)
                whiteTurn = originalTurn

                if (moves.contains(targetRow to targetCol)) {
                    return true
                }
            }
        }
        return false
    }

    private fun hasAnyValidMove(): Boolean {
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = board[row][col] ?: continue
                if (isCurrentPlayersPiece(piece)) {
                    if (getValidMoves(row, col).isNotEmpty()) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun isCurrentPlayersPiece(pieceResId: Int): Boolean {
        return if (whiteTurn) {
            isWhitePiece(pieceResId)
        } else {
            !isWhitePiece(pieceResId)
        }
    }

    private fun isWhitePiece(pieceResId: Int): Boolean {
        return when (pieceResId) {
            PieceSymbols.WHITE_PAWN,
            PieceSymbols.WHITE_ROOK,
            PieceSymbols.WHITE_KNIGHT,
            PieceSymbols.WHITE_BISHOP,
            PieceSymbols.WHITE_QUEEN,
            PieceSymbols.WHITE_KING -> true
            else -> false
        }
    }

    private fun updateTurnText() {
        turnText.text = if (whiteTurn) "White's turn" else "Black's turn"
    }

    private fun startWhiteTimer() {
        if (gameEnded) return
        if (::blackTimer.isInitialized) blackTimer.cancel()
        whiteTimer = object : CountDownTimer(whiteTimeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                whiteTimeLeft = millisUntilFinished
                whiteTimerView.text = formatTime(millisUntilFinished)
            }
            override fun onFinish() {
                gameEnded = true
                if (this@ChessGameActivity::whiteTimer.isInitialized) whiteTimer.cancel()
                if (this@ChessGameActivity::blackTimer.isInitialized) blackTimer.cancel()

                val result = if (this@ChessGameActivity.whiteTurn) "Black wins on time!" else "White wins on time!"
                moveHistory.add(result)
                updateHistoryView()

                Toast.makeText(this@ChessGameActivity, result, Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    private fun resetGame() {
        gameEnded = false
        moveHistory.clear()
        whiteTimeLeft = 5 * 60 * 1000L
        blackTimeLeft = 5 * 60 * 1000L
        whiteTurn = true
        initBoard()
        renderBoard()
        updateTurnText()
        updateHistoryView()
        startWhiteTimer()
    }

    private fun startBlackTimer() {
        if (gameEnded) return
        if (::whiteTimer.isInitialized) whiteTimer.cancel()
        blackTimer = object : CountDownTimer(blackTimeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                blackTimeLeft = millisUntilFinished
                blackTimerView.text = formatTime(millisUntilFinished)
            }
            override fun onFinish() {
                Toast.makeText(this@ChessGameActivity, "Black's time over!", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}