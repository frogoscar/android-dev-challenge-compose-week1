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
package com.example.androiddevchallenge.vo

/**
 * Generic class holding a value with its success, error or loading status, etc.
 * @author Enming XIE
 * @created 2021/3/2
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?, val strResId: Int?) {
    companion object {
        fun <T> success(data: T?, message: String? = null, strResId: Int? = null): Resource<T> {
            return Resource(Status.SUCCESS, data, message, strResId)
        }

        fun <T> error(_msg: String? = null, data: T? = null, strResId: Int? = null): Resource<T> {
            val msg = _msg ?: "Unknown error."
            return Resource(Status.ERROR, data, msg, strResId)
        }

        fun <T> loading(data: T? = null, message: String? = null, strResId: Int? = null): Resource<T> {
            return Resource(Status.LOADING, data, message, strResId)
        }
    }
}
