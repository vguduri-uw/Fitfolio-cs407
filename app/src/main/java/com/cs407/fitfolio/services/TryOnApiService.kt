package com.cs407.fitfolio.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import com.cs407.fitfolio.BuildConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

// ===============================================================================================
//                      DATA CLASSES (models used for requests and responses)
// ===============================================================================================

// data being sent to the api
data class FashnRunRequest(
    val model_name: String,            // name of the model we are making the request to (e.g., background-remove)
    val inputs: Map<String, String>    // list of all the image urls we want to upload
)

// response returned right after /run (only returns a prediction id for status polling)
data class FashnRunResponse(
    val id: String,
    val error: FashnApiError? = null
)

// response when checking status (where we collect the final output images)
data class FashnStatusResponse(
    val id: String,
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

    @GET("v1/status/{prediction_id}")
    suspend fun getPredictionStatus(
        @Path("prediction_id") predictionId: String
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