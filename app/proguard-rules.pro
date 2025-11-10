# Keep game classes
-keep class com.nexuspaths.game.** { *; }
-keepclassmembers class com.nexuspaths.game.** { *; }

# Optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
