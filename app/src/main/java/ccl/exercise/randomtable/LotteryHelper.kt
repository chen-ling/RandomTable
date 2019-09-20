package ccl.exercise.randomtable

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.random.Random

class LotteryHelper(private val column: Int, private val row: Int, private val listener: Listener?) :
    LifecycleObserver {
    private var disposable: Disposable? = null

    companion object {
        private const val TEN_SECONDS = 5L
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startRandom() {
        Observable.interval(TEN_SECONDS, TEN_SECONDS, TimeUnit.SECONDS)
            .map { (Random.nextInt().absoluteValue % row) to (Random.nextInt().absoluteValue % column) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                listener?.onPositionChange(it)
            }, { Log.d("LotteryHelper", "Error occurs on random: $it") })
            .let { disposable = it }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopRandom() {
        disposable?.dispose()
    }

    interface Listener {
        fun onPositionChange(selectedPosition: Pair<Int, Int>)
    }
}

