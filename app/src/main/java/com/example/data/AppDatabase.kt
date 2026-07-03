package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PhotoEntity::class,
        BookmarkEntity::class,
        NoteEntity::class,
        SubscriptionEntity::class,
        SocialMediaAccountEntity::class,
        BankAccountEntity::class,
        DonationEntity::class,
        PayPalAccountEntity::class,
        VisaCardEntity::class,
        BinanceAccountEntity::class,
        AiToolEntity::class,
        PasswordVaultEntity::class,
        TaskEntity::class,
        ActivityLogEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun noteDao(): NoteDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun socialMediaAccountDao(): SocialMediaAccountDao
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun donationDao(): DonationDao
    abstract fun payPalAccountDao(): PayPalAccountDao
    abstract fun visaCardDao(): VisaCardDao
    abstract fun binanceAccountDao(): BinanceAccountDao
    abstract fun aiToolDao(): AiToolDao
    abstract fun passwordVaultDao(): PasswordVaultDao
    abstract fun taskDao(): TaskDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ratib_halak_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(db: AppDatabase) {
                // Populate initial AI tools
                val aiTools = listOf(
                    AiToolEntity(name = "ChatGPT", category = "Chat AI", description = "المساعد الذكي الأول للتوليد والإجابة عن الأسئلة", url = "https://chat.openai.com", isFavorite = true),
                    AiToolEntity(name = "Gemini", category = "Chat AI", description = "نموذج Google المتقدم للذكاء الاصطناعي التوليدي Multi-modal", url = "https://gemini.google.com", isFavorite = true),
                    AiToolEntity(name = "Claude AI", category = "Chat AI", description = "مساعد م متميز في التحليل والتفكير والبرمجة من Anthropic", url = "https://claude.ai", isFavorite = true),
                    AiToolEntity(name = "DeepSeek", category = "Chat AI", description = "نموذج الذكاء الاصطناعي المتطور للاستدلال والبرمجة", url = "https://deepseek.com", isFavorite = true),
                    AiToolEntity(name = "Perplexity AI", category = "Chat AI", description = "محرك بحث ذكي مدعوم بالذكاء الاصطناعي مع توثيق المصادر", url = "https://perplexity.ai", isFavorite = false),
                    
                    AiToolEntity(name = "Midjourney", category = "Image AI", description = "أداة توليد الصور الفنية فائقة الدقة", url = "https://midjourney.com", isFavorite = true),
                    AiToolEntity(name = "Photoroom", category = "Image AI", description = "إزالة خلفيات الصور وتحسين جودة منتجات المتاجر", url = "https://photoroom.com", isFavorite = false),
                    AiToolEntity(name = "Canva AI Magic", category = "Image AI", description = "أدوات تصميم وتعديل الصور وتوليد الرسوم بالذكاء الاصطناعي", url = "https://canva.com", isFavorite = false),

                    AiToolEntity(name = "Runway Gen-2", category = "Video AI", description = "توليد وتعديل الفيديو من النصوص بالذكاء الاصطناعي", url = "https://runwayml.com", isFavorite = true),
                    AiToolEntity(name = "ElevenLabs", category = "Video AI", description = "تحويل النصوص إلى كلام بواقعية مذهلة وتنسيق الأصوات", url = "https://elevenlabs.io", isFavorite = true),

                    AiToolEntity(name = "Notion AI", category = "Writing AI", description = "مساعد الكتابة والتلخيص وتنظيم المستندات", url = "https://notion.so", isFavorite = true),
                    AiToolEntity(name = "Grammarly AI", category = "Writing AI", description = "التدقيق اللغوي وإعادة الصياغة الاحترافية", url = "https://grammarly.com", isFavorite = false),

                    AiToolEntity(name = "GitHub Copilot", category = "Coding AI", description = "مساعد البرمجة الذكي واقتراح الأكواد أوتوماتيكياً", url = "https://github.com/features/copilot", isFavorite = true),
                    AiToolEntity(name = "Cursor IDE", category = "Coding AI", description = "محرر أكواد ذكي مدمج بأحدث نماذج الذكاء الاصطناعي", url = "https://cursor.sh", isFavorite = true),

                    AiToolEntity(name = "Photomath", category = "Study AI", description = "شرح دروس الرياضيات وحل المسائل خطوة بخطوة", url = "https://photomath.com", isFavorite = false),
                    AiToolEntity(name = "Quizlet Q-Chat", category = "Study AI", description = "المذاكرة التفاعلية وإنشاء بطاقات المراجعة الذكية", url = "https://quizlet.com", isFavorite = false)
                )
                aiTools.forEach { db.aiToolDao().insertAiTool(it) }

                // Populate initial Bookmarks
                db.bookmarkDao().insertBookmark(
                    BookmarkEntity(title = "منصة جوجل للذكاء الاصطناعي", url = "https://ai.google", description = "آخر تحديثات وأبحاث نماذج جيميناي", category = "تقنية", isFavorite = true)
                )
                db.bookmarkDao().insertBookmark(
                    BookmarkEntity(title = "تراكس أمان الحسابات", url = "https://myaccount.google.com", description = "إدارة أمان وخصوصية الحسابات الشخصية", category = "أمان", isFavorite = true)
                )

                // Populate initial Subscriptions
                db.subscriptionDao().insertSubscription(
                    SubscriptionEntity(serviceName = "ChatGPT Plus", cost = 20.0, billingCycle = "شهري", startDate = "2026-06-15", endDate = "2026-07-15", category = "ذكاء اصطناعي", status = "نشط")
                )
                db.subscriptionDao().insertSubscription(
                    SubscriptionEntity(serviceName = "YouTube Premium", cost = 11.99, billingCycle = "شهري", startDate = "2026-06-01", endDate = "2026-07-01", category = "ترفيه", status = "نشط")
                )
                db.subscriptionDao().insertSubscription(
                    SubscriptionEntity(serviceName = "iCloud Storage 200GB", cost = 2.99, billingCycle = "شهري", startDate = "2026-06-10", endDate = "2026-07-10", category = "تخزين سحابي", status = "نشط")
                )

                // Populate initial Notes
                db.noteDao().insertNote(
                    NoteEntity(title = "خطط تطوير مشروع رتب حالك", content = "1. إضافة تشفير AES-256 للحسابات البنكية\n2. تفعيل المصادقة بالبصمة\n3. ربط المساعد الذكي مع Gemini REST API\n4. دعم الوضع الليلي واللغة العربية LTR/RTL", category = "أفكار", tags = "برمجة, أمان, تطوير")
                )

                // Populate initial Social Media Accounts
                val initialSocialAccounts = listOf(
                    SocialMediaAccountEntity(platform = "اليوتيوب", accountName = "قناة التقنية والبرمجة", url = "https://youtube.com/@tech_arabic", description = "دورة تطوير تطبيقات الموبايل بالذكاء الاصطناعي", niche = "تكنولوجيا", importanceRating = 5, isFavorite = true),
                    SocialMediaAccountEntity(platform = "التك توك", accountName = "حساب التصاميم والريلز", url = "https://tiktok.com/@ui_ux_arab", description = "فيديوهات قصيرة ونصائح في البرمجة والتصميم", niche = "تصميم", importanceRating = 4, isFavorite = true),
                    SocialMediaAccountEntity(platform = "الانستجرام", accountName = "صفحة رتب حالك الرسمية", url = "https://instagram.com/ratib.halak", description = "نصائح يومية للتنظيم وإدارة الوقت والأعمال", niche = "إنتاجية", importanceRating = 5, isFavorite = true),
                    SocialMediaAccountEntity(platform = "الفيس بوك", accountName = "مجتمع المطورين العرب", url = "https://facebook.com/groups/arabdevs", description = "مجموعة نقاشات برمجية وتجارب المطورين", niche = "برمجة", importanceRating = 4, isFavorite = false),
                    SocialMediaAccountEntity(platform = "بنتارس", accountName = "لوحة التصميم والديكور", url = "https://pinterest.com/design_inspiration", description = "أفكار تصاميم واجهة المستخدم والديكورات العصرية", niche = "إلهام فني", importanceRating = 3, isFavorite = false),
                    SocialMediaAccountEntity(platform = "توتر", accountName = "حساب التحديثات والأخبار (X)", url = "https://x.com/tech_news_ar", description = "تغطية أحدث أخبار الذكاء الاصطناعي والتقنية", niche = "أخبار", importanceRating = 5, isFavorite = true),
                    SocialMediaAccountEntity(platform = "الوايس اب", accountName = "مجتمع المطورين على واتساب", url = "https://wa.me/966500000000", description = "قناة واتساب الرسمية للتنبيهات والمحادثات المباشرة", niche = "تواصل", importanceRating = 4, isFavorite = true),
                    SocialMediaAccountEntity(platform = "التلجرام", accountName = "قناة التليجرام الإخبارية", url = "https://t.me/ratib_halak_official", description = "ملفات، كورسات مجانية، وإشعارات حصرية", niche = "تعليم", importanceRating = 5, isFavorite = true)
                )
                initialSocialAccounts.forEach { db.socialMediaAccountDao().insertAccount(it) }

                // Populate initial Bank Account (Encrypted)
                db.bankAccountDao().insertBankAccount(
                    BankAccountEntity(bankName = "البنك العربي - Arab Bank", accountNumber = "•••• •••• 4821", iban = "SA82 1000 0001 2345 6789 0123", beneficiaryName = "محمد أحمد", notes = "حساب الادخار الشخصي الرئيسي (مشفّر AES-256)")
                )

                // Populate initial Donation
                db.donationDao().insertDonation(
                    DonationEntity(beneficiaryOrg = "منصة إحسان الخيرية", amount = 150.0, date = "2026-06-25", category = "كفالة أيتام", notes = "تبرع دوري شهري", paymentMethod = "فيزا", paymentDetails = "بطاقة فيزا تنتهي بـ 4242")
                )

                // Populate initial PayPal Account
                db.payPalAccountDao().insertPayPalAccount(
                    PayPalAccountEntity(email = "m.otaibi.paypal@gmail.com", accountName = "حساب باي بال الشخصي (موثّق)", isDefault = true)
                )

                // Populate initial Visa Card
                db.visaCardDao().insertVisaCard(
                    VisaCardEntity(cardHolderName = "MOHAMMED AL-OTAIBI", cardNumberEncrypted = "•••• •••• •••• 4242", expiryDate = "09/28", cardType = "Visa Platinum", isDefault = true)
                )

                // Populate initial Binance Account
                db.binanceAccountDao().insertBinanceAccount(
                    BinanceAccountEntity(binanceId = "883920192", payEmailOrPhone = "m.otaibi@binance.com", walletType = "USDT (TRC20)", accountLabel = "حساب منصة بايننس الموثق (Binance Pay)", isDefault = true)
                )

                // Populate initial Task
                db.taskDao().insertTask(
                    TaskEntity(title = "مراجعة الاشتراكات القادمة هذا الشهر", dueDate = "2026-07-05", priority = "عالية", category = "مالية")
                )
                db.taskDao().insertTask(
                    TaskEntity(title = "حفظ نسخة احتياطية مشفرة من الملاحظات", dueDate = "2026-07-10", priority = "متوسطة", category = "أمان")
                )

                // Populate initial activity log
                db.activityLogDao().insertLog(
                    ActivityLogEntity(actionType = "تسجيل دخول", moduleName = "النظام", description = "تم تفعيل حساب المستخدم مع البصمة بنجاح")
                )
            }
        }
    }
}
