package com.example.rickandmorty.dependencyInjection

import androidx.room.Room
import com.example.rickandmorty.data.database.AppDataBase
import com.example.rickandmorty.data.database.LocationsDao
import com.example.rickandmorty.data.database.ResultDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext().applicationContext, AppDataBase::class.java, AppDataBase.DATABASE_NAME
        ).build()
    }
}

val daoModules = module {
    single {
        get<AppDataBase>().getResultDao()
    }
    single {
        get<AppDataBase>().getLocationsDao()
    }
}