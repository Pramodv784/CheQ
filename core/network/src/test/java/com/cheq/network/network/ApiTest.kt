package com.cheq.network.network

import com.cheq.network.response.NetworkResponse
import com.cheq.network.response.NetworkResponseCallAdapterFactory
import com.cheq.network.response.toResult
import com.google.gson.GsonBuilder
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


/**
 * Created by Akash Khatkale on 29th May, 2023.
 * akash.k@cheq.one
 */
class ApiTest {

    interface FakeInterface {
        @GET("/fakeData")
        fun getFakeResponse(): Call<NetworkResponse<FakeDto>>
    }

    private val mockServer = MockWebServer()
    private lateinit var retrofit: Retrofit
    private lateinit var service: FakeInterface

    @Before
    fun setUp() {
        val gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder()
            .baseUrl(mockServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(NetworkResponseCallAdapterFactory())
            .build()
        service = retrofit.create(FakeInterface::class.java)
    }

    @Test
    fun `When response body is empty, return null`() {
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(200)
        mockedResponse.setBody("{}")
        mockServer.enqueue(mockedResponse)

        val res = service.getFakeResponse().execute()
        assertEquals(res.isSuccessful, true)
        assertEquals(res.code(), 200)
        assertEquals(res.errorBody(), null)
        assertEquals(
            res.body(),
            NetworkResponse.Success(
                statusCode = 200,
                headers = mapOf("content-length" to listOf("2")),
                value = null
            )
        )
    }

    @Test
    fun `When response is 404, return http error`() {
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(404)
        mockedResponse.setBody("{data:\"\",error:\"\",httpStatus:404,message:\"\",status:false,traceId:\"\"}")
        mockServer.enqueue(mockedResponse)

        val res = service.getFakeResponse().execute()
        assertEquals(
            res.body(),
            NetworkResponse.Failure.Http(
                statusCode = 404,
                headers = mapOf("content-length" to listOf("68")),
                errorBody = "{data:\"\",error:\"\",httpStatus:404,message:\"\",status:false,traceId:\"\"}"
            )
        )
    }

    @Test
    fun `When response is 421, return http error`() {
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(200)
        mockedResponse.setBody("{data:\"\",error:\"\",httpStatus:421,message:\"\",status:false,traceId:\"\"}")
        mockServer.enqueue(mockedResponse)


        val res = service.getFakeResponse().execute().body()
        assertTrue(res?.toResult()?.isFailure ?: false)
        assertEquals(res?.toResult()?.getOrNull(), null)
        res?.let {
            assertEquals(
                it::class.java,
                NetworkResponse.Failure.Unknown::class.java
            )
        }
    }

    @Test
    fun `When response is 200, return success`() {
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(200)
        mockedResponse.setBody("{data:\"{name:\\\"Akash\\\",email:\\\"akash.k@cheq.one\\\",mobile:\\\"9999999999\\\"}\",error:\"\",httpStatus:200,message:\"\",status:false,traceId:\"\"}")
        mockServer.enqueue(mockedResponse)


        val res = service.getFakeResponse().execute()
        assertEquals(
            res.body(),
            NetworkResponse.Success(
                statusCode = 200,
                headers = mapOf("content-length" to listOf("133")),
                value = FakeDto(
                    name = "Akash",
                    email = "akash.k@cheq.one",
                    mobile = "9999999999"
                )
            )
        )
    }

    @Test
    fun `When no internet connection, return io exception`() {
        val mockedResponse = MockResponse()
        mockedResponse.setBody("{data:\"{name:\\\"Akash\\\",email:\\\"akash.k@cheq.one\\\",mobile:\\\"9999999999\\\"}\",error:\"\",httpStatus:200,message:\"\",status:false,traceId:\"\"}")
        mockedResponse.setSocketPolicy(
            SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY
        )
        mockServer.enqueue(mockedResponse)

        val res = service.getFakeResponse().execute()
        assertTrue(res.body()?.toResult()?.isFailure ?: false)
        assertEquals(res.body()?.toResult()?.getOrNull(), null)
        res.body()?.let {
            assertEquals(
                it::class.java,
                NetworkResponse.Failure.IO::class.java
            )
        }
    }

}