package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val imageUri: String,
    val albumName: String = "افتراضي",
    val isFavorite: Boolean = false,
    val isEncrypted: Boolean = true,
    val isTrashed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val description: String,
    val category: String,
    val faviconUrl: String = "",
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String,
    val tags: String = "",
    val imageAttachment: String = "",
    val audioAttachment: String = "",
    val isArchived: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serviceName: String,
    val cost: Double,
    val billingCycle: String, // Monthly, Quarterly, Yearly
    val startDate: String,
    val endDate: String,
    val category: String,
    val status: String = "نشط", // نشط, ملغى
    val autoRenew: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "social_media_accounts")
data class SocialMediaAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val platform: String = "اليوتيوب", // اليوتيوب, التك توك, الانستجرام, الفيس بوك, بنتارس, توتر, الوايس اب, التلجرام
    val accountName: String,
    val url: String,
    val description: String = "",
    val niche: String = "عام",
    val importanceRating: Int = 3, // 1 to 5
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

typealias YoutubeChannelEntity = SocialMediaAccountEntity

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bankName: String,
    val accountNumber: String,
    val iban: String,
    val beneficiaryName: String,
    val notes: String = "",
    val encryptionType: String = "AES-256",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val beneficiaryOrg: String,
    val amount: Double,
    val date: String,
    val category: String = "خيري",
    val notes: String = "",
    val paymentMethod: String = "فيزا", // "باي بال", "فيزا", "نقدي"
    val paymentDetails: String = "", // e.g. "paypal.user@gmail.com" or "•••• 4242"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "paypal_accounts")
data class PayPalAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val accountName: String = "حساب باي بال الشخصي",
    val isDefault: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "visa_cards")
data class VisaCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardHolderName: String,
    val cardNumberEncrypted: String, // e.g., "•••• •••• •••• 4242"
    val expiryDate: String, // e.g., "12/28"
    val cardType: String = "Visa", // Visa, Mastercard, Mada
    val isDefault: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "binance_accounts")
data class BinanceAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val binanceId: String, // e.g., "82940183" or Binance Pay ID
    val payEmailOrPhone: String = "", // e.g. "user@binance.com"
    val walletType: String = "USDT (TRC20)", // USDT, BUSD, BTC, ETH
    val accountLabel: String = "حساب منصة بايننس (Binance Pay)",
    val isDefault: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_tools")
data class AiToolEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Chat AI, Image AI, Video AI, Writing AI, Coding AI, Study AI
    val description: String,
    val url: String,
    val isFavorite: Boolean = false,
    val iconUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "passwords")
data class PasswordVaultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val username: String,
    val encryptedPasswordHash: String,
    val url: String = "",
    val notes: String = "",
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dueDate: String,
    val priority: String = "متوسطة", // عالية, متوسطة, منخفضة
    val isCompleted: Boolean = false,
    val category: String = "عام",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionType: String, // دخول, إضافة, تعديل, حذف, تشفير
    val moduleName: String,
    val description: String,
    val userRole: String = "مستخدم رئيسي",
    val timestamp: Long = System.currentTimeMillis()
)
