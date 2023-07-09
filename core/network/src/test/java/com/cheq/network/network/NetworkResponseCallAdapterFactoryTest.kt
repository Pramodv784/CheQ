package com.cheq.network.network

import com.cheq.network.models.CheqResponse
import com.cheq.network.response.NetworkResponse
import com.cheq.network.response.NetworkResponseCallAdapterFactory
import com.google.gson.reflect.TypeToken
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Akash Khatkale on 29th May, 2023.
 * akash.k@cheq.one
 */
class NetworkResponseCallAdapterFactoryTest {

    private val annotations = arrayOf<Annotation>()
    private val mockServer = MockWebServer()
    private val factory = NetworkResponseCallAdapterFactory()
    private lateinit var retrofit: Retrofit

    @Before
    fun setUp() {
        retrofit = Retrofit.Builder()
            .baseUrl(mockServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseCallAdapterFactory())
            .build()
    }

    @Test
    fun `If the type is valid, then return CheqResponse`() {
        val bodyClass = object : TypeToken<Call<NetworkResponse<FakeDto>>>() {}.type
        assertEquals(
            CheqResponse::class.java,
            factory.get(bodyClass, annotations, retrofit)?.responseType(),
        )
    }

    @Test
    fun `If outer class is not Call, then return null`() {
        val type1 = object : TypeToken<FakeDto>() {}.type
        val type2 = object : TypeToken<Int>() {}.type
        val type3 = object : TypeToken<String>() {}.type

        assertEquals(
            null,
            factory.get(type1, annotations, retrofit)?.responseType()
        )
        assertEquals(
            null,
            factory.get(type2, annotations, retrofit)?.responseType()
        )
        assertEquals(
            null,
            factory.get(type3, annotations, retrofit)?.responseType()
        )
    }

    @Test
    fun `If inner class is not NetworkResponse, then return null`() {
        val type1 = object : TypeToken<Call<FakeDto>>() {}.type
        val type2 = object : TypeToken<Call<Int>>() {}.type
        val type3 = object : TypeToken<Call<String>>() {}.type

        assertEquals(
            null,
            factory.get(type1, annotations, retrofit)?.responseType()
        )
        assertEquals(
            null,
            factory.get(type2, annotations, retrofit)?.responseType()
        )
        assertEquals(
            null,
            factory.get(type3, annotations, retrofit)?.responseType()
        )
    }

}