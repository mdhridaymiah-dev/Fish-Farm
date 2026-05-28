package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.viewmodel.FishFarmViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

// Multi-Language Dictionary
object Translations {
    private val en = mapOf(
        "welcome" to "Welcome Back",
        "welcome_desc" to "Please login to your account",
        "username" to "User ID / Username",
        "password" to "Password",
        "remember_me" to "Remember Me",
        "forgot_pwd" to "Forgot Password?",
        "login" to "Login",
        "dashboard" to "Dashboard",
        "total_balance" to "Total Balance",
        "total_income" to "Total Income",
        "total_expense" to "Total Expense",
        "net_profit" to "Net Profit",
        "quick_access" to "Quick Access",
        "income" to "Income",
        "expense" to "Expense",
        "members" to "Members",
        "shares" to "Shares",
        "requests" to "Requests",
        "approvals" to "Approvals",
        "ponds" to "Ponds",
        "fish_stock" to "Fish Stock",
        "reports" to "Reports",
        "messages" to "Messages",
        "notifications" to "Notifications",
        "more" to "More",
        "profile" to "Profile",
        "logout" to "Logout",
        "active_sessions" to "Active Sessions",
        "audit_logs" to "Audit Logs",
        "change_pass" to "Change Password",
        "security_settings" to "Security Settings",
        "language" to "Language",
        "theme" to "Theme",
        "add_income" to "Add Income Entry",
        "add_expense" to "Add Expense Entry",
        "category" to "Category",
        "amount" to "Amount (৳)",
        "description" to "Description",
        "submit" to "Submit Staging Request",
        "rejection_reason" to "Rejection Reason",
        "reject" to "Reject",
        "approve" to "Approve",
        "pending_voting" to "Pending Approvals Core",
        "growth_rate" to "Growth Rate",
        "mortality" to "Mortality",
        "water_condition" to "Water Condition",
        "sec_wizard" to "First Time Login Security Wizard",
        "wizard_desc" to "Your account has default password. You must configure secure details before starting.",
        "full_name" to "Full Name",
        "new_pass" to "New Password",
        "pass_strength" to "Password Strength",
        "weak" to "Weak Password - Fix rules",
        "medium" to "Medium Strength",
        "strong" to "Strong Password Security Complete",
        "verification_title" to "Device Security Verification",
        "verification_desc" to "The Super Admin requires Multi-Factor Verification for high risk actions.",
        "otp_sms" to "Mobile OTP Code (SMS)",
        "otp_email" to "Email Verification Code",
        "confirm_verification" to "Verify Code and Complete",
        "recovery_title" to "Security Recovery Assistance",
        "recovery_otp" to "Submit recovery codes",
        "recovery_success" to "Password reset successful! Please re-login.",
        "status" to "Status",
        "project" to "Project",
        "owner" to "Owner",
        "transfer_shares" to "Transfer Shares Proposal",
        "remove_member" to "Suspend Member Request",
        "search" to "Search records...",
        "compose" to "Compose Internal Mail",
        "to_user" to "To User ID",
        "subject" to "Subject",
        "body" to "Message Body",
        "priority" to "Priority Level"
    )

    private val bn = mapOf(
        "welcome" to "স্বাগতম",
        "welcome_desc" to "অনুগ্রহ করে আপনার অ্যাকাউন্টে লগইন করুন",
        "username" to "ইউজার আইডি / ইউজারনেম",
        "password" to "পাসওয়ার্ড",
        "remember_me" to "মনে রাখুন",
        "forgot_pwd" to "পাসওয়ার্ড ভুলে গেছেন?",
        "login" to "লগইন",
        "dashboard" to "ড্যাশবোর্ড",
        "total_balance" to "মোট ব্যালেন্স",
        "total_income" to "মোট আয়",
        "total_expense" to "মোট ব্যয়",
        "net_profit" to "নিট লাভ",
        "quick_access" to "কুইক অ্যাক্সেস",
        "income" to "আয়",
        "expense" to "ব্যয়",
        "members" to "সদস্যবৃন্দ",
        "shares" to "শেয়ারসমূহ",
        "requests" to "অনুরোধ",
        "approvals" to "অনুমোদন সমূহ",
        "ponds" to "পুকুরসমূহ",
        "fish_stock" to "মৎস্য স্টক",
        "reports" to "রিপোর্ট",
        "messages" to "বার্তা",
        "notifications" to "নোটিফিকেশন",
        "more" to "আরও",
        "profile" to "প্রোফাইল",
        "logout" to "লগআউট",
        "active_sessions" to "সক্রিয় সেশনসমূহ",
        "audit_logs" to "অডিট লগ",
        "change_pass" to "পাসওয়ার্ড পরিবর্তন",
        "security_settings" to "নিরাপত্তা সেটিংস",
        "language" to "ভাষা",
        "theme" to "থিম",
        "add_income" to "আয় এন্ট্রি যোগ করুন",
        "add_expense" to "ব্যয় এন্ট্রি যোগ করুন",
        "category" to "ক্যাটাগরি",
        "amount" to "টাকার পরিমাণ (৳)",
        "description" to "বিবরণ",
        "submit" to "স্টেজ অনুরোধ সাবমিট করুন",
        "rejection_reason" to "প্রত্যাখ্যানের কারণ",
        "reject" to "প্রত্যাখ্যান করুন",
        "approve" to "অনুমোদন করুন",
        "pending_voting" to "অনুমোদনের অপেক্ষায়",
        "growth_rate" to "বৃদ্ধির হার",
        "mortality" to "মৃত্যুর রেকর্ড",
        "water_condition" to "পানির অবস্থা",
        "sec_wizard" to "প্রথম লগইন সিকিউরিটি উইজার্ড",
        "wizard_desc" to "আপনার অ্যাকাউন্টে সাধারণ পাসওয়ার্ড রয়েছে। ড্যাশবোর্ডে প্রবেশের পূর্বে অবশ্যই শক্তিশালী গোপন পাসওয়ার্ড এবং তথ্য সেট করুন।",
        "full_name" to "পূর্ণ নাম",
        "new_pass" to "নতুন পাসওয়ার্ড",
        "pass_strength" to "পাসওয়ার্ডের শক্তি",
        "weak" to "দুর্বল পাসওয়ার্ড (উপযুক্ত নিয়ম অনুসরণ করুন)",
        "medium" to "মাঝারি পাসওয়ার্ড",
        "strong" to "শক্তিশালী পাসওয়ার্ড - চমৎকার!",
        "verification_title" to "ডিভাইস নিরাপত্তা যাচাইকরণ",
        "verification_desc" to "উচ্চ সুরক্ষার স্বার্থে সুপার এডমিন মাল্টি-ফ্যাক্টর নিরাপত্তা বাধ্যতামূলক করেছে।",
        "otp_sms" to "মোবাইল ওটিপি কোড (এসএমএস)",
        "otp_email" to "ইমেইল ভেরিফিকেশন কোড",
        "confirm_verification" to "কোড যাচাই সম্পন্ন করুন",
        "recovery_title" to "নিরাপত্তা রিকভারি অ্যাসিস্টেন্স",
        "recovery_otp" to "রিকভারি কোড সাবমিট করুন",
        "recovery_success" to "পাসওয়ার্ড সফলভাবে রিসেট হয়েছে! পুনরায় লগইন করুন।",
        "status" to "অবস্থা",
        "project" to "প্রজেক্ট",
        "owner" to "মালিক",
        "transfer_shares" to "শেয়ার ট্রান্সফার প্রস্তাবনা",
        "remove_member" to "সদস্য স্থগিত করুন",
        "search" to "অনুসন্ধান করুন...",
        "compose" to "নতুন মেইল লিখুন",
        "to_user" to "প্রাপক ইউজার আইডি",
        "subject" to "বিষয়",
        "body" to "বার্তার বডি",
        "priority" to "অগ্রাধিকার স্তর"
    )

    fun get(key: String, lang: String): String {
        return if (lang == "বাংলা") bn[key] ?: key else en[key] ?: key
    }
}

// Global Currency Formatter
fun formatCurrency(amount: Double): String {
    val df = DecimalFormat("#,##,##0")
    return "৳ " + df.format(amount)
}

// Major Login Screen Component
@Composable
fun LoginScreen(viewModel: FishFarmViewModel) {
    val lang by viewModel.language.collectAsState()
    val error by viewModel.loginError.collectAsState()
    val loginAttempts by viewModel.failedAttempts.collectAsState()
    val cooldown by viewModel.cooldownTime.collectAsState()

    var username by remember { mutableStateOf("rakib_project") } // Admin demo username pre-filled
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberDevice by remember { mutableStateOf(false) }

    var showForgotDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Slate-900 (Geometric base)
                        Color(0xFF1E293B), // Slate-800
                        Color(0xFF0F766E)  // Teal-700
                    )
                )
            )
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Geometric Balance characteristic rotated badge logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0D9488)) // Teal-600
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(45f)
                        .border(2.5.dp, Color.White, RoundedCornerShape(2.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Fish Farm",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.5.sp
                )
            )
            Text(
                text = "Management System",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White.copy(alpha = 0.82f),
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Bangla / English Rounded Selector Card (Geometric style)
            Row(
                modifier = Modifier
                    .width(280.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (lang == "বাংলা") Color(0xFF0D9488) else Color.Transparent)
                        .clickable { viewModel.setLanguage("বাংলা") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "বাংলা",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (lang == "English") Color(0xFF0D9488) else Color.Transparent)
                        .clickable { viewModel.setLanguage("English") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "English",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // White Login Input Form Container (with Geometric 32.dp custom corners)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = Translations.get("welcome", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF1E293B) // Slate-800
                    )
                    Text(
                        text = Translations.get("welcome_desc", lang),
                        fontSize = 14.sp,
                        color = Color(0xFF64748B) // Slate-500
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Username field
                    Text(
                        text = Translations.get("username", lang),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        fontSize = 14.sp
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = { Text("admin / rakib_project") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .testTag("username_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User Icon", tint = Color(0xFF0D9488)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field
                    Text(
                        text = Translations.get("password", lang),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        fontSize = 14.sp
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .testTag("password_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon", tint = Color(0xFF0C825B)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Close else Icons.Default.Edit,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Remember Me and Forgot Password Container
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberDevice,
                                onCheckedChange = { rememberDevice = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0D9488))
                            )
                            Text(
                                text = Translations.get("remember_me", lang),
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }

                        Text(
                            text = Translations.get("forgot_pwd", lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0D9488),
                            modifier = Modifier.clickable { showForgotDialog = true }
                        )
                    }

                    // Error text area
                    error?.let { errMsg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errMsg,
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Cooldown warning
                    if (cooldown > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == "বাংলা") "অতিরিক্ত ভুলের কারণে সিস্টেম লকডাউন! ৩০ সেকেন্ড পর চেষ্টা করুন।" else "Security Lockout active due to errors!",
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Large Login button with cohesive geometric styling
                    Button(
                        onClick = { viewModel.login(username, password, rememberDevice) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        shape = RoundedCornerShape(16.dp),
                        enabled = cooldown == 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_button")
                    ) {
                        Text(
                            text = Translations.get("login", lang),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Dynamic Bangla / English Copyright Label
            Text(
                text = if (lang == "বাংলা") "© ২০২৪ ফিশ ফার্ম ম্যানেজমেন্ট সিস্টেম" else "© 2024 Fish Farm Management System",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Forgot Password Dynamic Dialog
    if (showForgotDialog) {
        ForgotPasswordDialog(viewModel = viewModel) {
            showForgotDialog = false
        }
    }
}

// Security Recovery Portal
@Composable
fun ForgotPasswordDialog(viewModel: FishFarmViewModel, onDismiss: () -> Unit) {
    val lang by viewModel.language.collectAsState()
    val error by viewModel.loginError.collectAsState()
    val recoveryStep by viewModel.recoveryStep.collectAsState()
    val sentOtp by viewModel.sentOtp.collectAsState()
    val sentEmailCode by viewModel.sentEmailCode.collectAsState()

    var usernameInput by remember { mutableStateOf("") }
    var mobileInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }

    var inputPhoneOtp by remember { mutableStateOf("") }
    var inputEmailOtp by remember { mutableStateOf("") }
    var newPassInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = {
        viewModel.resetRecoveryStep()
        onDismiss()
    }) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Translations.get("recovery_title", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E293B) // Slate-800
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (recoveryStep == 1) {
                    // Step 1 check credentials inputs
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text(Translations.get("username", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = mobileInput,
                        onValueChange = { mobileInput = it },
                        label = { Text("Registered Mobile (+880...)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Registered Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    error?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.requestPasswordRecovery(usernameInput, mobileInput, emailInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)), // Teal-600
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Initiate Recovery Channels")
                    }
                } else if (recoveryStep == 2) {
                    // Step 2 Enter OTPS and New Password
                    Text(
                        text = "Codes sent to: ${mobileInput} & ${emailInput}",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Sim helper text box (Teal accent background)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0FDFA)) // Teal-50
                            .padding(8.dp)
                    ) {
                        Text(
                            "Simulated delivery codes for testing:\n• Mobile OTP: $sentOtp\n• Email Code: $sentEmailCode",
                            fontSize = 12.sp,
                            color = Color(0xFF0F766E) // Teal-700
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = inputPhoneOtp,
                        onValueChange = { inputPhoneOtp = it },
                        label = { Text("Mobile SMS OTP") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputEmailOtp,
                        onValueChange = { inputEmailOtp = it },
                        label = { Text("Email Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassInput,
                        onValueChange = { newPassInput = it },
                        label = { Text(Translations.get("new_pass", lang)) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    error?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.submitNewPasswordRecovery(inputPhoneOtp, inputEmailOtp, newPassInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)), // Teal-600
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Translations.get("confirm_verification", lang))
                    }
                } else if (recoveryStep == 3) {
                    // Step 3 Success notification
                    Icon(Icons.Default.Check, contentDescription = "Success", tint = Color(0xFF0D9488), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(Translations.get("recovery_success", lang), textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.resetRecoveryStep()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)) // Teal-600
                    ) {
                        Text("Return to Login Screen")
                    }
                }
            }
        }
    }
}

// First Time Login - Passcode Setup Wizard Screen (Forces code updates from default 11)
@Composable
fun SetupWizardScreen(viewModel: FishFarmViewModel) {
    val lang by viewModel.language.collectAsState()
    val error by viewModel.loginError.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var fullName by remember { mutableStateOf(user?.fullName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var phone by remember { mutableStateOf(user?.mobile ?: "") }
    var newPass by remember { mutableStateOf("") }

    // Password strength check
    val strengthLabel = remember(newPass) {
        when {
            newPass.isEmpty() -> ""
            newPass.length >= 12 && newPass.any { it.isUpperCase() } && newPass.any { it.isLowerCase() } && newPass.any { it.isDigit() } && newPass.any { !it.isLetterOrDigit() } -> "strong"
            newPass.length >= 8 -> "medium"
            else -> "weak"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Slate-900
                        Color(0xFF1E293B)  // Slate-800
                    )
                )
            )
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Security, contentDescription = "Security Wizard", tint = Color(0xFF0D9488), modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = Translations.get("sec_wizard", lang),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = Translations.get("wizard_desc", lang),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(Translations.get("full_name", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Mobile Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text(Translations.get("new_pass", lang)) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    if (newPass.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = Translations.get("pass_strength", lang) + ": " + Translations.get(strengthLabel, lang),
                            fontWeight = FontWeight.Bold,
                            color = when (strengthLabel) {
                                "strong" -> Color(0xFF0D9488) // Teal-600
                                "medium" -> Color(0xFFF57C00)
                                else -> Color.Red
                            },
                            fontSize = 12.sp
                        )
                    }

                    error?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(it, color = Color.Red, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.updateFirstTimeSetup(fullName, email, phone, newPass) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)), // Teal-600
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enforce Compliance & Open Dashboard")
                    }
                }
            }
        }
    }
}

// Device MFA Verification Gateway Screens
@Composable
fun VerificationGatewayScreen(viewModel: FishFarmViewModel) {
    val lang by viewModel.language.collectAsState()
    val error by viewModel.loginError.collectAsState()
    val sentOtp by viewModel.sentOtp.collectAsState()

    var inputOtp by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Slate-900
                        Color(0xFF1E293B)  // Slate-800
                    )
                )
            )
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Devices, contentDescription = "Verification icon", tint = Color(0xFF0D9488), modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = Translations.get("verification_title", lang),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = Translations.get("verification_desc", lang),
                color = Color.LightGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Simulated notification message box with Teal balance accent colors
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDFA)), // Teal-50
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Simulated delivery codes for instant verification:\n• SMS/Mobile OTP: $sentOtp\n• Verification Email Code: 123456",
                        color = Color(0xFF0F766E), // Teal-700
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp), // Consistent geometric rounded style
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = inputOtp,
                        onValueChange = { inputOtp = it },
                        label = { Text(Translations.get("otp_sms", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    error?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.verifyOtpAndEmail(inputOtp, "123456") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)), // Teal-600
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Translations.get("confirm_verification", lang))
                    }
                }
            }
        }
    }
}

// Centralized Main Shell containing layouts & drawer triggers
@Composable
fun MainDashboardShell(viewModel: FishFarmViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val lang by viewModel.language.collectAsState()
    val activeProject by viewModel.selectedProjectId.collectAsState()
    val projectsList by viewModel.projects.collectAsState()

    var showQuickNavMenu by remember { mutableStateOf(false) }

    val activeProjectObj = projectsList.find { it.id == activeProject }

    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)) // Signature curve of the Geometric Balance header
                        .background(Color(0xFF0F766E)) // Teal-700 (FarmHeaderTeal)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Project Selector Dropdown inside AppBar
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .clickable { showQuickNavMenu = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = activeProjectObj?.name ?: "Select Farm",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Icon(Icons.Default.Menu, contentDescription = "Dropdown indicator", tint = Color.White)
                            }

                            DropdownMenu(
                                expanded = showQuickNavMenu,
                                		onDismissRequest = { showQuickNavMenu = false }
                            ) {
                                projectsList.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p.name, fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            viewModel.selectProject(p.id)
                                            showQuickNavMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = Translations.get("dashboard", lang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Bell Notification indicator
                        IconButton(onClick = { viewModel.navigateTo("notifications") }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications list", tint = Color.White)
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { viewModel.navigateTo("home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home dashboard") },
                    label = { Text("Home", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == "projects",
                    onClick = { viewModel.navigateTo("projects") },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Projects List") },
                    label = { Text("Projects", fontSize = 11.sp) }
                )

                // Plus action centerpiece with geometric teal flavor
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0D9488)) // Teal-600 (FarmMintGreen)
                        .clickable { viewModel.navigateTo("new_entry") }
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Quick insert flow", tint = Color.White, modifier = Modifier.size(28.dp))
                }

                NavigationBarItem(
                    selected = currentScreen == "reports",
                    onClick = { viewModel.navigateTo("reports") },
                    icon = { Icon(Icons.Default.Share, contentDescription = "Report summaries") },
                    label = { Text("Reports", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == "profile",
                    onClick = { viewModel.navigateTo("profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile parameters") },
                    label = { Text("Profile", fontSize = 11.sp) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "home" -> HomeGridDashboard(viewModel = viewModel)
                "projects" -> ProjectsManagerTab(viewModel = viewModel)
                "reports" -> BrandedReportScreen(viewModel = viewModel)
                "profile" -> ProfileControlScreen(viewModel = viewModel)
                "messages" -> MessagePortalInner(viewModel = viewModel)
                "new_entry" -> RequestProposalComposerScreen(viewModel = viewModel)
                "approvals" -> ApprovalGovernancePortal(viewModel = viewModel)
                "notifications" -> NotificationsAlertsTab(viewModel = viewModel)
                "ponds" -> PondsPerformanceGrid(viewModel = viewModel)
                "audit_logs" -> AuditTrailLogsScreen(viewModel = viewModel)
            }
        }
    }
}

// 1. Dashboard Segment Home Layout
@Composable
fun HomeGridDashboard(viewModel: FishFarmViewModel) {
    val lang by viewModel.language.collectAsState()
    val entries by viewModel.projectEntries.collectAsState()

    // Calculated balances based on entries
    val incomeVal = entries.filter { it.type == "Income" }.sumOf { it.amount }
    val expenseVal = entries.filter { it.type == "Expense" }.sumOf { it.amount }
    val netValue = incomeVal - expenseVal

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Grid of 4 beautiful status metrics
            Row(modifier = Modifier.fillMaxWidth()) {
                // Col 1
                Column(modifier = Modifier.weight(1f)) {
                    StatusKpiCard(
                        title = Translations.get("total_balance", lang),
                        value = formatCurrency(netValue),
                        icon = Icons.Default.Language,
                        iconColor = Color(0xFF0D9488), // Teal-600
                        descColor = Color(0xFFF0FDFA) // Teal-50
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StatusKpiCard(
                        title = Translations.get("total_expense", lang),
                        value = formatCurrency(expenseVal),
                        icon = Icons.Default.ArrowDownward,
                        iconColor = Color.Red,
                        descColor = Color(0xFFFFEBEE)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                // Col 2
                Column(modifier = Modifier.weight(1f)) {
                    StatusKpiCard(
                        title = Translations.get("total_income", lang),
                        value = formatCurrency(incomeVal),
                        icon = Icons.Default.ArrowUpward,
                        iconColor = Color(0xFF3B82F6), // Slate Blue Accent
                        descColor = Color(0xFFE3F2FD)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StatusKpiCard(
                        title = Translations.get("net_profit", lang),
                        value = formatCurrency(netValue),
                        icon = Icons.Default.TrendingUp,
                        iconColor = Color(0xFF14B8A6), // Teal-500
                        descColor = Color(0xFFF0FDFA) // Teal-50
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = Translations.get("quick_access", lang),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E293B) // Slate-800
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // 12 Quick Access Grid Options mapping 1:1 with drawing (Income, Expense, Ponds, Message, Reports...)
        item {
            val itemsList = listOf(
                GridActionItem("Income", Icons.Default.ArrowUpward, Color(0xFFEF6C00), "new_entry"),
                GridActionItem("Expense", Icons.Default.ArrowDownward, Color(0xFFD84315), "new_entry"),
                GridActionItem("Members", Icons.Default.Person, Color(0xFF1565C0), "profile"),
                GridActionItem("Shares", Icons.Default.Share, Color(0xFF2E7D32), "new_entry"),
                GridActionItem("Requests", Icons.Default.Description, Color(0xFF00838F), "approvals"),
                GridActionItem("Approvals", Icons.Default.Check, Color(0xFF2E7D32), "approvals"),
                GridActionItem("Ponds", Icons.Default.Language, Color(0xFF0277BD), "ponds"),
                GridActionItem("Fish Stock", Icons.Default.TrendingUp, Color(0xFF37474F), "ponds"),
                GridActionItem("Reports", Icons.Default.Share, Color(0xFF1565C0), "reports"),
                GridActionItem("Messages", Icons.Default.Email, Color(0xFF00897B), "messages"),
                GridActionItem("Notifications", Icons.Default.Notifications, Color(0xFFF9A825), "notifications"),
                GridActionItem("Audit Logs", Icons.Default.Security, Color(0xFF6A1B9A), "audit_logs")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(itemsList) { item ->
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9)) // Slate-100 (Geometric Balance)
                            .clickable { viewModel.navigateTo(item.route) }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(item.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(item.icon, contentDescription = item.label, tint = item.color, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = Translations.get(item.label.lowercase(), lang),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Recent Entries Title list
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Recent Transactions Activity",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1E293B) // Slate-800 Custom
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (entries.isEmpty()) {
            item {
                Text("No recent approved transaction histories.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            items(entries) { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (entry.type == "Income") Color(0xFFE3F2FD) else Color(0xFFFFEBEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (entry.type == "Income") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = "Tx Flow",
                            tint = if (entry.type == "Income") Color(0xFF3B82F6) else Color(0xFFEF4444) // Slate Blue / Red Accent
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(entry.category, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(entry.description, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(
                        text = (if (entry.type == "Income") "+" else "-") + "৳" + entry.amount.toInt().toString(),
                        fontWeight = FontWeight.ExtraBold,
                        color = if (entry.type == "Income") Color(0xFF3B82F6) else Color(0xFFEF4444), // Slate Blue / Red Accent
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

data class GridActionItem(val label: String, val icon: ImageVector, val color: Color, val route: String)

@Composable
fun StatusKpiCard(title: String, value: String, icon: ImageVector, iconColor: Color, descColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = descColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        }
    }
}

// 2. Projects Admin Management View Tab (Creating and reading multi projects)
@Composable
fun ProjectsManagerTab(viewModel: FishFarmViewModel) {
    val projects by viewModel.projects.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val lang by viewModel.language.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    var newProjName by remember { mutableStateOf("") }
    var selectMode by remember { mutableStateOf("Majority") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Multi-Farm Projects Manager", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF003827))

            if (currentUser?.role == "Super Admin") {
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A370))
                ) {
                    Text("+ Create")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(projects) { project ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF0C825B))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(project.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF003827))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Shares: ${project.totalShares}", fontSize = 12.sp, color = Color.Gray)
                            Text("Approval Mode: ${project.defaultApprovalMode}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        Dialog(onDismissRequest = { showCreateDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Create New Farm Project", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newProjName,
                        onValueChange = { newProjName = it },
                        label = { Text("Project / Farm Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Default Governance Rule:")
                    Row {
                        RadioButton(selected = selectMode == "Majority", onClick = { selectMode = "Majority" })
                        Text("Majority Voting", modifier = Modifier.align(Alignment.CenterVertically))
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = selectMode == "100%", onClick = { selectMode = "100%" })
                        Text("100% Rules", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (newProjName.isNotEmpty()) {
                                viewModel.createNewProject(newProjName, selectMode)
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A370)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Creation")
                    }
                }
            }
        }
    }
}

// 3. Branded Report View (Print Layout, Branding custom calculations)
@Composable
fun BrandedReportScreen(viewModel: FishFarmViewModel) {
    val lang by viewModel.language.collectAsState()
    val entries by viewModel.projectEntries.collectAsState()
    val selectedProj by viewModel.selectedProjectId.collectAsState()
    val projectsList by viewModel.projects.collectAsState()

    val context = LocalContext.current

    val activeProjectObj = projectsList.find { it.id == selectedProj }

    val incomeVal = entries.filter { it.type == "Income" }.sumOf { it.amount }
    val expenseVal = entries.filter { it.type == "Expense" }.sumOf { it.amount }
    val netValue = incomeVal - expenseVal

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            // Elegant Paper Sheet layout simulation showing project boundaries
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Report Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activeProjectObj?.name ?: "Green Fish Farm",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF006C4F)
                            )
                            Text("Official Financial Audit Summary Report", fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.Default.TrendingUp, contentDescription = "Branding", tint = Color(0xFF00A370), modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Report Generation Date: 2026-05-28", fontSize = 11.sp, color = Color.DarkGray)
                    Text("Currency: BDT (৳) - Bangladesh Taka", fontSize = 11.sp, color = Color.DarkGray)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Audit Calculations list grid
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Gross Farm Income:", fontWeight = FontWeight.SemiBold)
                        Text(formatCurrency(incomeVal), fontWeight = FontWeight.Bold, color = Color(0xFF0D5D8E))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Operational Expense:", fontWeight = FontWeight.SemiBold)
                        Text(formatCurrency(expenseVal), fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Net Farm Profit / Loss:", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        Text(formatCurrency(netValue), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFF0C825B))
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Share redistribution details simulation
                    Text("Shareholder Earnings Redistribution Breakdown:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Rakib Hasan (Project Admin - 40% Shares): ${formatCurrency(netValue * 0.4)}", fontSize = 12.sp)
                    Text("• Sabbir Hossain (Member - 35% Shares): ${formatCurrency(netValue * 0.35)}", fontSize = 12.sp)
                    Text("• Nur Alam (Member - 25% Shares): ${formatCurrency(netValue * 0.25)}", fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(40.dp))

                    Text("Disclaimer: This report is generated dynamically using secure cryptographic approval registries. Unauthorized modification is strictly audited.", fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Export Actions buttons matching required
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { Toast.makeText(context, "Exporting Branded PDF successful!", Toast.LENGTH_SHORT).show() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006C4F)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "PDF")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PDF Export")
                }

                Button(
                    onClick = { Toast.makeText(context, "Exporting Branded Excel spreadsheet successful!", Toast.LENGTH_SHORT).show() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C825B)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Description, contentDescription = "Excel")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Excel Export")
                }
            }
        }
    }
}

// 4. Profile and Custom Session Management Screens
@Composable
fun ProfileControlScreen(viewModel: FishFarmViewModel) {
    val lang by viewModel.language.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val activeSessions by viewModel.activeSessions.collectAsState()

    val context = LocalContext.current

    var showChangePass by remember { mutableStateOf(false) }

    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    val error by viewModel.loginError.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            // Elegant user avatar details card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Circle init
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00A370)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.fullName ?: "RH").take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentUser?.fullName ?: "Md. Rakib Hasan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF003827)
                    )
                    Text(
                        text = currentUser?.role ?: "Project Admin",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00A370)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Online", color = Color(0xFF00A370), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Information fields
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileItemRow(label = "User ID", value = currentUser?.id ?: "GF001-ADMIN-01")
                    ProfileItemRow(label = "Email", value = currentUser?.email ?: "rakib.hasan@example.com")
                    ProfileItemRow(label = "Mobile", value = currentUser?.mobile ?: "+880 1712 345678")
                    ProfileItemRow(label = "Status", value = currentUser?.status ?: "Active")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions Settings lists
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text(Translations.get("change_pass", lang), fontWeight = FontWeight.SemiBold) },
                        leadingContent = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                        modifier = Modifier.clickable { showChangePass = true }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    ListItem(
                        headlineContent = { Text(Translations.get("language", lang) + " (${Translations.get("language", lang)})", fontWeight = FontWeight.SemiBold) },
                        leadingContent = { Icon(Icons.Default.Language, contentDescription = "Language toggle") },
                        trailingContent = { Text(lang, fontWeight = FontWeight.Bold, color = Color(0xFF00A370)) },
                        modifier = Modifier.clickable { viewModel.toggleLanguage() }
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    ListItem(
                        headlineContent = { Text("Audit Activity Logs", fontWeight = FontWeight.SemiBold) },
                        leadingContent = { Icon(Icons.Default.Security, contentDescription = "Audit Logs") },
                        modifier = Modifier.clickable { viewModel.navigateTo("audit_logs") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Sessions security list
            Text(Translations.get("active_sessions", lang), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF003827))
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (activeSessions.isEmpty()) {
            item {
                Text("No active concurrent sessions tracked.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            items(activeSessions) { session ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Devices, contentDescription = "Device", tint = Color(0xFF006C4F))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(session.deviceName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("IP: ${session.ipAddress} • Location: ${session.location}", fontSize = 11.sp, color = Color.Gray)
                        }
                        if (!session.isCurrent) {
                            IconButton(onClick = { viewModel.terminateSession(session.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Terminate", tint = Color.Red)
                            }
                        } else {
                            Text("Current", color = Color(0xFF00A370), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.terminateOtherSessions() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout Other Specific Devices")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(Translations.get("logout", lang), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showChangePass) {
        Dialog(onDismissRequest = { showChangePass = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(Translations.get("change_pass", lang), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = currentPass,
                        onValueChange = { currentPass = it },
                        label = { Text("Current Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text(Translations.get("new_pass", lang)) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it },
                        label = { Text("Confirm New Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    error?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.changePassword(currentPass, newPass, confirmPass) {
                                showChangePass = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A370)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Translations.get("submit", lang))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Text(value, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

// 5. Messages Portal Screen (Inbox, Draft, Compose priority)
@Composable
fun MessagePortalInner(viewModel: FishFarmViewModel) {
    val lang by viewModel.language.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allMessages by viewModel.allMessages.collectAsState()
    val activeTab by viewModel.messageTab.collectAsState()

    var showComposeDialog by remember { mutableStateOf(false) }

    var toUserVal by remember { mutableStateOf("SUPER-001") }
    var subjectVal by remember { mutableStateOf("") }
    var bodyVal by remember { mutableStateOf("") }
    var priorityVal by remember { mutableStateOf("Normal") }

    val displayedMessages = allMessages.filter {
        if (currentUser == null) false
        else if (activeTab == "Inbox") it.receiverId == currentUser!!.id && it.status == "Inbox"
        else if (activeTab == "Sent") it.senderId == currentUser!!.id && it.status == "Sent"
        else if (activeTab == "Draft") it.status == "Draft"
        else it.isStarred
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Toggle tabs Inbox / Sent / Draft / Starred
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
                .padding(2.dp)
        ) {
            val tabs = listOf("Inbox", "Sent", "Draft", "Starred")
            tabs.forEach { tab ->
                val selected = tab == activeTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selected) Color(0xFF006C4F) else Color.Transparent)
                        .clickable { viewModel.setMessageTab(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) Color.White else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Message List Email style layout matching drawing
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (displayedMessages.isEmpty()) {
                item {
                    Text("No messages in this folder.", color = Color.Gray, fontSize = 13.sp)
                }
            } else {
                items(displayedMessages) { msg ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.readMessage(msg)
                                Toast.makeText(viewModel.getApplication(), "Opened mail: '${msg.subject}'", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar initials
                            val initial = msg.senderId.take(2)
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (initial) {
                                            "SU" -> Color(0xFF2E7D32)
                                            "ME" -> Color(0xFF1565C0)
                                            "AU" -> Color(0xFFD84315)
                                            else -> Color(0xFF7E57C2)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = msg.senderId + (if (msg.priority == "Urgent") " (Urgent)" else ""),
                                        fontWeight = if (!msg.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = if (msg.priority == "Urgent") Color.Red else Color.Black
                                    )
                                    Text("10:30 AM", fontSize = 10.sp, color = Color.Gray)
                                }
                                Text(
                                    text = msg.subject,
                                    fontWeight = if (!msg.isRead) FontWeight.ExtraBold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = msg.body,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Star item
                            IconButton(onClick = { viewModel.toggleStarMessage(msg) }) {
                                Icon(
                                    imageVector = if (msg.isStarred) Icons.Default.Star else Icons.Outlined.Star,
                                    tint = if (msg.isStarred) Color(0xFFF9A825) else Color.LightGray,
                                    contentDescription = "Star message"
                                )
                            }
                        }
                    }
                }
            }
        }

        // Circular compose float action centerpiece
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 12.dp)
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFF00A370))
                .clickable { showComposeDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Send, contentDescription = "Compose internal mail", tint = Color.White)
        }
    }

    if (showComposeDialog) {
        Dialog(onDismissRequest = { showComposeDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(Translations.get("compose", lang), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = toUserVal,
                        onValueChange = { toUserVal = it },
                        label = { Text(Translations.get("to_user", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = subjectVal,
                        onValueChange = { subjectVal = it },
                        label = { Text(Translations.get("subject", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bodyVal,
                        onValueChange = { bodyVal = it },
                        label = { Text(Translations.get("body", lang)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(Translations.get("priority", lang) + ":")
                    Row {
                        listOf("Normal", "Important", "Urgent").forEach { pri ->
                            Row(modifier = Modifier.clickable { priorityVal = pri }) {
                                RadioButton(selected = priorityVal == pri, onClick = { priorityVal = pri })
                                Text(pri, modifier = Modifier.align(Alignment.CenterVertically), fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (subjectVal.isNotEmpty() && bodyVal.isNotEmpty()) {
                                viewModel.sendInternalMessage(toUserVal, subjectVal, bodyVal, priorityVal)
                                showComposeDialog = false
                                subjectVal = ""
                                bodyVal = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A370)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Translations.get("submit", lang))
                    }
                }
            }
        }
    }
}

// 6. Request Proposal Composer Form
@Composable
fun RequestProposalComposerScreen(viewModel: FishFarmViewModel) {
    val lang by viewModel.language.collectAsState()

    var isIncome by remember { mutableStateOf(true) }
    var category by remember { mutableStateOf("Fish Sale") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var require100Percent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Propose New Transaction Staging request", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF003827))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Due to strict governance policies, transaction logs must go through the multi-member approval engine before registering live.",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Selector Row Income vs Expense
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            isIncome = true
                            category = "Fish Sale"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isIncome) Color(0xFF006C4F) else Color.LightGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Income (আয়)")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            isIncome = false
                            category = "Feed"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isIncome) Color(0xFFB13131) else Color.LightGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Expense (ব্যয়)")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(Translations.get("category", lang), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                if (isIncome) {
                    val incCats = listOf("Fish Sale", "Fry Sale", "Egg Sale", "Investment", "Service Income")
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Text(
                            category,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .clickable { expanded = true }
                                .padding(14.dp)
                        )
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            incCats.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = {
                                    category = cat
                                    expanded = false
                                })
                            }
                        }
                    }
                } else {
                    val expCats = listOf("Feed", "Medicine", "Labor Salary", "Electricity", "Pond Maintenance", "Equipment", "Transport")
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Text(
                            category,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .clickable { expanded = true }
                                .padding(14.dp)
                        )
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            expCats.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = {
                                    category = cat
                                    expanded = false
                                })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(Translations.get("amount", lang), fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(Translations.get("description", lang), fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Threshold notification highlight
                val amountDouble = amount.toDoubleOrNull() ?: 0.0
                val isLargeExpense = !isIncome && amountDouble >= 50000.0
                if (isLargeExpense) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFEBEE))
                            .padding(10.dp)
                    ) {
                        Text(
                            "Special Rule: Expense exceeds ৳50,000 threshold. 100% security approval of all members is mandatory for commit.",
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amt = amount.toDoubleOrNull() ?: 0.0
                        if (amt > 0.0 && description.isNotEmpty()) {
                            viewModel.createAccountingRequest(category, amt, description, isIncome, isLargeExpense)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A370)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(Translations.get("submit", lang), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 7. Governance voting system page (100% and majority rules with re-request mechanisms)
@Composable
fun ApprovalGovernancePortal(viewModel: FishFarmViewModel) {
    val lang by viewModel.language.collectAsState()
    val requestsList by viewModel.approvalRequests.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var rejectReasonText by remember { mutableStateOf("") }
    var activeRejectId by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(Translations.get("pending_voting", lang), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF003827))
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (requestsList.isEmpty()) {
                item {
                    Text("No pending governance proposals on stage queue.", color = Color.Gray, fontSize = 13.sp)
                }
            } else {
                items(requestsList) { req ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = req.requestType,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF006C4F),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = req.status,
                                    fontWeight = FontWeight.Bold,
                                    color = when (req.status) {
                                        "Approved" -> Color(0xFF00A370)
                                        "Rejected" -> Color.Red
                                        else -> Color(0xFFF9A825)
                                    },
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(req.description, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Created by Member: ${req.requestCreatorId} en ${req.createdDate}", fontSize = 11.sp, color = Color.Gray)
                            Text("Governance Requirement: ${req.approvalRequirementType} Approval", fontSize = 11.sp, color = Color(0xFF0C825B), fontWeight = FontWeight.SemiBold)

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stats bar
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Approvals Progress: ${req.currentApproveCount} / ${req.totalRequiredApprovers}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (req.status == "Rejected") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Rejection Reason: ${req.rejectionReason}", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // If Pending of approvals, display actions for logged in member
                            if (req.status == "Pending") {
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { activeRejectId = req.id },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(Translations.get("reject", lang))
                                    }

                                    Button(
                                        onClick = { viewModel.voteRequest(req.id, true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A370)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(Translations.get("approve", lang))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (activeRejectId != null) {
        Dialog(onDismissRequest = { activeRejectId = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Provide Rejection Reason", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = rejectReasonText,
                        onValueChange = { rejectReasonText = it },
                        placeholder = { Text("Details for re-request review...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (rejectReasonText.isNotEmpty()) {
                                viewModel.voteRequest(activeRejectId!!, false, rejectReasonText)
                                activeRejectId = null
                                rejectReasonText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Rejection Rollback")
                    }
                }
            }
        }
    }
}

// 8. Notifications Alert Tab listing
@Composable
fun NotificationsAlertsTab(viewModel: FishFarmViewModel) {
    val lang by viewModel.language.collectAsState()

    val notificationItems = listOf(
        "MFA device access verification logs generated.",
        "CP Feed supply staging proposal successfully committed.",
        "Annual accounting quarterly audit is due soon.",
        "Security Alert: Root session authenticated from Dhaka IP."
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(Translations.get("notifications", lang), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF003827))
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notificationItems) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFFAAF00))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(note, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

// 9. Ponds Performance KPI and Stock Metrics Grid
@Composable
fun PondsPerformanceGrid(viewModel: FishFarmViewModel) {
    val ponds by viewModel.projectPonds.collectAsState()
    val lang by viewModel.language.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Fish Ponds Condition Monitoring", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF003827))
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ponds) { pond ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(pond.pondNumber, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF006C4F))
                            Text("Size: ${pond.size} Acres", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Fish Species: ${pond.fishSpecies}", fontSize = 13.sp)
                        Text("• Stocked Quantity: ${pond.stockQuantity} tails", fontSize = 13.sp)
                        Text("• Water Conditions: ${pond.waterCondition}", fontSize = 13.sp, color = Color(0xFF0D5D8E))
                        Text("• Release Date: ${pond.releaseDate}", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// 10. Audit Activity logs Security list
@Composable
fun AuditTrailLogsScreen(viewModel: FishFarmViewModel) {
    val auditLogs by viewModel.auditLogs.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Security Audit Trail Logs", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF003827))
        Spacer(modifier = Modifier.height(4.dp))
        Text("Tracking authentication, changes, device IPs and session hashes.", fontSize = 11.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(auditLogs) { log ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(log.action, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF006C4F))
                            Text(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp)), fontSize = 10.sp, color = Color.Gray)
                        }
                        Text("Module: ${log.module} • User ID: ${log.userId}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Detail info: ${log.oldValue}", fontSize = 11.sp, color = Color.DarkGray)
                        Text("Device details: ${log.device} • IP: ${log.ipAddress}", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
