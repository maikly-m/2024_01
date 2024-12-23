package com.example.u.scan_code.mutli

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var src: Rect? = null
    private var dst: RectF? = null
    private var image: Bitmap? = null
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private var barcodeRects: MutableList<RectF> = mutableListOf()
    private var onBarcodeSelected: ((RectF) -> Unit)? = null

    fun setBarcodeRects(bitmap: Bitmap, rects: List<Rect>) {
        calc(bitmap, rects)

        invalidate()
    }

    private fun calc(bitmap: Bitmap, rects: List<Rect>) {
        image = bitmap
        barcodeRects.clear()
        bitmap.let {
            val viewWidth = width.toFloat()
            val viewHeight = height.toFloat()
            val bitmapWidth = it.width.toFloat()
            val bitmapHeight = it.height.toFloat()
            // 计算缩放比例
            val scale = minOf(viewWidth / bitmapWidth, viewHeight / bitmapHeight)
            // 计算位移以居中显示
            val dx = (viewWidth - bitmapWidth * scale) / 2
            val dy = (viewHeight - bitmapHeight * scale) / 2

            src = Rect(0, 0, it.width, it.height)
            dst = RectF(dx, dy, dx + bitmapWidth * scale, dy + bitmapHeight * scale)
            rects.run {
                for (r in rects) {
                    RectF(
                        dx + r.left * scale,
                        dy + r.top * scale,
                        dx + r.right * scale,
                        dy + r.bottom * scale
                    ).apply {
                        barcodeRects.add(this)
                    }
                }
            }
        }
    }

    fun setOnBarcodeSelectedListener(listener: (RectF) -> Unit) {
        onBarcodeSelected = listener
    }

    fun reset(): Unit {
        image = null
        barcodeRects.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        image?.let {
            if (src != null && dst != null){
                // 绘制 Bitmap
                //canvas.drawBitmap(it, src, dst!!, paint)
                barcodeRects.forEach { rect ->
                    canvas.drawRect(rect, paint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x.toInt()
            val y = event.y.toInt()
            barcodeRects.find { it.contains(x.toFloat(), y.toFloat()) }?.let {
                onBarcodeSelected?.invoke(it)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
