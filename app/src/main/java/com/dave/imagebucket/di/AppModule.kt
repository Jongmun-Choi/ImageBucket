package com.dave.imagebucket.di

import android.content.Context
import androidx.room.Room
import com.dave.imagebucket.data.database.AppDatabase
import com.dave.imagebucket.data.database.BucketItemDao
import com.dave.imagebucket.data.database.RemoteKeyDao
import com.dave.imagebucket.data.database.SearchResultDao
import com.dave.imagebucket.network.KakaoApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideKakaoApiService(retrofit: Retrofit): KakaoApiService {
        return retrofit.create(KakaoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "image_search_db"
        )
            .fallbackToDestructiveMigration() // 스키마 변경 시 기존 데이터 삭제 (간단한 처리)
            .build()
    }

    @Provides
    fun provideLockerDao(database: AppDatabase): BucketItemDao {
        return database.bucketItemDao()
    }

    // 아래 두 Provider 추가
    @Provides
    fun provideSearchResultDao(database: AppDatabase): SearchResultDao {
        return database.searchResultDao()
    }

    @Provides
    fun provideRemoteKeyDao(database: AppDatabase): RemoteKeyDao {
        return database.remoteKeyDao()
    }

}