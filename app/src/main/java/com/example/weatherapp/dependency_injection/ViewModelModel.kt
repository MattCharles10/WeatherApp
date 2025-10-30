package com.example.weatherapp.dependency_injection

import com.example.weatherapp.fragments.home.HomeViewModule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModel = module{
    viewModel { HomeViewModule(weatherDataRepository = get())}
}