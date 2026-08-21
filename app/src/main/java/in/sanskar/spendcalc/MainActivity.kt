package `in`.sanskar.spendcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import `in`.sanskar.spendcalc.ui.SpendCalcApp
import `in`.sanskar.spendcalc.ui.SpendCalcViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val container = (application as SpendCalcApplication).container
        val spendCalcViewModel = ViewModelProvider(
            this,
            SpendCalcViewModel.factory(container),
        )[SpendCalcViewModel::class.java]

        splashScreen.setKeepOnScreenCondition {
            !spendCalcViewModel.preferencesLoaded
        }

        setContent {
            SpendCalcApp(spendCalcViewModel)
        }
    }
}
