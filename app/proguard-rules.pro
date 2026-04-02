# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /path/to/sdk/tools/proguard/proguard-android.txt

# Keep Firebase classes
-keepattributes Signature
-keepattributes *Annotation*

# Firebase Auth
-keepattributes Signature
-keepattributes InnerClasses

# Firestore
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keepattributes Signature
-keep class com.google.gson.** { *; }
-keep class com.mtgai.companion.data.model.** { *; }

# Coil
-dontwarn coil.**

# ML Kit
-keep class com.google.mlkit.** { *; }
