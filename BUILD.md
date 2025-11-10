# Building Nexus Paths Android Game

This document provides detailed instructions for building the Nexus Paths Android game APK from source.

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 17 or higher**
   ```bash
   # Check Java version
   java -version
   ```
   - Download from: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/

2. **Android SDK**
   - Can be installed via Android Studio or standalone
   - Required SDK version: API 34 (Android 14)
   - Minimum SDK: API 24 (Android 7.0)

3. **Gradle** (included via wrapper, no separate installation needed)

### Optional (Recommended)

- **Android Studio** (for easier development and debugging)
  - Download from: https://developer.android.com/studio
  - Includes Android SDK, emulator, and build tools

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/tek911/psychic-octopus.git
cd psychic-octopus
```

### 2. Set Android SDK Location

Create a `local.properties` file in the project root:

```bash
# On Linux/Mac
echo "sdk.dir=$HOME/Android/Sdk" > local.properties

# On Windows (PowerShell)
echo "sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk" > local.properties
```

Or let Android Studio create it automatically when you open the project.

### 3. Verify Gradle Wrapper

The project includes Gradle wrapper scripts that download the correct Gradle version automatically:

```bash
# Linux/Mac
./gradlew --version

# Windows
gradlew.bat --version
```

If the wrapper is missing, you can regenerate it:
```bash
gradle wrapper --gradle-version 8.0
```

## Building the APK

### Debug Build (Development)

Debug builds are signed with a debug keystore and include debugging information.

```bash
# Linux/Mac
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

**Output location:**
```
app/build/outputs/apk/debug/app-debug.apk
```

**Features:**
- Fast compilation
- Includes debugging symbols
- Larger APK size (~5-8 MB)
- Signed with debug key
- Debuggable on device

### Release Build (Production)

Release builds are optimized and minified for distribution.

```bash
# Linux/Mac
./gradlew assembleRelease

# Windows
gradlew.bat assembleRelease
```

**Output location:**
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

**Features:**
- ProGuard/R8 optimization enabled
- Code obfuscation
- Smaller APK size (~2-4 MB)
- Requires signing for installation

### Signing Release Build (Optional)

For distribution, you need to sign the release APK:

#### Create a Keystore (first time only)

```bash
keytool -genkey -v -keystore nexus-paths.keystore \
  -alias nexus-release -keyalg RSA -keysize 2048 -validity 10000
```

#### Configure Signing in `app/build.gradle.kts`

Add signing configuration:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../nexus-paths.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "nexus-release"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... existing config
        }
    }
}
```

#### Build Signed APK

```bash
# Set environment variables
export KEYSTORE_PASSWORD="your-keystore-password"
export KEY_PASSWORD="your-key-password"

# Build signed release
./gradlew assembleRelease
```

## Installing the APK

### Via USB (ADB)

1. **Enable USB Debugging** on your Android device:
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times to enable Developer Options
   - Go to Settings > Developer Options
   - Enable "USB Debugging"

2. **Connect device** via USB

3. **Install APK:**

```bash
# Debug build
./gradlew installDebug

# Or manually with adb
adb install app/build/outputs/apk/debug/app-debug.apk

# Release build (if signed)
adb install app/build/outputs/apk/release/app-release.apk
```

### Via File Transfer

1. Build the APK using steps above
2. Copy the APK file to your device
3. Open the APK file on your device
4. Allow installation from unknown sources if prompted
5. Tap "Install"

### Using Android Studio

1. Open the project in Android Studio
2. Click the "Run" button (green triangle) or press Shift+F10
3. Select your device or create an emulator
4. The app will be built and installed automatically

## Build Variants

### Available Build Types

- **debug**: Development build with debugging enabled
- **release**: Optimized production build

### Build Commands Reference

```bash
# Clean build files
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Build both variants
./gradlew assemble

# Install debug on connected device
./gradlew installDebug

# Uninstall from device
./gradlew uninstallDebug

# Run unit tests
./gradlew test

# Generate dependency report
./gradlew dependencies
```

## Optimization Settings

The release build includes these optimizations (configured in `app/build.gradle.kts`):

```kotlin
buildTypes {
    release {
        isMinifyEnabled = true  // Enable ProGuard/R8
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

**ProGuard/R8 Benefits:**
- Removes unused code
- Obfuscates class/method names
- Optimizes bytecode
- Reduces APK size by 30-50%

## Troubleshooting

### Common Issues

#### 1. SDK Not Found

**Error:** `SDK location not found`

**Solution:**
```bash
# Create local.properties with SDK path
echo "sdk.dir=/path/to/android/sdk" > local.properties
```

#### 2. Java Version Mismatch

**Error:** `Unsupported class file major version`

**Solution:**
```bash
# Check Java version (need JDK 17+)
java -version

# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/jdk-17
```

#### 3. Gradle Daemon Issues

**Error:** `Gradle daemon disappeared unexpectedly`

**Solution:**
```bash
# Stop all Gradle daemons
./gradlew --stop

# Increase memory in gradle.properties
echo "org.gradle.jvmargs=-Xmx4096m" >> gradle.properties

# Rebuild
./gradlew clean assembleDebug
```

#### 4. Build Tools Version Not Found

**Error:** `Failed to find Build Tools revision`

**Solution:**
```bash
# Install required build tools via sdkmanager
sdkmanager "build-tools;34.0.0"

# Or update in build.gradle.kts to match your installed version
```

#### 5. Out of Memory

**Error:** `OutOfMemoryError: Java heap space`

**Solution:**
```bash
# Increase Gradle heap size
echo "org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m" >> gradle.properties
```

#### 6. Installation Failed (INSTALL_FAILED_UPDATE_INCOMPATIBLE)

**Solution:**
```bash
# Uninstall existing version first
adb uninstall com.nexuspaths.game

# Then reinstall
./gradlew installDebug
```

### Build Cache Issues

If you encounter strange build errors:

```bash
# Clean project
./gradlew clean

# Clear Gradle cache
rm -rf ~/.gradle/caches/

# Clear build directories
rm -rf app/build
rm -rf build

# Rebuild
./gradlew assembleDebug
```

## Performance Tips

### Faster Builds

1. **Enable Gradle Daemon** (usually on by default):
   ```properties
   # gradle.properties
   org.gradle.daemon=true
   ```

2. **Enable Build Cache**:
   ```properties
   # gradle.properties
   org.gradle.caching=true
   ```

3. **Increase Memory**:
   ```properties
   # gradle.properties
   org.gradle.jvmargs=-Xmx4096m
   ```

4. **Use Parallel Execution**:
   ```properties
   # gradle.properties
   org.gradle.parallel=true
   ```

### APK Size Optimization

The release build is already optimized, but you can further reduce size:

1. **Enable resource shrinking**:
   ```kotlin
   // app/build.gradle.kts
   buildTypes {
       release {
           isShrinkResources = true
           isMinifyEnabled = true
       }
   }
   ```

2. **Use APK splits** (for different architectures):
   ```kotlin
   android {
       splits {
           abi {
               isEnable = true
               reset()
               include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
               isUniversalApk = true
           }
       }
   }
   ```

## Verifying the Build

### Check APK Contents

```bash
# List contents
unzip -l app/build/outputs/apk/debug/app-debug.apk

# Or use aapt
aapt dump badging app/build/outputs/apk/debug/app-debug.apk
```

### Analyze APK Size

```bash
# Using Android Studio
./gradlew :app:analyzeDebug

# Or use bundletool
bundletool dump manifest --bundle=app/build/outputs/bundle/release/app-release.aab
```

### Verify Signing

```bash
# Check APK signature
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk
```

## Building for Different Architectures

To build for specific CPU architectures:

```kotlin
// app/build.gradle.kts
android {
    defaultConfig {
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }
}
```

## Expected Build Output

### Debug Build
- **Size**: ~5-8 MB
- **Build Time**: 30-60 seconds (first build), 10-20 seconds (incremental)
- **Installation**: Immediate (debug signed)

### Release Build
- **Size**: ~2-4 MB (with ProGuard)
- **Build Time**: 60-120 seconds (optimization enabled)
- **Installation**: Requires signing or unknown sources enabled

## Support

If you encounter issues not covered here:

1. Check the [GitHub Issues](https://github.com/tek911/psychic-octopus/issues)
2. Review Gradle build logs: `./gradlew assembleDebug --info`
3. Clean and rebuild: `./gradlew clean assembleDebug`

## Quick Reference

```bash
# Most common commands
./gradlew clean                    # Clean build artifacts
./gradlew assembleDebug           # Build debug APK
./gradlew assembleRelease         # Build release APK
./gradlew installDebug            # Install debug on device
adb logcat | grep NexusPaths      # View app logs
```

## Project-Specific Notes

- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Target SDK**: API 34 (Android 14)
- **Package Name**: `com.nexuspaths.game`
- **Permissions**: VIBRATE only
- **Dependencies**: All managed via Gradle (no manual downloads needed)
- **Assets**: 100% code-generated (no asset folder required)

Happy building! 🎮
