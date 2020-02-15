package com.example.slidesappv2

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar
import android.net.Uri
import java.util.*
import android.widget.*
import android.widget.LinearLayout
import android.widget.ScrollView




class MainActivity : AppCompatActivity() {

    private val studRes = "https://studres.cs.st-andrews.ac.uk/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//       val scrollPane = findViewById<HorizontalScrollView>(R.id.scroll)
//
//        val options = LinearLayout(this)
//
//        for (i in 0..19) {
////            val b = Button(this)
////            b.text = "Button $i"
////            b.setOnClickListener {
////                showToast(i.toString())
////            }
//            val imageView = ImageView(this)
//            imageView.id = i
//            imageView.setPadding(10, 10, 10, 10)
//            imageView.adjustViewBounds = true
//            imageView.setBackgroundColor(Color.RED)
//            imageView.setImageResource(R.drawable.example_notification_icon)
////            imageView.setImageBitmap(
////                BitmapFactory.decodeResource(
////                    resources, R.drawable.example_notification_icon
////                )
////            )
//            //imageView.scaleType = ImageView.ScaleType.FIT_XY
//            imageView.setOnClickListener {
//                showToast(i.toString())
//            }
//            options.addView(imageView)
//        }
//
//        scrollPane.addView(options)

//        val myScrollView = findViewById<HorizontalScrollView>(R.id.scroll)
//        val myLinearLayout = findViewById<LinearLayout>(R.id.linear)
//
//        for (i in 0..19) {
//            val b = Button(this)
//            b.text = "Button $i"
//            myLinearLayout.addView(b)
//        }
//
//        myScrollView.addView(myLinearLayout)

       // myLinearLayout.addView(options)

//        val layout = findViewById<LinearLayout>(R.id.linear)
//        layout.orientation = LinearLayout.HORIZONTAL
//        val a = LinearLayout(this)
//        a.orientation = LinearLayout.HORIZONTAL
//        for (i in 0..5) {
//            val imageView = ImageView(this)
//            imageView.id = i
//            imageView.setPadding(2, 2, 2, 2)
//            imageView.setImageBitmap(
//                BitmapFactory.decodeResource(
//                    resources, R.drawable.example_notification_icon
//                )
//            )
//            imageView.scaleType = ScaleType.FIT_XY
//            a.addView(imageView)
//        }
//        layout.addView(a)
//        val scroll = findViewById<HorizontalScrollView>(R.id.scroll)
//        scroll.addView(a)
//        layout.setBackgroundColor(Color.RED)
//
//        //layout.visibility = View.VISIBLE
//
//        showToast(layout.width.toString())
//
//        layout.setOnClickListener {
////            exitProcess(1)
//        }

//        val list = findViewById<RecyclerView>(R.id.my_recycler)
//        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        list.adapter = RecyclerView.Adapter<RecyclerView.ViewHolder>()


        val btn = findViewById<Button>(R.id.button2)
        btn.setOnClickListener {
           example1()
        }
        findViewById<Button>(R.id.button).setOnClickListener{

            // button to view all downloads
            // Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
            // startActivity(intent)


//            val pickContactIntent = Intent(Intent.ACTION_PICK).apply {
//                // Show user only the contacts that include phone numbers.
//                setDataAndType(
//                    Uri.parse("content://downloads/all_downloads/37"),
//                    "application/pdf"
//                )
//            }
//
//            startActivity(pickContactIntent)


            Selector(this, it).selectModule(studRes)
          // Downloader3(WeakReference(this), it).downloadPDF("https://studres.cs.st-andrews.ac.uk/CS3301/Lectures/L03-large-scale-design.pdf", "hello")
        }
    }

    private fun example1() {
        // hello world toast
        showToast("hello world")
    }

    private fun example2() {
        // snackbar without action
        findViewById<Button>(R.id.button).setOnClickListener{
            view -> run {
                showSnackbar(view, "my snackbar", null, null, null)
            }
        }
    }

    private fun example3() {
        // snackbar with action, also "it"
        findViewById<Button>(R.id.button).setOnClickListener{
            showSnackbar(it, "my snackbar with action",null, "display my toast", View.OnClickListener {
                showToast("you displayed me")
            })
        }
    }

    private fun example4() {
        findViewById<Button>(R.id.button).setOnClickListener{
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

    private fun generateID(): Int {
        return (Date().time / 1000).toInt() % Int.MAX_VALUE
    }

}
