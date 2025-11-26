package com.cs407.fitfolio.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import com.cs407.fitfolio.BuildConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

// ===============================================================================================
//        USE CASE (background removal example, update this file as needed for your needs)
// ===============================================================================================

//val request = FashnRunRequest(
//    model_name = "background-removal",
//    inputs = listOf(imageUrl)
//)
//
//val runResult = RetrofitInstance.fashnApi.runModel(request)
//val predictionId = runResult.prediction_id
//
//var status: FashnStatusResponse
//do {
//    delay(1500)
//    status = RetrofitInstance.fashnApi.getPredictionStatus(predictionId)
//} while (status.status != "succeeded" && status.status != "failed")
//
//if (status.status == "succeeded") {
//    val outputUrl = status.output.firstOrNull()
//}

// ===============================================================================================
//                      DATA CLASSES (models used for requests and responses)
// ===============================================================================================

// data being sent to the api
data class FashnRunRequest(
    val model_name: String,     // name of the model we are making the request to (e.g., background-remove)
    val inputs: List<String>    // list of all the image urls we want to upload
)

// response returned right after /run (only returns a prediction id for status polling)
data class FashnRunResponse(
    val prediction_id: String
)

// response when checking status (where we collect the final output images)
data class FashnStatusResponse(
    val prediction_id: String,
    val status: String,
    val output: List<String?>,
    val error: FashnApiError? = null
)

// format for the error object returned by the API
data class FashnApiError(
    val name: String?,
    val message: String?
)

// ===============================================================================================
//                                   RETROFIT SERVICE INTERFACE
// ===============================================================================================

interface TryOnApiService {
    @POST(value = "v1/run")
    suspend fun runModel(
        @Body request: FashnRunRequest
    ): FashnRunResponse

    @GET("v1/status")
    suspend fun getPredictionStatus(
        @Query("prediction_id") predictionId: String
    ): FashnStatusResponse
}

// ===============================================================================================
//                                       RETROFIT INSTANCE
// ===============================================================================================

object RetrofitInstance {
    private const val BASE_URL = "https://api.fashn.ai/"
    private const val API_KEY = BuildConfig.FASHN_API_KEY

    private val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $API_KEY")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val fashnApi: TryOnApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TryOnApiService::class.java)
    }
}