package com.cheq.network.di

import android.content.Context
import com.cheq.config.AppConfig
import com.cheq.config.CheqCrashlytics
import com.cheq.encryption.EncryptionPass
import com.cheq.network.api.NetworkApi
import com.cheq.network.interceptors.AuthInterceptor
import com.cheq.network.interceptors.DecryptionInterceptor
import com.cheq.network.interceptors.EncryptionInterceptor
import com.cheq.network.interceptors.HeaderInterceptor
import com.cheq.network.interceptors.TokenAuthenticator
import com.cheq.network.response.NetworkResponseCallAdapterFactory
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideNetworkApi(
        @Named("WithoutTokenRetrofit") client: Retrofit
    ) = client.create(NetworkApi::class.java)

    @Singleton
    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        config: AppConfig
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .addCallAdapterFactory(NetworkResponseCallAdapterFactory())
        .baseUrl(config.apiEndPoint())
        .build()

    @Singleton
    @Provides
    @Named("WithoutTokenRetrofit")
    fun provideRetrofitWithoutToken(
        @Named("WithoutToken") client: OkHttpClient,
        config: AppConfig
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .addCallAdapterFactory(NetworkResponseCallAdapterFactory())
        .baseUrl(config.apiEndPoint())
        .build()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)
        .setLevel(HttpLoggingInterceptor.Level.HEADERS)

    @Singleton
    @Provides
    fun provideEncryptionInterceptor(
        appConfig: AppConfig,
        cheqCrashlytics: CheqCrashlytics,
        encryptionPass: EncryptionPass
    ): EncryptionInterceptor = EncryptionInterceptor(appConfig, cheqCrashlytics, encryptionPass)

    @Singleton
    @Provides
    fun provideDecryptionInterceptor(
        encryptionPass: EncryptionPass
    ): DecryptionInterceptor =
        DecryptionInterceptor(encryptionPass)

    @Singleton
    @Provides
    fun provideChuckInterceptor(@ApplicationContext context: Context): ChuckerInterceptor =
        ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(true)
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: HeaderInterceptor,
        authInterceptor: AuthInterceptor,
        encryptionInterceptor: EncryptionInterceptor,
        decryptionInterceptor: DecryptionInterceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .authenticator(tokenAuthenticator)
            .addInterceptor(headerInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(encryptionInterceptor)
            .addInterceptor(decryptionInterceptor)
            .addInterceptor(chuckerInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    @Named("WithoutToken")
    fun provideOkHttpClientWithoutToken(
        loggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: HeaderInterceptor,
        authInterceptor: AuthInterceptor,
        encryptionInterceptor: EncryptionInterceptor,
        decryptionInterceptor: DecryptionInterceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(encryptionInterceptor)
            .addInterceptor(decryptionInterceptor)
            .addInterceptor(chuckerInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
}