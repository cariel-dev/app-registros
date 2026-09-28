# Proguard rules for App Registros
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.jetbrains.annotations.NotNull *;
}
# Supabase & Ktor
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }
