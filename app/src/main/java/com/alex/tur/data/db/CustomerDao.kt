package com.alex.tur.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.alex.tur.model.api.ResponseTemplate

@Dao
interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTemplates(list: MutableList<ResponseTemplate>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTemplate(item: ResponseTemplate)

    @Query("select * from template")
    fun findAllTemplates(): MutableList<ResponseTemplate>

    @Query("select * from template where id = :id")
    fun findTemplate(id: Int): ResponseTemplate

    @Query("delete from template")
    fun removeAll()
}
