/*
 * Copyright (c) 2020 Halcyon Mobile.
 * https://www.halcyonmobile.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.halcyonmobile.page.coroutine

import androidx.paging.DataSource
import androidx.paging.PagedList
import com.halcyonmobile.page.DataSourceState
import kotlinx.coroutines.channels.ReceiveChannel

/**
 * Contains everything that is needed to create a PagedList and an additionalData channel
 *
 * This expected to be returned from repositories using pagination.
 */
data class PagedResultWithAdditionalData<Key, Value,AdditionalData, Error>(
    val stateChannel: ReceiveChannel<DataSourceState<Error>>,
    val additionalDataChannel: ReceiveChannel<AdditionalData>,
    override val dataSourceFactory: DataSource.Factory<Key, Value>,
    override val boundaryCallback: PagedList.BoundaryCallback<Value>? = null
) : com.halcyonmobile.page.PagedResult<Key, Value>