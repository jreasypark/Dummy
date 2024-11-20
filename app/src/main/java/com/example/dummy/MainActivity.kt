package com.example.dummy

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dummy.ui.theme.DummyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DummyTheme {
                // 1 - Why is it important to handle innerPadding in a Scaffold? Please fix this issue.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DummyScreen()
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DummyScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    // 3 - Why doesn't `nameList.value` trigger a UI update in Compose?
    val nameList = viewModel.nameList.value

    // 2 - How can you add vertical space/padding between the elements inside the Column with just one line of code?
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "User Names", fontSize = 24.sp)

        repeat(nameList.size){
            Text(text = nameList[it], fontSize = 16.sp)
        }

        Button(onClick = { viewModel.addRandomName() }) {
            Text("Add Name")
        }
    }
}

// 4 - There is something wrong with this ViewModel. What is it?
class MainViewModel : ViewModel() {
    private val _nameList = MutableStateFlow<List<String>>(emptyList())
    val nameList: StateFlow<List<String>> = _nameList

    fun addRandomName() {
        CoroutineScope(Dispatchers.IO).launch {
            _nameList.value += fetchNewNameFromNetwork()
        }
    }

    private suspend fun fetchNewNameFromNetwork(): String {
        delay(500) // Simulate network delay
        val names = listOf("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank")
        return names[Random.nextInt(names.size)]
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    DummyTheme {
        Surface {
            DummyScreen()
        }
    }
}