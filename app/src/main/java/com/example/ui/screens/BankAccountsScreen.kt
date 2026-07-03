package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankAccountsScreen(viewModel: MainViewModel) {
    val bankAccounts by viewModel.bankAccounts.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var iban by remember { mutableStateOf("") }
    var beneficiary by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var isUnlocked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الحسابات البنكية المشفّرة (AES-256)", fontWeight = FontWeight.Bold) },
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
                    containerColor = Color(0xFF8B5CF6),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_bank_account_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة حساب بنكي")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Security status header banner
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgDark)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Security, contentDescription = null, tint = BrandAccent, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("مستوّدع التشفير البنكي AES-256", fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            if (isUnlocked) "تم فتح الخزنة بواسطة بصمة الإصبع" else "مغلق بالأمان - يتطلب المصادقة بالبصمة",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }

            if (!isUnlocked) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Fingerprint, contentDescription = null, modifier = Modifier.size(80.dp), tint = BrandPrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("الحسابات البنكية محمية بالبصمة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.verifyBiometric()
                                isUnlocked = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text("فتح الخزنة بالبصمة")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(bankAccounts) { account ->
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
                                    Text(account.bankName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Surface(color = BrandAccent.copy(0.2f), shape = RoundedCornerShape(6.dp)) {
                                        Text("AES-256", fontSize = 10.sp, color = BrandAccent, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("المستفيد: ${account.beneficiaryName}", fontSize = 13.sp)
                                Text("رقم الحساب: ${account.accountNumber}", fontSize = 13.sp)

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "IBAN: ${account.iban}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(account.iban))
                                                Toast.makeText(context, "تم نسخ الـ IBAN بنجاح", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(Icons.Outlined.ContentCopy, contentDescription = "نسخ IBAN", tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                        }
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
            title = { Text("إضافة حساب بنكي مشفّر") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text("اسم البنك") })
                    OutlinedTextField(value = beneficiary, onValueChange = { beneficiary = it }, label = { Text("اسم المستفيد") })
                    OutlinedTextField(value = accountNumber, onValueChange = { accountNumber = it }, label = { Text("رقم الحساب") })
                    OutlinedTextField(value = iban, onValueChange = { iban = it }, label = { Text("رقم الـ IBAN") })
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("ملاحظات إضافية") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (bankName.isNotBlank() && iban.isNotBlank()) {
                        viewModel.addBankAccount(bankName, accountNumber, iban, beneficiary, notes)
                        bankName = ""
                        iban = ""
                        showAddDialog = false
                    }
                }) {
                    Text("حفظ وتشفير")
                }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") } }
        )
    }
}
