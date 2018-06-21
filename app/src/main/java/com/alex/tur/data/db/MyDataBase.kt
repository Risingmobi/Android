package com.alex.tur.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.alex.tur.converters.*
import com.alex.tur.model.Customer
import com.alex.tur.model.Driver
import com.alex.tur.model.Order
import com.alex.tur.model.api.ResponseTemplate

@Database(entities = [Customer::class, Driver::class, Order::class, ResponseTemplate::class], version = 1)
@TypeConverters(
        DriverStatusConverter::class,
        DriverTransportModeConverter::class,
        DriverServiceListConverter::class,
        OrderStatusConverter::class,
        PaymentStatusConverter::class,
        DoubleArrayConverter::class,
        LineArrayConverter::class,
        DayOfWeekConverter::class)
abstract class MyDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao

    abstract fun driverDao(): DriverDao

    abstract fun orderDao(): OrderDao
}