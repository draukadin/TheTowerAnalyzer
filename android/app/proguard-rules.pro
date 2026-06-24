# AndroidX and Kotlin libraries bundle their own rules via consumerProguardFiles,
# so no extra keep rules are needed for them here.

# Retain line numbers in stack traces for crash reports.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile