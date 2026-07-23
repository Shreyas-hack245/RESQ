package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.navigation.ResqAppNavigation
import com.example.ui.theme.ResqTheme
import com.example.ui.viewmodel.ResqViewModel

class MainActivity : ComponentActivity() {

    private val resqViewModel: ResqViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResqTheme(darkTheme = false) {
                ResqAppNavigation(viewModel = resqViewModel)
            }
        }
    }
}

