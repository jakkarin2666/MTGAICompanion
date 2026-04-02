# MTG AI Companion 📱

Magic: The Gathering deck builder พร้อม AI ช่วยจัดดีค, สแกนการ์ด, เช็คราคา และขายการ์ดใน marketplace

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK** 17+
- **Android SDK** 34+
- **Firebase Project** (for Auth, Firestore, Storage)

### Setup Steps

#### 1. Create Firebase Project

1. ไปที่ [Firebase Console](https://console.firebase.google.com/)
2. สร้าง Project ใหม่
3. เพิ่ม Android App โดยใช้ package name: `com.mtgai.companion`
4. Download `google-services.json` และวางในโฟลเดอร์ `app/`
5. เปิดใช้งาน:
   - **Authentication** (Email/Password)
   - **Firestore Database**
   - **Storage**

#### 2. Open in Android Studio

1. Open Android Studio
2. File → Open → เลือกโฟลเดอร์ `MTGAICompanion`
3. Wait for Gradle sync to complete
4. Run on emulator หรือ device

#### 3. (Optional) Get Gemini API Key

ถ้าต้องการใช้ AI features เพิ่มเติม:
1. ไปที่ [Google AI Studio](https://makersuite.google.com/app/apikey)
2. สร้าง API key
3. เพิ่มใน `local.properties`:
   ```
   GEMINI_API_KEY=your_api_key_here
   ```

## 📁 Project Structure

```
MTGAICompanion/
├── app/
│   ├── src/main/
│   │   ├── java/com/mtgai/companion/
│   │   │   ├── data/
│   │   │   │   ├── api/          # Scryfall API
│   │   │   │   ├── model/        # Data classes
│   │   │   │   └── repository/   # Data access
│   │   │   └── ui/
│   │   │       ├── screens/      # UI screens
│   │   │       ├── theme/        # Material theme
│   │   │       └── MTGApp.kt     # Main navigation
│   │   └── res/
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

## 🎯 Features

### ✅ Phase 1 - Foundation
- [x] Email/Password Authentication
- [x] Guest mode
- [x] Card search (Scryfall API)
- [x] Deck CRUD

### 🔄 Phase 2 - AI Integration (Next)
- [ ] ML Kit camera integration
- [ ] Card scanning
- [ ] Gemini AI card identification

### 📅 Phase 3 - Marketplace
- [x] Create listing
- [x] Browse marketplace
- [x] My listings management
- [ ] In-app messaging

### 🔮 Phase 4 - Polish
- [ ] AI deck suggestions
- [ ] Price charts
- [ ] Profile settings

## 🔌 API Reference

### Scryfall API (Free)
- Card search & details
- Card images
- Price data
- Documentation: https://scryfall.com/docs/api

### Firebase
- **Auth**: Email/password authentication
- **Firestore**: Decks, listings, conversations
- **Storage**: Card images (future)

## 📝 Build & Run

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run on connected device
./gradlew installDebug
```

## ⚠️ Common Issues

**Gradle sync fails**
- ตรวจสอบว่า `local.properties` มี `sdk.dir` ถูกต้อง
- ลอง File → Invalidate Caches → Restart

**Firebase connection errors**
- ตรวจสอบ `google-services.json` ว่าถูกต้อง
- ตรวจสอบว่า package name ตรงกัน

**Camera not working**
- ตรวจสอบว่า device/emulator มี camera
- ตรวจสอบ `AndroidManifest.xml` permissions

## 📄 License

MIT License
