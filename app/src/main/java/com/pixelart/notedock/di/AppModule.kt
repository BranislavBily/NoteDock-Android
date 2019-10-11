package com.pixelart.notedock.di

import com.pixelart.notedock.domain.usecase.KoinTestingImpl
import com.pixelart.notedock.domain.usecase.KoinTestingUseCase
import com.pixelart.notedock.viewModel.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { KoinTestingImpl() as KoinTestingUseCase}
    viewModel { MainViewModel(get()) }
}