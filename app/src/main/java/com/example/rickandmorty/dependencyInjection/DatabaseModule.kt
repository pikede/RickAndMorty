package com.example.rickandmorty.dependencyInjection

import android.content.Context
import androidx.room.Room
import com.example.rickandmorty.data.database.AppDataBase
import com.example.rickandmorty.data.database.migrations.ResultMigrationFrom1To2
import org.koin.dsl.module

val databaseModule = module {
    single<AppDataBase> {
        val context = get<Context>()
        getDataBase(context)
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

fun getDataBase(context: Context): AppDataBase = Room.databaseBuilder(
    context.applicationContext, AppDataBase::class.java, AppDataBase.DATABASE_NAME
).fallbackToDestructiveMigration().addMigrations(ResultMigrationFrom1To2()).build()