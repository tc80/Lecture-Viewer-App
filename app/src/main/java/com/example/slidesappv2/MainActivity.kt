/**
 * Group 3 Android Demo (CS3301, 02/2020)
 * @author 170006583
 */

package com.example.slidesappv2

import android.app.*
import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar
import java.util.*
import android.widget.*
import androidx.core.view.drawToBitmap
import android.widget.Button
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import java.lang.ref.WeakReference
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    private val studRes = "https://studres.cs.st-andrews.ac.uk/"
    private val moneyID = "user_balance"
    private val shareCost = 25
    private val initMoney = 250
    private var money = 0

    // ui elements
    private lateinit var selectButton: Button
    private lateinit var shareButton: Button
    private lateinit var resetButton: Button
    private lateinit var watchButton: Button
    private lateinit var scrollView: HorizontalScrollView
    private lateinit var progressSpinner: ProgressBar
    private lateinit var progressBar: ProgressBar
    private lateinit var logo: ImageView
    private lateinit var moneyText: TextView

    // shared preferences to save state
    private lateinit var sharedPref: SharedPreferences

    // called on activity start
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // find and initialize ui elements
        selectButton = findViewById(R.id.select_lecture)
        shareButton = findViewById(R.id.share_slide)
        resetButton = findViewById(R.id.reset)
        watchButton = findViewById(R.id.watch_ad)
        scrollView = findViewById(R.id.scroll)
        progressSpinner = findViewById(R.id.spinner)
        progressBar = findViewById(R.id.progress)
        logo = findViewById(R.id.logo)
        moneyText = findViewById(R.id.money)

        // set click listeners
        selectButton.setOnClickListener { Selector(WeakReference(this), it).selectModule(studRes) }
        resetButton.setOnClickListener { reset() }

        // get saved balance if it exists
        sharedPref = getPreferences(Context.MODE_PRIVATE)
        val balance = sharedPref.getInt(moneyID, initMoney)

        earnMoney(balance)      // starting money
        disableAdButton()       // disable ad button, start preparing ads
        reset()                 // reset to 'home' screen
    }

    // gets the scrollview
    internal fun getScrollView(): HorizontalScrollView {
        return scrollView
    }

    // gets the progress bar
    internal fun getProgressBar(): ProgressBar {
        return progressBar
    }

    // remove child inside scroll, repaint it
    internal fun resetScrollView() {
        scrollView.removeAllViews()
        scrollView.invalidate()
    }

    // go back to 'home' screen
    private fun reset() {
        resetScrollView()
        showLogo()
        disableResetButton()
        disableShareButton()
        hideProgress()
    }

    // shows a toast
    internal fun showToast(text: String) {
        Toast
            .makeText(applicationContext, text, Toast.LENGTH_LONG)
            .show()
    }

    // shows a snackbar
    internal fun showSnackbar(
        view: View,
        title: String,
        duration: Int?,
        actionTitle: String?,
        listener: View.OnClickListener?
    ) {
        Snackbar
            .make(view, title, duration ?: Snackbar.LENGTH_LONG)
            .setAction(actionTitle, listener)
            .show()
    }

    // shows a notification
    internal fun showNotification(
        title: String?,
        body: String?,
        actionTitle: String?,
        pIntent: PendingIntent?
    ) {
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
            .addAction(
                NotificationCompat.Action(
                    R.drawable.example_notification_icon,
                    actionTitle,
                    pIntent
                )
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(body)
            )
            .build()
        nManager.notify(generateID(), notification)
    }

    // generate a random id based on the time
    private fun generateID(): Int {
        return (Date().time / 1000).toInt() % Int.MAX_VALUE
    }

    // enable the share button
    internal fun enableShareButton(title: String, imageView: ImageView) {
        shareButton.setOnClickListener {
            if (money < shareCost) {
                // insufficient funds!
                showSnackbar(it,
                    "Sharing costs $shareCost coins. Insufficient funds.",
                    null,
                    "LEARN MORE",
                    View.OnClickListener {
                        AlertDialog.Builder(this)
                            .setTitle("How to Earn Coins")
                            .setMessage("You can earn coins by watching ads! Note that you will only earn your reward if you watch the entire ad.")
                            .show()
                    }
                )
                return@setOnClickListener
            }
            // enough money!
            Selector.deselectImage(imageView, WeakReference(this))
            val bitmap = imageView.drawToBitmap(Bitmap.Config.ARGB_8888)
            Selector.selectImage(title, imageView, WeakReference(this))
            spendMoney(shareCost)
            showToast("Shared! New balance is $money.")
            Sharer(WeakReference(this)).shareSlide(title, bitmap)
        }
        shareButton.isEnabled = true
    }

    // disable share button
    internal fun disableShareButton() {
        shareButton.isEnabled = false
    }

    // show progress bars
    internal fun showProgress() {
        progressSpinner.visibility = ProgressBar.VISIBLE
        progressBar.visibility = ProgressBar.VISIBLE
    }

    // hide progress bars
    internal fun hideProgress() {
        progressSpinner.visibility = ProgressBar.INVISIBLE
        progressBar.visibility = ProgressBar.INVISIBLE
    }

    // show st andrews logo
    private fun showLogo() {
        logo.visibility = ImageView.VISIBLE
    }

    // hide st andrews logo
    internal fun hideLogo() {
        logo.visibility = ImageView.INVISIBLE
    }

    // enable reset button
    internal fun enableResetButton() {
        resetButton.isEnabled = true
    }

    // disable reset button
    private fun disableResetButton() {
        resetButton.isEnabled = false
    }

    // enable ad button
    internal fun enableAdButton(rewardedAd: RewardedAd, rewardCallback: RewardedAdCallback) {
        watchButton.isEnabled = true
        watchButton.setOnClickListener {
            rewardedAd.show(null, rewardCallback)
            disableAdButton()
        }
    }

    // disable ad button
    private fun disableAdButton() {
        watchButton.isEnabled = false
        AdHandler(WeakReference(this)).getNewAd() // starts loading next ad, enabling button on load
    }

    // money update
    private fun moneyUpdate(newMoney: Int) {
        money = newMoney
        moneyText.text = newMoney.toString()
        sharedPref.edit().putInt(moneyID, money).commit()
    }

    // spend money
    private fun spendMoney(spent: Int) {
        moneyUpdate(money - spent)
    }

    // earn money
    internal fun earnMoney(reward: Int) {
        moneyUpdate(money + reward)
    }

}
