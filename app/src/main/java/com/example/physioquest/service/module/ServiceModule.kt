package com.example.physioquest.service.module

import com.example.physioquest.screens.quiz.shared.QuizRepository
import com.example.physioquest.screens.quiz.shared.QuizRepositoryImpl
import com.example.physioquest.service.AccountService
import com.example.physioquest.service.AccountServiceImpl
import com.example.physioquest.service.LevelService
import com.example.physioquest.service.LevelServiceImpl
import com.example.physioquest.service.StorageService
import com.example.physioquest.service.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun provideStorageService(impl: StorageServiceImpl): StorageService

    @Binds
    abstract fun provideQuizRepository(impl: QuizRepositoryImpl): QuizRepository

    @Binds
    abstract fun provideLevelService(impl: LevelServiceImpl): LevelService
}