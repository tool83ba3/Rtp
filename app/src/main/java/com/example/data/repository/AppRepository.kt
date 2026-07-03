package com.example.data.repository

import com.example.data.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {

    // Photos
    val allPhotos: Flow<List<PhotoEntity>> = db.photoDao().getAllPhotos()
    val trashedPhotos: Flow<List<PhotoEntity>> = db.photoDao().getTrashedPhotos()
    suspend fun insertPhoto(photo: PhotoEntity) = db.photoDao().insertPhoto(photo)
    suspend fun updatePhoto(photo: PhotoEntity) = db.photoDao().updatePhoto(photo)
    suspend fun deletePhotoPermanently(id: Long) = db.photoDao().deletePhotoPermanently(id)

    // Bookmarks
    val allBookmarks: Flow<List<BookmarkEntity>> = db.bookmarkDao().getAllBookmarks()
    suspend fun insertBookmark(bookmark: BookmarkEntity) = db.bookmarkDao().insertBookmark(bookmark)
    suspend fun updateBookmark(bookmark: BookmarkEntity) = db.bookmarkDao().updateBookmark(bookmark)
    suspend fun deleteBookmark(id: Long) = db.bookmarkDao().deleteBookmark(id)

    // Notes
    val allNotes: Flow<List<NoteEntity>> = db.noteDao().getAllNotes()
    suspend fun insertNote(note: NoteEntity) = db.noteDao().insertNote(note)
    suspend fun updateNote(note: NoteEntity) = db.noteDao().updateNote(note)
    suspend fun deleteNote(id: Long) = db.noteDao().deleteNote(id)

    // Subscriptions
    val allSubscriptions: Flow<List<SubscriptionEntity>> = db.subscriptionDao().getAllSubscriptions()
    suspend fun insertSubscription(sub: SubscriptionEntity) = db.subscriptionDao().insertSubscription(sub)
    suspend fun updateSubscription(sub: SubscriptionEntity) = db.subscriptionDao().updateSubscription(sub)
    suspend fun deleteSubscription(id: Long) = db.subscriptionDao().deleteSubscription(id)

    // Social Media Accounts
    val allSocialMediaAccounts: Flow<List<SocialMediaAccountEntity>> = db.socialMediaAccountDao().getAllAccounts()
    suspend fun insertSocialMediaAccount(account: SocialMediaAccountEntity) = db.socialMediaAccountDao().insertAccount(account)
    suspend fun updateSocialMediaAccount(account: SocialMediaAccountEntity) = db.socialMediaAccountDao().updateAccount(account)
    suspend fun deleteSocialMediaAccount(id: Long) = db.socialMediaAccountDao().deleteAccount(id)

    // Bank Accounts
    val allBankAccounts: Flow<List<BankAccountEntity>> = db.bankAccountDao().getAllBankAccounts()
    suspend fun insertBankAccount(account: BankAccountEntity) = db.bankAccountDao().insertBankAccount(account)
    suspend fun updateBankAccount(account: BankAccountEntity) = db.bankAccountDao().updateBankAccount(account)
    suspend fun deleteBankAccount(id: Long) = db.bankAccountDao().deleteBankAccount(id)

    // Donations
    val allDonations: Flow<List<DonationEntity>> = db.donationDao().getAllDonations()
    suspend fun insertDonation(donation: DonationEntity) = db.donationDao().insertDonation(donation)
    suspend fun deleteDonation(id: Long) = db.donationDao().deleteDonation(id)

    // PayPal Accounts
    val allPayPalAccounts: Flow<List<PayPalAccountEntity>> = db.payPalAccountDao().getAllPayPalAccounts()
    suspend fun insertPayPalAccount(account: PayPalAccountEntity) = db.payPalAccountDao().insertPayPalAccount(account)
    suspend fun deletePayPalAccount(id: Long) = db.payPalAccountDao().deletePayPalAccount(id)

    // Visa Cards
    val allVisaCards: Flow<List<VisaCardEntity>> = db.visaCardDao().getAllVisaCards()
    suspend fun insertVisaCard(card: VisaCardEntity) = db.visaCardDao().insertVisaCard(card)
    suspend fun deleteVisaCard(id: Long) = db.visaCardDao().deleteVisaCard(id)

    // Binance Accounts
    val allBinanceAccounts: Flow<List<BinanceAccountEntity>> = db.binanceAccountDao().getAllBinanceAccounts()
    suspend fun insertBinanceAccount(account: BinanceAccountEntity) = db.binanceAccountDao().insertBinanceAccount(account)
    suspend fun deleteBinanceAccount(id: Long) = db.binanceAccountDao().deleteBinanceAccount(id)

    // AI Tools
    val allAiTools: Flow<List<AiToolEntity>> = db.aiToolDao().getAllAiTools()
    suspend fun insertAiTool(tool: AiToolEntity) = db.aiToolDao().insertAiTool(tool)
    suspend fun updateAiTool(tool: AiToolEntity) = db.aiToolDao().updateAiTool(tool)
    suspend fun deleteAiTool(id: Long) = db.aiToolDao().deleteAiTool(id)

    // Password Vault
    val allPasswords: Flow<List<PasswordVaultEntity>> = db.passwordVaultDao().getAllPasswords()
    suspend fun insertPassword(password: PasswordVaultEntity) = db.passwordVaultDao().insertPassword(password)
    suspend fun updatePassword(password: PasswordVaultEntity) = db.passwordVaultDao().updatePassword(password)
    suspend fun deletePassword(id: Long) = db.passwordVaultDao().deletePassword(id)

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = db.taskDao().getAllTasks()
    suspend fun insertTask(task: TaskEntity) = db.taskDao().insertTask(task)
    suspend fun updateTask(task: TaskEntity) = db.taskDao().updateTask(task)
    suspend fun deleteTask(id: Long) = db.taskDao().deleteTask(id)

    // Activity Logs
    val recentLogs: Flow<List<ActivityLogEntity>> = db.activityLogDao().getRecentLogs()
    suspend fun insertLog(actionType: String, moduleName: String, description: String) {
        db.activityLogDao().insertLog(
            ActivityLogEntity(actionType = actionType, moduleName = moduleName, description = description)
        )
    }
}
