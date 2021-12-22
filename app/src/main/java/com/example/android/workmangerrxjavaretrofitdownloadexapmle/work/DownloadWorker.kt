package com.example.android.workmangerrxjavaretrofitdownloadexapmle.work

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.android.workmangerrxjavaretrofitdownloadexapmle.network.RetrofitService.Companion.downloadApi
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class DownloadWorker(context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    var totalSize: Long = 543879L
    var fileSizeDownloaded = 0L
    private val fileName = "Sample Video"

    override fun doWork(): Result {
        val pathWhereYouWantToSaveFile =
            applicationContext.filesDir.absoluteFile.toString() + fileName
        downloadApi.downloadFile()
            .flatMap { responseBody ->
                val input = responseBody.byteStream()
                val fos = FileOutputStream(pathWhereYouWantToSaveFile)
                fos.use { output ->
                    val buffer = ByteArray(4 * 1024) // or other buffer size
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        fileSizeDownloaded += read.toLong()
                        Log.i("WorkerDownloaded",fileSizeDownloaded.toString())
                        val percentDownloaded = calculateProgress(totalSize,fileSizeDownloaded)
                        Log.i("WorkerPercentDownloaded",percentDownloaded.toString())
                        val update = workDataOf(Progress to percentDownloaded)
                        setProgressAsync(update)
                    }
                    output.flush()
                }
                return@flatMap Observable.just(responseBody)
            }.subscribe(object: Observer<ResponseBody>{
                override fun onSubscribe(d: Disposable) {
                    Log.i("Worker","Subscribed")
                }

                override fun onNext(responseBody: ResponseBody) {
                    Log.i("WorkerTotalSize",totalSize.toString())
                    Log.i("WorkerDownloaded",fileSizeDownloaded.toString())
                }

                override fun onError(e: Throwable) {
                    Log.e("Worker",e.message,e)
                }

                override fun onComplete() {
                    Log.i("Worker","Completed")
                }

            })
        return Result.success()
    }
    private fun calculateProgress(totalSize: Long, downloadSize: Long): Long {
        return ((downloadSize / totalSize) * 100L)
    }

    companion object {
        const val Progress = "Progress"
    }
}

