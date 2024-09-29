package com.rmitsubayashi.pennantmanager

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            val themeId = viewModel.getThemeId()
            setTheme(themeId)
            viewModel.setCurrentThemeColors(
                toolbarColor = getColorFromAttr(androidx.appcompat.R.attr.colorPrimary),
                systemBarColor = getColorFromAttr(androidx.appcompat.R.attr.colorPrimaryDark)
            )
        }

        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.playerListFragment,
            R.id.noteListFragment
        ))
        setSupportActionBar(findViewById(R.id.toolbar))
        setupActionBarWithNavController(navController, appBarConfiguration)

        if (viewModel.hasThemeChanged()) {
            animateToolbarColor()
            animateSystemBarColor()
        }

        viewModel.finishUpdatingTheme()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun animateToolbarColor() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val from = viewModel.previousToolbarColor ?: return
        val to = viewModel.currentToolbarColor ?: return
        val anim = ValueAnimator()
        anim.setIntValues(from, to)
        anim.setEvaluator(ArgbEvaluator())
        anim.duration = 1000L
        anim.addUpdateListener { valueAnimator ->
            toolbar.setBackgroundColor(valueAnimator.animatedValue as Int)
        }
        anim.start()
    }

    private fun animateSystemBarColor() {
        val window = window
        val from = viewModel.previousSystemBarColor ?: return
        val to = viewModel.currentSystemBarColor ?: return
        val anim = ValueAnimator()
        anim.setIntValues(from, to)
        anim.setEvaluator(ArgbEvaluator())
        anim.duration = 1000L
        anim.addUpdateListener { valueAnimator ->
            window.statusBarColor = valueAnimator.animatedValue as Int
        }
        anim.start()
    }

    private fun getColorFromAttr(attrColor: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrColor, typedValue, true)
        return typedValue.data
    }
}