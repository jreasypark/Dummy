package com.example.dummy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dummy.ui.theme.DummyTheme
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

                /* The innerPadding parameter in the Scaffold lambda represents the padding applied
                   to the content of the Scaffold. This padding is typically used to account for elements
                   like the TopBar, BottomBar, or system UI
                 */
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DummyScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DummyScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    // 3 - Why doesn't `nameList.value` trigger a UI update in Compose?

    /*
      Compose doesn’t automatically observe changes to StateFlow objects.
      To update the UI reactively, use collectAsState() or variants,
      which converts the StateFlow into a State that Compose can observe.
    */

    val nameList by viewModel.nameList.collectAsState()

    // 2 - How can you add vertical space/padding between the elements inside the Column with just one line of code?

    /*
      By adding the verticalArrangement parameter with Arrangement.spacedBy,
      you can introduce consistent spacing between items in the column.
    */

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
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

/*
Launching a coroutine using CoroutineScope(Dispatchers.IO).launch inside the ViewModel is incorrect because it
creates a new coroutine scope that is not tied to the lifecycle of the ViewModel

In a ViewModel, you should use viewModelScope.launch instead of manually creating a coroutine scope.
viewModelScope is tied to the lifecycle of the ViewModel, automatically canceling any running coroutines
when the ViewModel is cleared. This ensures that coroutines are properly managed and canceled when no longer needed.
*/

class MainViewModel : ViewModel() {
    private val _nameList = MutableStateFlow<List<String>>(emptyList())
    val nameList: StateFlow<List<String>> = _nameList

    fun addRandomName() {
        viewModelScope.launch {
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