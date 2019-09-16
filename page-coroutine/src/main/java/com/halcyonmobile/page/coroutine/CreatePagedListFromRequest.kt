/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */

package com.halcyonmobile.page.coroutine

import com.halcyonmobile.page.DataSourceAggregatingDataSourceFactory
import com.halcyonmobile.page.DataSourceInvalidator
import com.halcyonmobile.page.DataSourceUpdateListener
import com.halcyonmobile.page.ProvideCacheByKey
import com.halcyonmobile.page.ProvideCacheByKeyAndPageSizeState
import com.halcyonmobile.page.StateProvidingListDataSourceFactory
import kotlinx.coroutines.CoroutineScope

/**
 * Helper function which sets up your [PagedResult] with coroutine based [DataSourceUpdateListener] & [SuspendProvideDataByPagedKeyAndSize]
 */
inline fun <Key, Value, reified Error : Throwable> createPagedResultFromRequest(
    coroutineScope: CoroutineScope,
    initialPageKey: Key,
    dataSourceInvalidator: DataSourceInvalidator<Key, Value>? = null,
    crossinline request: suspend (Key, Int) -> Pair<List<Value>, Key>,
    crossinline cache: (Key, Int) -> Pair<List<Value>, Key>? = { _, _ -> null }
): PagedResult<Key, Value, Error> {
    val channelBasedDataSourceUpdateListener = ChannelBasedDataSourceUpdateListener<Error>()
    val dataSourceFactory = StateProvidingListDataSourceFactory(
        initialPageKey = initialPageKey,
        provideCacheByKey = ProvideCacheByKey { key, pageSize -> cache(key, pageSize) },
        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize<Key, Value, Error>(coroutineScope) { key, pageSize ->
            request(key, pageSize)
        },
        dataSourceUpdateListener = channelBasedDataSourceUpdateListener
    )
    return PagedResult(
        boundaryCallback = null,
        dataSourceFactory = dataSourceInvalidator?.let { DataSourceAggregatingDataSourceFactory(dataSourceFactory, it) } ?: dataSourceFactory,
        stateChannel = channelBasedDataSourceUpdateListener.stateChannel
    )
}

inline fun <Key, Value, reified Error : Throwable> createPagedResultFromRequestWithStateProvideingCache(
    coroutineScope: CoroutineScope,
    initialPageKey: Key,
    dataSourceInvalidator: DataSourceInvalidator<Key, Value>?,
    crossinline request: suspend (Key, Int) -> Pair<List<Value>, Key>,
    crossinline cache: (Key, Int) -> ProvideCacheByKey.Result<Key, Value>
): PagedResult<Key, Value, Error> {
    val channelBasedDataSourceUpdateListener = ChannelBasedDataSourceUpdateListener<Error>()
    val dataSourceFactory = StateProvidingListDataSourceFactory(
        initialPageKey = initialPageKey,
        provideCacheByKey = ProvideCacheByKeyAndPageSizeState(cache),
        provideDataByPageKeyAndSize = SuspendProvideDataByPagedKeyAndSize(coroutineScope, request),
        dataSourceUpdateListener = channelBasedDataSourceUpdateListener
    )
    return PagedResult(
        boundaryCallback = null,
        dataSourceFactory = dataSourceInvalidator?.let {
            DataSourceAggregatingDataSourceFactory(
                dataSourceFactory,
                it
            )
        }
            ?: dataSourceFactory,
        stateChannel = channelBasedDataSourceUpdateListener.stateChannel
    )
}