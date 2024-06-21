package com.catches.securities_message.retrofit.config

import com.catches.securities_message.properties.HttpProperty
import com.catches.securities_message.retrofit.api.SecuritiesApiInterface
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
class SecuritiesApiRetrofitConfig(
    private val httpProperty: HttpProperty
) {

    @Bean
    @Qualifier("securitiesApiRetrofit")
    fun securitiesApiRetrofit(): SecuritiesApiInterface {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

        val connectionPool = ConnectionPool(10, 5, TimeUnit.MINUTES)
        val client =
            OkHttpClient.Builder().connectionPool(connectionPool).callTimeout(Duration.ofMinutes(10)).writeTimeout(
                Duration.ofMinutes(10),
            )
                .connectTimeout(Duration.ofMinutes(10)).readTimeout(Duration.ofMinutes(10)).addInterceptor(interceptor)
                .addInterceptor(
                    (
                            { chain ->
                                val request: Request =
                                    chain.request().newBuilder()
                                        .addHeader("Content-Type", "application/json")
                                        .build()
                                chain.proceed(request)
                            }
                            ),
                ).build()
        return Retrofit.Builder().baseUrl(
            HttpUrl.Builder().scheme(httpProperty.securitiesApi.scheme).host(httpProperty.securitiesApi.host).port(8080).build(),
        ).client(client).addConverterFactory(
            Json {
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            }.asConverterFactory("application/json".toMediaTypeOrNull()!!),
        ).build().create(SecuritiesApiInterface::class.java)
    }
}