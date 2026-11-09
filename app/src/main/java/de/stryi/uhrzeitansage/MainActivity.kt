package de.stryi.uhrzeitansage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import de.stryi.uhrzeitansage.ui.MainScreen
import de.stryi.uhrzeitansage.ui.MainViewModel
import de.stryi.uhrzeitansage.ui.theme.UhrzeitAnsageTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UhrzeitAnsageTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
