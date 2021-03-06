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
package com.halcyonmobile.pagination_extension.db.sources

import com.halcyonmobile.core.Bar
import com.halcyonmobile.core.shared.BarDBKeyLocalStorage
import com.halcyonmobile.page.coroutine.db.localstorage.PagedSuspendKeyLocalStorage
import com.halcyonmobile.page.db.KeyOrEndOfList
import com.halcyonmobile.pagination_extension.db.room.dao.PageKeyDao
import com.halcyonmobile.pagination_extension.db.room.entity.KeyEntity

class BarPageKeyLocalSource(private val pagedKeyDao: PageKeyDao) : PagedSuspendKeyLocalStorage<Int, Bar, String, KeyEntity>(
    BAR_PAGE_KEY_ID, pagedKeyDao), BarDBKeyLocalStorage {

    override fun endToKeyEntity(): KeyEntity =
        KeyEntity(keyEntityId, 0, true)

    override fun keyToKeyEntity(key: Int): KeyEntity =
        KeyEntity(keyEntityId, key.toLong(), false)

    override fun keyEntityToKey(keyEntity: KeyEntity): KeyOrEndOfList<Int> =
        if (keyEntity.isEnd) KeyOrEndOfList.EndReached() else KeyOrEndOfList.Key(keyEntity.value.toInt())

    override suspend fun clear() {
        pagedKeyDao.delete(keyEntityId)
    }

    companion object {
        private const val BAR_PAGE_KEY_ID = "BAR"
    }
}