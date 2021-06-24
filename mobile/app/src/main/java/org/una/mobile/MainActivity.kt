package org.una.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import org.una.mobile.client.WebSocketClient
import org.una.mobile.ui.theme.MobileTheme
import org.una.mobile.viewmodel.MessageViewModel

class MainActivity : ComponentActivity() {
    override fun onResume() {
        super.onResume()
        WebSocketClient.connect(lifecycleScope)
    }

    override fun onPause() {
        super.onPause()
        WebSocketClient.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileTheme {
                Surface(color = MaterialTheme.colors.background) {
                    App(Modifier.animateContentSize())
                }
            }
        }
    }
}

@Composable
private fun App(modifier: Modifier = Modifier) {
    Content(modifier)
}

// TODO: Delete this test composable
@Composable
private fun Content(modifier: Modifier = Modifier) {
    val viewModel: MessageViewModel = viewModel()
    var text: String by remember { mutableStateOf("") }

    Column(modifier) {
        LazyColumn(Modifier
            .padding(16.dp)
            .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(viewModel.items) { item ->
                Card(Modifier.fillMaxWidth()) { Text(item, Modifier.padding(8.dp)) }
            }
        }
        Row(Modifier.padding(32.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(text, { text = it }, Modifier.weight(1f).padding(end = 16.dp))
            Button(onClick = { viewModel.send(text) }) { Text("SEND") }
        }
    }
}