package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FishFarmViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = FishFarmRepository(db)

    // Language State
    private val _language = MutableStateFlow("English") // "English" or "বাংলা"
    val language: StateFlow<String> = _language.asStateFlow()

    // Authentication States
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Auth screen steps: "LOGIN_ID_PASS", "VERIFICATION", "FIRST_TIME_WIZARD", "DASHBOARD"
    private val _authState = MutableStateFlow("LOGIN_ID_PASS")
    val authState: StateFlow<String> = _authState.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Verification Inputs
    private val _sentOtp = MutableStateFlow("")
    val sentOtp: StateFlow<String> = _sentOtp.asStateFlow()
    private val _sentEmailCode = MutableStateFlow("")
    val sentEmailCode: StateFlow<String> = _sentEmailCode.asStateFlow()

    private val _isRememberDevice = MutableStateFlow(false)
    val isRememberDevice: StateFlow<Boolean> = _isRememberDevice.asStateFlow()

    // Failed tracking rules
    private val _failedAttempts = MutableStateFlow(0)
    val failedAttempts: StateFlow<Int> = _failedAttempts.asStateFlow()
    private val _cooldownTime = MutableStateFlow(0) // Cool down seconds
    val cooldownTime: StateFlow<Int> = _cooldownTime.asStateFlow()

    // Active project state
    private val _selectedProjectId = MutableStateFlow(1)
    val selectedProjectId: StateFlow<Int> = _selectedProjectId.asStateFlow()

    // Active screen navigation inside Dashboard
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Messages Category tab
    private val _messageTab = MutableStateFlow("Inbox") // "Inbox", "Sent", "Draft", "Starred"
    val messageTab: StateFlow<String> = _messageTab.asStateFlow()

    // Flow Data exposes
    val projects: StateFlow<List<Project>> = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val users: StateFlow<List<User>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessages: StateFlow<List<InternalMessage>> = repository.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val approvalRequests: StateFlow<List<ApprovalRequest>> = repository.getAllApprovalRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = repository.getAuditLogs(0)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sub-data observers driven by active project ID
    val projectUsers: StateFlow<List<User>> = _selectedProjectId
        .flatMapLatest { id -> repository.getProjectUsers(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projectEntries: StateFlow<List<AccountEntry>> = _selectedProjectId
        .flatMapLatest { id -> repository.getProjectEntries(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projectPonds: StateFlow<List<Pond>> = _selectedProjectId
        .flatMapLatest { id -> repository.getProjectPonds(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projectFeedUsage: StateFlow<List<FeedUsage>> = _selectedProjectId
        .flatMapLatest { id -> repository.getProjectFeedUsage(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            // Seeding database
            repository.seedIfNeeded()
            startCooldownTimer()
        }
    }

    // Languages toggle
    fun toggleLanguage() {
        _language.value = if (_language.value == "English") "বাংলা" else "English"
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    // Navigation helper
    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun selectProject(id: Int) {
        _selectedProjectId.value = id
    }

    // Cooldown handler
    private fun startCooldownTimer() {
        viewModelScope.launch {
            while (true) {
                if (_cooldownTime.value > 0) {
                    _cooldownTime.value -= 1
                }
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    // User authentication flow
    fun login(usernameInput: String, passwordInput: String, rememberDevice: Boolean) {
        if (_cooldownTime.value > 0) {
            val error = if (_language.value == "বাংলা") "দয়া করে ${_cooldownTime.value} সেকেন্ড অপেক্ষা করুন।" else "Please wait ${_cooldownTime.value} seconds."
            _loginError.value = error
            return
        }

        viewModelScope.launch {
            val user = repository.getUserByUsername(usernameInput.trim())
            if (user == null) {
                _failedAttempts.value += 1
                triggerWrongLoginText()
                return@launch
            }

            if (user.status == "Suspended") {
                val error = if (_language.value == "বাংলা") "আপনার অ্যাকাউন্টটি সাময়িকভাবে স্থগিত।" else "Your account is temporarily suspended."
                _loginError.value = error
                return@launch
            }

            // Check passwords (Plain simulation or default 11 verification rules code)
            if (user.currentPasswordHash == passwordInput) {
                _failedAttempts.value = 0
                _currentUser.value = user
                _isRememberDevice.value = rememberDevice

                _sentOtp.value = (100000..999999).random().toString()
                _sentEmailCode.value = (1000..9999).random().toString()

                repository.logAudit(user.id, "Login Succeeded", "Credential matched. Initiating verification.", user.projectAssignmentId)

                // Navigation check
                if (user.isFirstLogin) {
                    _authState.value = "FIRST_TIME_WIZARD"
                } else {
                    _authState.value = "VERIFICATION"
                }
                _loginError.value = null
            } else {
                _failedAttempts.value += 1
                if (_failedAttempts.value >= 3) {
                    _cooldownTime.value = 30 // 30 sec lockdown
                }
                triggerWrongLoginText()
            }
        }
    }

    private fun triggerWrongLoginText() {
        val countLeft = 5 - (_failedAttempts.value % 5)
        _loginError.value = if (_language.value == "বাংলা") {
            "পাসওয়ার্ড বা ইউজারনেম ভুল হয়েছে। অনুগ্রহ করে সঠিক পাসওয়ার্ড দিন। (বাকি চেষ্টা: $countLeft)"
        } else {
            "Incorrect Username or Password. Please enter the correct details. (Retries left: $countLeft)"
        }
    }

    // OTP Verification submit
    fun verifyOtpAndEmail(otp: String, emailCode: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            if (otp == _sentOtp.value || otp == "123456" || _isRememberDevice.value) { // "123456" for demo testing ease
                // success
                val updated = user.copy(isVerified = true)
                repository.insertUser(updated)
                _currentUser.value = updated

                // Track Session details
                repository.addSession(user.id, "Android Mobile Emulator", "10.0.2.16", "Dhaka, Bangladesh")
                repository.logAudit(user.id, "Device Verified", "OTP verification completed. Session authorized.", user.projectAssignmentId)

                // Select assigned project if any
                if (user.projectAssignmentId > 0) {
                    _selectedProjectId.value = user.projectAssignmentId
                }

                _authState.value = "DASHBOARD"
                _loginError.value = null
            } else {
                _loginError.value = if (_language.value == "বাংলা") "ভুল ওটিপি কোড। অনুগ্রহ করে সঠিক কোড দিন।" else "Invalid Verification Code. Please try again."
            }
        }
    }

    // Super Admin / Member Initial Setup force passcode change
    fun updateFirstTimeSetup(fullName: String, email: String, phone: String, newPass: String) {
        val user = _currentUser.value ?: return

        // Policy match: 12 chars, upper, lower, num, spec
        val isValid = newPass.length >= 12 &&
                newPass.any { it.isUpperCase() } &&
                newPass.any { it.isLowerCase() } &&
                newPass.any { it.isDigit() } &&
                newPass.any { !it.isLetterOrDigit() }

        if (!isValid) {
            _loginError.value = if (_language.value == "বাংলা") {
                "পাসওয়ার্ডটি অবশ্যই ন্যূনতম ১২ অক্ষরের হতে হবে এবং এতে বড় হাতের অক্ষর, ছোট হাতের অক্ষর, সংখ্যা ও বিশেষ চিহ্ন থাকতে হবে।"
            } else {
                "Password must be at least 12 characters, including uppercase, lowercase, numbers, and special characters."
            }
            return
        }

        viewModelScope.launch {
            val updated = user.copy(
                fullName = fullName,
                email = email,
                mobile = phone,
                currentPasswordHash = newPass,
                isFirstLogin = false,
                isVerified = true,
                defaultPasswordActive = false
            )
            repository.insertUser(updated)
            _currentUser.value = updated
            repository.logAudit(user.id, "First Login wizard complete", "Credentials updated and default '11' revoked permanently.", user.projectAssignmentId)

            _authState.value = "DASHBOARD"
            _loginError.value = null
        }
    }

    // Regular manual password change
    fun changePassword(current: String, newP: String, confirm: String, onSuccess: () -> Unit) {
        val user = _currentUser.value ?: return
        if (user.currentPasswordHash != current) {
            _loginError.value = if (_language.value == "বাংলা") "বর্তমান পাসওয়ার্ডটি সঠিক নয়।" else "Current password is incorrect."
            return
        }

        val isValid = newP.length >= 12 &&
                newP.any { it.isUpperCase() } &&
                newP.any { it.isLowerCase() } &&
                newP.any { it.isDigit() } &&
                newP.any { !it.isLetterOrDigit() }

        if (!isValid) {
            _loginError.value = if (_language.value == "বাংলা") {
                "নতুন পাসওয়ার্ডটি শক্তিশালী হতে হবে (১২+ অক্ষর, বড়/ছোট হাতের অক্ষর, সংখ্যা, বিশেষ চিহ্ন)।"
            } else {
                "New password must be strong (12+ characters, upper/lower details, digit, symbol)."
            }
            return
        }

        if (newP != confirm) {
            _loginError.value = if (_language.value == "বাংলা") "নতুন পাসওয়ার্ডদ্বয় মেলেনি।" else "Confirm password does not match new password."
            return
        }

        viewModelScope.launch {
            val updated = user.copy(currentPasswordHash = newP)
            repository.insertUser(updated)
            _currentUser.value = updated
            repository.logAudit(user.id, "Password Changed Successfully", "User changed their password from Profile Settings.", user.projectAssignmentId)
            _loginError.value = null
            onSuccess()
        }
    }

    // Submit a new transactional request in Draft System
    fun createAccountingRequest(category: String, amount: Double, desc: String, isIncome: Boolean, requires100Percent: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val title = if (isIncome) {
                "Add Income Entry: $category ৳$amount"
            } else {
                "Add Expense Entry: $category ৳$amount"
            }
            val details = "Category: $category, Amount: $amount, Description: $desc"
            val type = if (isIncome) "Income Update" else "Expense Update"

            repository.submitRequest(
                projectId = _selectedProjectId.value,
                type = type,
                creatorId = user.id,
                title = title,
                stagedJsonContent = details,
                requires100Percent = requires100Percent
            )

            navigateTo("approvals") // Route to active approvals page
        }
    }

    fun removeMemberRequest(memberId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.submitRequest(
                projectId = _selectedProjectId.value,
                type = "Member Remove",
                creatorId = user.id,
                title = "Revoke and Suspend Member Account: $memberId",
                stagedJsonContent = memberId,
                requires100Percent = true // 100% standard for removing members
            )
            navigateTo("approvals")
        }
    }

    fun requestShareTransfer(sender: String, toOwner: String, shares: Int) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.submitRequest(
                projectId = _selectedProjectId.value,
                type = "Share Transfer",
                creatorId = user.id,
                title = "Transfer $shares shares from $sender to $toOwner",
                stagedJsonContent = "Shares: $shares, To: $toOwner",
                requires100Percent = true // Required for share redistribution
            )
            navigateTo("approvals")
        }
    }

    // Super Admin Create New Project
    fun createNewProject(name: String, appMode: String) {
        viewModelScope.launch {
            repository.insertProject(
                Project(
                    name = name,
                    defaultApprovalMode = appMode,
                    totalShares = 1000,
                    shareValue = 10000.0
                )
            )
        }
    }

    // Add a new pond for the active project
    fun addPond(pondNumber: String, size: Double, waterCondition: String, fishSpecies: String, stockQuantity: Int, releaseDate: String) {
        val currentProjId = _selectedProjectId.value
        viewModelScope.launch {
            val nextPond = Pond(
                projectId = currentProjId,
                pondNumber = pondNumber,
                size = size,
                waterCondition = waterCondition,
                fishSpecies = fishSpecies,
                stockQuantity = stockQuantity,
                releaseDate = releaseDate
            )
            repository.addPond(nextPond)
            repository.logAudit(
                userId = _currentUser.value?.id ?: "UNKNOWN",
                action = "Pond Added",
                details = "Pond $pondNumber with $stockQuantity $fishSpecies",
                projectId = currentProjId
            )
        }
    }

    // Add feed usage log
    fun addFeedUsage(feedType: String, brand: String, quantity: Double, cost: Double, dailyConsumption: Double, date: String) {
        val currentProjId = _selectedProjectId.value
        viewModelScope.launch {
            val usage = FeedUsage(
                projectId = currentProjId,
                feedType = feedType,
                brand = brand,
                quantity = quantity,
                cost = cost,
                dailyConsumption = dailyConsumption,
                date = date
            )
            repository.addFeedUsage(usage)
            repository.logAudit(
                userId = _currentUser.value?.id ?: "UNKNOWN",
                action = "Feed Added",
                details = "$feedType ($brand) - $quantity kg",
                projectId = currentProjId
            )

            // Cohesively add to accounting entries as Expense!
            val entry = AccountEntry(
                projectId = currentProjId,
                type = "Expense",
                category = "Feed",
                amount = cost,
                date = date,
                description = "Auto recorded: Feed usage ($feedType by $brand)",
                creatorId = _currentUser.value?.id ?: "SUPER-001",
                status = "Approved"
            )
            // Save to database
            db.accountEntryDao().insertEntry(entry)
        }
    }

    // Update pond water conditions with pH, Temp, etc.
    fun updatePondWaterCondition(pondId: Int, newWaterCondition: String) {
        val currentProjId = _selectedProjectId.value
        viewModelScope.launch {
            val allPonds = repository.getProjectPonds(currentProjId).first()
            val targetPond = allPonds.find { it.id == pondId }
            if (targetPond != null) {
                val updated = targetPond.copy(waterCondition = newWaterCondition)
                repository.addPond(updated) // REPLACE operation
                repository.logAudit(
                    userId = _currentUser.value?.id ?: "UNKNOWN",
                    action = "Water Quality Updated",
                    details = "Pond ${targetPond.pondNumber}: $newWaterCondition",
                    projectId = currentProjId
                )
            }
        }
    }

    // Add stock history log / Sampling or mortality tracking or growth rates
    fun addFishStockHistory(pondId: Int, quantity: Int, species: String, weight: Double, growthRate: String, mortality: Int, date: String) {
        val currentProjId = _selectedProjectId.value
        viewModelScope.launch {
            val history = FishStockHistory(
                pondId = pondId,
                quantity = quantity,
                species = species,
                weight = weight,
                growthRate = growthRate,
                mortality = mortality,
                harvestStatus = "Stocked",
                date = date
            )
            repository.addFishStockHistory(history)
            
            // Also deduct mortality from total pond stock count if needed (or just log it)
            val allPonds = repository.getProjectPonds(currentProjId).first()
            val targetPond = allPonds.find { it.id == pondId }
            if (targetPond != null && mortality > 0) {
                val nextStock = maxOf(0, targetPond.stockQuantity - mortality)
                repository.addPond(targetPond.copy(stockQuantity = nextStock))
            }
            
            repository.logAudit(
                userId = _currentUser.value?.id ?: "UNKNOWN",
                action = "Stock/Sampling Logged",
                details = "Pond ID $pondId: $species average weight $weight g, mortality $mortality",
                projectId = currentProjId
            )
        }
    }

    // Super Admin Create Project User / Superuser ID
    fun createProjectUser(username: String, fullName: String, role: String, projectId: Int, rawPassword: String, mobile: String, email: String) {
        viewModelScope.launch {
            val prefix = when(role) {
                "Super Admin" -> "SUPER"
                "Project Admin" -> "ADMIN"
                "Member" -> "MEM"
                else -> "USR"
            }
            val uniqueId = "$prefix-${(10000..99999).random()}"
            val newUser = User(
                id = uniqueId,
                username = username.trim().lowercase(Locale.getDefault()),
                fullName = fullName,
                mobile = mobile,
                email = email,
                role = role,
                projectAssignmentId = if (role == "Super Admin") 0 else projectId,
                isFirstLogin = true, // Forces first-time setup / passcode change
                isVerified = false,
                currentPasswordHash = rawPassword,
                defaultPasswordActive = true
            )
            repository.insertUser(newUser)
        }
    }

    // Reset database to completely fresh state
    fun resetDatabase() {
        viewModelScope.launch {
            try {
                // Clear all database tables
                db.clearAllTables()

                // Re-seed ONLY the default pristine Super Admin account
                repository.insertUser(
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
                        currentPasswordHash = "11",
                        defaultPasswordActive = true
                    )
                )

                // Log out & go to login screen
                _currentUser.value = null
                _authState.value = "LOGIN_ID_PASS"
                _currentScreen.value = "home"
                _loginError.value = if (_language.value == "বাংলা") {
                    "সফটওয়্যার টি সফলভাবে রিসেট করা হয়েছে। অনুগ্রহ করে ইউজারনেম 'admin' এবং পাসওয়ার্ড '11' দিয়ে লগইন করে নতুন প্রজেক্ট ও হিসাব শুরু করুন।"
                } else {
                    "Software successfully reset. Please log in with username 'admin' and password '11' to initialize fresh projects & ledger books."
                }
            } catch (e: Exception) {
                Log.e("ResetError", "Error resetting database: ${e.message}")
            }
        }
    }

    // Casting vote: Approved or Rejected
    fun voteRequest(requestId: Int, approve: Boolean, reason: String = "") {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.castVote(user.id, requestId, approve, reason)
        }
    }

    // Sessions View
    val activeSessions = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getUserSessions(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun terminateSession(id: Int) {
        viewModelScope.launch {
            repository.deleteSession(id)
        }
    }

    fun terminateOtherSessions() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.logoutOtherSessions(user.id)
            repository.logAudit(user.id, "Sessions Terminated", "All concurrent device sessions logged out.", user.projectAssignmentId)
        }
    }

    // Messaging tabs composing
    fun setMessageTab(tab: String) {
        _messageTab.value = tab
    }

    fun sendInternalMessage(toId: String, subject: String, body: String, priority: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.sendMessage(user.id, toId, subject, body, priority)
        }
    }

    fun toggleStarMessage(msg: InternalMessage) {
        viewModelScope.launch {
            repository.updateMessage(msg.copy(isStarred = !msg.isStarred))
        }
    }

    fun deleteMessage(msg: InternalMessage) {
        viewModelScope.launch {
            repository.updateMessage(msg.copy(status = "Trash"))
        }
    }

    // Single click trigger to read a message
    fun readMessage(msg: InternalMessage) {
        viewModelScope.launch {
            repository.updateMessage(msg.copy(isRead = true))
        }
    }

    // Forgot password workflow
    private val _recoveryStep = MutableStateFlow(1) // 1: Inputs, 2: Verification, 3: Success New Pass
    val recoveryStep: StateFlow<Int> = _recoveryStep.asStateFlow()

    fun resetRecoveryStep() {
        _recoveryStep.value = 1
        _loginError.value = null
    }

    fun requestPasswordRecovery(username: String, mobile: String, email: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username.trim())
            if (user != null && user.mobile == mobile && user.email == email) {
                // success inputs
                _currentUser.value = user
                _sentOtp.value = (100000..999999).random().toString()
                _sentEmailCode.value = (1000..9999).random().toString()
                _recoveryStep.value = 2
                _loginError.value = null
                Log.d("Recovery", "Codes: Email OTP ${_sentEmailCode.value}, Phone OTP ${_sentOtp.value}")
            } else {
                _loginError.value = if (_language.value == "বাংলা") "ইউজারনেম, মোবাইল নাম্বার অথবা ইমেইল ভুল হয়েছে।" else "Credentials mismatched. Reset request rejected."
            }
        }
    }

    fun submitNewPasswordRecovery(phoneOtp: String, emailOtp: String, newPass: String) {
        val user = _currentUser.value ?: return
        if (phoneOtp != _sentOtp.value || emailOtp != _sentEmailCode.value) {
            _loginError.value = if (_language.value == "বাংলা") "ভুল নিরাপত্তা কোড দেওয়া হয়েছে।" else "Incorrect verification codes."
            return
        }

        val isValid = newPass.length >= 12 &&
                newPass.any { it.isUpperCase() } &&
                newPass.any { it.isLowerCase() } &&
                newPass.any { it.isDigit() } &&
                newPass.any { !it.isLetterOrDigit() }

        if (!isValid) {
            _loginError.value = if (_language.value == "বাংলা") {
                "নিরাপত্তা নীতি অনুযায়ী পাসওয়ার্ড ন্যূনতম ১২টি অক্ষর হতে হবে।"
            } else {
                "Security policy requires password to be 12+ characters."
            }
            return
        }

        viewModelScope.launch {
            val updated = user.copy(currentPasswordHash = newPass, isVerified = true, isFirstLogin = false)
            repository.insertUser(updated)
            _currentUser.value = updated
            // Terminate all sessions
            repository.logoutAllSessions(user.id)
            repository.logAudit(user.id, "Passkey Reset Recovery Mode", "Password reset succeeded, all sessions terminated securely.", user.projectAssignmentId)

            _recoveryStep.value = 3
            _loginError.value = null
        }
    }

    // Complete session secure logout
    fun logout() {
        val user = _currentUser.value
        viewModelScope.launch {
            if (user != null) {
                repository.logAudit(user.id, "Logout Succeeded", "Closed user dashboard session securely.", user.projectAssignmentId)
                repository.logoutAllSessions(user.id)
            }
            _currentUser.value = null
            _authState.value = "LOGIN_ID_PASS"
            _currentScreen.value = "home"
        }
    }
}
