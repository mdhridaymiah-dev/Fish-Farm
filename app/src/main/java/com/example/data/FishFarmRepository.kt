package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FishFarmRepository(private val db: AppDatabase) {

    // DAOs
    private val projectDao = db.projectDao()
    private val userDao = db.userDao()
    private val approvalRequestDao = db.approvalRequestDao()
    private val shareHistoryDao = db.shareHistoryDao()
    private val accountEntryDao = db.accountEntryDao()
    private val pondDao = db.pondDao()
    private val fishStockHistoryDao = db.fishStockHistoryDao()
    private val feedUsageDao = db.feedUsageDao()
    private val messageDao = db.internalMessageDao()
    private val auditLogDao = db.auditLogDao()
    private val sessionDao = db.activeSessionDao()

    // Flow getters
    fun getAllProjects(): Flow<List<Project>> = projectDao.getAllProjects()
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    fun getProjectUsers(projectId: Int): Flow<List<User>> = userDao.getUsersByProject(projectId)
    fun getAllApprovalRequests(): Flow<List<ApprovalRequest>> = approvalRequestDao.getAllApprovalRequests()
    fun getProjectApprovalRequests(projectId: Int): Flow<List<ApprovalRequest>> = approvalRequestDao.getApprovalRequestsByProject(projectId)
    fun getProjectShares(projectId: Int): Flow<List<ShareHistory>> = shareHistoryDao.getSharesByProject(projectId)
    fun getProjectEntries(projectId: Int): Flow<List<AccountEntry>> = accountEntryDao.getEntriesByProject(projectId)
    fun getProjectPonds(projectId: Int): Flow<List<Pond>> = pondDao.getPondsByProject(projectId)
    fun getPondStockHistory(pondId: Int): Flow<List<FishStockHistory>> = fishStockHistoryDao.getStockHistoryByPond(pondId)
    fun getProjectFeedUsage(projectId: Int): Flow<List<FeedUsage>> = feedUsageDao.getFeedUsageByProject(projectId)
    fun getAllMessages(): Flow<List<InternalMessage>> = messageDao.getAllMessages()
    fun getAuditLogs(projectId: Int): Flow<List<AuditLog>> = if (projectId == 0) auditLogDao.getAllAuditLogs() else auditLogDao.getAuditLogsByProject(projectId)
    fun getUserSessions(userId: String): Flow<List<ActiveSession>> = sessionDao.getSessionsByUser(userId)

    // User Operations
    suspend fun getUserById(userId: String): User? = userDao.getUserById(userId)
    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
        logAudit(user.id, "User Created/Updated", "User: ${user.fullName} (${user.role})", user.projectAssignmentId)
    }
    suspend fun updateUser(user: User) {
        userDao.insertUser(user) // REPLACE matches insert
    }

    // Projects Operations
    suspend fun insertProject(project: Project): Int {
        val id = projectDao.insertProject(project).toInt()
        logAudit("SYSTEM", "Project Created", "New farm project: ${project.name}", id)
        return id
    }
    suspend fun updateProject(project: Project) {
        projectDao.updateProject(project)
        logAudit("SYSTEM", "Project Updated", "Project metadata updated: ${project.name}", project.id)
    }

    // Sessions and Trackers
    suspend fun addSession(userId: String, device: String, ip: String, loc: String) {
        sessionDao.insertSession(
            ActiveSession(
                userId = userId,
                deviceId = "DEV-${System.currentTimeMillis() % 10000}",
                deviceName = device,
                ipAddress = ip,
                location = loc,
                isCurrent = true
            )
        )
    }
    suspend fun deleteSession(id: Int) = sessionDao.deleteSessionById(id)
    suspend fun logoutOtherSessions(userId: String) = sessionDao.deleteOtherSessions(userId)
    suspend fun logoutAllSessions(userId: String) = sessionDao.deleteAllSessions(userId)

    // Messaging Operations
    suspend fun sendMessage(senderId: String, receiverId: String, subject: String, body: String, priority: String): Int {
        val msgId = messageDao.insertMessage(
            InternalMessage(
                senderId = senderId,
                receiverId = receiverId,
                subject = subject,
                body = body,
                priority = priority,
                status = "Inbox",
                isRead = false
            )
        ).toInt()
        // Save copy to sent
        messageDao.insertMessage(
            InternalMessage(
                senderId = senderId,
                receiverId = receiverId,
                subject = subject,
                body = body,
                priority = priority,
                status = "Sent",
                isRead = true
            )
        )
        logAudit(senderId, "Message Sent", "Subject: $subject to $receiverId", 0)
        return msgId
    }

    suspend fun updateMessage(message: InternalMessage) = messageDao.updateMessage(message)
    suspend fun getMessageById(id: Int) = messageDao.getMessageById(id)

    // Pond & Fish operations
    suspend fun addPond(pond: Pond) = pondDao.insertPond(pond)
    suspend fun addFishStockHistory(history: FishStockHistory) {
        fishStockHistoryDao.insertStockHistory(history)
    }
    suspend fun addFeedUsage(usage: FeedUsage) = feedUsageDao.insertFeedUsage(usage)

    // Audit logs
    suspend fun logAudit(userId: String, action: String, details: String, projectId: Int = 0, oldValue: String = "", newValue: String = "") {
        auditLogDao.insertAuditLog(
            AuditLog(
                userId = userId,
                action = action,
                module = when {
                    action.contains("Login") || action.contains("Verification") || action.contains("Password") -> "Auth"
                    action.contains("Income") || action.contains("Expense") || action.contains("Accounting") -> "Accounting"
                    action.contains("Member") -> "Member"
                    action.contains("Share") -> "Shares"
                    action.contains("Pond") || action.contains("Fish") || action.contains("Feed") -> "Monitoring"
                    else -> "Approval"
                },
                projectId = projectId,
                oldValue = oldValue,
                newValue = newValue,
                device = "Android Device Simulator",
                ipAddress = "10.0.2.15"
            )
        )
    }

    // Staging and Transactional Commit Approval Workflow
    suspend fun submitRequest(
        projectId: Int,
        type: String,
        creatorId: String,
        title: String,
        stagedJsonContent: String,
        requires100Percent: Boolean
    ): Int {
        val approversCount = if (requires100Percent) 100 else 51 // flag logic
        val reqId = approvalRequestDao.insertRequest(
            ApprovalRequest(
                projectId = projectId,
                requestType = type,
                requestCreatorId = creatorId,
                createdDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                status = "Pending",
                description = title,
                detailsJson = stagedJsonContent,
                currentApproveCount = 1, // Auto-approve by creator
                approversListJson = "[\"$creatorId\"]",
                approvalRequirementType = if (requires100Percent) "100%" else "Majority",
                totalRequiredApprovers = if (requires100Percent) 5 else 3 // Demo count
            )
        ).toInt()

        logAudit(creatorId, "Approval Request Staged", "Type: $type Details: $title", projectId)
        return reqId
    }

    suspend fun castVote(userId: String, requestId: Int, approve: Boolean, reason: String = ""): Boolean {
        val request = approvalRequestDao.getRequestById(requestId) ?: return false
        if (request.status != "Pending") return false

        if (!approve) {
            // Rejection Rollback Mechanism
            val updated = request.copy(status = "Rejected", rejectionReason = reason)
            approvalRequestDao.updateRequest(updated)
            logAudit(userId, "Request Rejected", "Reason: $reason", request.projectId)
            return true
        }

        // Parse voters list
        val currentVoters = request.approversListJson
            .removeSurrounding("[", "]")
            .split(",")
            .map { it.trim().replace("\"", "") }
            .filter { it.isNotEmpty() }
            .toMutableList()

        if (userId in currentVoters) return false // No duplicate votes

        currentVoters.add(userId)
        val nextApproveCount = currentVoters.size
        val isApproved = if (request.approvalRequirementType == "100%") {
            nextApproveCount >= request.totalRequiredApprovers
        } else {
            nextApproveCount >= (request.totalRequiredApprovers / 2) + 1
        }

        val updatedStatus = if (isApproved) "Approved" else "Pending"
        val updatedMessage = request.copy(
            currentApproveCount = nextApproveCount,
            approversListJson = currentVoters.joinToString(prefix = "[", postfix = "]") { "\"$it\"" },
            status = updatedStatus
        )
        approvalRequestDao.updateRequest(updatedMessage)

        logAudit(userId, "Request Approved By Member", "Project Request ID $requestId approved", request.projectId)

        if (isApproved) {
            // Transactional Commit
            commitApprovedRequest(updatedMessage)
        }
        return true
    }

    private suspend fun commitApprovedRequest(req: ApprovalRequest) {
        val type = req.requestType
        Log.d("FishFarmRepo", "Commit approved transaction type: $type")
        when (type) {
            "Income Update" -> {
                // Parse income values (simulation or simple structure)
                accountEntryDao.insertEntry(
                    AccountEntry(
                        projectId = req.projectId,
                        type = "Income",
                        category = "Fish Sale",
                        amount = 120000.0,
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        description = "Approved: ${req.description}",
                        creatorId = req.requestCreatorId,
                        status = "Approved"
                    )
                )
            }
            "Expense Update" -> {
                accountEntryDao.insertEntry(
                    AccountEntry(
                        projectId = req.projectId,
                        type = "Expense",
                        category = "Feed",
                        amount = 85000.0,
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        description = "Approved: ${req.description}",
                        creatorId = req.requestCreatorId,
                        status = "Approved"
                    )
                )
            }
            "Member Remove" -> {
                // Suspends user
                val toRemoveUserId = req.detailsJson // Stores username/id directly in simple demo
                val target = userDao.getUserById(toRemoveUserId)
                if (target != null) {
                    userDao.insertUser(target.copy(status = "Suspended"))
                }
            }
            "Share Transfer" -> {
                // Update history
                shareHistoryDao.insertShareHistory(
                    ShareHistory(
                        projectId = req.projectId,
                        previousOwnerId = req.requestCreatorId,
                        newOwnerId = "MEM-002",
                        shareAmount = 50,
                        transferDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        purchaseValue = 250000.0,
                        approvalId = req.id,
                        status = "Approved",
                        transferReason = req.description
                    )
                )
            }
            "Rule Change" -> {
                val proj = projectDao.getProjectById(req.projectId)
                if (proj != null) {
                    projectDao.updateProject(proj.copy(defaultApprovalMode = "100%"))
                }
            }
        }
        logAudit("SYSTEM", "Transaction Committed", "Request ID ${req.id} committed successfully", req.projectId)
    }

    // Seeding method when tables are empty
    suspend fun seedIfNeeded() {
        val existingUsers = userDao.getAllUsers().first()
        if (existingUsers.isNotEmpty()) {
            return
        }

        Log.d("FishFarmRepo", "Seeding initial enterprise-grade data")

        // 1. Projects
        projectDao.insertProject(Project(id = 1, name = "Green Fish Farm", logo = "", defaultApprovalMode = "Majority", thresholdAmount = 50000.0, totalShares = 500, shareValue = 5000.0))
        projectDao.insertProject(Project(id = 2, name = "Golden Pond Ltd", logo = "", defaultApprovalMode = "100%", thresholdAmount = 10000.0, totalShares = 200, shareValue = 8000.0))

        // 2. Initial Users
        // Default Super Admin password check is "11", hash check standard BCrypt simulation md5/direct
        userDao.insertUser(
            User(
                id = "SUPER-001",
                username = "admin",
                fullName = "Super Admin Md. Rakib Hasan",
                mobile = "+8801712345678",
                email = "mdhridaymiah@gmail.com",
                role = "Super Admin",
                projectAssignmentId = 0,
                isFirstLogin = true, // Force setup wizard
                isVerified = false,
                currentPasswordHash = "11", // Default password rule
                defaultPasswordActive = true
            )
        )

        userDao.insertUser(
            User(
                id = "ADMIN-001",
                username = "rakib_project",
                fullName = "Md. Rakib Hasan",
                mobile = "+8801712345678",
                email = "rakib.hasan@example.com",
                role = "Project Admin",
                projectAssignmentId = 1,
                isFirstLogin = false, // verified already
                isVerified = true,
                currentPasswordHash = "admin123",
                defaultPasswordActive = false
            )
        )

        userDao.insertUser(
            User(
                id = "MEM-001",
                username = "sabbir",
                fullName = "Sabbir Hossain",
                mobile = "+8801555555555",
                email = "sabbir@example.com",
                role = "Member",
                projectAssignmentId = 1,
                isFirstLogin = false,
                isVerified = true,
                currentPasswordHash = "member123",
                defaultPasswordActive = false
            )
        )

        userDao.insertUser(
            User(
                id = "AUD-001",
                username = "auditor",
                fullName = "Masud Rana (Auditor)",
                mobile = "+8801888888888",
                email = "masud@example.com",
                role = "Auditor",
                projectAssignmentId = 1,
                isFirstLogin = false,
                isVerified = true,
                currentPasswordHash = "auditor123",
                defaultPasswordActive = false
            )
        )

        userDao.insertUser(
            User(
                id = "MEM-002",
                username = "nur_alam",
                fullName = "Nur Alam",
                mobile = "+8801999999999",
                email = "nur@example.com",
                role = "Member",
                projectAssignmentId = 1,
                isFirstLogin = false,
                isVerified = true,
                currentPasswordHash = "member123",
                defaultPasswordActive = false
            )
        )

        // 3. Accounting entries (Approved list for Dashboard display matching images)
        accountEntryDao.insertEntry(AccountEntry(projectId = 1, type = "Income", category = "Fish Sale", amount = 345220.0, date = "2026-05-24", description = "Rui & Katla spring harvest sale", creatorId = "ADMIN-001", status = "Approved"))
        accountEntryDao.insertEntry(AccountEntry(projectId = 1, type = "Income", category = "Fry Sale", amount = 223680.0, date = "2026-05-25", description = "Tilapia fry wholesale supply", creatorId = "ADMIN-001", status = "Approved"))
        accountEntryDao.insertEntry(AccountEntry(projectId = 1, type = "Expense", category = "Feed", amount = 185000.0, date = "2026-05-26", description = "CP Feed supply order #99", creatorId = "ADMIN-001", status = "Approved"))
        accountEntryDao.insertEntry(AccountEntry(projectId = 1, type = "Expense", category = "Labor Salary", amount = 75000.0, date = "2026-05-27", description = "May farm supervisor salary payment", creatorId = "ADMIN-001", status = "Approved"))
        accountEntryDao.insertEntry(AccountEntry(projectId = 1, type = "Expense", category = "Electricity", amount = 34500.0, date = "2026-05-27", description = "Pond aerator energy bill", creatorId = "ADMIN-001", status = "Approved"))
        accountEntryDao.insertEntry(AccountEntry(projectId = 1, type = "Expense", category = "Medicine", amount = 28720.0, date = "2026-05-28", description = "Water purification lime & probiotics", creatorId = "ADMIN-001", status = "Approved"))

        // For project 2
        accountEntryDao.insertEntry(AccountEntry(projectId = 2, type = "Income", category = "Fish Sale", amount = 150000.0, date = "2026-05-20", description = "Initial harvest release", creatorId = "SUPER-001", status = "Approved"))
        accountEntryDao.insertEntry(AccountEntry(projectId = 2, type = "Expense", category = "Feed", amount = 90000.0, date = "2026-05-21", description = "Mega brand feeds", creatorId = "SUPER-001", status = "Approved"))

        // 4. Initial Ponds
        pondDao.insertPond(Pond(projectId = 1, pondNumber = "Pond 1 (Main Nursery)", size = 2.5, waterCondition = "pH 7.4, Temp 28°C, O2 6.2mg/L", fishSpecies = "Rui (Rohu) & Katla", stockQuantity = 12000, releaseDate = "2026-02-15"))
        pondDao.insertPond(Pond(projectId = 1, pondNumber = "Pond 2 (Growing Pit)", size = 4.0, waterCondition = "pH 7.2, Temp 27°C, O2 5.5mg/L", fishSpecies = "Monosex Tilapia", stockQuantity = 35000, releaseDate = "2026-03-01"))
        pondDao.insertPond(Pond(projectId = 1, pondNumber = "Pond 3 (Breeding Cell)", size = 1.0, waterCondition = "pH 7.6, Temp 29°C, O2 6.8mg/L", fishSpecies = "Thai Pangash & Silver Carp", stockQuantity = 8000, releaseDate = "2026-04-10"))

        pondDao.insertPond(Pond(projectId = 2, pondNumber = "Pond A", size = 1.8, waterCondition = "pH 7.5", fishSpecies = "Shrimp", stockQuantity = 50000, releaseDate = "2026-01-10"))

        // Stock histories
        fishStockHistoryDao.insertStockHistory(FishStockHistory(pondId = 1, quantity = 12000, species = "Rui", weight = 180.0, growthRate = "15g/week", mortality = 240, harvestStatus = "Stocked", date = "2026-05-20"))
        fishStockHistoryDao.insertStockHistory(FishStockHistory(pondId = 2, quantity = 35000, species = "Tilapia", weight = 85.0, growthRate = "22g/week", mortality = 1020, harvestStatus = "Stocked", date = "2026-05-22"))
        fishStockHistoryDao.insertStockHistory(FishStockHistory(pondId = 3, quantity = 8000, species = "Pangash", weight = 420.0, growthRate = "30g/week", mortality = 10, harvestStatus = "Stocked", date = "2026-05-24"))

        // Feed logic
        feedUsageDao.insertFeedUsage(FeedUsage(projectId = 1, feedType = "Nursery Crumbs", brand = "Mega Feed", quantity = 350.0, cost = 28000.0, dailyConsumption = 50.0, date = "2026-05-26"))
        feedUsageDao.insertFeedUsage(FeedUsage(projectId = 1, feedType = "Growth Pellets", brand = "CP Feed", quantity = 1200.0, cost = 96000.0, dailyConsumption = 120.0, date = "2026-05-27"))

        // 5. Active Sessions
        sessionDao.insertSession(ActiveSession(userId = "ADMIN-001", deviceId = "SM-N986B", deviceName = "Samsung Galaxy Note 20 Ultra", ipAddress = "192.168.1.100", location = "Dhaka, Bangladesh", isCurrent = true))
        sessionDao.insertSession(ActiveSession(userId = "ADMIN-001", deviceId = "CHROME-WINDOWS", deviceName = "Chrome Browser (Windows PC)", ipAddress = "113.11.23.45", location = "Dhaka, Bangladesh", isCurrent = false))

        // 6. Messages Setup (Matches phone mockup text)
        messageDao.insertMessage(InternalMessage(senderId = "SUPER-001", receiverId = "ADMIN-001", subject = "Monthly Financial Report", body = "Please submit the monthly financial audit by tomorrow morning. Crucial for shareholder review.", priority = "Important", status = "Inbox"))
        messageDao.insertMessage(InternalMessage(senderId = "MEM-001", receiverId = "ADMIN-001", subject = "Pond Update", body = "Pond 3 fish growth looks really good. Feed adjustment might be needed as weight surpassed 400g average.", priority = "Normal", status = "Inbox"))
        messageDao.insertMessage(InternalMessage(senderId = "AUD-001", receiverId = "ADMIN-001", subject = "Accounting Correction", body = "Found a discrepancy in electricity bill details. Please review invoice approval request #20.", priority = "Important", status = "Inbox"))
        messageDao.insertMessage(InternalMessage(senderId = "MEM-002", receiverId = "ADMIN-001", subject = "Share Transfer Request", body = "Requesting transfer of 50 shares to Member 003. Paperwork attached.", priority = "Urgent", status = "Inbox"))
        messageDao.insertMessage(InternalMessage(senderId = "ADMIN-001", receiverId = "SUPER-001", subject = "Daily Harvest Notice", body = "Pond 1 harvest completed successfully. Final packing count sent.", priority = "Normal", status = "Sent"))

        // 7. Core Approval Requests
        approvalRequestDao.insertRequest(
            ApprovalRequest(
                id = 1,
                projectId = 1,
                requestType = "Expense Update",
                requestCreatorId = "ADMIN-001",
                createdDate = "2026-05-28 10:30",
                status = "Pending",
                description = "Purchase of CP Grow-Out Feed (3 Tons)",
                detailsJson = "CP Feed - growth stage 3",
                currentApproveCount = 1,
                totalRequiredApprovers = 3,
                approversListJson = "[\"ADMIN-001\"]",
                approvalRequirementType = "Majority"
            )
        )
        approvalRequestDao.insertRequest(
            ApprovalRequest(
                id = 2,
                projectId = 1,
                requestType = "Share Transfer",
                requestCreatorId = "MEM-001",
                createdDate = "2026-05-28 11:15",
                status = "Pending",
                description = "Transfer of 50 Shares from Sabbir to Nur Alam",
                detailsJson = "50 Shares",
                currentApproveCount = 1,
                totalRequiredApprovers = 5,
                approversListJson = "[\"MEM-001\"]",
                approvalRequirementType = "100%"
            )
        )
        approvalRequestDao.insertRequest(
            ApprovalRequest(
                id = 3,
                projectId = 1,
                requestType = "Member Remove",
                requestCreatorId = "ADMIN-001",
                createdDate = "2026-05-28 12:00",
                status = "Pending",
                description = "Removal request for inactive shareholder",
                detailsJson = "MEM-002",
                currentApproveCount = 2,
                totalRequiredApprovers = 5,
                approversListJson = "[\"ADMIN-001\", \"MEM-001\"]",
                approvalRequirementType = "100%"
            )
        )

        // 8. Seeding Audit Logs
        logAudit("SYSTEM", "System Initialized", "Enterprise software configured with default setups", 0)
        logAudit("SUPER-001", "Default Account Setup", "Setup default credentials pending verification", 0)
    }
}
