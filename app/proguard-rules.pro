# Keep Compose
-keep class androidx.compose.** { *; }

# Keep Room entities
-keep class com.fftool.soundboard.data.** { *; }

# Keep EncryptedSharedPreferences
-keep class androidx.security.crypto.** { *; }

# Keep ExoPlayer
-keep class androidx.media3.** { *; }

# Keep kotlinx.coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
