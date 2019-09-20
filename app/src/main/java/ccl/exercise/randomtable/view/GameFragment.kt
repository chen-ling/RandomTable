package ccl.exercise.randomtable.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ccl.exercise.randomtable.LotteryHelper
import ccl.exercise.randomtable.R
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment(), LotteryHelper.Listener {

    private var rowCount: Int? = null
    private var columnCount: Int? = null
    private var lotteryHelper: LotteryHelper? = null

    companion object {
        private const val ARG_ROW_COUNT = "row_count"
        private const val ARG_COL_COUNT = "col_count"

        fun newInstance(rowCount: Int, columnCount: Int): GameFragment {
            val fragment = GameFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_ROW_COUNT, rowCount)
                putInt(ARG_COL_COUNT, columnCount)
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rowCount = arguments?.getInt(ARG_ROW_COUNT) ?: return
        columnCount = arguments?.getInt(ARG_COL_COUNT) ?: return

        gameView.size = rowCount!! to columnCount!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (columnCount != null && rowCount != null) {
            lotteryHelper = LotteryHelper(columnCount!!, rowCount!!, this)
            lifecycle.addObserver(lotteryHelper!!)
            Log.d("GameFragment", "onActivityCreated addObserver")
        }
    }

    override fun onPositionChange(selectedPosition: Pair<Int, Int>) {
        gameView.selectedPosition = selectedPosition
        Log.d("GameFragment", "selected: $selectedPosition")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lotteryHelper?.let {
            lifecycle.removeObserver(it)
            Log.d("GameFragment", "onDestroyView removeObserver")
        }
    }
}