package com.example.android.workmangerrxjavaretrofitdownloadexapmle.network

import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

interface DownloadService {
    @Streaming
    @GET("/download/sample-mp4-video-file-download-for-testing/?wpdmdl=2727&refresh=617329fb6e8b31634937339")
    fun downloadFile(): Observable<ResponseBody>
}