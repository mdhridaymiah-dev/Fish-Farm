package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Project::class,
        User::class,
        ApprovalRequest::class,
        ShareHistory::class,
        AccountEntry::class,
        Pond::class,
        FishStockHistory::class,
        FeedUsage::class,
        InternalMessage::class,
        AuditLog::class,
        ActiveSession::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun userDao(): UserDao
    abstract fun approvalRequestDao(): ApprovalRequestDao
    abstract fun shareHistoryDao(): ShareHistoryDao
    abstract fun accountEntryDao(): AccountEntryDao
    abstract fun pondDao(): PondDao
    abstract fun fishStockHistoryDao(): FishStockHistoryDao
    abstract fun feedUsageDao(): FeedUsageDao
    abstract fun internalMessageDao(): InternalMessageDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun activeSessionDao(): ActiveSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fish_farm_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
