package com.fivemintub.asynctaskdemo

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var defaultBtn: Button
    private lateinit var threadBtn: Button
    private lateinit var threadDelayedBtn: Button
    private lateinit var asyncTaskBtn: Button
    private val asyncTaskTest: AsyncTask<String, Int, String> by lazy {
        @SuppressLint("StaticFieldLeak")
        object : AsyncTask<String, Int, String>() {
            override fun doInBackground(vararg params: String?): String {
                for (index in 0..7) {
                    publishProgress(index)
                    SystemClock.sleep(1000)
                }
                return params.first() ?: ""
            }

            override fun onProgressUpdate(vararg progress: Int?) {
                Toast.makeText(this@MainActivity, progress.first().toString(), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onPostExecute(result: String?) {
                Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        defaultBtn = findViewById(R.id.default_btn)
        threadBtn = findViewById(R.id.thread_btn)
        threadDelayedBtn = findViewById(R.id.thread_delayed_btn)
        asyncTaskBtn = findViewById(R.id.async_task_btn)

        defaultBtn.setOnClickListener {
            SystemClock.sleep(7000)
            Toast.makeText(this@MainActivity, "Default", Toast.LENGTH_SHORT).show()
        }
        threadBtn.setOnClickListener { v ->
            Thread {
                SystemClock.sleep(7000)
                v.post {
                    Toast.makeText(this@MainActivity, "Thread", Toast.LENGTH_SHORT).show()
                }
            }.start()
        }
        threadDelayedBtn.setOnClickListener { v ->
            Thread {
                v.postDelayed( {
                    Toast.makeText(this@MainActivity, "Thread", Toast.LENGTH_SHORT).show()
                }, 7000)
            }.start()
        }
        asyncTaskBtn.setOnClickListener {
            asyncTaskTest.execute("Async Task")
        }
    }
}