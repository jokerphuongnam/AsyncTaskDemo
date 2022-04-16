package com.fivemintub.asynctaskdemo

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MainActivity : AppCompatActivity() {
    private lateinit var defaultBtn: Button
    private lateinit var threadBtn: Button
    private lateinit var threadDelayedBtn: Button
    private lateinit var asyncTaskBtn: Button
    private lateinit var suspendFunBtn: Button
    private lateinit var flowFunBtn: Button

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

    private suspend fun suspendFunTest(): String = suspendCancellableCoroutine { cont ->
        SystemClock.sleep(7000)
        cont.resume("Suspend fun")
    }

    private fun flowFunTest(): Flow<Int> = channelFlow {
        for (index in 0..7) {
            SystemClock.sleep(1000)
            trySend(index)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        defaultBtn = findViewById(R.id.default_btn)
        threadBtn = findViewById(R.id.thread_btn)
        threadDelayedBtn = findViewById(R.id.thread_delayed_btn)
        asyncTaskBtn = findViewById(R.id.async_task_btn)
        suspendFunBtn = findViewById(R.id.suspend_fun_btn)
        flowFunBtn = findViewById(R.id.flow_fun_btn)

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
                v.postDelayed({
                    Toast.makeText(this@MainActivity, "Thread", Toast.LENGTH_SHORT).show()
                }, 7000)
            }.start()
        }
        asyncTaskBtn.setOnClickListener {
            asyncTaskTest.execute("Async Task")
        }
        suspendFunBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val text = suspendFunTest()
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                }
            }
        }
        flowFunBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                flowFunTest().collect { index ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, index.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                val text = suspendFunTest()
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}