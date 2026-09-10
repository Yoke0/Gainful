# Repackage classes into the default package to reduce the size of descriptors.
-repackageclasses

# --- Ktor ---
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# --- kotlinx.serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all @Serializable classes and their $serializer fields
-keep,includedescriptorclasses class com.yoke.gainful.**$$serializer { *; }
-keepclassmembers class com.yoke.gainful.** {
    *** Companion;
}
-keepclasseswithmembers class com.yoke.gainful.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Keep API DTOs explicitly ---
-keep class com.yoke.gainful.api.** { *; }

# --- Keep BuildConfig ---
-keep class com.yoke.gainful.common.BuildConfig { *; }

# --- OkHttp (Ktor engine on Android) ---
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# --- Koin DI ---
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# --- Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}