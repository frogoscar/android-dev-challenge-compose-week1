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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.example.androiddevchallenge.entity.Kitty
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.utils.Shadow
import com.example.androiddevchallenge.vo.Resource
import com.example.androiddevchallenge.vo.Status

class MainActivity : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = getString(R.string.app_name),
                                )
                            },
                            backgroundColor = Color.Transparent, elevation = 0.dp
                        )
                    }
                ) {
                    DisplayKittyList(kitties = viewModel.kitties) { position, kitty ->
                        Intent(this, KittyDetailActivity::class.java).apply {
                            putExtra(SELECTED_POSITION, position)
                            putExtra(SELECTED_KITTY, kitty)
                            startForResult.launch(this)
                        }
                    }
                }
            }
        }
        viewModel.getKitties()
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val adopted = it.getBooleanExtra(ADOPTED, false)
                    val position = it.getIntExtra(SELECTED_POSITION, 0)
                    if (adopted) {
                        viewModel.setKittyAdopted(position)
                    }
                }
            }
        }
}

// Start building your app here!
@Composable
fun DisplayKittyList(
    kitties: LiveData<Resource<List<Kitty>>>,
    onClick: (position: Int, kitty: Kitty) -> Unit
) {
    val resource by kitties.observeAsState()
    resource.let {
        when (it?.status) {
            Status.SUCCESS -> {
                // Kitty data is ready.
                it.data?.let { kitties ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        LazyColumn(Modifier.fillMaxWidth()) {
                            itemsIndexed(kitties) { position, kitty ->
                                DisplayKittyItem(position, kitty, onClick)
                            }
                        }
                    }
                }
            }
            Status.LOADING -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Toast.makeText(
                    MyApp.context,
                    "Failed to get kitty data. ${it?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

@Composable
fun DisplayKittyItem(position: Int, kitty: Kitty, onClick: (position: Int, kitty: Kitty) -> Unit) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .requiredHeight(220.dp)
            .clickable { onClick(position, kitty) }
    ) {
        CardItem(name = kitty.name, photo = kitty.photoFileName)
    }
}

@Composable
fun CardItem(name: String, photo: String) {
    val imageIdentity = MyApp.context.resources.getIdentifier(
        photo, "drawable",
        MyApp.context.packageName
    )
    val image: Painter = painterResource(imageIdentity)
    Image(
        painter = image,
        contentDescription = name,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Surface(
            color = Color.Shadow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = name,
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}