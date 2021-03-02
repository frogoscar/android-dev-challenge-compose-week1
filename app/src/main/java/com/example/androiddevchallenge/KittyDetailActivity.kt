/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.entity.Kitty

/**
 *
 * @author Enming XIE
 * @created 2021/3/2
 */
const val SELECTED_KITTY = "selected_kitty"
const val SELECTED_POSITION = "selected_position"
const val ADOPTED = "adopted"

class KittyDetailActivity : BaseActivity() {
    private lateinit var selectedKitty: Kitty
    private var selectedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarToLight()
        val kitty = intent.getParcelableExtra<Kitty>(SELECTED_KITTY)
        selectedPosition = intent.getIntExtra(SELECTED_POSITION, 0)
        if (kitty == null) {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        selectedKitty = kitty
        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = selectedKitty.name
                            )
                        },
                        backgroundColor = Color.Transparent, elevation = 0.dp,
                        navigationIcon = {
                            IconButton(onClick = { navigateBack() }) {
                                val backIcon: Painter = painterResource(R.drawable.ic_back)
                                Icon(painter = backIcon, contentDescription = "ic_back")
                            }
                        }
                    )
                }
            ) {
                DisplayKittyDetail(kitty = selectedKitty)
            }
        }
    }

    override fun onBackPressed() {
        navigateBack()
    }

    private fun navigateBack() {
        val intent = Intent()
        intent.putExtra(SELECTED_POSITION, selectedPosition)
        intent.putExtra(ADOPTED, selectedKitty.adopted)
        setResult(RESULT_OK, intent)
        finish()
    }
}

var showConfirmDialog by mutableStateOf(false)

@Composable
fun DisplayKittyDetail(kitty: Kitty) {
    val stateKitty by remember { mutableStateOf(kitty) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        KittyAvatar(
            avatar = stateKitty.photoFileName,
            name = stateKitty.name
        )
        Spacer(
            modifier = Modifier.requiredHeight(26.dp)
        )
        AdoptButton(
            adopted = stateKitty.adopted
        )
        Spacer(
            modifier = Modifier.requiredHeight(26.dp)
        )
        KittyIntroduction(
            introduction = stateKitty.introduction
        )
    }
    if (showConfirmDialog) {
        AdoptConfirmDialog(kitty = stateKitty)
    }
}

@Composable
fun KittyAvatar(avatar: String, name: String) {
    val imageIdentity = MyApp.context.resources.getIdentifier(
        avatar, "drawable",
        MyApp.context.packageName
    )
    val image: Painter = painterResource(imageIdentity)
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = image,
            contentDescription = name,
            modifier = Modifier
                .requiredSize(150.dp)
                .clip(shape = CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun KittyIntroduction(introduction: String) {
    Text(
        text = introduction,
        fontSize = 18.sp,
        style = MaterialTheme.typography.body1
    )
}

@Composable
fun AdoptButton(adopted: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { showConfirmDialog = true },
            enabled = !adopted
        ) {
            Text(text = if (adopted) "Adopted" else "Adopt")
        }
    }
}

@Composable
fun AdoptConfirmDialog(kitty: Kitty) {
    AlertDialog(
        onDismissRequest = {
            showConfirmDialog = false
        },
        text = {
            Text(
                text = "Would you like to adopt this cute kitty ?",
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    showConfirmDialog = false
                    kitty.adopted = true
                }
            ) {
                Text(
                    text = "Yes"
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showConfirmDialog = false
                }
            ) {
                Text(
                    text = "No"
                )
            }
        }
    )
}
