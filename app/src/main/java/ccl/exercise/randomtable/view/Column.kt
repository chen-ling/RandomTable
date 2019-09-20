package ccl.exercise.randomtable.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import ccl.exercise.randomtable.R
import ccl.exercise.randomtable.extension.getColor
import ccl.exercise.randomtable.extension.getStr
import java.lang.IllegalArgumentException


class ColumnView : View {

    companion object {
        private const val CONTROL_COLUMN_HEIGHT_RATIO = 0.7f
        private const val WHITE_SEPARATOR_CELL_RATIO = 2f / 30
        private const val DARK_CELL_RATIO = 4f / 30
        private const val CONTROL_PADDING = 4f / 30
        private const val DEFAULT_ROW_COUNT = 1
        private const val BORDER_WIDTH = 2f
        private const val SELECTION_BORDER_WIDTH = 8f
        private const val MAX_HUE = 360f
        private const val CORNER_RADIUS = 15F
    }

    var selectedRow: Int? = null
    var rowCount: Int = DEFAULT_ROW_COUNT

    private val filledPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val selectedBorderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = SELECTION_BORDER_WIDTH
        color = getColor(R.color.lightBlue)
    }

    private val controlBorderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = getColor(R.color.grey)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrSet: AttributeSet?) : this(context, attrSet, 0)
    constructor(context: Context, attrSet: AttributeSet?, defStyleAttr: Int) : super(context, attrSet, defStyleAttr) {
        val typedArray =
            context.obtainStyledAttributes(attrSet, R.styleable.Column, defStyleAttr, 0)
        rowCount = typedArray.getInt(R.styleable.Column_rowCount, DEFAULT_ROW_COUNT)
        typedArray.recycle()
        if (rowCount < DEFAULT_ROW_COUNT) {
            throw IllegalArgumentException("rowCount should be greater than O")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            resolveSizeAndState(0, widthMeasureSpec, 0),
            resolveSizeAndState(0, heightMeasureSpec, 0)
        )
    }

    override fun onDraw(canvas: Canvas) {
        drawRows(canvas)
        drawControl(canvas)
        if (selectedRow != null) {
            drawCellText(canvas)
        }
        drawBorder(canvas)
    }

    private fun drawRows(canvas: Canvas) {
        val rowHeight = getRowHeight()
        val whiteHeight = getWhiteSeparatorHeight()
        val darkHeight = DARK_CELL_RATIO * rowHeight
        val brightHeight = rowHeight - whiteHeight - darkHeight

        canvas.save()
        for (i in 0 until rowCount) {
            val hue = getHueByRowIndex(i)
            // bright part
            filledPaint.color = Color.HSVToColor(floatArrayOf(hue, 0.2f, 0.8f))
            canvas.drawRect(0f, whiteHeight, width.toFloat(), whiteHeight + brightHeight, filledPaint)
            // dark part
            filledPaint.color = Color.HSVToColor(floatArrayOf(hue, 0.8f, 0.6f))
            canvas.drawRect(
                0f,
                whiteHeight + brightHeight,
                width.toFloat(),
                whiteHeight + brightHeight + darkHeight,
                filledPaint
            )
            canvas.translate(0f, rowHeight)
        }
        canvas.restore()
    }

    private fun drawControl(canvas: Canvas) {
        // background
        filledPaint.color = getColor(R.color.darkGrey)
        val controlHeight = getControlHeight()
        val whiteHeight = getWhiteSeparatorHeight()
        canvas.drawRect(0f, height - controlHeight + whiteHeight, width.toFloat(), height.toFloat(), filledPaint)

        // border
        val borderPaint = if (selectedRow == null) {
            controlBorderPaint
        } else {
            filledPaint.color = getColor(R.color.lightBlue)
            filledPaint
        }
        val padding = controlHeight * CONTROL_PADDING
        val rect = RectF(padding, height - controlHeight + padding + whiteHeight, width - padding, height - padding)
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, borderPaint)

        // border background
        filledPaint.apply {
            color = if (selectedRow == null) {
                getColor(R.color.grey)
            } else {
                getColor(R.color.white)
            }
            textSize = 100f
        }

        // confirm text
        val confirmText = getStr(R.string.confirm)
        val availableWidth = width - padding * 4
        val availableHeight = (controlHeight - padding * 4) * 0.6f
        filledPaint.textSize = getScaledTextSize(confirmText, availableWidth, availableHeight)
        val fontWidth = filledPaint.measureText(confirmText)
        canvas.drawText(confirmText, (width - fontWidth) / 2, height - 2 * padding, filledPaint)
    }

    private fun drawBorder(canvas: Canvas) {
        if (selectedRow == null) {
            val height = rowCount * getRowHeight()
            filledPaint.color = getColor(R.color.white)
            canvas.drawRect(0f, 0f, BORDER_WIDTH, height, filledPaint)
            canvas.drawRect(width - BORDER_WIDTH, 0f, width.toFloat(), height, filledPaint)
        } else {
            val halfBorderWidth = SELECTION_BORDER_WIDTH / 2
            val rect = RectF(
                halfBorderWidth,
                halfBorderWidth,
                width.toFloat() - halfBorderWidth,
                height.toFloat() - halfBorderWidth
            )
            canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, selectedBorderPaint)
        }
    }

    private fun drawCellText(canvas: Canvas) {
        val selectedRow = selectedRow ?: return
        val rowHeight = getRowHeight()
        val brightHeight = rowHeight - getWhiteSeparatorHeight() - DARK_CELL_RATIO * rowHeight
        val padding = 100f
        val availableWidth = width - 2 * padding
        val randomText = getStr(R.string.random)

        canvas.save()
        canvas.translate(0f, selectedRow * rowHeight + getWhiteSeparatorHeight())
        filledPaint.color = getColor(R.color.black)
        filledPaint.textSize = getScaledTextSize(randomText, availableWidth, brightHeight)
        val fontHeight = filledPaint.fontMetrics.bottom - filledPaint.fontMetrics.top
        val fontWidth = filledPaint.measureText(randomText)
        canvas.drawText(
            randomText,
            (width - fontWidth) / 2,
            brightHeight / 2 + fontHeight / 2 - filledPaint.fontMetrics.bottom,
            filledPaint
        )
        canvas.restore()
    }

    private fun getRowHeight(): Float = height / (rowCount + CONTROL_COLUMN_HEIGHT_RATIO)

    private fun getControlHeight(): Float = getRowHeight() * CONTROL_COLUMN_HEIGHT_RATIO

    private fun getWhiteSeparatorHeight(): Float = getRowHeight() * WHITE_SEPARATOR_CELL_RATIO

    private fun getHueByRowIndex(index: Int) = MAX_HUE / rowCount * index

    private fun getScaledTextSize(text: String, viewWidth: Float, viewHeight: Float): Float {
        var textWidth = Float.MAX_VALUE
        var textHeight = Float.MAX_VALUE
        val paint = Paint()
        var textSize = 80F
        while (textHeight > viewHeight || textWidth > viewWidth) {
            paint.textSize = textSize
            val bounds = Rect()
            paint.getTextBounds(text, 0, text.length, bounds)
            textWidth = bounds.width().toFloat()
            textHeight = bounds.height().toFloat()
            if (textWidth > viewWidth) {
                textSize *= (viewWidth / textWidth)
            } else if (textHeight > viewHeight) {
                textSize *= (viewHeight / textHeight)
            }

        }
        return textSize
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val event = event ?: return false
        Log.d("Column onTouchEvent", "x:${event.x}, y:${event.y}")
        return isInsideControl(event)
    }

    private fun isInsideControl(event: MotionEvent): Boolean {
        val controlStartPositionY = height - getControlHeight()
        if (event.y > controlStartPositionY) {
            return true
        }
        return false
    }

}