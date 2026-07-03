package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(viewModel: MainViewModel) {
    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val trashedPhotos by viewModel.trashedPhotos.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var showTrashDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("شخصي") }
    var album by remember { mutableStateOf("ألبومي الخاص") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إدارة الصور الخاصة والتشفير", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.selectModule("dashboard") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showTrashDialog = true }) {
                        BadgedBox(badge = { Badge { Text(trashedPhotos.size.toString()) } }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "سلة المحذوفات", tint = DangerRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BrandPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_photo_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة صورة")
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
            Surface(
                color = BrandPrimary.copy(0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = BrandPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "جميع الصور المرفوعة محمية ومشفرة بكلمة مرور وبصمة الإصبع.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (photos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, modifier = Modifier.size(64.dp), tint = TextSecondaryLight)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("لا توجد صور محفوظة حالياً", color = TextSecondaryLight)
                        Text("اضغط على زر (+) لإضافة صورة مشفرة جديدة", fontSize = 12.sp, color = TextSecondaryLight)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(photos) { photo ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column {
                                Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_hero_banner_1783006143919),
                                        contentDescription = photo.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Surface(
                                        color = BgDark.copy(0.7f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = BrandAccent, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("مشفّر", fontSize = 10.sp, color = Color.White)
                                        }
                                    }
                                }

                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(photo.title, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text(photo.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(color = BrandPrimary.copy(0.15f), shape = RoundedCornerShape(6.dp)) {
                                            Text(photo.albumName, fontSize = 10.sp, color = BrandPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }

                                        IconButton(onClick = { viewModel.moveToTrashPhoto(photo) }) {
                                            Icon(Icons.Outlined.Delete, contentDescription = "حذف", tint = DangerRed, modifier = Modifier.size(20.dp))
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
            title = { Text("إضافة صورة مشفرة جديدة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان الصورة") })
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("الوصف") })
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("التصنيف") })
                    OutlinedTextField(value = album, onValueChange = { album = it }, label = { Text("اسم الألبوم") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addPhoto(title, description, category, album)
                        title = ""
                        description = ""
                        showAddDialog = false
                    }
                }) {
                    Text("حفظ الصورة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }

    if (showTrashDialog) {
        AlertDialog(
            onDismissRequest = { showTrashDialog = false },
            title = { Text("سلة المحذوفات") },
            text = {
                Column {
                    if (trashedPhotos.isEmpty()) {
                        Text("سلة المحذوفات فارغة.")
                    } else {
                        trashedPhotos.forEach { photo ->
                            Text("• ${photo.title}", fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showTrashDialog = false }) { Text("إغلاق") }
            }
        )
    }
}
