package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BinanceAccountEntity
import com.example.data.DonationEntity
import com.example.data.PayPalAccountEntity
import com.example.data.VisaCardEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationsScreen(viewModel: MainViewModel) {
    val donations by viewModel.donations.collectAsStateWithLifecycle()
    val paypalAccounts by viewModel.paypalAccounts.collectAsStateWithLifecycle()
    val visaCards by viewModel.visaCards.collectAsStateWithLifecycle()
    val binanceAccounts by viewModel.binanceAccounts.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Dialog & Payment Flow States
    var showDonateDialog by remember { mutableStateOf(false) }
    var showAddPayPalDialog by remember { mutableStateOf(false) }
    var showAddVisaDialog by remember { mutableStateOf(false) }
    var showAddBinanceDialog by remember { mutableStateOf(false) }
    var showReceiptDialog by remember { mutableStateOf<DonationEntity?>(null) }
    var isProcessingPayment by remember { mutableStateOf(false) }

    // New Donation Form Fields
    var beneficiaryOrg by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-07-02") }
    var category by remember { mutableStateOf("كفالة أيتام") }
    var notes by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("فيزا") } // "باي بال", "فيزا", "بايننس", "نقدي"
    var selectedPayPalId by remember { mutableLongStateOf(paypalAccounts.firstOrNull()?.id ?: 0L) }
    var selectedVisaId by remember { mutableLongStateOf(visaCards.firstOrNull()?.id ?: 0L) }
    var selectedBinanceId by remember { mutableLongStateOf(binanceAccounts.firstOrNull()?.id ?: 0L) }
    var customPaymentInput by remember { mutableStateOf("") }

    // Add PayPal Form Fields
    var newPayPalEmail by remember { mutableStateOf("") }
    var newPayPalAccountName by remember { mutableStateOf("حساب PayPal الشخصي") }

    // Add Visa Form Fields
    var newVisaHolder by remember { mutableStateOf("") }
    var newVisaNumber by remember { mutableStateOf("") }
    var newVisaExpiry by remember { mutableStateOf("") }
    var newVisaType by remember { mutableStateOf("Visa") }

    // Add Binance Form Fields
    var newBinanceId by remember { mutableStateOf("") }
    var newBinanceEmailOrPhone by remember { mutableStateOf("") }
    var newBinanceWalletType by remember { mutableStateOf("USDT (TRC20)") }
    var newBinanceLabel by remember { mutableStateOf("محفظة بايننس الشخصية (Binance Pay)") }

    val totalDonations = donations.sumOf { it.amount }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("قسم التبرعات والدفع الإلكتروني", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("إدارة حسابات PayPal والفيزا ومنصة بايننس", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectModule("dashboard") },
                        modifier = Modifier.testTag("donations_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDonateDialog = true },
                containerColor = SuccessGreen,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_donation_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null)
                    Text("تبرع الآن", fontWeight = FontWeight.Bold)
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
            // Overview Top Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("إجمالي التبرعات والمساهمات", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${String.format("%.2f", totalDonations)}", color = SuccessGreen, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.VolunteerActivism, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(28.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // PayPal Badge Count
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF003087)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("P", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }
                            Text("${paypalAccounts.size} PayPal", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }

                        // Visa Badge Count
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1A1F71)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                            }
                            Text("${visaCards.size} فيزا", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }

                        // Binance Badge Count
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0B90B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("B", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }
                            Text("${binanceAccounts.size} بايننس", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Tabs Navigation
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("donations_tabs")
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("السجل", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Outlined.History, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_donations_history")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("PayPal", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Text("P", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFF003087)) },
                    modifier = Modifier.testTag("tab_paypal")
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("فيزا", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Outlined.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_visa")
                )
                Tab(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    text = { Text("بايننس", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Text("B", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFFF0B90B)) },
                    modifier = Modifier.testTag("tab_binance")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Content
            when (selectedTabIndex) {
                0 -> DonationsHistoryTab(
                    donations = donations,
                    onDeleteDonation = { viewModel.deleteDonation(it.id) },
                    onShowReceipt = { showReceiptDialog = it },
                    onQuickDonate = { showDonateDialog = true }
                )
                1 -> PayPalAccountsTab(
                    paypalAccounts = paypalAccounts,
                    onAddPayPal = { showAddPayPalDialog = true },
                    onDeletePayPal = { viewModel.deletePayPalAccount(it.id) }
                )
                2 -> VisaCardsTab(
                    visaCards = visaCards,
                    onAddVisa = { showAddVisaDialog = true },
                    onDeleteVisa = { viewModel.deleteVisaCard(it.id) }
                )
                3 -> BinanceAccountsTab(
                    binanceAccounts = binanceAccounts,
                    onAddBinance = { showAddBinanceDialog = true },
                    onDeleteBinance = { viewModel.deleteBinanceAccount(it.id) }
                )
            }
        }
    }

    // --- DIALOG 1: NEW DONATION WITH PAYPAL / VISA PAYMENTS ---
    if (showDonateDialog) {
        AlertDialog(
            onDismissRequest = { if (!isProcessingPayment) showDonateDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = SuccessGreen)
                    Text("تبرع جديد والدفع المباشر", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                if (isProcessingPayment) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = SuccessGreen)
                        Text("جاري معالجة الدفع المالي المشفر...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("الاتصال ببوابة الدفع (${selectedPaymentMethod})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            OutlinedTextField(
                                value = beneficiaryOrg,
                                onValueChange = { beneficiaryOrg = it },
                                label = { Text("الجهة المستفيدة / اسم الجمعية") },
                                leadingIcon = { Icon(Icons.Outlined.Business, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().testTag("donation_org_input")
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = amount,
                                onValueChange = { amount = it },
                                label = { Text("المبلغ ($)") },
                                leadingIcon = { Icon(Icons.Outlined.AttachMoney, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("donation_amount_input")
                            )
                        }

                        item {
                            Text("التصنيف / خيار التبرع:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("كفالة أيتام", "زكاة", "إطعام", "عام").forEach { cat ->
                                    FilterChip(
                                        selected = category == cat,
                                        onClick = { category = cat },
                                        label = { Text(cat, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = date,
                                onValueChange = { date = it },
                                label = { Text("التاريخ") },
                                leadingIcon = { Icon(Icons.Outlined.Event, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("اختر طريقة الدفع (Payment Method):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // Payment Methods Selector
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // PayPal Option
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedPaymentMethod = "باي بال" }
                                            .border(
                                                width = if (selectedPaymentMethod == "باي بال") 2.dp else 1.dp,
                                                color = if (selectedPaymentMethod == "باي بال") Color(0xFF003087) else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedPaymentMethod == "باي بال") Color(0xFF003087).copy(0.12f) else MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("P", color = Color(0xFF003087), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                            Text("PayPal", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }

                                    // Visa Option
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedPaymentMethod = "فيزا" }
                                            .border(
                                                width = if (selectedPaymentMethod == "فيزا") 2.dp else 1.dp,
                                                color = if (selectedPaymentMethod == "فيزا") BrandPrimary else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedPaymentMethod == "فيزا") BrandPrimary.copy(0.12f) else MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(22.dp))
                                            Text("بطاقة فيزا", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Binance Pay Option
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedPaymentMethod = "بايننس" }
                                            .border(
                                                width = if (selectedPaymentMethod == "بايننس") 2.dp else 1.dp,
                                                color = if (selectedPaymentMethod == "بايننس") Color(0xFFF0B90B) else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedPaymentMethod == "بايننس") Color(0xFFF0B90B).copy(0.18f) else MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text("B", color = Color(0xFFD9A000), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                            Text("Binance Pay", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }

                                    // Cash Option
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedPaymentMethod = "نقدي" }
                                            .border(
                                                width = if (selectedPaymentMethod == "نقدي") 2.dp else 1.dp,
                                                color = if (selectedPaymentMethod == "نقدي") SuccessGreen else MaterialTheme.colorScheme.outlineVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedPaymentMethod == "نقدي") SuccessGreen.copy(0.12f) else MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(Icons.Default.Payments, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(22.dp))
                                            Text("نقداً / آخر", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Sub-selection depending on Payment Method
                        item {
                            if (selectedPaymentMethod == "باي بال") {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("اختر حساب PayPal للاقتطاع:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    if (paypalAccounts.isEmpty()) {
                                        OutlinedTextField(
                                            value = customPaymentInput,
                                            onValueChange = { customPaymentInput = it },
                                            label = { Text("بريد PayPal للتبرع") },
                                            placeholder = { Text("example@paypal.com") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        paypalAccounts.forEach { acc ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (selectedPayPalId == acc.id) Color(0xFF003087).copy(0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                                                    .clickable { selectedPayPalId = acc.id }
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = (selectedPayPalId == acc.id),
                                                    onClick = { selectedPayPalId = acc.id }
                                                )
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(acc.email, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text(acc.accountName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (selectedPaymentMethod == "فيزا") {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("اختر بطاقة الفيزا للدفع:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    if (visaCards.isEmpty()) {
                                        OutlinedTextField(
                                            value = customPaymentInput,
                                            onValueChange = { customPaymentInput = it },
                                            label = { Text("رقم بطاقة الفيزا / CVV") },
                                            placeholder = { Text("•••• •••• •••• 4242") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        visaCards.forEach { card ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (selectedVisaId == card.id) BrandPrimary.copy(0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                                                    .clickable { selectedVisaId = card.id }
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = (selectedVisaId == card.id),
                                                    onClick = { selectedVisaId = card.id }
                                                )
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("${card.cardType} (${card.cardNumberEncrypted})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("${card.cardHolderName} - Expiry: ${card.expiryDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (selectedPaymentMethod == "بايننس") {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("اختر حساب Binance Pay للاقتطاع:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    if (binanceAccounts.isEmpty()) {
                                        OutlinedTextField(
                                            value = customPaymentInput,
                                            onValueChange = { customPaymentInput = it },
                                            label = { Text("Binance Pay ID / Crypto Wallet") },
                                            placeholder = { Text("e.g. 883920192") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        binanceAccounts.forEach { bAcc ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (selectedBinanceId == bAcc.id) Color(0xFFF0B90B).copy(0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                                    .clickable { selectedBinanceId = bAcc.id }
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = (selectedBinanceId == bAcc.id),
                                                    onClick = { selectedBinanceId = bAcc.id }
                                                )
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Binance ID: ${bAcc.binanceId} (${bAcc.walletType})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("${bAcc.accountLabel} • ${bAcc.payEmailOrPhone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("ملاحظات إضافية") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (!isProcessingPayment) {
                    Button(
                        onClick = {
                            val amtNum = amount.toDoubleOrNull() ?: 0.0
                            if (beneficiaryOrg.isNotBlank() && amtNum > 0) {
                                isProcessingPayment = true
                                coroutineScope.launch {
                                    delay(1500) // Simulate fast secure gateway handshake

                                    val paymentDetailStr = when (selectedPaymentMethod) {
                                        "باي بال" -> {
                                            val acc = paypalAccounts.find { it.id == selectedPayPalId }
                                            acc?.email ?: if (customPaymentInput.isNotBlank()) customPaymentInput else "PayPal Account"
                                        }
                                        "فيزا" -> {
                                            val c = visaCards.find { it.id == selectedVisaId }
                                            c?.cardNumberEncrypted ?: if (customPaymentInput.isNotBlank()) customPaymentInput else "Visa •••• 4242"
                                        }
                                        "بايننس" -> {
                                            val b = binanceAccounts.find { it.id == selectedBinanceId }
                                            if (b != null) "Binance ID: ${b.binanceId}" else if (customPaymentInput.isNotBlank()) customPaymentInput else "Binance Pay Wallet"
                                        }
                                        else -> "دفع نقدي مباشر"
                                    }

                                    val newDonation = DonationEntity(
                                        beneficiaryOrg = beneficiaryOrg,
                                        amount = amtNum,
                                        date = date,
                                        category = category,
                                        notes = notes,
                                        paymentMethod = selectedPaymentMethod,
                                        paymentDetails = paymentDetailStr
                                    )

                                    viewModel.addDonation(
                                        beneficiary = beneficiaryOrg,
                                        amount = amtNum,
                                        date = date,
                                        category = category,
                                        notes = notes,
                                        paymentMethod = selectedPaymentMethod,
                                        paymentDetails = paymentDetailStr
                                    )

                                    isProcessingPayment = false
                                    showDonateDialog = false
                                    showReceiptDialog = newDonation

                                    beneficiaryOrg = ""
                                    amount = ""
                                    notes = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier.testTag("confirm_donation_button")
                    ) {
                        Text("إتمام الدفع والتبرع بـ $${amount.ifBlank { "0" }}", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (!isProcessingPayment) {
                    TextButton(onClick = { showDonateDialog = false }) { Text("إلغاء") }
                }
            }
        )
    }

    // --- DIALOG 2: ADD NEW PAYPAL ACCOUNT ---
    if (showAddPayPalDialog) {
        AlertDialog(
            onDismissRequest = { showAddPayPalDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("P", color = Color(0xFF003087), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    Text("إضافة حساب PayPal جديد", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newPayPalEmail,
                        onValueChange = { newPayPalEmail = it },
                        label = { Text("البريد الإلكتروني لحساب PayPal") },
                        placeholder = { Text("user@example.com") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("paypal_email_input")
                    )

                    OutlinedTextField(
                        value = newPayPalAccountName,
                        onValueChange = { newPayPalAccountName = it },
                        label = { Text("تسمية الحساب / الوصف") },
                        leadingIcon = { Icon(Icons.Outlined.Label, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPayPalEmail.isNotBlank()) {
                            viewModel.addPayPalAccount(
                                email = newPayPalEmail,
                                accountName = newPayPalAccountName,
                                isDefault = paypalAccounts.isEmpty()
                            )
                            newPayPalEmail = ""
                            showAddPayPalDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003087)),
                    modifier = Modifier.testTag("save_paypal_button")
                ) {
                    Text("ربط وحفظ الحساب", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPayPalDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // --- DIALOG 3: ADD NEW VISA CARD ---
    if (showAddVisaDialog) {
        AlertDialog(
            onDismissRequest = { showAddVisaDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = BrandPrimary)
                    Text("إضافة بطاقة فيزا جديدة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newVisaHolder,
                        onValueChange = { newVisaHolder = it },
                        label = { Text("اسم صاحب البطاقة (كما في البطاقة)") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("visa_holder_input")
                    )

                    OutlinedTextField(
                        value = newVisaNumber,
                        onValueChange = { newVisaNumber = it },
                        label = { Text("رقم بطاقة الفيزا (16 رقم)") },
                        placeholder = { Text("4000 1234 5678 9010") },
                        leadingIcon = { Icon(Icons.Outlined.CreditCard, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("visa_number_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newVisaExpiry,
                            onValueChange = { newVisaExpiry = it },
                            label = { Text("تاريخ الانتهاء") },
                            placeholder = { Text("12/28") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = newVisaType,
                            onValueChange = { newVisaType = it },
                            label = { Text("نوع البطاقة") },
                            placeholder = { Text("Visa Platinum") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newVisaHolder.isNotBlank() && newVisaNumber.isNotBlank()) {
                            viewModel.addVisaCard(
                                cardHolder = newVisaHolder,
                                rawCardNumber = newVisaNumber,
                                expiry = newVisaExpiry.ifBlank { "12/28" },
                                cardType = newVisaType.ifBlank { "Visa" },
                                isDefault = visaCards.isEmpty()
                            )
                            newVisaHolder = ""
                            newVisaNumber = ""
                            showAddVisaDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    modifier = Modifier.testTag("save_visa_button")
                ) {
                    Text("تشفير وحفظ البطاقة", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddVisaDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // --- DIALOG 4: ADD NEW BINANCE ACCOUNT ---
    if (showAddBinanceDialog) {
        AlertDialog(
            onDismissRequest = { showAddBinanceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0B90B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("B", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                    Text("إضافة حساب منصة بايننس (Binance)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newBinanceId,
                        onValueChange = { newBinanceId = it },
                        label = { Text("معرف بايننس / Binance Pay ID") },
                        placeholder = { Text("883920192") },
                        leadingIcon = { Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("binance_id_input")
                    )

                    OutlinedTextField(
                        value = newBinanceEmailOrPhone,
                        onValueChange = { newBinanceEmailOrPhone = it },
                        label = { Text("البريد أو الهاتف المربوط بالحساب") },
                        placeholder = { Text("user@binance.com / +966...") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newBinanceWalletType,
                        onValueChange = { newBinanceWalletType = it },
                        label = { Text("نوع المحفظة / الشبكة") },
                        placeholder = { Text("USDT (TRC20)") },
                        leadingIcon = { Icon(Icons.Outlined.CurrencyExchange, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newBinanceLabel,
                        onValueChange = { newBinanceLabel = it },
                        label = { Text("تسمية الحساب") },
                        leadingIcon = { Icon(Icons.Outlined.Label, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newBinanceId.isNotBlank()) {
                            viewModel.addBinanceAccount(
                                binanceId = newBinanceId,
                                payEmailOrPhone = newBinanceEmailOrPhone.ifBlank { "حساب بايننس الموثق" },
                                walletType = newBinanceWalletType.ifBlank { "USDT (TRC20)" },
                                label = newBinanceLabel.ifBlank { "حساب بايننس الشخصي" },
                                isDefault = binanceAccounts.isEmpty()
                            )
                            newBinanceId = ""
                            newBinanceEmailOrPhone = ""
                            showAddBinanceDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0B90B), contentColor = Color.Black),
                    modifier = Modifier.testTag("save_binance_button")
                ) {
                    Text("حفظ وربط محفظة بايننس", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBinanceDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // --- DIALOG 4: PAYMENT SUCCESS RECEIPT ---
    showReceiptDialog?.let { donation ->
        AlertDialog(
            onDismissRequest = { showReceiptDialog = null },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("تم التبرع والدفع بنجاح!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = SuccessGreen)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.5f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الجهة المستفيدة:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(donation.beneficiaryOrg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("المبلغ المدفوع:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${donation.amount}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = SuccessGreen)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("طريقة الدفع:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${donation.paymentMethod} (${donation.paymentDetails})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("التاريخ:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(donation.date, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("رقم العملية Reference:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("TXN-${System.currentTimeMillis().toString().takeLast(8)}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showReceiptDialog = null }, colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) {
                    Text("إغلاق السند")
                }
            }
        )
    }
}

// ==========================================
// TAB 0: DONATIONS HISTORY TAB
// ==========================================
@Composable
fun DonationsHistoryTab(
    donations: List<DonationEntity>,
    onDeleteDonation: (DonationEntity) -> Unit,
    onShowReceipt: (DonationEntity) -> Unit,
    onQuickDonate: () -> Unit
) {
    if (donations.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Outlined.VolunteerActivism, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("لا توجد سجلات تبرعات سابقة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("قم بالتبرع عبر PayPal أو بطاقة الفيزا الآن لتوثيق أعمالك الخيرية.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onQuickDonate, colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) {
                Text("تبرع الآن")
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(donations) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen.copy(0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = SuccessGreen)
                                }

                                Column {
                                    Text(item.beneficiaryOrg, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("التصنيف: ${item.category} • ${item.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Text("$${item.amount}", fontWeight = FontWeight.ExtraBold, color = SuccessGreen, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Payment Method Chip Badge
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (item.paymentMethod.contains("باي بال")) {
                                    Surface(
                                        color = Color(0xFF003087).copy(0.12f),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text("P", color = Color(0xFF003087), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("PayPal: ${item.paymentDetails}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF003087))
                                        }
                                    }
                                } else if (item.paymentMethod.contains("فيزا")) {
                                    Surface(
                                        color = BrandPrimary.copy(0.12f),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Visa: ${item.paymentDetails}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = BrandPrimary)
                                        }
                                    }
                                } else if (item.paymentMethod.contains("بايننس")) {
                                    Surface(
                                        color = Color(0xFFF0B90B).copy(0.2f),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text("B", color = Color(0xFFB38600), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("${item.paymentDetails}", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF806000))
                                        }
                                    }
                                } else {
                                    Surface(
                                        color = SuccessGreen.copy(0.12f),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text("نقداً / آخر", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, color = SuccessGreen)
                                    }
                                }
                            }

                            Row {
                                IconButton(onClick = { onShowReceipt(item) }) {
                                    Icon(Icons.Outlined.ReceiptLong, contentDescription = "سند التبرع", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { onDeleteDonation(item) }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 1: PAYPAL ACCOUNTS TAB
// ==========================================
@Composable
fun PayPalAccountsTab(
    paypalAccounts: List<PayPalAccountEntity>,
    onAddPayPal: () -> Unit,
    onDeletePayPal: (PayPalAccountEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF003087)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("حسابات PayPal المربوطة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("تبرع بضغطة زر واحدة بأمان موثّق عالمياً", color = Color.White.copy(0.8f), fontSize = 12.sp)
                }

                Button(
                    onClick = onAddPayPal,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF003087)),
                    modifier = Modifier.testTag("add_paypal_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة حساب", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (paypalAccounts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا يوجد حساب PayPal مضاف حالياً. انقر على إضافة حساب للبدء.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(paypalAccounts) { acc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF003087)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("P", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(acc.email, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    if (acc.isDefault) {
                                        Surface(color = SuccessGreen.copy(0.15f), shape = RoundedCornerShape(4.dp)) {
                                            Text("افتراضي", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(acc.accountName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            IconButton(onClick = { onDeletePayPal(acc) }) {
                                Icon(Icons.Outlined.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: VISA CARDS TAB
// ==========================================
@Composable
fun VisaCardsTab(
    visaCards: List<VisaCardEntity>,
    onAddVisa: () -> Unit,
    onDeleteVisa: (VisaCardEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("البطاقات الائتمانية (Visa / Mastercard)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(
                onClick = onAddVisa,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                modifier = Modifier.testTag("add_visa_card_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة بطاقة", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (visaCards.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد بطاقة فيزا مضافة. اضغط إضافة بطاقة جديدة لتشفيرها وحفظها.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(visaCards) { card ->
                    // Realistic Stylish Credit Card UI Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF1A1F71),
                                        Color(0xFF0F2027),
                                        Color(0xFF203A43)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Row: Visa Logo & Delete Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = card.cardType.ifBlank { "VISA" }.uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    letterSpacing = 2.sp
                                )

                                IconButton(
                                    onClick = { onDeleteVisa(card) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "حذف البطاقة", tint = Color.White.copy(0.7f))
                                }
                            }

                            // Middle: Chip Icon & Masked Card Number
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp, 26.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFD4AF37)) // Gold Chip color
                                )
                                Text(
                                    text = card.cardNumberEncrypted,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    letterSpacing = 2.sp
                                )
                            }

                            // Bottom: Holder Name & Expiry
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text("CARD HOLDER", fontSize = 9.sp, color = Color.White.copy(0.6f))
                                    Text(card.cardHolderName.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Column {
                                    Text("EXPIRES", fontSize = 9.sp, color = Color.White.copy(0.6f))
                                    Text(card.expiryDate, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 3: BINANCE ACCOUNTS TAB
// ==========================================
@Composable
fun BinanceAccountsTab(
    binanceAccounts: List<BinanceAccountEntity>,
    onAddBinance: () -> Unit,
    onDeleteBinance: (BinanceAccountEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2329)), // Binance dark slate/black theme
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0B90B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("B", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    }

                    Column {
                        Text("محافظ منصة بايننس (Binance)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("تبرع واستقبل العملات الرقمية عبر Binance Pay", color = Color(0xFFF0B90B), fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = onAddBinance,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0B90B), contentColor = Color.Black),
                    modifier = Modifier.testTag("add_binance_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة محفظة", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (binanceAccounts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد محفظة بايننس مضافة حالياً. انقر على إضافة محفظة للربط.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(binanceAccounts) { acc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color(0xFFF0B90B).copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0B90B).copy(0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("B", color = Color(0xFFD9A000), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("ID: ${acc.binanceId}", fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = FontFamily.Monospace)
                                    Surface(color = Color(0xFFF0B90B).copy(0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text(acc.walletType, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFF997300), fontWeight = FontWeight.Bold)
                                    }
                                    if (acc.isDefault) {
                                        Surface(color = SuccessGreen.copy(0.15f), shape = RoundedCornerShape(4.dp)) {
                                            Text("افتراضي", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text("${acc.accountLabel} • ${acc.payEmailOrPhone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            IconButton(onClick = { onDeleteBinance(acc) }) {
                                Icon(Icons.Outlined.Delete, contentDescription = "حذف المحفظة", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
