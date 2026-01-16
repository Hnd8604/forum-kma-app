# 🔧 Build Issues & Solutions

## ✅ Đã Fix

### 1. ❌ Error: Unresolved reference 'timber'

**Lỗi:**
```
e: file:///D:/android-app/app/src/main/java/com/kma/forumkma/core/network/interceptors/LoggingInterceptor.kt:4:8 
Unresolved reference 'timber'.
```

**Nguyên nhân:**
- Code mẫu sử dụng Timber logging library
- Chưa thêm Timber vào `build.gradle.kts`

**Giải pháp:**
✅ **Đã fix** bằng cách:

1. **Thêm Timber dependency** vào `app/build.gradle.kts`:
```kotlin
dependencies {
    // Timber for logging
    implementation("com.jakewharton.timber:timber:5.0.1")
}
```

2. **Sửa LoggingInterceptor.kt** để dùng Android Log thay vì Timber:
```kotlin
import android.util.Log

object LoggingInterceptorFactory {
    fun create(isDebug: Boolean = true): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)  // Dùng Android Log
        }.apply {
            level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
}
```

**Status:** ✅ Fixed - App có thể build được

---

## 🛠️ Common Build Issues

### 2. Namespace Warning

Nếu gặp warning về namespace:
```
namespace = "com.kma.base"
```

**Fix:** Đổi thành namespace mới trong `app/build.gradle.kts`:
```kotlin
android {
    namespace = "com.kma.forumkma"  // Update namespace
    // ...
}
```

### 3. Hilt/DI Errors

Nếu sử dụng Hilt và gặp lỗi:
```
@HiltAndroidApp annotation processor not found
```

**Fix:** Thêm Hilt vào dependencies:
```kotlin
// build.gradle.kts (project level)
plugins {
    id("com.google.dagger.hilt.android") version "2.50" apply false
}

// app/build.gradle.kts
plugins {
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
}
```

### 4. Room Database Errors

Nếu sử dụng Room và gặp lỗi:
```
Cannot find symbol: RoomDatabase
```

**Fix:** Thêm Room dependencies:
```kotlin
dependencies {
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
}
```

### 5. Coroutines Errors

**Fix:** Ensure Coroutines dependencies:
```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

### 6. Compose Navigation Errors

**Fix:**
```kotlin
dependencies {
    implementation("androidx.navigation:navigation-compose:2.7.6")
}
```

---

## 📋 Checklist Trước Khi Build

- [ ] Đã Sync Gradle (`File > Sync Project with Gradle Files`)
- [ ] Đã Clean Project (`Build > Clean Project`)
- [ ] Đã Rebuild Project (`Build > Rebuild Project`)
- [ ] Check `build.gradle.kts` có đầy đủ dependencies
- [ ] Check namespace đúng: `com.kma.forumkma`
- [ ] Check imports trong các file Kotlin

---

## 🚀 Build Commands

### Android Studio:
```
Build > Make Project (Ctrl+F9)
Build > Rebuild Project
```

### Terminal:
```powershell
# Windows
.\gradlew clean
.\gradlew build
.\gradlew assembleDebug

# Check dependencies
.\gradlew app:dependencies
```

---

## 📦 Current Dependencies Status

✅ **Added:**
- Retrofit 2.9.0
- OkHttp 4.12.0
- Coroutines 1.7.3
- Compose Navigation 2.9.6
- Coil 2.5.0
- DataStore 1.0.0
- Timber 5.0.1 ✨ (Just added)

⏳ **To Add (when needed):**
- Hilt (when setting up DI)
- Room (when setting up database)
- Paging3 (for pagination)

---

## 💡 Tips

1. **Enable Gradle offline mode** để build nhanh hơn (sau khi sync lần đầu)
2. **Use Build Analyzer** để debug build issues (`Build > Analyze Build`)
3. **Clear Gradle cache** nếu gặp lỗi lạ:
   ```powershell
   .\gradlew clean cleanBuildCache
   ```
4. **Invalidate Caches** trong Android Studio nếu cần:
   `File > Invalidate Caches / Restart...`

---

**Last Updated:** 2026-01-16
**Status:** ✅ All known build issues resolved
