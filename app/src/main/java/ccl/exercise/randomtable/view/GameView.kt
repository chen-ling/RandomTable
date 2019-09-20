package ccl.exercise.randomtable.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import ccl.exercise.randomtable.R

private const val DEFAULT_ROW_COUNT = 1
private const val DEFAULT_COLUMN_COUNT = 1

class GameView : View {

    var selectedPosition: Pair<Int, Int>? = null
        set(value) {
            if (field == value) {
                return
            }

            val oldValue = field
            field = value

            oldValue?.second?.let(this::updateColumn)
            value?.second?.let(this::updateColumn)
        }

    var size: Pair<Int, Int> = DEFAULT_ROW_COUNT to DEFAULT_COLUMN_COUNT
        set(value) {
            if (field == value) {
                return
            }
            field = value
            updateSize()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrSet: AttributeSet?) : this(context, attrSet, 0)
    constructor(context: Context, attrSet: AttributeSet?, defStyleAttr: Int) : super(context, attrSet, defStyleAttr) {
        val typedArray =
            context.obtainStyledAttributes(attrSet, R.styleable.GameView, defStyleAttr, 0)
        val rowCount =
            typedArray.getInt(R.styleable.GameView_rowCount, DEFAULT_ROW_COUNT)
        val columnCount =
            typedArray.getInt(R.styleable.GameView_columnCount, DEFAULT_COLUMN_COUNT)
        typedArray.recycle()
        init(rowCount, columnCount)
    }

    private val linearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
    }

    private fun init(rowCount: Int, columnCount: Int) {
        if (rowCount < DEFAULT_ROW_COUNT) {
            throw IllegalArgumentException("rowCount should be greater than O")
        } else if (columnCount < DEFAULT_COLUMN_COUNT) {
            throw IllegalArgumentException("columnCount should be greater than O")
        }
        size = rowCount to columnCount
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        linearLayout.measure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        if (MeasureSpec.AT_MOST == widthSpecMode || MeasureSpec.AT_MOST == heightSpecMode) {
            throw RuntimeException("Wrap_content doesn't work")
        }
        setMeasuredDimension(
            resolveSizeAndState(linearLayout.measuredWidth, widthMeasureSpec, 0),
            resolveSizeAndState(linearLayout.measuredHeight, heightMeasureSpec, 0)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        linearLayout.layout(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        linearLayout.draw(canvas)
    }

    private fun updateSize() {
        val (rowCount, columnCount) = size

        val layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                .apply { weight = 1f }

        (0 until columnCount)
            .map {
                ColumnView(context).apply {
                    setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                    this.rowCount = rowCount
                }.let { linearLayout.addView(it, layoutParams) }
            }
        invalidate()
    }

    private fun updateColumn(column: Int) {
        if (column >= linearLayout.childCount) {
            return
        }

        val selectedRow = selectedPosition?.first
        val selectedColumn = selectedPosition?.second

        val columnView = linearLayout.getChildAt(column) as? ColumnView ?: return
        columnView.selectedRow = if (selectedColumn == column) {
            selectedRow
        } else {
            null
        }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val event = event ?: return false
        if (event.action != MotionEvent.ACTION_DOWN) return false
        if (selectedPosition?.second == getClickedColumn(event)) {
            selectedPosition = null
        }
        return true
    }

    private fun getColumnWidth() = width / size.second

    private fun getClickedColumn(event: MotionEvent): Int? {
        val childIndex = event.x.toInt() / getColumnWidth()
        if (linearLayout.getChildAt(childIndex).onTouchEvent(event)) {
            return childIndex
        }
        return null
    }
}

