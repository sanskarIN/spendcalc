package `in`.sanskar.spendcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.sanskar.spendcalc.ui.SpendCalcApp
import `in`.sanskar.spendcalc.ui.SpendCalcViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val container = (application as SpendCalcApplication).container
        setContent {
            val spendCalcViewModel: SpendCalcViewModel = viewModel(
                factory = SpendCalcViewModel.factory(container),
            )
            SpendCalcApp(spendCalcViewModel)
        }
    }
}
