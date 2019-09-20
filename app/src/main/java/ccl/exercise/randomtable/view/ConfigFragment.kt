package ccl.exercise.randomtable.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ccl.exercise.randomtable.MainActivity
import ccl.exercise.randomtable.R
import ccl.exercise.randomtable.extension.getString
import kotlinx.android.synthetic.main.fragment_config.*

class ConfigFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startButton.setOnClickListener { startGame() }
    }

    private fun startGame() {
        val context = context ?: return
        if (validate()) {
            if (context is MainActivity) {
                context.startGame(
                    Integer.parseInt(rowCountEdit.getString()),
                    Integer.parseInt(colCountEdit.getString())
                )
            }
        }
    }

    private fun validate(): Boolean {
        val errorMsg = getString(R.string.please_enter_number)
        if (rowCountEdit.text.isNullOrEmpty() || rowCountEdit.getString() == "0") {
            rowInputLayout.error = errorMsg
            return false
        } else if (colCountEdit.text.isNullOrEmpty() || colCountEdit.getString() == "0") {
            colInputLayout.error = errorMsg
            return false
        }
        return true
    }
}