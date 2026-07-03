package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(viewModel: MainViewModel) {
    val activityLogs by viewModel.activityLogs.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedAdminTab by remember { mutableStateOf(0) } // 0: المستخدمين, 1: السجلات Logs, 2: النسخ الاحتياطي
    var showAddUserDialog by remember { mutableStateOf(false) }

    val mockUsers = remember {
        mutableStateListOf(
            Triple("محمد العتيبي", "user@ratibhalak.app", "نشط"),
            Triple("سارة الشمري", "sara@ratibhalak.app", "نشط"),
            Triple("خالد الغامدي", "khalid@ratibhalak.app", "موقوف")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("لوحة تحكم المدير (Admin Panel)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.selectModule("dashboard") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Admin Tabs
            TabRow(selectedTabIndex = selectedAdminTab) {
                Tab(selected = selectedAdminTab == 0, onClick = { selectedAdminTab = 0 }) {
                    Text("إدارة المستخدمين", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedAdminTab == 1, onClick = { selectedAdminTab = 1 }) {
                    Text("سجلات الأمان (Logs)", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedAdminTab == 2, onClick = { selectedAdminTab = 2 }) {
                    Text("النسخ والمزامنة", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedAdminTab) {
                0 -> {
                    // User Management Tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("قائمة مستخدمي المنصة:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Button(
                            onClick = { showAddUserDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text("إضافة مستخدم")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(mockUsers) { u ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Person, contentDescription = null, tint = BrandPrimary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(u.first, fontWeight = FontWeight.Bold)
                                        Text(u.second, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Surface(
                                        color = if (u.third == "نشط") SuccessGreen.copy(0.2f) else DangerRed.copy(0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            u.third,
                                            fontSize = 11.sp,
                                            color = if (u.third == "نشط") SuccessGreen else DangerRed,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Security Logs Tab
                    Text("سجل عمليات وتدقيق أمان النظام (Audit Trail):", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(activityLogs) { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${log.moduleName} - ${log.actionType}", fontWeight = FontWeight.Bold, color = BrandPrimary)
                                        Text(log.userRole, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(log.description, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Backup & Sync Tab
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("النسخ الاحتياطي والمزامنة السحابية", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("قم بحفظ نسخة احتياطية مشفرة لجميع بيانات المنصة في Google Drive أو AWS S3.", fontSize = 13.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    Toast.makeText(context, "تم رفع النسخة الاحتياطية بنجاح إلى Google Drive", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
                            ) {
                                Icon(Icons.Outlined.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("مزامنة فورية مع Google Drive")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "تم إنشاء ملف النسخة الاحتياطية المحلية (JSON)", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Outlined.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تصدير ملف احتياطي محلي مشفر")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddUserDialog) {
        var newUserName by remember { mutableStateOf("") }
        var newUserEmail by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            title = { Text("إضافة مستخدم جديد للنظام") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = newUserName, onValueChange = { newUserName = it }, label = { Text("الاسم") })
                    OutlinedTextField(value = newUserEmail, onValueChange = { newUserEmail = it }, label = { Text("البريد الإلكتروني") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newUserName.isNotBlank()) {
                        mockUsers.add(Triple(newUserName, newUserEmail, "نشط"))
                        showAddUserDialog = false
                    }
                }) {
                    Text("إضافة")
                }
            },
            dismissButton = { TextButton(onClick = { showAddUserDialog = false }) { Text("إلغاء") } }
        )
    }
}
