package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.util.AppLanguage

data class StatCardItem(
    val title: String,
    val count: Int,
    val icon: ImageVector,
    val color: Color,
    val moduleKey: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onOpenAiAssistant: () -> Unit
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isRtl by viewModel.isRtl.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()
    val socialMediaAccounts by viewModel.socialMediaAccounts.collectAsStateWithLifecycle()
    val bankAccounts by viewModel.bankAccounts.collectAsStateWithLifecycle()
    val donations by viewModel.donations.collectAsStateWithLifecycle()
    val aiTools by viewModel.aiTools.collectAsStateWithLifecycle()
    val passwords by viewModel.passwords.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    var showQuickAddBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val strings = AppLanguage

    val statCards = listOf(
        StatCardItem(strings.modulePhotos, photos.size, Icons.Outlined.PhotoLibrary, BrandPrimary, "photos"),
        StatCardItem(strings.moduleBookmarks, bookmarks.size, Icons.Outlined.Bookmark, BrandAccent, "bookmarks"),
        StatCardItem(strings.moduleNotes, notes.size, Icons.Outlined.Lightbulb, WarningAmber, "notes"),
        StatCardItem(strings.moduleSubscriptions, subscriptions.size, Icons.Outlined.Subscriptions, DangerRed, "subscriptions"),
        StatCardItem(strings.moduleSocialMedia, socialMediaAccounts.size, Icons.Outlined.Public, Color(0xFF1877F2), "social_media"),
        StatCardItem(strings.moduleBank, bankAccounts.size, Icons.Outlined.AccountBalance, Color(0xFF8B5CF6), "bank"),
        StatCardItem(strings.moduleDonations, donations.size, Icons.Outlined.VolunteerActivism, SuccessGreen, "donations"),
        StatCardItem(strings.moduleAiTools, aiTools.size, Icons.Outlined.AutoAwesome, BrandPrimary, "aitools"),
        StatCardItem(strings.modulePasswords, passwords.size, Icons.Outlined.Lock, WarningAmber, "passwords"),
        StatCardItem(strings.moduleTasks, tasks.size, Icons.Outlined.TaskAlt, SuccessGreen, "tasks")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_logo_1783006129958),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = strings.appName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = strings.welcomeUser(userName),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme Toggle"
                        )
                    }

                    IconButton(onClick = { viewModel.toggleLanguage() }) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(0.4f),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = if (isRtl) "EN" else "عربي",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.selectModule("admin") }) {
                        Icon(Icons.Outlined.AdminPanelSettings, contentDescription = "لوحة المدير", tint = WarningAmber)
                    }

                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Outlined.Logout, contentDescription = "خروج", tint = DangerRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ExtendedFloatingActionButton(
                    onClick = onOpenAiAssistant,
                    icon = { Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = Color.White) },
                    text = { Text(strings.aiAssistantTitle, fontWeight = FontWeight.Bold, color = Color.White) },
                    containerColor = BrandPrimary,
                    modifier = Modifier.testTag("ai_assistant_fab")
                )

                FloatingActionButton(
                    onClick = { showQuickAddBottomSheet = true },
                    containerColor = BrandAccent,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("quick_add_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = strings.quickAdd)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner Widget
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("hero_banner_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandSecondary)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner_1783006143919),
                            contentDescription = "Header Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .opacity(0.45f)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            BrandSecondary.copy(0.9f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .padding(20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Surface(
                                    color = BrandAccent.copy(0.2f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = if (isRtl) "التنظيم الشامل والأمان العالي" else "Comprehensive Organization & Security",
                                        color = BrandAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isRtl) "لوحة التحكم الرئيسية" else "Main Dashboard",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isRtl) "جميع ملفاتك واشتراكاتك وحساباتك البنكية مشفرة في مكان واحد" else "All your files, subscriptions, and bank accounts encrypted in one place",
                                    color = TextSecondaryDark,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Global Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text(strings.searchPlaceholder) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            // Search Results Section if query is not empty
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase()
                val matchedNotes = notes.filter { it.title.contains(q, true) || it.content.contains(q, true) }
                val matchedBookmarks = bookmarks.filter { it.title.contains(q, true) || it.url.contains(q, true) }
                val matchedSubs = subscriptions.filter { it.serviceName.contains(q, true) }
                val matchedPhotos = photos.filter { it.title.contains(q, true) || it.category.contains(q, true) }

                item {
                    Text(
                        text = if (isRtl) "نتائج البحث الشامل:" else "Search Results:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (matchedNotes.isEmpty() && matchedBookmarks.isEmpty() && matchedSubs.isEmpty() && matchedPhotos.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(strings.noResultsFor(searchQuery), modifier = Modifier.padding(16.dp))
                        }
                    }
                } else {
                    items(matchedNotes) { n ->
                        Card(
                            onClick = { viewModel.selectModule("notes") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = WarningAmber)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("ملاحظة: ${n.title}", fontWeight = FontWeight.Bold)
                                    Text(n.content, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    items(matchedSubs) { s ->
                        Card(
                            onClick = { viewModel.selectModule("subscriptions") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Subscriptions, contentDescription = null, tint = DangerRed)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("اشتراك: ${s.serviceName}", fontWeight = FontWeight.Bold)
                                    Text("السعر: $${s.cost} - تجديد: ${s.endDate}", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    items(matchedBookmarks) { b ->
                        Card(
                            onClick = { viewModel.selectModule("bookmarks") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Bookmark, contentDescription = null, tint = BrandAccent)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("موقع: ${b.title}", fontWeight = FontWeight.Bold)
                                    Text(b.url, fontSize = 12.sp, color = BrandPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Statistics Grid Header
            item {
                Text(
                    text = if (isRtl) "الأقسام والإحصائيات السريعة" else "Quick Stats & Categories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // 10 Stat Cards Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val chunked = statCards.chunked(2)
                    chunked.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { card ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.selectModule(card.moduleKey) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(card.color.copy(0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(card.icon, contentDescription = null, tint = card.color)
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = card.count.toString(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                            Text(
                                                text = card.title,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Subscriptions Expiring Soon Alert Widget
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DangerRed.copy(0.1f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DangerRed.copy(0.4f), DangerRed.copy(0.1f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = DangerRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isRtl) "تنبيه الاشتراكات القادمة للتجديد" else "Upcoming Subscriptions Alert",
                                fontWeight = FontWeight.Bold,
                                color = DangerRed
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        subscriptions.take(2).forEach { sub ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(sub.serviceName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("$${sub.cost} (${sub.endDate})", color = DangerRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Recent Notes Widget
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isRtl) "آخر الأفكار والملاحظات" else "Recent Notes & Ideas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        TextButton(onClick = { viewModel.selectModule("notes") }) {
                            Text(if (isRtl) "عرض الكل" else "View All", color = BrandPrimary)
                        }
                    }

                    if (notes.isEmpty()) {
                        Text(if (isRtl) "لا توجد ملاحظات مضافة بعد." else "No notes added yet.", fontSize = 12.sp, color = TextSecondaryLight)
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(notes.take(4)) { note ->
                                Card(
                                    modifier = Modifier
                                        .width(180.dp)
                                        .clickable { viewModel.selectModule("notes") },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(note.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(note.content, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(color = WarningAmber.copy(0.2f), shape = RoundedCornerShape(6.dp)) {
                                            Text(note.category, fontSize = 10.sp, color = WarningAmber, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Quick Add Bottom Sheet
    if (showQuickAddBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQuickAddBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (isRtl) "إضافة محتوى سريع إلى 'رتب حالك'" else "Quick Add to Ratib Halak",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                val actions = listOf(
                    Triple(strings.modulePhotos, Icons.Outlined.PhotoLibrary) { viewModel.selectModule("photos") },
                    Triple(strings.addFavoriteBookmark, Icons.Outlined.Bookmark) { viewModel.selectModule("bookmarks") },
                    Triple(strings.addNoteOrIdea, Icons.Outlined.Lightbulb) { viewModel.selectModule("notes") },
                    Triple(strings.addNewSubscription, Icons.Outlined.Subscriptions) { viewModel.selectModule("subscriptions") },
                    Triple(strings.addSocialAccount, Icons.Outlined.Public) { viewModel.selectModule("social_media") },
                    Triple(strings.addBankAccount, Icons.Outlined.AccountBalance) { viewModel.selectModule("bank") },
                    Triple(strings.addNewDonation, Icons.Outlined.VolunteerActivism) { viewModel.selectModule("donations") }
                )

                actions.forEach { (label, icon, onClick) ->
                    Card(
                        onClick = {
                            onClick()
                            showQuickAddBottomSheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, contentDescription = null, tint = BrandPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(label, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// Helper Modifier opacity extension
private fun Modifier.opacity(alpha: Float) = this.then(
    Modifier.graphicsLayer(alpha = alpha)
)
