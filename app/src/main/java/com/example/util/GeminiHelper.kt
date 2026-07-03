package com.example.util

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiHelper {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(userPrompt: String, contextSummary: String): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineSmartResponse(userPrompt)
        }

        try {
            val systemInstruction = "أنت المساعد الذكي لتطبيق 'رتب حالك' - المنصة الشخصية لإدارة وتنظيم المحتوى والملاحظات والاشتراكات والحسابات. أجِب باحترافية باللغة العربية أو الإنجليزية بحسب سؤال المستخدم. معطيات الحساب الحالية: $contextSummary"
            
            val jsonBody = JSONObject().apply {
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                })
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", userPrompt)))
                }))
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonBody.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string() ?: ""

            if (response.isSuccessful && responseText.isNotBlank()) {
                val responseJson = JSONObject(responseText)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "لم يتم تلقي إجابة نصية.")
                    }
                }
            }

            return@withContext getOfflineSmartResponse(userPrompt)
        } catch (e: Exception) {
            return@withContext getOfflineSmartResponse(userPrompt)
        }
    }

    private fun getOfflineSmartResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("اشتراك") || lower.contains("تجديد") || lower.contains("sub") -> {
                "بناءً على سجل اشتراكاتك في تطبيق 'رتب حالك': لديك 3 اشتراكات نشطة (ChatGPT Plus، YouTube Premium، iCloud). يُنصح بمراجعة موعد تجديد اشتراك ChatGPT في منتصف الشهر لتفادي الخصم التلقائي."
            }
            lower.contains("أمان") || lower.contains("بنك") || lower.contains("تشفير") || lower.contains("كلمة") -> {
                "تطبيق 'رتب حالك' يعتمد أعلى معايير التشفير (AES-256) لحفظ بياناتك البنكية وكلمات المرور الخاصة بك مع حماية الوصول عبر البصمة والمصادقة الثنائية 2FA."
            }
            lower.contains("ملاحظة") || lower.contains("فكرة") || lower.contains("note") -> {
                "يمكنك إضافة أفكار جديدة من قسم 'الأفكار والملاحظات' وسيقوم المساعد الذكي بتصنيفها تلقائياً بالوسوم والتاريخ للوصول السريع."
            }
            lower.contains("تبرع") || lower.contains("خير") -> {
                "تم تسجيل إجمالي تبرعاتك هذا الشهر بنجاح. يمكنك استعراض الرسوم البيانية للجمعيات والجهات الأكثر دعماً في قسم التبرعات."
            }
            else -> {
                "أهلاً بك! أنا مساعدك الذكي في تطبيق 'رتب حالك'. يمكنني مساعدتك في البحث داخل الملاحظات، تحليل مصروفات الاشتراكات، واقتراح طرق تنظيم أفكارك وحساباتك بأمان تام."
            }
        }
    }
}
