package com.vereskul.tc51versusxml.data.network

import android.util.Base64
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime


object ApiFactory {

    private var gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()
    //Основной УРЛ сайта
    const val BASE_URL = "http://biz-portal.pp.ua:59843/test/hs/"

    var apiService: ApiService = Retrofit.Builder()
        //для конвертации Гсоном
        .addConverterFactory(GsonConverterFactory.create(gson))
        //Подключаем РХджава
//            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .build()
        .create(ApiService::class.java)

    fun register(login: String, password: String) {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader(
                        "Authorization",
                        "Basic "
                                + Base64.encodeToString(
                            "$login:$password".toByteArray(),
                            Base64.NO_WRAP
                        )
                    )
                    .build()
                chain.proceed(request)
            }.build()
        //Строим ретрофит
        //создаем сервис
        apiService =  Retrofit.Builder()
            //для конвертации Гсоном
            .addConverterFactory(GsonConverterFactory.create(gson))
            //Подключаем РХджава
//            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}