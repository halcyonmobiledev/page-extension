package com.halcyonmobile.pagination_extension

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.halcyonmobile.core.Bar
import com.halcyonmobile.page.coroutine.SuspendPagedDao

/**
 * Purpose
 * <p>
 * Description
 * <p/>
 * Notes:
 * @author (OPTIONAL! Use only if the code is complex, otherwise delete this line.)
 */
@Dao
abstract class BarDao : SuspendPagedDao<Int, Bar, BarEntity> {

    @Query("SELECT * from barEntity ORDER BY id")
    abstract override fun getValueEntitiesFactory(): DataSource.Factory<Int, BarEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override suspend fun insert(valueEntities: List<BarEntity>)

    override fun valueEntityToValue(valueEntity: BarEntity): Bar = Bar(id = valueEntity.id, title = valueEntity.something)

    override fun valueToValueEntry(value: Bar): BarEntity = BarEntity(id = value.id, something = value.title)

}