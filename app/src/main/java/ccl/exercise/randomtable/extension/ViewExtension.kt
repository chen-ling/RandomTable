package ccl.exercise.randomtable.extension

import android.view.View
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun View.getStr(@StringRes resId: Int): String = context.getString(resId)

fun View.getColor(@ColorRes resId: Int): Int = ContextCompat.getColor(context, resId)

fun EditText.getString() = text.toString()