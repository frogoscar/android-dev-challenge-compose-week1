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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.data.KittyRepository
import com.example.androiddevchallenge.entity.Kitty
import com.example.androiddevchallenge.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * @author Enming XIE
 * @created 2021/3/2
 */
class MainViewModel(private val repository: KittyRepository) : ViewModel() {

    private val _kitties = MutableLiveData<Resource<List<Kitty>>>()
    val kitties: LiveData<Resource<List<Kitty>>>
        get() = _kitties

    private val kittyList = mutableListOf<Kitty>()

    fun getKitties() {
        _kitties.value = Resource.loading()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val kitties = repository.getKitties()
                    kittyList.addAll(kitties)
                    _kitties.postValue(Resource.success(kitties))
                } catch (e: Exception) {
                    Timber.e(e, "Error while loading kitties.")
                    _kitties.postValue(Resource.error(e.message))
                }
            }
        }
    }

    fun setKittyAdopted(position: Int) {
        kittyList[position].adopted = true
        _kitties.value = _kitties.value
    }
}
