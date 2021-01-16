package ru.shirobokov.reanimation.presentation

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import ru.shirobokov.reanimation.R

interface Navigable {
    fun initNavigator(appCompatActivity: AppCompatActivity, containerId: Int)
    fun navigateTo(
        fragment: Fragment,
        navigateBackBehavior: NavigateBackBehavior = NavigateBackBehavior.Back
    )

    fun navigateBack()
}

class Navigator : Navigable {

    private val backBehaviorList = mutableListOf<Pair<NavigateBackBehavior, Int?>>()
    private var exitReanimationFragmentPressed = 0L
    private lateinit var activity: AppCompatActivity
    private var container = 0

    override fun initNavigator(appCompatActivity: AppCompatActivity, containerId: Int) {
        activity = appCompatActivity
        container = containerId
    }

    override fun navigateTo(fragment: Fragment, navigateBackBehavior: NavigateBackBehavior) {
        val transaction = activity.supportFragmentManager.beginTransaction()
            .replace(container, fragment)
            .addToBackStack(fragment::class.java.name)

        when (navigateBackBehavior) {
            is NavigateBackBehavior.SaveTransaction -> {
                backBehaviorList.add(Pair(navigateBackBehavior, transaction.commit()))
            }
            else -> {
                backBehaviorList.add(Pair(navigateBackBehavior, null))
                transaction.commit()
            }
        }
    }

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    override fun navigateBack() {
        val behavior = backBehaviorList.last().first
        when {
            behavior is NavigateBackBehavior.TwoClickBack -> {
                if (exitReanimationFragmentPressed + EXIT_TIME_INTERVAL > System.currentTimeMillis()) {
                    exitReanimationFragmentPressed = 0L
                    activity.supportFragmentManager.popBackStack()
                    backBehaviorList.removeLast()
                } else {
                    exitReanimationFragmentPressed = System.currentTimeMillis()
                    Toast.makeText(activity, R.string.exit_reanimation_screen_text, Toast.LENGTH_SHORT).show()
                }
            }

            behavior is NavigateBackBehavior.BackToSaveTransaction -> {
                backBehaviorList.findLast { it.first is NavigateBackBehavior.SaveTransaction }?.let {
                    backBehaviorList.subList(backBehaviorList.indexOf(it) + 1, backBehaviorList.size).clear()
                }
                backBehaviorList.last().second?.let { activity.supportFragmentManager.popBackStack(it, 0) }
            }

            behavior is NavigateBackBehavior.Back -> {
                activity.supportFragmentManager.popBackStack()
                backBehaviorList.removeLast()
            }

            behavior is NavigateBackBehavior.SaveTransaction && activity.supportFragmentManager.backStackEntryCount > 1 -> {
                activity.supportFragmentManager.popBackStack()
                backBehaviorList.removeLast()
            }
            else -> activity.finish()
        }
    }

    companion object {
        private const val EXIT_TIME_INTERVAL = 2000
    }
}

sealed class NavigateBackBehavior {
    object SaveTransaction : NavigateBackBehavior()
    object BackToSaveTransaction : NavigateBackBehavior()
    object TwoClickBack : NavigateBackBehavior()
    object Back : NavigateBackBehavior()
}