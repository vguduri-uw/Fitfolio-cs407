package com.cs407.fitfolio.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import com.cs407.fitfolio.BuildConfig

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
    
}

// ===============================================================================================
//                                       RETROFIT INSTANCE
// ===============================================================================================

object TryOnApiClient {
    private const val BASE_URL = "https://api.fashn.ai/"
    private const val API_KEY = BuildConfig.FASHN_API_KEY

}