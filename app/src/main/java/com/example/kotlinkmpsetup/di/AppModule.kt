package com.example.kotlinkmpsetup.di

import com.example.kotlinkmpsetup.data.local.DeviceInfoProvider
import com.example.kotlinkmpsetup.data.local.TokenDataStoreManager
import com.example.kotlinkmpsetup.data.network.NetworkConnectivityObserver
import com.example.kotlinkmpsetup.data.remote.KtorAuthClient
import com.example.kotlinkmpsetup.data.remote.KtorAuthRemoteDataSource
import com.example.kotlinkmpsetup.data.repository.AuthRepositoryImpl
import com.example.kotlinkmpsetup.data.repository.NetworkRepositoryImpl
import com.example.kotlinkmpsetup.domain.repository.AuthRepository
import com.example.kotlinkmpsetup.domain.repository.NetworkRepository
import com.example.kotlinkmpsetup.domain.usecase.LoginUseCase
import com.example.kotlinkmpsetup.domain.usecase.SignupUseCase
import com.example.kotlinkmpsetup.domain.usecase.ValidateEmailUseCase
import com.example.kotlinkmpsetup.domain.usecase.ValidateNameUseCase
import com.example.kotlinkmpsetup.domain.usecase.ValidatePasswordUseCase
import com.example.kotlinkmpsetup.presentation.auth.LoginViewModel
import com.example.kotlinkmpsetup.presentation.auth.SignupViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * ====================================================================================
 * DEPENDENCY INJECTION: KOIN MODULES
 * ====================================================================================
 * Defines object creation graphs for Network, Local Data, Repositories, Use Cases, and ViewModels.
 */

val networkModule = module {
    single { KtorAuthClient.createHttpClient(get(), get()) }
}

val dataModule = module {
    single { TokenDataStoreManager(androidContext()) }
    single { DeviceInfoProvider(androidContext()) }
    single { NetworkConnectivityObserver(androidContext()) }
    single { KtorAuthRemoteDataSource(get(), get()) }
    single { com.example.kotlinkmpsetup.data.remote.KtorHomeRemoteDataSource(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<NetworkRepository> { NetworkRepositoryImpl(get()) }
    single<com.example.kotlinkmpsetup.domain.repository.HomeRepository> { com.example.kotlinkmpsetup.data.repository.HomeRepositoryImpl(get()) }
}

val domainModule = module {
    factory { ValidateEmailUseCase() }
    factory { com.example.kotlinkmpsetup.domain.usecase.ValidateMobileUseCase() }
    factory { ValidatePasswordUseCase() }
    factory { ValidateNameUseCase() }
    factory { LoginUseCase(get(), get()) }
    factory { SignupUseCase(get(), get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get(), get(), get(), get()) }
    viewModel { SignupViewModel(get(), get()) }
    viewModel { com.example.kotlinkmpsetup.presentation.home.HomeViewModel(get()) }
}

val appModules = listOf(
    networkModule,
    dataModule,
    domainModule,
    viewModelModule
)
