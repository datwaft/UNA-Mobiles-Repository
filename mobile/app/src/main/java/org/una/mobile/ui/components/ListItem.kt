package org.una.mobile.ui.components

import android.util.Log
import androidx.compose.foundation.Indication
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.una.mobile.ui.theme.Theme

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Card(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth(), propagateMinConstraints = true, content = content)
    }
}

@Composable
fun ListItem(
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication = rememberRipple(),
    content: @Composable BoxScope.() -> Unit,
) {
    ListItem(modifier.indication(interactionSource, indication)) {
        Box(Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val event = PressInteraction.Press(offset)
                        interactionSource.emit(event)
                        awaitRelease()
                        interactionSource.emit(PressInteraction.Release(event))
                    },
                    onLongPress = { onLongPress() }
                )
            }
            .fillMaxWidth(), propagateMinConstraints = true, content = content)
    }
}

@Preview
@Composable
private fun ListItemPreview() {
    Theme {
        Surface(color = MaterialTheme.colors.background) {
            ListItem(Modifier.padding(16.dp)) {
                Text("Hello World", Modifier.padding(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LongPressableListItemPreview() {
    Theme {
        ListItem(onLongPress = { Log.d("PREVIEW", "Emitted longPress event") },
            Modifier.padding(16.dp)) {
            Row(Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Hello World")
                    Text("Hello World")
                }
                Text("Hello World")
            }
        }
    }
}