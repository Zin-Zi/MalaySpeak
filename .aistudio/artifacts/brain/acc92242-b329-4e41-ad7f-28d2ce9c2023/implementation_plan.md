# မလေးစကားပြော (MalaySpeak) - Android App & Live Companion

A specialized Malay language learning and conversational translation app tailored for Burmese speakers. The application bridges Myanmar (Burmese), English, and colloquial Malaysian Malay (Bahasa Melayu Pasar/Santai) with AI-powered nuance explanations, swipeable phrase decks, and offline local progress tracking.

> [!IMPORTANT]
> **Confirmed Project Foundations**:
> - **Dual Delivery**: Production-grade native Android codebase (`app/src/...`, `build.gradle.kts`, Compose, Room, Retrofit) plus a high-fidelity interactive live web preview in the AI Studio environment so you can test all features directly in-browser.
> - **Essential Starter Deck**: Curated phrase cards covering 5 everyday Malaysian contexts: Greetings, Market/Bargaining, Dining/Mamak, Transport, and Workplace.
> - **Tri-Directional Input**: Translation accepts Burmese (Zawgyi/Unicode auto-handled), English, or Malay, returning structured 3-way translations with colloquial Malaysian explanations.
> - **Key Security**: Gemini API key loaded strictly from `local.properties` via `BuildConfig` (Android) and server-side environment secrets (Live Preview) with no hardcoded keys.

---

## 1. Overview & Core Concept

### Target Audience & Persona
- **Burmese expatriates, workers, students, and travelers** living in or visiting Malaysia who need immediate, colloquial Malaysian Malay (Bahasa Melayu harian/pasar) rather than formal textbook Indonesian or rigid Standard Malay.
- **Language Pairings**:
  - **Burmese (ဗမာစာ)**: Native comprehension anchor and phonetic intuition.
  - **Malaysian Malay (Bahasa Melayu)**: Target spoken dialect with authentic particles (`lah`, `kan`, `tengok`, `kat mana`, `tapau`, `bungkus`, `kira`).
  - **English**: Secondary reference bridge.

### Core Value Proposition
- Instant contextual translation that doesn't just translate words, but explains *how real Malaysians speak* (e.g., why "How much?" translates to "Berapa ni boss?" at a night market or "Berapa harganya?" in shops).
- Interactive tactile flashcards for phrase memorization with offline mastery tracking.

---

## 2. User Experience & Visual Design

### Design System: Material 3 Android Ergonomics
- **Aesthetic Direction**: Clean Material You / Material 3 styling with adaptive tonal palettes, smooth card elevation, and intuitive thumb-zone navigation.
- **Color Palette (Light & Dark)**:
  - **Primary**: Deep Malaysian Teal / Emerald (`#0F766E` / dark: `#2DD4BF`)
  - **Secondary / Accent**: Warm Amber Gold (`#D97706` / dark: `#FBBF24`)
  - **Surface Canvas**: Clean soft slate (`#F8FAFC` / dark: `#0F172A`)
  - **Cards & Sheets**: Pure white (`#FFFFFF` / dark: `#1E293B`) with hairline borders
- **Typography & Font Pairings**:
  - Display & Headlines: `Plus Jakarta Sans` / Roboto Flex for crisp hierarchy
  - Burmese Script: High-legibility Unicode font stack (`Padauk`, `Noto Sans Myanmar`, Pyidaungsu fallback)
- **Top Bar & Navigation Contract**:
  - Sticky compact Android TopAppBar: App wordmark (`မလေးစကားပြော`), theme switch (Light/Dark), and sound/haptic toggles.
  - Material 3 Navigation Bar (Bottom):
    - `Translate` (ဘာသာပြန်) — Tri-directional translation with quick chips.
    - `Vocabulary` (ဝေါဟာရကတ်) — Gesture-based swipeable flashcards.
    - `Progress` (တိုးတက်မှု) — Local Room DB mastery metrics & review deck.

---

## 3. Technical Architecture & Component Hierarchy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├───────────────────┬───────────────────────────┬────────────────────────┤
│ TranslationScreen │   VocabularyDeckScreen    │     ProgressScreen     │
│ - Language Chips  │   - Swipeable Card Deck   │   - Mastery Stat Rings │
│ - Input Field     │   - Flip / Audio / Action │   - Category Breakdown │
│ - Result Card     │   - "Learned" Floating CTA│   - Reset / Filter     │
└─────────┬─────────┴─────────────┬─────────────┴───────────┬────────────┘
          │                       │                         │
┌─────────▼───────────────────────▼─────────────────────────▼────────────┐
│                    MVVM ViewModels (StateFlow)                         │
│   TranslationViewModel    VocabularyViewModel      ProgressViewModel   │
└─────────┬───────────────────────┬─────────────────────────┬────────────┘
          │                       │                         │
┌─────────▼───────────────┐ ┌─────▼─────────────────────────▼────────────┐
│   Remote Data Source    │ │            Local Data Source               │
│ Retrofit / Gemini API   │ │   Room Database (AppDatabase)              │
│ - Structured JSON Schema│ │   - VocabularyCardDao                      │
│ - Malaysian Malay Prompt│ │   - LearningProgressDao                    │
│ - BuildConfig API Key   │ │   - Prepopulated Essential Deck            │
└─────────────────────────┘ └────────────────────────────────────────────┘
```

### Data Models & Room Schema

#### 1. `VocabularyCard` Entity
- `id: Long` (Primary Key, autoincrement)
- `category: String` (e.g., "GREETINGS", "MARKET", "DINING", "TRANSPORT", "WORK")
- `malayPhrase: String` (e.g., "Berapa harga ini?", "Boleh kurang sikit tak?")
- `pronunciationGuide: String` (Phonetic guide in Burmese script)
- `englishMeaning: String` (e.g., "Can you give a discount?")
- `myanmarMeaning: String` (e.g., "ဈေးနည်းနည်းလျှော့ပေးလို့ရမလား။")
- `culturalContext: String` (e.g., "Night market bargain phrase with polite 'tak'")
- `isLearned: Boolean` (default: false)
- `learnedAt: Long?` (timestamp)

#### 2. `LearningProgress` Entity
- `id: Int` (Single row tracker, id = 1)
- `totalCardsLearned: Int`
- `lastStudiedTimestamp: Long`
- `dailyStreak: Int`

---

## 4. Gemini API Integration & Structured Output

### Prompt Strategy
The system instruction enforces authentic Malaysian colloquial Malay (Bahasa Melayu Pasar / Santai) rather than Indonesian or textbook standard Malay:

```json
{
  "detectedSourceLanguage": "MYANMAR | ENGLISH | MALAY",
  "myanmarText": "မင်္ဂလာပါ၊ နေကောင်းလား။",
  "englishText": "Hello, how are you?",
  "malayCasual": "Hai, apa khabar? Sihat ke?",
  "malayPronunciation": "ဟိုင်း၊ အာပါ ခါဘာရ်? ဆီဟတ် ကယ်?",
  "culturalExplanation": "In daily Malaysian conversation, 'Sihat ke?' (Are you healthy/well?) is commonly added. 'Khabar' is pronounced softly.",
  "alternativePhrases": [
    { "phrase": "Dah makan belum?", "context": "Very common friendly Malaysian greeting asking if you've eaten" }
  ]
}
```

---

## 5. Phased Deliverable Roadmap (User-Requested Order)

1. **Scaffolding & Project Setup**:
   - Android Gradle structure (`build.gradle.kts`, `settings.gradle.kts`, `AndroidManifest.xml`).
   - Web application setup with Express backend proxy (`/api/translate`) and Gemini API server client.
   - Material 3 theme, navigation scaffolding, and local state management.
2. **Room DB Schema & Local Seed Data**:
   - Entities, DAOs, Database class, and Room prepopulation callback with essential Malaysian phrase decks.
3. **Gemini API Service**:
   - Retrofit API definition, OkHttp interceptor for `BuildConfig.GEMINI_API_KEY`, structured JSON schema parser, and error handling.
4. **Translation Screen UI & ViewModel**:
   - Trilingual inputs, quick suggested query chips, response cards with Burmese phonetics, and nuance explanation banner.
5. **Vocabulary Cards Screen UI & ViewModel**:
   - Interactive swipe gestures (left/right swipe animations), flip card view, and "Mark as Learned" action.
6. **Progress Tracking Screen UI & ViewModel**:
   - Category completion percentage rings, daily stats, and list of mastered phrases.
7. **Local Storage & Offline Synchronization**:
   - Room repository flow collectors, reactive updates between vocabulary swipes and progress bars.
8. **Unit Tests & Verification**:
   - Response parser tests for Gemini JSON output and Room DAO test scenarios.
