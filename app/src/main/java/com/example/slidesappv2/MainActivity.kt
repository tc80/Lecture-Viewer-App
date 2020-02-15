package com.example.slidesappv2

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar
import android.net.Uri
import java.util.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity() {

    private val studRes = "https://studres.cs.st-andrews.ac.uk/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.hide_ads).setOnClickListener{
            showToast("no")
        }
        findViewById<Button>(R.id.select_lecture).setOnClickListener{
            Selector(this, it).selectModule(studRes)
        }
        val reset = findViewById<Button>(R.id.reset)
        reset.setOnClickListener{
            val scroll = findViewById<HorizontalScrollView>(R.id.scroll)
            scroll.removeAllViews()
            scroll.invalidate()
            showLogo()
            deactivateResetButton()
            deactivateShareButton()
        }
        deactivateResetButton()
        deactivateShareButton()
    }

    private fun example1() {
        // hello world toast
        showToast("hello world")
    }

    private fun example2() {
        // snackbar without action
        findViewById<Button>(R.id.select_lecture).setOnClickListener{
            view -> run {
                showSnackbar(view, "my snackbar", null, null, null)
            }
        }
    }

    private fun example3() {
        // snackbar with action, also "it"
        findViewById<Button>(R.id.select_lecture).setOnClickListener{
            showSnackbar(it, "my snackbar with action",null, "display my toast", View.OnClickListener {
                showToast("you displayed me")
            })
        }
    }

    private fun example4() {
        findViewById<Button>(R.id.select_lecture).setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
            val pIntent = PendingIntent.getActivity(this, 0, intent, 0)
            showNotification("Example 4", "Navigate to Google!", "Click me", pIntent)
        }
    }

    internal fun showToast(text: String) {
        Toast
            .makeText(applicationContext, text, Toast.LENGTH_LONG)
            .show()
    }

    internal fun showSnackbar(view: View, title: String, duration: Int?, actionTitle: String?, listener: View.OnClickListener?) {
        Snackbar
            .make(view, title, duration ?: Snackbar.LENGTH_LONG)
            .setAction(actionTitle, listener)
            .show()
    }

    internal fun showNotification(title: String?, body: String?, actionTitle: String?, pIntent: PendingIntent) {
        val nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelID = "my_channel_id"
        val channelName = "my channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val myChannel = nManager.getNotificationChannel(channelID)
            ?: NotificationChannel(channelID, channelName, importance)
        nManager.createNotificationChannel(myChannel)
        val notification = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.example_notification_icon)
            .setAutoCancel(true)
            .addAction(NotificationCompat.Action(R.drawable.example_notification_icon, actionTitle, pIntent))
            .setStyle(NotificationCompat.BigTextStyle()
                .setBigContentTitle(title)
                .bigText(body)
            )
            .build()
        nManager.notify(generateID(), notification)
    }

    internal fun activateShareButton(imageView: ImageView) {
        val btn = findViewById<Button>(R.id.share_slide)
        btn.setOnClickListener{
            showToast(imageView.toString())
        }
        btn.isEnabled = true
    }

    internal fun hideLogo() {
        findViewById<ImageView>(R.id.logo).visibility = ImageView.INVISIBLE
    }

    private fun showLogo() {
        findViewById<ImageView>(R.id.logo).visibility = ImageView.VISIBLE
    }

    internal fun deactivateShareButton() {
        findViewById<Button>(R.id.share_slide).isEnabled = false
    }

    internal fun activateResetButton() {
        val btn = findViewById<Button>(R.id.reset)
        btn.isEnabled = true
    }

    private fun deactivateResetButton() {
        val btn = findViewById<Button>(R.id.reset)
        btn.isEnabled = false
    }

    private fun generateID(): Int {
        return (Date().time / 1000).toInt() % Int.MAX_VALUE
    }

}
