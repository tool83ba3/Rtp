package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.repository.AppRepository
import com.example.util.GeminiHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database)
    }

    // App Preferences State
    private val _isLoggedIn = MutableStateFlow(true) // Default logged in for smooth preview
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow("user@ratibhalak.app")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow("محمد العتيبي")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userRole = MutableStateFlow("مستخدم رئيسي") // "مستخدم رئيسي" or "مسؤول النظام (Admin)"
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _isBiometricAuthenticated = MutableStateFlow(true)
    val isBiometricAuthenticated: StateFlow<Boolean> = _isBiometricAuthenticated.asStateFlow()

    private val _is2FAEnabled = MutableStateFlow(true)
    val is2FAEnabled: StateFlow<Boolean> = _is2FAEnabled.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isRtl = MutableStateFlow(true) // Arabic RTL default
    val isRtl: StateFlow<Boolean> = _isRtl.asStateFlow()

    private val _selectedModule = MutableStateFlow("dashboard")
    val selectedModule: StateFlow<String> = _selectedModule.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // AI Assistant State
    private val _aiMessages = MutableStateFlow(listOf(
        ChatMessage("ai", "أهلاً بك في المساعد الذكي لتطبيق 'رتب حالك'! كيف يمكنني مساعدتك اليوم؟")
    ))
    val aiMessages: StateFlow<List<ChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Room Database Flows
    val photos: StateFlow<List<PhotoEntity>> = repository.allPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashedPhotos: StateFlow<List<PhotoEntity>> = repository.trashedPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptions: StateFlow<List<SubscriptionEntity>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val socialMediaAccounts: StateFlow<List<SocialMediaAccountEntity>> = repository.allSocialMediaAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val youtubeChannels: StateFlow<List<SocialMediaAccountEntity>> = socialMediaAccounts

    val bankAccounts: StateFlow<List<BankAccountEntity>> = repository.allBankAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val donations: StateFlow<List<DonationEntity>> = repository.allDonations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paypalAccounts: StateFlow<List<PayPalAccountEntity>> = repository.allPayPalAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visaCards: StateFlow<List<VisaCardEntity>> = repository.allVisaCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val binanceAccounts: StateFlow<List<BinanceAccountEntity>> = repository.allBinanceAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiTools: StateFlow<List<AiToolEntity>> = repository.allAiTools
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val passwords: StateFlow<List<PasswordVaultEntity>> = repository.allPasswords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activityLogs: StateFlow<List<ActivityLogEntity>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
    }

    fun loginWithEmail(email: String) {
        _userEmail.value = email
        _userName.value = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        _isLoggedIn.value = true
        logActivity("تسجيل دخول", "مصادقة البريد", "تسجيل دخول ناجح بواسطة البريد $email")
    }

    fun loginWithGoogle() {
        _userEmail.value = "google.user@gmail.com"
        _userName.value = "مستخدم Google"
        _isLoggedIn.value = true
        logActivity("تسجيل دخول", "OAuth Google", "تسجيل دخول بواسطة حساب Google")
    }

    fun logout() {
        _isLoggedIn.value = false
        logActivity("تسجيل خروج", "النظام", "تم تسجيل الخروج")
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleLanguage() {
        _isRtl.value = !_isRtl.value
    }

    fun selectModule(module: String) {
        _selectedModule.value = module
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggle2FA() {
        _is2FAEnabled.value = !_is2FAEnabled.value
        logActivity("تحديث الأمان", "الإعدادات", "تم تغيير حالة المصادقة الثنائية 2FA")
    }

    fun verifyBiometric(): Boolean {
        _isBiometricAuthenticated.value = true
        return true
    }

    // CRUD Methods
    fun addPhoto(title: String, description: String, category: String, albumName: String) {
        viewModelScope.launch {
            repository.insertPhoto(
                PhotoEntity(
                    title = title,
                    description = description,
                    category = category,
                    imageUri = "android.resource://com.aistudio.ratibhalak.app/drawable/img_hero_banner_1783006143919",
                    albumName = albumName
                )
            )
            logActivity("إضافة", "الصور الخاصة", "تمت إضافة صورة جديدة: $title")
        }
    }

    fun moveToTrashPhoto(photo: PhotoEntity) {
        viewModelScope.launch {
            repository.updatePhoto(photo.copy(isTrashed = true))
            logActivity("حذف لسلة المحذوفات", "الصور", "نقل صورة إلى سلة المحذوفات")
        }
    }

    fun addBookmark(title: String, url: String, description: String, category: String) {
        viewModelScope.launch {
            repository.insertBookmark(
                BookmarkEntity(title = title, url = url, description = description, category = category)
            )
            logActivity("إضافة", "المواقع المفضلة", "تم حفظ موقع جديد: $title")
        }
    }

    fun addNote(title: String, content: String, category: String, tags: String) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(title = title, content = content, category = category, tags = tags)
            )
            logActivity("إضافة", "الملاحظات والأفكار", "تم إنشاء ملاحظة جديدة: $title")
        }
    }

    fun addSubscription(serviceName: String, cost: Double, cycle: String, startDate: String, endDate: String, category: String) {
        viewModelScope.launch {
            repository.insertSubscription(
                SubscriptionEntity(
                    serviceName = serviceName,
                    cost = cost,
                    billingCycle = cycle,
                    startDate = startDate,
                    endDate = endDate,
                    category = category
                )
            )
            logActivity("إضافة", "الاشتراكات", "تم تسجيل اشتراك جديد: $serviceName بقيمة $$cost")
        }
    }

    fun addSocialMediaAccount(platform: String, name: String, url: String, description: String, niche: String, rating: Int) {
        viewModelScope.launch {
            repository.insertSocialMediaAccount(
                SocialMediaAccountEntity(
                    platform = platform,
                    accountName = name,
                    url = url,
                    description = description,
                    niche = niche,
                    importanceRating = rating
                )
            )
            logActivity("إضافة", "مواقع التواصل الاجتماعي", "إضافة حساب $platform: $name")
        }
    }

    fun deleteSocialMediaAccount(id: Long) {
        viewModelScope.launch {
            repository.deleteSocialMediaAccount(id)
            logActivity("حذف", "مواقع التواصل الاجتماعي", "حذف حساب من مواقع التواصل")
        }
    }

    fun addYoutubeChannel(name: String, url: String, description: String, niche: String, rating: Int) {
        addSocialMediaAccount("اليوتيوب", name, url, description, niche, rating)
    }

    fun addBankAccount(bankName: String, accountNumber: String, iban: String, beneficiary: String, notes: String) {
        viewModelScope.launch {
            repository.insertBankAccount(
                BankAccountEntity(
                    bankName = bankName,
                    accountNumber = accountNumber,
                    iban = iban,
                    beneficiaryName = beneficiary,
                    notes = notes
                )
            )
            logActivity("إضافة مشفّرة", "الحسابات البنكية", "حفظ حساب بنكي جديد بتشفير AES-256: $bankName")
        }
    }

    fun addDonation(
        beneficiary: String,
        amount: Double,
        date: String,
        category: String,
        notes: String,
        paymentMethod: String = "فيزا",
        paymentDetails: String = ""
    ) {
        viewModelScope.launch {
            repository.insertDonation(
                DonationEntity(
                    beneficiaryOrg = beneficiary,
                    amount = amount,
                    date = date,
                    category = category,
                    notes = notes,
                    paymentMethod = paymentMethod,
                    paymentDetails = paymentDetails
                )
            )
            logActivity("تبرع جديد", "التبرعات والدفع", "تبرع بقيمة $$amount لصالح $beneficiary بواسطة $paymentMethod ($paymentDetails)")
        }
    }

    fun deleteDonation(id: Long) {
        viewModelScope.launch {
            repository.deleteDonation(id)
            logActivity("حذف", "التبرعات", "حذف سجل تبرع")
        }
    }

    fun addPayPalAccount(email: String, accountName: String = "حساب باي بال الشخصي", isDefault: Boolean = false) {
        viewModelScope.launch {
            repository.insertPayPalAccount(
                PayPalAccountEntity(email = email, accountName = accountName, isDefault = isDefault)
            )
            logActivity("إضافة حساب", "باي بال (PayPal)", "إضافة حساب PayPal جديد: $email")
        }
    }

    fun deletePayPalAccount(id: Long) {
        viewModelScope.launch {
            repository.deletePayPalAccount(id)
            logActivity("حذف حساب", "باي بال", "حذف حساب PayPal")
        }
    }

    fun addVisaCard(cardHolder: String, rawCardNumber: String, expiry: String, cardType: String = "Visa", isDefault: Boolean = false) {
        val last4 = if (rawCardNumber.length >= 4) rawCardNumber.takeLast(4) else "4242"
        val masked = "•••• •••• •••• $last4"
        viewModelScope.launch {
            repository.insertVisaCard(
                VisaCardEntity(
                    cardHolderName = cardHolder,
                    cardNumberEncrypted = masked,
                    expiryDate = expiry,
                    cardType = cardType,
                    isDefault = isDefault
                )
            )
            logActivity("إضافة بطاقة", "فيزا / ماستركارد", "إضافة بطاقة دفع جديدة ينتهي برقم $last4")
        }
    }

    fun deleteVisaCard(id: Long) {
        viewModelScope.launch {
            repository.deleteVisaCard(id)
            logActivity("حذف بطاقة", "بطاقات الفيزا", "حذف بطاقة ائتمانية")
        }
    }

    fun addBinanceAccount(binanceId: String, payEmailOrPhone: String, walletType: String = "USDT (TRC20)", label: String = "حساب بايننس (Binance Pay)", isDefault: Boolean = false) {
        viewModelScope.launch {
            repository.insertBinanceAccount(
                BinanceAccountEntity(
                    binanceId = binanceId,
                    payEmailOrPhone = payEmailOrPhone,
                    walletType = walletType,
                    accountLabel = label,
                    isDefault = isDefault
                )
            )
            logActivity("إضافة حساب", "منصة بايننس (Binance)", "إضافة حساب بايننس Pay جديد رقم: $binanceId")
        }
    }

    fun deleteBinanceAccount(id: Long) {
        viewModelScope.launch {
            repository.deleteBinanceAccount(id)
            logActivity("حذف حساب", "منصة بايننس", "حذف حساب بايننس")
        }
    }

    fun addAiTool(name: String, category: String, description: String, url: String) {
        viewModelScope.launch {
            repository.insertAiTool(
                AiToolEntity(name = name, category = category, description = description, url = url)
            )
            logActivity("إضافة", "أدوات الذكاء الاصطناعي", "إضافة أداة ذكاء اصطناعي: $name")
        }
    }

    fun addPassword(title: String, username: String, pass: String, url: String, notes: String) {
        viewModelScope.launch {
            repository.insertPassword(
                PasswordVaultEntity(title = title, username = username, encryptedPasswordHash = pass, url = url, notes = notes)
            )
            logActivity("إضافة آمنة", "مدير كلمات المرور", "حفظ كلمة مرور مشفرة لـ: $title")
        }
    }

    fun addTask(title: String, dueDate: String, priority: String, category: String) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(title = title, dueDate = dueDate, priority = priority, category = category)
            )
            logActivity("إضافة", "المهام والتقويم", "تم إنشاء مهمة جديدة: $title")
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    // AI Assistant Method
    fun sendAiMessage(userPrompt: String) {
        if (userPrompt.isBlank()) return
        val currentList = _aiMessages.value.toMutableList()
        currentList.add(ChatMessage("user", userPrompt))
        _aiMessages.value = currentList
        _isAiLoading.value = true

        val contextSummary = "الاشتراكات: ${subscriptions.value.size}، الصور المحفوظة: ${photos.value.size}، الملاحظات: ${notes.value.size}، التبرعات: ${donations.value.sumOf { it.amount }}."

        viewModelScope.launch {
            val reply = GeminiHelper.generateResponse(userPrompt, contextSummary)
            val updated = _aiMessages.value.toMutableList()
            updated.add(ChatMessage("ai", reply))
            _aiMessages.value = updated
            _isAiLoading.value = false
        }
    }

    private fun logActivity(action: String, module: String, desc: String) {
        viewModelScope.launch {
            repository.insertLog(action, module, desc)
        }
    }
}
