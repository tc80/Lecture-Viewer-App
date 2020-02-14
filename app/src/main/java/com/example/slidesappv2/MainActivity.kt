package com.example.slidesappv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).setOnClickListener{
            Selector(WeakReference(this), it).selectPDF()
        }
        //example3()
//        Downloader(WeakReference(this)).execute()
//        requestWebsiteText(studRes)
        //Downloader().requestWebsiteText("https://studres.cs.st-andrews.ac.uk", this)
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

}
