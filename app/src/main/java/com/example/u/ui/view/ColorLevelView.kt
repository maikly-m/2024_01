package com.example.u.ui.view

import com.example.u.R
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.u.uitls.DisplayUtils

class ColorLevelView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint = Paint()
    private var rectWidth = 0f
    private val rectHeight:Float
    private val cornerRadius:Float
    private val gap:Int
    private val rectColors = arrayOf(R.color.white, R.color.purple_500, R.color.teal_200, R.color.teal_700)
    private val rectInt = arrayOf(0, 25, 50, 75, 100)
    private val textPaint: Paint = Paint()
    private val image: Bitmap // 图片资源
    private var score: Int = 0

    init {
        // 初始化 Paint 对象，设置画笔样式等
        paint.color = Color.parseColor("#FF6200EE") // 矩形的填充颜色
        paint.isAntiAlias = true // 设置抗锯齿

        textPaint.color = Color.BLACK
        textPaint.textSize = DisplayUtils.dp2px(context, 15f).toFloat()
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        gap = DisplayUtils.dp2px(context, 10f)
        cornerRadius = DisplayUtils.dp2px(context, 5f).toFloat()
        rectHeight = DisplayUtils.dp2px(context, 10f).toFloat()

        // 加载图片资源
        image = BitmapFactory.decodeResource(context.resources, R.drawable.ic_qp_video)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rectWidth = (width - gap * 5f) / 4
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        score = (Math.random()*100).toInt()
        // 计算每个矩形的起始位置
        val startX = (width - (rectWidth * 4 + gap * 5)) / 2  // 计算矩形的横向起始位置，居中显示

        // 绘制四个带圆角的矩形
        for (i in 0..3) {
            val left = startX + (rectWidth + gap) * i + gap
            val top = DisplayUtils.dp2px(context, 10f).toFloat() + image.height
            val right = left + rectWidth
            val bottom = top + rectHeight

            // 绘制圆角矩形
            paint.setColor(context.getColor(rectColors[i]))
            canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, paint)

            if (score/25 == i){
                // 绘制图片（放置在矩形的上方）
                // val imageLeft = left + (rectWidth - image.width) / 2
                val imageLeft = left + (rectWidth) * (score%25/25f) - image.width/2
                val imageTop = top - image.height - DisplayUtils.dp2px(context, 5f)
                canvas.drawBitmap(image, imageLeft, imageTop, null)
            }

            // 绘制文字（序号，放置在矩形的下方）
            val text = rectInt[i].toString()
            val textX = left
            val textY = bottom + DisplayUtils.dp2px(context, 15f)
            canvas.drawText(text, textX, textY, textPaint)
            if (i==3){
                canvas.drawText( rectInt[i+1].toString(), right, bottom + DisplayUtils.dp2px(context, 15f).toFloat(), textPaint)
            }
        }
    }
}
