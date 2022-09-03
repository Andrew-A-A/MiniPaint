package com.example.android.minipaint

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat

private const val STROKE_WIDTH=12F

class MyCanvasView(context: Context): View(context) {
    private lateinit var extraCanvas: Canvas
    private lateinit var  extraBitmap: Bitmap
    private lateinit var frame: Rect

    private var motionTouchEventX=0f
    private var motionTouchEventY=0f

    private val touchTolerance=ViewConfiguration.get(context).scaledTouchSlop
    private var currentX=0f
    private var currentY=0f
    private val drawColor=ResourcesCompat.getColor(resources,R.color.colorPaint,null)
    private val paint=Paint().apply {
        color=drawColor
        isAntiAlias=true
        isDither=true
        style=Paint.Style.STROKE
        strokeCap=Paint.Cap.ROUND
        strokeJoin=Paint.Join.ROUND
        strokeWidth= STROKE_WIDTH
    }

    private var path= Path()
    private val backgroundColor=ResourcesCompat.getColor(resources,R.color.colorBackground,null)
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val inset=40
        frame= Rect(inset,inset,w-inset,h-inset)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        extraBitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        extraCanvas= Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap,0f,0f,null)
        canvas.drawRect(frame,paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            motionTouchEventX= event.x
            motionTouchEventY= event.y
        }
        when(event?.action){
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchUp() {
    path.reset()
    }

    private fun touchMove() {
        val dx=Math.abs(motionTouchEventX - currentX)
        val dy=Math.abs(motionTouchEventY - currentY)

        if (dx>= touchTolerance || dy>=touchTolerance){
            path.quadTo(currentX,currentY,(motionTouchEventX+currentX)/2,(motionTouchEventY+currentY)/2)
            currentX=motionTouchEventX
            currentY=motionTouchEventY
            extraCanvas.drawPath(path,paint)
        }
        invalidate()
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX,motionTouchEventY)
        currentX=motionTouchEventX
        currentY=motionTouchEventY

    }
}