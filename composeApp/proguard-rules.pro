# Keep source information in release crash reports while still allowing R8 optimization.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlin serialization discovers generated serializers through companion methods.
-keepclassmembers,allowoptimization,allowshrinking class **$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclasseswithmembers,allowoptimization,allowshrinking,includedescriptorclasses class * {
    kotlinx.serialization.KSerializer serializer(...);
}
