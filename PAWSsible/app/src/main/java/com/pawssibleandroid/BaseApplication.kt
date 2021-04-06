package com.pawssibleandroid

import androidx.multidex.MultiDexApplication
import com.google.gson.GsonBuilder
import com.pawssibleandroid.utils.PawssibleStorage
import com.pawssibleandroid.utils.StaticUtils
import com.pawssibleandroid.wsutils.WSInterface
import com.pawssibleandroid.wsutils.WSUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * We are using base application class as extension of Application class for more control over application
 */
class BaseApplication : MultiDexApplication() {

    private var okHttpClient: OkHttpClient? = null

    override fun onCreate() {
        super.onCreate()
        initRetrofit()
        pawssibleStorage = PawssibleStorage().getInstance(this)
        userType = pawssibleStorage?.getValue(PawssibleStorage.SP_USER_TYPE, "C")
    }

    /**
     * getting api interface accessible everywhere
     */
    val wsClientListener: WSInterface? get() = wsInterface

    /**
     * Initialises retrofit base object, which will be reused everywhere
     */
    fun initRetrofit() {
        val headerInterceptor = Interceptor { chain ->
            val requestBuilder: Request.Builder = chain.request().newBuilder()
            requestBuilder.header(StaticUtils.CONTENT_TYPE, StaticUtils.CONTENT_TYPE_JSON)
            chain.proceed(requestBuilder.build())
        }
        okHttpClient = OkHttpClient().newBuilder().addInterceptor(headerInterceptor).addInterceptor(
            HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.BODY
            )
        ).readTimeout(WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS).build()
        wsInterface = buildRetrofitClient().create(WSInterface::class.java)
    }

    /**
     * Builds base retrofit builder with base URL
     */
    private fun buildRetrofitClient(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(WSUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        return retrofit
    }

    companion object {
        private var baseApplication: BaseApplication? = null
        var pawssibleStorage: PawssibleStorage? = null
        var userType: String? = ""
        val isCustomer: Boolean get() = userType.equals("C", ignoreCase = true)
        private var wsInterface: WSInterface? = null

        @get:Synchronized
        val instance: BaseApplication?
            get() {
                if (baseApplication == null) baseApplication = BaseApplication()
                return baseApplication
            }
    }
}