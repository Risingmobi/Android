package com.alex.tur.di.module.app.db

import android.arch.persistence.room.Room
import android.content.Context
import com.alex.tur.data.db.MyDatabase
import com.alex.tur.di.qualifier.global.QualifierAppContext
import com.alex.tur.di.qualifier.global.QualifierDbName
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule {

    @Provides
    @Singleton
    @QualifierDbName
    fun provideDbName() = "tur.db"

    @Provides
    @Singleton
    fun provideDatabase(@QualifierAppContext context: Context, @QualifierDbName name: String): MyDatabase {
        return Room.databaseBuilder(context, MyDatabase::class.java, name).build()
    }
}