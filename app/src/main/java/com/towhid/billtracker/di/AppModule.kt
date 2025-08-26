package com.towhid.billtracker.di

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.towhid.billtracker.BuildConfig
import com.towhid.billtracker.data.local.AppDatabase
import com.towhid.billtracker.data.remote.ExchangeApi
import com.towhid.billtracker.data.repository.ConverterRepository
import com.towhid.billtracker.data.repository.SubscriptionRepositoryImpl
import com.towhid.billtracker.domain.repository.SubscriptionRepository
import com.towhid.billtracker.domain.usecase.DeleteSubscriptionUseCase
import com.towhid.billtracker.domain.usecase.GetAllSubscriptionsUseCase
import com.towhid.billtracker.domain.usecase.GetByIdUseCase
import com.towhid.billtracker.domain.usecase.UpsertSubscriptionUseCase
import com.towhid.billtracker.prefs.UserPrefs
import com.towhid.billtracker.presentation.subscription.edit.EditViewModel
import com.towhid.billtracker.presentation.subscription.list.ListViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = module {
    single { UserPrefs(get()) }

    single {
        Room.databaseBuilder(get<Application>(), AppDatabase::class.java, "sub_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppDatabase>().subscriptionDao() }

    single {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newUrl = originalRequest.url.newBuilder()
                    .addQueryParameter("access_key", BuildConfig.API_KEY)
                    .build()
                val newRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(logger)
            .build()
    }
    single {
        val moshi = Moshi.Builder().build()
        Retrofit.Builder()
            .baseUrl("https://api.exchangerate.host/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(get())
            .build()
            .create(ExchangeApi::class.java)
    }
    single { ConverterRepository(get()) }

    // Repos
    single<SubscriptionRepository> { SubscriptionRepositoryImpl(get()) }

    // Use cases
    single { GetAllSubscriptionsUseCase(get()) }
    single { UpsertSubscriptionUseCase(get()) }
    single { DeleteSubscriptionUseCase(get()) }
    single { GetByIdUseCase(get()) }

    // ViewModels
    viewModel { ListViewModel(get(), get(), get(), get(), get()) }
    viewModel { EditViewModel(get(), get()) }
}