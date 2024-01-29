package com.akhil.demotask.service

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.akhil.demotask.DemoApp
import com.akhil.demotask.R
import com.akhil.demotask.model.FileUploadResponse
import com.akhil.demotask.util.ApiException
import com.akhil.demotask.util.Constant
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException


abstract class SafeApiRequest {

    suspend fun <T : Any> apiRequest(call: () -> Call<T>): T {
        val response = call.invoke().execute()
        when {
            response.isSuccessful -> {
                return response.body()!!
            }

            response.code() == HttpURLConnection.HTTP_UNAUTHORIZED -> {
                val intent = Intent(Constant.HTTP_UNAUTHORIZED)
                LocalBroadcastManager.getInstance(DemoApp.getInstance()).sendBroadcast(intent)
                val error = response.errorBody()?.string()
                val message = StringBuilder()
                error?.let {
                    try {
                        message.append(JSONObject(it).getString("Message"))
                    } catch (e: JSONException) {
                    }
                    message.append("\n")
                }
                message.append("Error Code: ${response.code()}")
                throw ApiException(message.toString())
            }

            response.code() == HttpURLConnection.HTTP_CREATED -> {
                val intent = Intent(Constant.HTTP_CREATED)
                LocalBroadcastManager.getInstance(DemoApp.getInstance()).sendBroadcast(intent)
                val error = response.errorBody()?.string()
                val message = StringBuilder()
                error?.let {
                    try {
                        message.append(JSONObject(it).getString("Message"))
                    } catch (e: JSONException) {
                    }
                    message.append("\n")
                }
                message.append("Error Code: ${response.code()}")
                throw ApiException(message.toString())
            }

            response.code() == HttpURLConnection.HTTP_BAD_REQUEST -> {
                val intent = Intent(Constant.HTTP_BAD_REQUEST)
                LocalBroadcastManager.getInstance(DemoApp.getInstance()).sendBroadcast(intent)

                val error = response.errorBody()?.string()
                val message = StringBuilder()

                error?.let {
                    try {
                        val jsonObject = JSONObject(it)
                        val statusMessage = jsonObject.getString("statusmessage")
                        message.append(statusMessage)
                    } catch (e: JSONException) {
                        // Handle JSON parsing error
                        Log.d("JSONException", "apiRequest: " + e.message)
                    } catch (e: ApiException) {
                        // Handle JSON parsing error
                        Log.d("ApiException", "apiRequest: " + e.message)
                    } catch (e: Throwable) {
                        // Handle JSON parsing error
                        Log.d("ApiException", "apiRequest: " + e.message)
                    }
                    message.append("\n")
                }

                message.append("Error Code: ${response.code()}")
                throw ApiException(message.toString())

            }

            response.code() == HttpURLConnection.HTTP_NO_CONTENT -> {
                val intent = Intent(Constant.HTTP_BAD_REQUEST)
                LocalBroadcastManager.getInstance(DemoApp.getInstance()).sendBroadcast(intent)

                val error = response.errorBody()?.string()
                val message = StringBuilder()

                error?.let {
                    try {
                        val jsonObject = JSONObject(it)
                        val statusMessage = jsonObject.getString("statusmessage")
                        message.append(statusMessage)
                    } catch (e: JSONException) {
                        // Handle JSON parsing error
                        Log.d("JSONException", "apiRequest: " + e.message)
                    } catch (e: ApiException) {
                        message.append("Error Code: ${response.code()}")

                        // Handle JSON parsing error
                        Log.d("ApiException", "apiRequest: " + e.message)
                    } catch (e: Throwable) {
                        // Handle JSON parsing error
                        Log.d("ApiException", "apiRequest: " + e.message)
                    } catch (e: SocketTimeoutException) {
                        // Handle JSON parsing error
                        Log.d("SocketTimeoutException", "apiRequest: " + e.message)
                    } catch (e: UnknownHostException) {
                        // Handle JSON parsing error
                        Log.d("UnknownHostException", "apiRequest: " + e.message)
                    }
                    message.append("\n")
                }

                throw ApiException(message.toString())

            }

            response.code() == HttpURLConnection.HTTP_NOT_FOUND -> {
                val intent = Intent(Constant.HTTP_NOT_FOUND)
                LocalBroadcastManager.getInstance(DemoApp.getInstance()).sendBroadcast(intent)

                val error = response.errorBody()?.string()
                val message = StringBuilder()


                error?.let {
                    try {
                        val jsonObject = JSONObject(it)
                        val statusMessage = jsonObject.getString("statusmessage")
                        message.append(statusMessage)
                    } catch (e: JSONException) {
                        // Handle JSON parsing error
                        Log.d("JSONException", "apiRequest: " + e.message)
                    } catch (e: ApiException) {
                        message.append("Error Code: ${response.code()}")

                        // Handle JSON parsing error
                        Log.d("ApiException", "apiRequest: " + e.message)
                    } catch (e: Throwable) {
                        // Handle JSON parsing error
                        Log.d("ApiException", "apiRequest: " + e.message)
                    } catch (e: SocketTimeoutException) {
                        // Handle JSON parsing error
                        Log.d("SocketTimeoutException", "apiRequest: " + e.message)
                    } catch (e: UnknownHostException) {
                        // Handle JSON parsing error
                        Log.d("UnknownHostException", "apiRequest: " + e.message)
                    }
                    message.append("\n")
                }

                throw ApiException(message.toString())

            }

            else -> {
                val error = response.errorBody()?.string()
                val headerMessage = response.headers()
                val message = StringBuilder()
                try {
                    if (message != null) {
                        if (message.isEmpty()) {
                            message.append(
                                DemoApp.getInstance().getString(R.string.server_not_responding)
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                throw ApiException(message.toString())
                throw SocketTimeoutException(message.toString())
                throw IOException(message.toString())
            }
        }
    }

}