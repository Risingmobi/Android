package com.alex.tur.data.db

import android.arch.persistence.room.*
import com.alex.tur.model.Order
import com.alex.tur.model.OrderStatus

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orders: MutableList<Order>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(orders: Order)

    @Query("select * from orders")
    fun findAllOrders(): MutableList<Order>

    @Query("select * from orders where id = :id")
    fun findOrder(id: Int): Order

    @Query("delete from orders")
    fun removeAll()

    @Query("select * from orders where order_status = :status")
    fun findOrderListByStatus(status: String): MutableList<Order>

    @Query("select * from orders where order_status = :status")
    fun findOrderByStatus(status: String): Order

    @Query("delete from orders where order_status = :status")
    fun removeAllByStatus(status: String)
}