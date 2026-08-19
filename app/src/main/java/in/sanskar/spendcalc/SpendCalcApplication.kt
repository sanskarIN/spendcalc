package in.sanskar.spendcalc

import android.app.Application

class SpendCalcApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
