package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.util.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: MainViewModel) {
    val isRtl by viewModel.isRtl.collectAsStateWithLifecycle()
    val strings = AppLanguage

    var isRegisterTab by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("user@ratibhalak.app") }
    var password by remember { mutableStateOf("••••••••") }
    var name by remember { mutableStateOf("") }
    var showBiometricDialog by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BrandSecondary, BgDark)
                )
            )
            .padding(24.dp)
    ) {
        // Language switcher button at top
        IconButton(
            onClick = { viewModel.toggleLanguage() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(0.4f),
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = if (isRtl) "EN" else "عربي",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon & Title
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary.copy(alpha = 0.2f))
                    .border(2.dp, BrandPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_logo_1783006129958),
                    contentDescription = strings.appName,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = strings.appName,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = if (isRtl) "منصتك الشخصية الذكية لإدارة المحتوى والبيانات بأمان" else "Your smart personal platform to organize content & data securely",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_card"),
                colors = CardDefaults.cardColors(containerColor = CardDark.copy(alpha = 0.9f)),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BrandPrimary.copy(0.4f), BorderDark)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Switch Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BgDark)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isRegisterTab) BrandPrimary else Color.Transparent)
                                .clickable { isRegisterTab = false }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(strings.signIn, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isRegisterTab) BrandPrimary else Color.Transparent)
                                .clickable { isRegisterTab = true }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isRtl) "حساب جديد" else "Sign Up", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isRegisterTab) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(if (isRtl) "الاسم الكامل" else "Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(strings.email) },
                        leadingIcon = { Icon(Icons.Outlined.Mail, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(strings.password) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (!isRegisterTab) {
                        TextButton(
                            onClick = { showForgotPasswordDialog = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(strings.forgotPassword, color = BrandAccent)
                        }
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = { viewModel.loginWithEmail(email) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text(
                            text = if (isRegisterTab) (if (isRtl) "إنشاء حساب جديد" else "Create New Account") else strings.signIn,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = BorderDark)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Social & Biometric Logins
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.loginWithGoogle() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = BrandAccent)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Google", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { showBiometricDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Outlined.Fingerprint, contentDescription = null, tint = BrandPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isRtl) "البصمة" else "Biometric", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Biometric Dialog
    if (showBiometricDialog) {
        AlertDialog(
            onDismissRequest = { showBiometricDialog = false },
            icon = {
                Icon(
                    Icons.Outlined.Fingerprint,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(56.dp)
                )
            },
            title = { Text(if (isRtl) "المصادقة بالبصمة" else "Biometric Authentication", textAlign = TextAlign.Center) },
            text = {
                Text(
                    if (isRtl) "يرجى وضع إصبعك على مستشعر البصمة للتحقق وتأكيد هويتك في 'رتب حالك'" else "Please place your finger on the biometric sensor to verify your identity in Ratib Halak",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyBiometric()
                        viewModel.loginWithEmail("biometric.user@ratibhalak.app")
                        showBiometricDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
                ) {
                    Text(if (isRtl) "محاكاة المسح الناجح" else "Simulate Scan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBiometricDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf(email) }
        var isSent by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text(strings.forgotPassword) },
            text = {
                Column {
                    if (isSent) {
                        Text(if (isRtl) "تم إرسال رابط إعادة تعيين كلمة المرور إلى بريدك الإلكتروني بنجاح." else "Password reset link has been sent to your email successfully.")
                    } else {
                        Text(if (isRtl) "أدخل بريدك الإلكتروني ليصلك رابط إعادة ضبط كلمة المرور:" else "Enter your email address to receive password reset instructions:")
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text(strings.email) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (isSent) {
                    Button(onClick = { showForgotPasswordDialog = false }) {
                        Text(if (isRtl) "حسناً" else "OK")
                    }
                } else {
                    Button(onClick = { isSent = true }) {
                        Text(if (isRtl) "إرسال الرابط" else "Send Link")
                    }
                }
            },
            dismissButton = {
                if (!isSent) {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text(strings.cancel)
                    }
                }
            }
        )
    }
}
