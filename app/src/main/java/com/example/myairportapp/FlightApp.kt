package com.example.myairportapp

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myairportapp.viewmodel.FlightViewModel
import com.example.myairportapp.ui.FlightScreen

@Composable
fun FlightApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext as Application

    val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context)
    val viewModel: FlightViewModel = viewModel(factory = factory)

    FlightScreen(viewModel = viewModel, modifier = modifier)
}