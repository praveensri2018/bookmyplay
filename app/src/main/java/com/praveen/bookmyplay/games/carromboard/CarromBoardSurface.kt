package com.praveen.bookmyplay.games.carromboard

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.*

class CarromBoardSurface(context: Context, attrs: AttributeSet?) :
    SurfaceView(context, attrs), SurfaceHolder.Callback {

    private val thread: GameThread
    private val paint = Paint()
    private val boardMargin = 100f
    private val strikerRadius = 40f
    private val coinRadius = 20f
    private val coins = mutableListOf<Coin>()

    private var striker = Striker(0f, 0f, 0f, 0f, 0f)
    private var isDragging = false
    private var startX = 0f
    private var startY = 0f

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        resetGame()
        thread.setRunning(true)
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        thread.setRunning(false)
        while (retry) {
            try {
                thread.join()
                retry = false
            } catch (e: InterruptedException) {}
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isTouchingStriker(event.x, event.y)) {
                    isDragging = true
                    startX = event.x
                    startY = event.y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    striker.x = event.x
                    striker.y = event.y
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    isDragging = false
                    val dx = striker.x - startX
                    val dy = striker.y - startY
                    striker.vx = dx / 10
                    striker.vy = dy / 10
                }
            }
        }
        return true
    }

    private fun isTouchingStriker(x: Float, y: Float): Boolean {
        val dx = x - striker.x
        val dy = y - striker.y
        return sqrt(dx * dx + dy * dy) <= strikerRadius
    }

    fun drawBoard(canvas: Canvas?) {
        if (canvas == null) return

        canvas.drawColor(Color.rgb(245, 222, 179))

        // board frame
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        canvas.drawRect(boardMargin, boardMargin, width - boardMargin, height - boardMargin, paint)

        // pockets
        paint.style = Paint.Style.FILL
        val pocketRadius = 30f
        val pockets = listOf(
            PointF(boardMargin, boardMargin),
            PointF(width - boardMargin, boardMargin),
            PointF(boardMargin, height - boardMargin),
            PointF(width - boardMargin, height - boardMargin)
        )
        for (p in pockets) {
            canvas.drawCircle(p.x, p.y, pocketRadius, paint)
        }

        // striker
        paint.color = Color.RED
        canvas.drawCircle(striker.x, striker.y, strikerRadius, paint)

        // coins
        paint.color = Color.BLACK
        for (coin in coins) {
            canvas.drawCircle(coin.x, coin.y, coinRadius, paint)
        }
    }

    fun update() {
        striker.x += striker.vx
        striker.y += striker.vy
        striker.vx *= 0.98f
        striker.vy *= 0.98f

        if (abs(striker.vx) < 0.1f && abs(striker.vy) < 0.1f) {
            striker.vx = 0f
            striker.vy = 0f
        }

        // Coin collision (very basic)
        for (coin in coins) {
            val dx = coin.x - striker.x
            val dy = coin.y - striker.y
            val dist = sqrt(dx * dx + dy * dy)
            if (dist < coinRadius + strikerRadius) {
                coin.vx = striker.vx * 0.8f
                coin.vy = striker.vy * 0.8f
            }
            coin.x += coin.vx
            coin.y += coin.vy
            coin.vx *= 0.98f
            coin.vy *= 0.98f
        }
    }

    private fun resetGame() {
        striker = Striker(width / 2f, height - boardMargin - strikerRadius, 0f, 0f, strikerRadius)
        coins.clear()
        coins.add(Coin(width / 2f, height / 2f, 0f, 0f, coinRadius))
        coins.add(Coin(width / 2f - 60f, height / 2f, 0f, 0f, coinRadius))
        coins.add(Coin(width / 2f + 60f, height / 2f, 0f, 0f, coinRadius))
    }

    data class Coin(var x: Float, var y: Float, var vx: Float, var vy: Float, val radius: Float)
    data class Striker(var x: Float, var y: Float, var vx: Float, var vy: Float, val radius: Float)

    private class GameThread(
        private val surfaceHolder: SurfaceHolder,
        private val gameView: CarromBoardSurface
    ) : Thread() {
        private var running = false
        fun setRunning(run: Boolean) {
            running = run
        }

        override fun run() {
            while (running) {
                val canvas = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    synchronized(surfaceHolder) {
                        gameView.update()
                        gameView.drawBoard(canvas)
                    }
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}