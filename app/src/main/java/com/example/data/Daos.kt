package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE isTrashed = 0 ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE isTrashed = 1 ORDER BY timestamp DESC")
    fun getTrashedPhotos(): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity): Long

    @Update
    suspend fun updatePhoto(photo: PhotoEntity)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun deletePhotoPermanently(id: Long)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Update
    suspend fun updateBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: Long)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY timestamp DESC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity): Long

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteSubscription(id: Long)
}

@Dao
interface SocialMediaAccountDao {
    @Query("SELECT * FROM social_media_accounts ORDER BY importanceRating DESC, timestamp DESC")
    fun getAllAccounts(): Flow<List<SocialMediaAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: SocialMediaAccountEntity): Long

    @Update
    suspend fun updateAccount(account: SocialMediaAccountEntity)

    @Query("DELETE FROM social_media_accounts WHERE id = :id")
    suspend fun deleteAccount(id: Long)
}

@Dao
interface BankAccountDao {
    @Query("SELECT * FROM bank_accounts ORDER BY timestamp DESC")
    fun getAllBankAccounts(): Flow<List<BankAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccountEntity): Long

    @Update
    suspend fun updateBankAccount(account: BankAccountEntity)

    @Query("DELETE FROM bank_accounts WHERE id = :id")
    suspend fun deleteBankAccount(id: Long)
}

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY timestamp DESC")
    fun getAllDonations(): Flow<List<DonationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntity): Long

    @Query("DELETE FROM donations WHERE id = :id")
    suspend fun deleteDonation(id: Long)
}

@Dao
interface PayPalAccountDao {
    @Query("SELECT * FROM paypal_accounts ORDER BY isDefault DESC, timestamp DESC")
    fun getAllPayPalAccounts(): Flow<List<PayPalAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayPalAccount(account: PayPalAccountEntity): Long

    @Query("DELETE FROM paypal_accounts WHERE id = :id")
    suspend fun deletePayPalAccount(id: Long)
}

@Dao
interface VisaCardDao {
    @Query("SELECT * FROM visa_cards ORDER BY isDefault DESC, timestamp DESC")
    fun getAllVisaCards(): Flow<List<VisaCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisaCard(card: VisaCardEntity): Long

    @Query("DELETE FROM visa_cards WHERE id = :id")
    suspend fun deleteVisaCard(id: Long)
}

@Dao
interface BinanceAccountDao {
    @Query("SELECT * FROM binance_accounts ORDER BY isDefault DESC, timestamp DESC")
    fun getAllBinanceAccounts(): Flow<List<BinanceAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBinanceAccount(account: BinanceAccountEntity): Long

    @Query("DELETE FROM binance_accounts WHERE id = :id")
    suspend fun deleteBinanceAccount(id: Long)
}

@Dao
interface AiToolDao {
    @Query("SELECT * FROM ai_tools ORDER BY isFavorite DESC, category ASC")
    fun getAllAiTools(): Flow<List<AiToolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiTool(tool: AiToolEntity): Long

    @Query("SELECT COUNT(*) FROM ai_tools")
    suspend fun getCount(): Int

    @Update
    suspend fun updateAiTool(tool: AiToolEntity)

    @Query("DELETE FROM ai_tools WHERE id = :id")
    suspend fun deleteAiTool(id: Long)
}

@Dao
interface PasswordVaultDao {
    @Query("SELECT * FROM passwords ORDER BY timestamp DESC")
    fun getAllPasswords(): Flow<List<PasswordVaultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordVaultEntity): Long

    @Update
    suspend fun updatePassword(password: PasswordVaultEntity)

    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deletePassword(id: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, timestamp DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: Long)
}

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLogEntity)
}
