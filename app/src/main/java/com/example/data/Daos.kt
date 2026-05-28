package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Int): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE projectAssignmentId = :projectId")
    fun getUsersByProject(projectId: Int): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface ApprovalRequestDao {
    @Query("SELECT * FROM approval_requests ORDER BY id DESC")
    fun getAllApprovalRequests(): Flow<List<ApprovalRequest>>

    @Query("SELECT * FROM approval_requests WHERE projectId = :projectId")
    fun getApprovalRequestsByProject(projectId: Int): Flow<List<ApprovalRequest>>

    @Query("SELECT * FROM approval_requests WHERE id = :id")
    suspend fun getRequestById(id: Int): ApprovalRequest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: ApprovalRequest): Long

    @Update
    suspend fun updateRequest(request: ApprovalRequest)
}

@Dao
interface ShareHistoryDao {
    @Query("SELECT * FROM share_histories WHERE projectId = :projectId")
    fun getSharesByProject(projectId: Int): Flow<List<ShareHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShareHistory(history: ShareHistory)
}

@Dao
interface AccountEntryDao {
    @Query("SELECT * FROM account_entries WHERE projectId = :projectId ORDER BY date DESC")
    fun getEntriesByProject(projectId: Int): Flow<List<AccountEntry>>

    @Query("SELECT * FROM account_entries WHERE id = :id")
    suspend fun getEntryById(id: Int): AccountEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: AccountEntry): Long

    @Update
    suspend fun updateEntry(entry: AccountEntry)
}

@Dao
interface PondDao {
    @Query("SELECT * FROM ponds WHERE projectId = :projectId")
    fun getPondsByProject(projectId: Int): Flow<List<Pond>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPond(pond: Pond)

    @Update
    suspend fun updatePond(pond: Pond)
}

@Dao
interface FishStockHistoryDao {
    @Query("SELECT * FROM fish_stock_histories WHERE pondId = :pondId ORDER BY date DESC")
    fun getStockHistoryByPond(pondId: Int): Flow<List<FishStockHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockHistory(history: FishStockHistory)
}

@Dao
interface FeedUsageDao {
    @Query("SELECT * FROM feed_usages WHERE projectId = :projectId ORDER BY date DESC")
    fun getFeedUsageByProject(projectId: Int): Flow<List<FeedUsage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedUsage(usage: FeedUsage)
}

@Dao
interface InternalMessageDao {
    @Query("SELECT * FROM internal_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<InternalMessage>>

    @Query("SELECT * FROM internal_messages WHERE id = :id")
    suspend fun getMessageById(id: Int): InternalMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: InternalMessage): Long

    @Update
    suspend fun updateMessage(message: InternalMessage)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Query("SELECT * FROM audit_logs WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getAuditLogsByProject(projectId: Int): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog)
}

@Dao
interface ActiveSessionDao {
    @Query("SELECT * FROM active_sessions WHERE userId = :userId")
    fun getSessionsByUser(userId: String): Flow<List<ActiveSession>>

    @Query("DELETE FROM active_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)

    @Query("DELETE FROM active_sessions WHERE userId = :userId AND isCurrent = 0")
    suspend fun deleteOtherSessions(userId: String)

    @Query("DELETE FROM active_sessions WHERE userId = :userId")
    suspend fun deleteAllSessions(userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ActiveSession)
}
