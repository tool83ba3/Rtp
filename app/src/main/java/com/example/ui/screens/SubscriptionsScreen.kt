package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(viewModel: MainViewModel) {
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var serviceName by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var cycle by remember { mutableStateOf("شهري") }
    var startDate by remember { mutableStateOf("2026-07-01") }
    var endDate by remember { mutableStateOf("2026-08-01") }
    var category by remember { mutableStateOf("خدمات سحابية") }

    val totalMonthlyCost = subscriptions.sumOf { it.cost }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إدارة الاشتراكات والمصروفات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.selectModule("dashboard") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DangerRed,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_subscription_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "اشتراك جديد")
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
            // Spend summary card
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BrandSecondary)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("إجمالي المصروفات الشهرية", color = TextSecondaryDark, fontSize = 12.sp)
                        Text("$${String.format("%.2f", totalMonthlyCost)}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Surface(color = BrandAccent.copy(0.2f), shape = RoundedCornerShape(10.dp)) {
                        Text("${subscriptions.size} اشتراكات نشطة", color = BrandAccent, fontSize = 12.sp, modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(subscriptions) { sub ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Subscriptions, contentDescription = null, tint = DangerRed, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(sub.serviceName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("تاريخ التجديد: ${sub.endDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("$${sub.cost}", fontWeight = FontWeight.Bold, color = DangerRed, fontSize = 16.sp)
                                Surface(color = BrandPrimary.copy(0.1f), shape = RoundedCornerShape(6.dp)) {
                                    Text(sub.billingCycle, fontSize = 10.sp, color = BrandPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
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
            title = { Text("تسجيل اشتراك مدفوع جديد") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = serviceName, onValueChange = { serviceName = it }, label = { Text("اسم الخدمة") })
                    OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("قيمة الاشتراك ($)") })
                    OutlinedTextField(value = cycle, onValueChange = { cycle = it }, label = { Text("نوع الاشتراك (شهري/سنوي)") })
                    OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("تاريخ نهاية/تجديد الاشتراك") })
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("التصنيف") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val costNum = cost.toDoubleOrNull() ?: 0.0
                    if (serviceName.isNotBlank()) {
                        viewModel.addSubscription(serviceName, costNum, cycle, startDate, endDate, category)
                        serviceName = ""
                        cost = ""
                        showAddDialog = false
                    }
                }) {
                    Text("حفظ الاشتراك")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }
}
