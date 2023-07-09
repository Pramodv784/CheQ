package com.cheq.network.response

import com.cheq.network.models.CheqResponse
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

/**
 * Created by Akash Khatkale on 28th May, 2023.
 * akash.k@cheq.one
 */
class NetworkResponseCall(
    private val delegate: Call<Any?>,
    private val type: Type
) : Call<NetworkResponse<Any?>> {

    override fun execute(): Response<NetworkResponse<Any?>> {
        val result = try {
            delegate.execute().toResult()
        } catch (io: IOException) {
            NetworkResponse.Failure.IO(io)
        } catch (throwable: Throwable) {
            NetworkResponse.Failure.Unknown(throwable)
        }
        return Response.success(result)
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    override fun clone(): Call<NetworkResponse<Any?>> = NetworkResponseCall(delegate.clone(), type)

    override fun enqueue(callback: Callback<NetworkResponse<Any?>>) {
        delegate.enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                val result = response.toResult()
                callback.onResponse(this@NetworkResponseCall, Response.success(result))
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {
                val failure = when (t) {
                    is IOException -> NetworkResponse.Failure.IO(t)
                    else -> NetworkResponse.Failure.Unknown(t)
                }

                callback.onResponse(
                    this@NetworkResponseCall,
                    Response.success(failure)
                )
            }
        })
    }

    private fun Response<Any?>.toResult() = if (isSuccessful) {
        if (isSuccessful && code() in 200 until 300) {
            if (body() != null) {
                try {
                    val cheqResponse = (body() as CheqResponse)
                    if (cheqResponse.httpStatus == 421) {
                        NetworkResponse.Failure.Unknown(Exception("Something went wrong"))
                    } else {
                        val mappedResponse = toModel(
                            cheqResponse.data,
                            Class.forName((type as Class<*>).name)
                        )
                        NetworkResponse.Success(code(), headers().toMultimap(), mappedResponse)
                    }
                } catch (e: Exception) {
                    NetworkResponse.Failure.Unknown(Exception("Something went wrong"))
                }
            } else {
                NetworkResponse.Failure.Unknown(Exception("Something went wrong"))
            }
        } else {
            NetworkResponse.Failure.Http(
                code(),
                headers().toMultimap(),
                errorBody()?.string().orEmpty()
            )
        }
    } else {
        NetworkResponse.Failure.Http(
            code(),
            headers().toMultimap(),
            errorBody()?.string().orEmpty()
        )
    }
}