package com.example.grocery_app.data.hilt


import android.content.Context
import com.example.grocery_app.data.remote.CartRepository
import com.example.grocery_app.data.remote.OrderRepository
import com.example.grocery_app.data.remote.ProductRepository
import com.example.grocery_app.data.room.AppDatabase
import com.example.grocery_app.data.room.CartDao
import com.example.grocery_app.data.room.OrderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideCartDao(appDatabase: AppDatabase): CartDao {
        return appDatabase.cartDao()
    }

    @Provides
    @Singleton
    fun provideCartRepository(cartDao: CartDao): CartRepository {
        return CartRepository(cartDao)
    }

    @Provides
    @Singleton
    fun provideProductRepository(): ProductRepository {
        return ProductRepository()
    }

    @Provides
    @Singleton
    fun provideOrderDao(appDatabase: AppDatabase): OrderDao {
        return appDatabase.orderDao()
    }

    @Provides
    @Singleton
    fun provideOrderRepository(orderDao: OrderDao): OrderRepository {
        return OrderRepository(orderDao)
    }
}