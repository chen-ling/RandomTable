package ccl.exercise.randomtable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ccl.exercise.randomtable.view.ConfigFragment
import ccl.exercise.randomtable.view.GameFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showConfigFragment()
    }

    fun startGame(rowCount: Int, columnCount: Int) {
        val fragment = GameFragment.newInstance(rowCount, columnCount)
        addFragment(fragment, "GameFragment")
    }

    private fun showConfigFragment() {
        addFragment(ConfigFragment(), "ConfigFragment")
    }

    private fun addFragment(fragment: Fragment, tagName: String?) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, tagName)
            addToBackStack(null)
            commitAllowingStateLoss()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
