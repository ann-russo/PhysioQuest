package com.example.physioquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.physioquest.navigation.Navigation
import com.example.physioquest.ui.theme.PhysioQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhysioQuestTheme {
                Navigation()
            }
        }
    }
}