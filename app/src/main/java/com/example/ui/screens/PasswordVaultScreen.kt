package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordVaultScreen(viewModel: MainViewModel) {
    val passwords by viewModel.passwords.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("https://") }
    var notes by remember { mutableStateOf("") }

    var isUnlocked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مدير كلمات المرور والنوتباد الآمن", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.selectModule("dashboard") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            if (isUnlocked) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = WarningAmber,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_password_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "كلمة مرور جديدة")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
        ) {
            if (!isUnlocked) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(80.dp), tint = WarningAmber)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("خزنة كلمات المرور مغلقة بالبصمة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.verifyBiometric()
                                isUnlocked = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                        ) {
                            Text("التحقق بالبصمة وإظهار الخزنة")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(passwords) { item ->
                        var isPassVisible by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(item.encryptedPasswordHash))
                                            Toast.makeText(context, "تم نسخ كلمة المرور بنجاح", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Outlined.ContentCopy, contentDescription = "نسخ", tint = BrandPrimary)
                                    }
                                }

                                Text("اسم المستخدم / البريد: ${item.username}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isPassVisible) item.encryptedPasswordHash else "••••••••••••",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    IconButton(onClick = { isPassVisible = !isPassVisible }) {
                                        Icon(
                                            if (isPassVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("حفظ كلمة مرور جديدة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("اسم الموقع / الحساب") })
                    OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("اسم المستخدم / البريد") })
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("كلمة المرور") },
                        trailingIcon = {
                            TextButton(onClick = { passwordInput = generateRandomPassword() }) {
                                Text("توليد كلمة آمنة", fontSize = 10.sp, color = BrandAccent)
                            }
                        }
                    )
                    OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("رابط الموقع") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank() && passwordInput.isNotBlank()) {
                        viewModel.addPassword(title, username, passwordInput, url, notes)
                        title = ""
                        passwordInput = ""
                        showAddDialog = false
                    }
                }) {
                    Text("تشفير وحفظ")
                }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") } }
        )
    }
}

private fun generateRandomPassword(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
    return (1..14).map { chars.random() }.joinToString("")
}
