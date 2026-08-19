package in.sanskar.spendcalc.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object ExternalLinks {
    fun openUrl(context: Context, url: String): Boolean =
        launchSafely(
            context,
            Intent(Intent.ACTION_VIEW, Uri.parse(url)),
        )

    fun email(context: Context, address: String, subject: String = "SpendCalc"): Boolean =
        launchSafely(
            context,
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${Uri.encode(address)}")
                putExtra(Intent.EXTRA_SUBJECT, subject)
            },
        )

    private fun launchSafely(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    }
}
