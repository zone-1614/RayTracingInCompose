package com.zone.rt

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zone.rt.ui.theme.RayTracingInComposeTheme
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.system.measureTimeMillis

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RayTracingCompose(saveBitmap = ::saveBitmap)
        }
    }

    fun saveBitmap(bitmap: Bitmap) {
        val filename = System.currentTimeMillis().toString()
        try {
            MediaStore.Images.Media.insertImage(contentResolver, bitmap, filename, "render")
            Toast.makeText(this, "Save success", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

}


@Composable
fun RayTracingCompose(
    vm: MainViewModel = viewModel(),
    saveBitmap: (bitmap: Bitmap) -> Unit
) = RayTracingInComposeTheme {
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
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                saveBitmap(vm.bitmap)
            }) {
                Text(text = "Save Image", color = Color.Black)
            }
        } else {
            Text(text = "Remain: ${vm.progress} lines", color = Color.Black)
        }
    }
}
