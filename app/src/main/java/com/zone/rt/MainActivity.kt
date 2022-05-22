package com.zone.rt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zone.rt.ui.theme.RayTracingInComposeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RayTracingCompose()
        }
    }
}


@Composable
fun RayTracingCompose(vm: MainViewModel = viewModel()) = RayTracingInComposeTheme {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var enable by remember { mutableStateOf(true) }
        Image(
            bitmap = vm.bitmap.asImageBitmap(),
            contentDescription = "image",
            modifier = Modifier.size(vm.imageWidth.dp, vm.imageHeight.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            enable = false
            if (vm.finishRender()) {
                vm.refresh()
            }
            vm.draw()
            enable = true
        },
            enabled = enable
        ) {
            if (vm.finishRender()) {
                Text(text = "Restart", color = Color.Black)
            } else {
                Text(text = "Begin Render", color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (vm.finishRender()) {
            Text(text = "Finish Render !", color = Color.Black)
        } else {
            Text(text = "Remain: ${vm.progress} lines", color = Color.Black)
        }
    }
}