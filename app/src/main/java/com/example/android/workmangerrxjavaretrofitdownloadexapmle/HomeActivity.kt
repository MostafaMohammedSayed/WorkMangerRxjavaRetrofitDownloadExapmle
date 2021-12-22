package com.example.android.workmangerrxjavaretrofitdownloadexapmle

import android.app.Dialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.android.workmangerrxjavaretrofitdownloadexapmle.work.DownloadWorker
import com.example.android.workmangerrxjavaretrofitdownloadexapmle.work.DownloadWorker.Companion.Progress

class HomeActivity : AppCompatActivity() {
    private lateinit var downloadProgress: ProgressDialog
    private val progressLiveData = MutableLiveData<Long>()
    companion object {
        const val PROGRESS_BAR_TYPE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        downloadProgress = ProgressDialog(this)
        setUpViews()
    }

    private fun setUpViews() {
        val downloadButton = findViewById<Button>(R.id.downloadButton)
        downloadButton.setOnClickListener {
            startDownloadWork()
            showDialog(PROGRESS_BAR_TYPE)
        }
    }

    private fun startDownloadWork() {
        val workManager = WorkManager.getInstance(this)
        val downloadWorker = OneTimeWorkRequestBuilder<DownloadWorker>().build()

        workManager.enqueue(downloadWorker)

        workManager.getWorkInfoByIdLiveData(downloadWorker.id)
            .observe(this, Observer { workInfo->
                val status = workInfo.state
                if (status == WorkInfo.State.RUNNING){
                    Toast.makeText(this,"running", Toast.LENGTH_SHORT).show()
                    val progress = workInfo.progress
                    val value = progress.getLong(Progress,0)
                    Log.i("HomeActivityProgValue",value.toString())
                    progressLiveData.value = value
                }
            })
    }

    override fun onCreateDialog(id: Int): Dialog? {
        return if (id == PROGRESS_BAR_TYPE) {
            downloadProgress.apply {
                setMessage("Downloading file. Please wait...")
                isIndeterminate = false
                progressLiveData.observe(this@HomeActivity, Observer {mProgress->
                    progress = mProgress.toInt()
                })
                Log.i("HomeActivityProgress", progress.toString())
                max = 100
                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                setCancelable(true)
            }
            downloadProgress
        } else {
            null
        }
    }
}