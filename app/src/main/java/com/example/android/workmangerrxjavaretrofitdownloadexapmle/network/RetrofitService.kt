package com.example.android.workmangerrxjavaretrofitdownloadexapmle.network

import com.example.android.workmangerrxjavaretrofitdownloadexapmle.network.NetworkConstants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

class RetrofitService {
    companion object {
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        val downloadApi: DownloadService by lazy {
                    retrofit.create(DownloadService::class.java)
                }
    }
}