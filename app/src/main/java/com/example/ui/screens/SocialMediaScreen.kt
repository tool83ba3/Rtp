package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SocialMediaAccountEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

data class PlatformMeta(
    val name: String,
    val brandColor: Color,
    val icon: ImageVector,
    val defaultUrlPrefix: String
)

val socialPlatforms = listOf(
    PlatformMeta("اليوتيوب", Color(0xFFFF0000), Icons.Outlined.SmartDisplay, "https://youtube.com/@"),
    PlatformMeta("التك توك", Color(0xFFFE2C55), Icons.Outlined.MusicNote, "https://tiktok.com/@"),
    PlatformMeta("الانستجرام", Color(0xFFE1306C), Icons.Outlined.CameraAlt, "https://instagram.com/"),
    PlatformMeta("الفيس بوك", Color(0xFF1877F2), Icons.Outlined.Public, "https://facebook.com/"),
    PlatformMeta("بنتارس", Color(0xFFE60023), Icons.Outlined.PushPin, "https://pinterest.com/"),
    PlatformMeta("توتر", Color(0xFF1DA1F2), Icons.Outlined.AlternateEmail, "https://x.com/"),
    PlatformMeta("الوايس اب", Color(0xFF25D366), Icons.Outlined.Chat, "https://wa.me/"),
    PlatformMeta("التلجرام", Color(0xFF229ED9), Icons.Outlined.Send, "https://t.me/")
)

fun getPlatformMeta(platformName: String): PlatformMeta {
    return socialPlatforms.find { it.name == platformName || platformName.contains(it.name) }
        ?: PlatformMeta(platformName, BrandPrimary, Icons.Outlined.Share, "https://")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialMediaScreen(viewModel: MainViewModel) {
    val accounts by viewModel.socialMediaAccounts.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedPlatformFilter by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedPlatform by remember { mutableStateOf("اليوتيوب") }
    var accountName by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var niche by remember { mutableStateOf("تكنولوجيا") }
    var rating by remember { mutableIntStateOf(5) }

    // Filtered list based on platform chip and search query
    val filteredAccounts = accounts.filter { acc ->
        val matchesPlatform = (selectedPlatformFilter == "الكل" || acc.platform == selectedPlatformFilter)
        val matchesSearch = searchQuery.isBlank() ||
                acc.accountName.contains(searchQuery, ignoreCase = true) ||
                acc.url.contains(searchQuery, ignoreCase = true) ||
                acc.niche.contains(searchQuery, ignoreCase = true) ||
                acc.description.contains(searchQuery, ignoreCase = true)
        matchesPlatform && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("مواقع التواصل الاجتماعي", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("اليوتيوب، التك توك، انستجرام، فيس بوك، بينتارس، توتر، واتساب، تلجرام", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.selectModule("dashboard") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    url = getPlatformMeta(selectedPlatform).defaultUrlPrefix
                    showAddDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                text = { Text("إضافة حساب تواصل", fontWeight = FontWeight.Bold, color = Color.White) },
                containerColor = BrandPrimary,
                modifier = Modifier.testTag("add_social_account_fab")
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
            // Header Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("إجمالي الحسابات المسجلة", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${accounts.size} حساب وقناة", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = BrandPrimary)
                    }

                    Surface(
                        color = BrandAccent.copy(0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Public, contentDescription = null, tint = BrandAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("8 منصات مدعومة", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandAccent)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث باسم الحساب، الرابط، أو المجال...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_social_accounts"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Platforms Horizontal Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = (selectedPlatformFilter == "الكل"),
                        onClick = { selectedPlatformFilter = "الكل" },
                        label = { Text("الكل (${accounts.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                items(socialPlatforms) { platform ->
                    val count = accounts.count { it.platform == platform.name }
                    val isSelected = (selectedPlatformFilter == platform.name)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPlatformFilter = platform.name },
                        label = { Text("${platform.name} ($count)") },
                        leadingIcon = {
                            Icon(
                                platform.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else platform.brandColor,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = platform.brandColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Accounts List
            if (filteredAccounts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (searchQuery.isNotBlank() || selectedPlatformFilter != "الكل")
                                "لا توجد حسابات مطابقة للتصفية."
                            else
                                "لا توجد حسابات تواصل مضافة حالياً.\nانقر على زر الإضافة لربط حساباتك وقنواتك.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredAccounts) { acc ->
                        val meta = getPlatformMeta(acc.platform)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Platform Icon Avatar
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(meta.brandColor.copy(0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(meta.icon, contentDescription = null, tint = meta.brandColor, modifier = Modifier.size(24.dp))
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(acc.accountName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Surface(
                                            color = meta.brandColor.copy(0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(acc.platform, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = meta.brandColor, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (acc.description.isNotBlank()) {
                                        Text(acc.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(4.dp)) {
                                            Text(acc.niche, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                        }

                                        Row {
                                            repeat(acc.importanceRating) {
                                                Icon(Icons.Filled.Star, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(13.dp))
                                            }
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            try {
                                                val uriStr = if (acc.url.startsWith("http://") || acc.url.startsWith("https://")) acc.url else "https://${acc.url}"
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr))
                                                context.startActivity(intent)
                                            } catch (e: Exception) { }
                                        }
                                    ) {
                                        Icon(Icons.Outlined.OpenInNew, contentDescription = "فتح الحساب", tint = meta.brandColor)
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteSocialMediaAccount(acc.id) }
                                    ) {
                                        Icon(Icons.Outlined.Delete, contentDescription = "حذف الحساب", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- ADD DIALOG ---
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val meta = getPlatformMeta(selectedPlatform)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(meta.brandColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(meta.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Text("حفظ حساب / قناة جديدة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("اختر منصة التواصل الاجتماعي:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(socialPlatforms) { plat ->
                            val isSel = (selectedPlatform == plat.name)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        selectedPlatform = plat.name
                                        if (url.isBlank() || url.startsWith("https://")) {
                                            url = plat.defaultUrlPrefix
                                        }
                                    },
                                color = if (isSel) plat.brandColor else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(plat.icon, contentDescription = null, tint = if (isSel) Color.White else plat.brandColor, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(plat.name, fontSize = 11.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        label = { Text("اسم الحساب / القناة / الصفحة / المجموعة") },
                        placeholder = { Text("مثال: قناة التقنية والبرمجة") },
                        leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("social_name_input")
                    )

                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("رابط الحساب / المعرف / رقم الهاتف") },
                        placeholder = { Text("https://...") },
                        leadingIcon = { Icon(Icons.Outlined.Link, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("social_url_input")
                    )

                    OutlinedTextField(
                        value = niche,
                        onValueChange = { niche = it },
                        label = { Text("المجال / التصنيف") },
                        placeholder = { Text("تكنولوجيا، أفكار، أخبار...") },
                        leadingIcon = { Icon(Icons.Outlined.Category, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("الوصف / ملاحظات") },
                        leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column {
                        Text("مستوى الأهمية / التفضيل:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (1..5).forEach { star ->
                                IconButton(onClick = { rating = star }) {
                                    Icon(
                                        imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = null,
                                        tint = if (star <= rating) WarningAmber else MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (accountName.isNotBlank()) {
                            viewModel.addSocialMediaAccount(
                                platform = selectedPlatform,
                                name = accountName,
                                url = url,
                                description = description,
                                niche = niche,
                                rating = rating
                            )
                            accountName = ""
                            description = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = getPlatformMeta(selectedPlatform).brandColor),
                    modifier = Modifier.testTag("save_social_account_button")
                ) {
                    Text("حفظ الحساب", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }
}
