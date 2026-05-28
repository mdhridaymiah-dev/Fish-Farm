package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val logo: String = "",
    val isActive: Boolean = true,
    val defaultApprovalMode: String = "Majority", // "Majority" or "100%"
    val thresholdAmount: Double = 50000.0, // Expense above this requires 100% approval
    val totalShares: Int = 1000,
    val shareValue: Double = 5000.0
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String, // e.g. "SUPER-001", "ADMIN-001", "MEM-001"
    val username: String,
    val fullName: String,
    val mobile: String,
    val email: String,
    val role: String, // "Super Admin", "Project Admin", "Member", "Auditor", "Custom Role"
    val projectAssignmentId: Int = 0, // 0 for all or Super Admin
    val status: String = "Active", // "Active", "Suspended"
    val profileImage: String = "",
    val isFirstLogin: Boolean = true,
    val isVerified: Boolean = false,
    val currentPasswordHash: String, // bcrypt/argon2 simulation (secure hash)
    val backupRecoveryCode: String = "",
    val defaultPasswordActive: Boolean = true,
    val lastLoginIP: String = "192.168.1.1",
    val lastLoginDevice: String = "Mobile Device"
)

@Entity(tableName = "approval_requests")
data class ApprovalRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val requestType: String, // "Member Remove", "Share Transfer", "Expense Update", "Income Update", "Rule Change", "Project Closure"
    val requestCreatorId: String,
    val createdDate: String,
    val status: String, // "Draft", "Pending", "Approved", "Rejected"
    val description: String,
    val detailsJson: String, // JSON payload representing what change is staged
    val currentApproveCount: Int = 0,
    val totalRequiredApprovers: Int = 3,
    val approversListJson: String = "[]", // List of userIds who voted approved
    val approvalRequirementType: String = "Majority", // "100%" or "Majority"
    val rejectionReason: String = ""
)

@Entity(tableName = "share_histories")
data class ShareHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val previousOwnerId: String,
    val newOwnerId: String,
    val shareAmount: Int,
    val transferDate: String,
    val purchaseValue: Double,
    val approvalId: Int,
    val status: String, // "Pending", "Approved"
    val transferReason: String
)

@Entity(tableName = "account_entries")
data class AccountEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val type: String, // "Income" or "Expense"
    val category: String, // "Fish Sale", "Feed", "Medicine", etc.
    val amount: Double,
    val date: String,
    val description: String,
    val attachmentUrl: String = "",
    val creatorId: String,
    val status: String = "Approved", // "Pending" (needs approval first) or "Approved"
    val approvalRequestId: Int = 0
)

@Entity(tableName = "ponds")
data class Pond(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val pondNumber: String,
    val size: Double, // in Decimal/Acres
    val waterCondition: String, // pH, Temperature etc.
    val fishSpecies: String,
    val stockQuantity: Int,
    val releaseDate: String
)

@Entity(tableName = "fish_stock_histories")
data class FishStockHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pondId: Int,
    val quantity: Int,
    val species: String,
    val weight: Double, // average in grams
    val growthRate: String, // e.g. "12g/week"
    val mortality: Int,
    val harvestStatus: String, // "Stocked", "Harvested"
    val date: String
)

@Entity(tableName = "feed_usages")
data class FeedUsage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val feedType: String,
    val brand: String,
    val quantity: Double, // kg
    val cost: Double,
    val dailyConsumption: Double, // kg/day
    val date: String
)

@Entity(tableName = "internal_messages")
data class InternalMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderId: String,
    val receiverId: String,
    val subject: String,
    val body: String,
    val priority: String = "Normal", // "Normal", "Important", "Urgent"
    val status: String = "Inbox", // "Inbox", "Sent", "Draft", "Trash"
    val isStarred: Boolean = false,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val repliesJson: String = "[]" // Thread list representation
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val action: String, // "Login", "Logout", "Verification Sent", "Password Reset", "Approval Commit", etc.
    val module: String, // "Auth", "Accounting", "Member", "Shares", "Approval"
    val projectId: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val oldValue: String = "",
    val newValue: String = "",
    val device: String = "Android Device",
    val ipAddress: String = "192.168.1.1"
)

@Entity(tableName = "active_sessions")
data class ActiveSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val deviceId: String,
    val deviceName: String,
    val ipAddress: String,
    val location: String,
    val loginTime: Long = System.currentTimeMillis(),
    val isCurrent: Boolean = false
)
