package com.example.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FirestoreManager {
    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Firebase is not initialized or Google Services configuration is missing. Using offline cache.", e)
            null
        }
    }

    /**
     * Fetch subjects of a specific class from Firestore.
     * Automatically seeding Firestore if empty, and falls back to clean, offline-ready local data if Firestore is unavailable.
     */
    suspend fun getSubjectsForClass(classLevel: Int): List<SubjectEntity> {
        val fs = firestore
        if (fs == null) {
            Log.d("FirestoreManager", "Firestore is not available. Yielding cached local offline subjects.")
            return getLocalFallbackSubjects(classLevel)
        }

        return try {
            val querySnapshot = suspendCancellableCoroutine { continuation ->
                fs.collection("subjects")
                    .whereEqualTo("classLevel", classLevel)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        continuation.resume(snapshot)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            if (querySnapshot.isEmpty) {
                Log.d("FirestoreManager", "Firestore querySnapshot is empty. Auto-seeding default subjects to cloud.")
                seedDefaultSubjectsToFirestore(classLevel)
                getLocalFallbackSubjects(classLevel)
            } else {
                querySnapshot.documents.map { doc ->
                    SubjectEntity(
                        id = doc.getString("id") ?: doc.id,
                        category = doc.getString("category") ?: "Core Course",
                        title = doc.getString("title") ?: "Subject",
                        progress = doc.getLong("progress")?.toInt() ?: 0,
                        isOfflineReady = doc.getBoolean("isOfflineReady") ?: true,
                        imageUrl = doc.getString("imageUrl") ?: "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?auto=format&fit=crop&q=80&w=400",
                        iconName = doc.getString("iconName") ?: "book"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Firestore fetch error for Class $classLevel. Using offline local database fallback.", e)
            getLocalFallbackSubjects(classLevel)
        }
    }

    private fun seedDefaultSubjectsToFirestore(classLevel: Int) {
        val fs = firestore ?: return
        val defaultSubjects = getLocalFallbackSubjects(classLevel)
        for (subj in defaultSubjects) {
            val documentId = "class_${classLevel}_${subj.id}"
            val data = hashMapOf(
                "id" to subj.id,
                "classLevel" to classLevel,
                "title" to subj.title,
                "category" to subj.category,
                "progress" to subj.progress,
                "isOfflineReady" to subj.isOfflineReady,
                "imageUrl" to subj.imageUrl,
                "iconName" to subj.iconName
            )
            fs.collection("subjects").document(documentId).set(data)
                .addOnSuccessListener {
                    Log.d("FirestoreManager", "Successfully seeded cloud document: $documentId")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreManager", "Cloud seeding failed for document: $documentId", e)
                }
        }
    }

    /**
     * Fetch chapters of a specific subject/course from Firestore.
     * Automatically seeds Firestore if empty, and falls back to offline local templates.
     */
    suspend fun getChaptersForSubject(subjectId: String): List<LessonChapterEntity> {
        val fs = firestore
        if (fs == null) {
            Log.d("FirestoreManager", "Firestore is not available. Yielding cached local offline chapters.")
            return getLocalFallbackChapters(subjectId)
        }

        return try {
            val querySnapshot = suspendCancellableCoroutine { continuation ->
                fs.collection("chapters")
                    .whereEqualTo("subjectId", subjectId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        continuation.resume(snapshot)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            if (querySnapshot.isEmpty) {
                Log.d("FirestoreManager", "Firestore chapters are empty. Auto-seeding default chapters.")
                seedDefaultChaptersToFirestore(subjectId)
                getLocalFallbackChapters(subjectId)
            } else {
                querySnapshot.documents.map { doc ->
                    LessonChapterEntity(
                        id = 0, // database auto-generates
                        subjectId = doc.getString("subjectId") ?: subjectId,
                        chapterNumber = doc.getString("chapterNumber") ?: "01",
                        chapterTitle = doc.getString("chapterTitle") ?: "Chapter",
                        status = doc.getString("status") ?: "Locked",
                        lessonsCount = doc.getLong("lessonsCount")?.toInt() ?: 1
                    )
                }.sortedBy { it.chapterNumber }
            }
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Firestore chapter fetch error for $subjectId. Fallback to offline databases.", e)
            getLocalFallbackChapters(subjectId)
        }
    }

    private fun seedDefaultChaptersToFirestore(subjectId: String) {
        val fs = firestore ?: return
        val chapters = getLocalFallbackChapters(subjectId)
        for (ch in chapters) {
            val documentId = "chapter_${subjectId}_${ch.chapterNumber}"
            val data = hashMapOf(
                "subjectId" to ch.subjectId,
                "chapterNumber" to ch.chapterNumber,
                "chapterTitle" to ch.chapterTitle,
                "status" to ch.status,
                "lessonsCount" to ch.lessonsCount
            )
            fs.collection("chapters").document(documentId).set(data)
                .addOnSuccessListener { Log.d("FirestoreManager", "Seeded chapter: $documentId") }
                .addOnFailureListener { e -> Log.e("FirestoreManager", "Failed seeding chapter: $documentId", e) }
        }
    }

    /**
     * Fetch study notes for a specific Chapter of a Subject from Firestore.
     * Integrates transparent local fallback caching if cloud services are unreachable.
     */
    suspend fun getNoteForChapter(subjectId: String, chapterNumber: String, defaultTitle: String): NoteEntity {
        val fs = firestore
        val noteId = "${subjectId}_$chapterNumber"
        
        if (fs == null) {
            Log.d("FirestoreManager", "Firestore offline. Delivering rich pre-built educational notes for $noteId.")
            return getLocalFallbackNote(subjectId, chapterNumber, defaultTitle)
        }

        return try {
            val docSnapshot = suspendCancellableCoroutine { continuation ->
                fs.collection("notes")
                    .document(noteId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        continuation.resume(snapshot)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            if (!docSnapshot.exists()) {
                Log.d("FirestoreManager", "Firestore note NOT found for $noteId. Generating & Seeding cloud copy.")
                val localNote = getLocalFallbackNote(subjectId, chapterNumber, defaultTitle)
                val data = hashMapOf(
                    "id" to localNote.id,
                    "subjectId" to localNote.subjectId,
                    "chapterNumber" to localNote.chapterNumber,
                    "title" to localNote.title,
                    "content" to localNote.content,
                    "keywords" to localNote.keywords,
                    "totalReadingTimeMinutes" to localNote.totalReadingTimeMinutes,
                    "isBookmarked" to localNote.isBookmarked,
                    "lastSyncedAt" to localNote.lastSyncedAt
                )
                fs.collection("notes").document(noteId).set(data)
                localNote
            } else {
                NoteEntity(
                    id = docSnapshot.getString("id") ?: noteId,
                    subjectId = docSnapshot.getString("subjectId") ?: subjectId,
                    chapterNumber = docSnapshot.getString("chapterNumber") ?: chapterNumber,
                    title = docSnapshot.getString("title") ?: defaultTitle,
                    content = docSnapshot.getString("content") ?: "Notes content loading...",
                    keywords = docSnapshot.getString("keywords") ?: "Study, Key Concepts",
                    totalReadingTimeMinutes = docSnapshot.getLong("totalReadingTimeMinutes")?.toInt() ?: 5,
                    isBookmarked = docSnapshot.getBoolean("isBookmarked") ?: false,
                    lastSyncedAt = docSnapshot.getLong("lastSyncedAt") ?: System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Firestore note loading failed for $noteId. Fetching offline archive.", e)
            getLocalFallbackNote(subjectId, chapterNumber, defaultTitle)
        }
    }


    // ==========================================
    // BACKUP LOCAL FALLBACK ARCHIVES (OFFLINE STORAGE GENERATION)
    // ==========================================

    fun getLocalFallbackSubjects(classLevel: Int): List<SubjectEntity> {
        val imageLinksBySubject = mapOf(
            "english" to "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?auto=format&fit=crop&q=80&w=400",
            "urdu" to "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&q=80&w=400",
            "islamiyat" to "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?auto=format&fit=crop&q=80&w=400",
            "general_knowledge" to "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&q=80&w=400",
            "mathematics" to "https://images.unsplash.com/photo-1509228468518-180dd4864904?auto=format&fit=crop&q=80&w=400"
        )

        return listOf(
            SubjectEntity(
                id = "english",
                category = "Language Arts",
                title = "English",
                progress = if (classLevel == 4) 65 else 45 + (classLevel * 7) % 35,
                isOfflineReady = true,
                imageUrl = imageLinksBySubject["english"]!!,
                iconName = "translate"
            ),
            SubjectEntity(
                id = "urdu",
                category = "National Language",
                title = "Urdu",
                progress = if (classLevel == 4) 40 else 30 + (classLevel * 9) % 45,
                isOfflineReady = true,
                imageUrl = imageLinksBySubject["urdu"]!!,
                iconName = "edit_note"
            ),
            SubjectEntity(
                id = "islamiyat",
                category = "Ethics & Religion",
                title = "Islamiyat",
                progress = if (classLevel == 4) 85 else 60 + (classLevel * 6) % 30,
                isOfflineReady = true,
                imageUrl = imageLinksBySubject["islamiyat"]!!,
                iconName = "star"
            ),
            SubjectEntity(
                id = "general_knowledge",
                category = "GK / Social Science",
                title = "General Knowledge",
                progress = if (classLevel == 4) 20 else 15 + (classLevel * 8) % 40,
                isOfflineReady = false,
                imageUrl = imageLinksBySubject["general_knowledge"]!!,
                iconName = "public"
            ),
            SubjectEntity(
                id = "mathematics",
                category = "Mathematics & Logic",
                title = "Mathematics",
                progress = if (classLevel == 4) 55 else 50 + (classLevel * 4) % 30,
                isOfflineReady = true,
                imageUrl = imageLinksBySubject["mathematics"]!!,
                iconName = "calculate"
            )
        )
    }

    fun getLocalFallbackChapters(subjectId: String): List<LessonChapterEntity> {
        return when (subjectId) {
            "english" -> listOf(
                LessonChapterEntity(subjectId = "english", chapterNumber = "01", chapterTitle = "Parts of Speech Basics", status = "Completed", lessonsCount = 6),
                LessonChapterEntity(subjectId = "english", chapterNumber = "02", chapterTitle = "Dynamic Sentence Structure", status = "In Progress", lessonsCount = 8),
                LessonChapterEntity(subjectId = "english", chapterNumber = "03", chapterTitle = "Reading Comprehension", status = "Locked", lessonsCount = 5),
                LessonChapterEntity(subjectId = "english", chapterNumber = "04", chapterTitle = "Vocabulary & Phonics", status = "Locked", lessonsCount = 4)
            )
            "urdu" -> listOf(
                LessonChapterEntity(subjectId = "urdu", chapterNumber = "01", chapterTitle = "Hamd-o-Naat (Poetry)", status = "Completed", lessonsCount = 4),
                LessonChapterEntity(subjectId = "urdu", chapterNumber = "02", chapterTitle = "Urdu Grammar: Fe'al, Ism, Harf", status = "In Progress", lessonsCount = 7),
                LessonChapterEntity(subjectId = "urdu", chapterNumber = "03", chapterTitle = "Creative Essay Composition", status = "Locked", lessonsCount = 6),
                LessonChapterEntity(subjectId = "urdu", chapterNumber = "04", chapterTitle = "Idioms & Proverbs (Amsal)", status = "Locked", lessonsCount = 5)
            )
            "islamiyat" -> listOf(
                LessonChapterEntity(subjectId = "islamiyat", chapterNumber = "01", chapterTitle = "Tauheed & Pillars of Faith", status = "Completed", lessonsCount = 5),
                LessonChapterEntity(subjectId = "islamiyat", chapterNumber = "02", chapterTitle = "The Life of Prophet Muhammad (PBUH)", status = "In Progress", lessonsCount = 9),
                LessonChapterEntity(subjectId = "islamiyat", chapterNumber = "03", chapterTitle = "Ethics and Daily Respect", status = "Locked", lessonsCount = 4),
                LessonChapterEntity(subjectId = "islamiyat", chapterNumber = "04", chapterTitle = "Adab and Cleanliness (Taharah)", status = "Locked", lessonsCount = 5)
            )
            "general_knowledge" -> listOf(
                LessonChapterEntity(subjectId = "general_knowledge", chapterNumber = "01", chapterTitle = "Our Solar System & Planets", status = "Completed", lessonsCount = 8),
                LessonChapterEntity(subjectId = "general_knowledge", chapterNumber = "02", chapterTitle = "Historical Civilizations", status = "In Progress", lessonsCount = 5),
                LessonChapterEntity(subjectId = "general_knowledge", chapterNumber = "03", chapterTitle = "The Human Body & Health", status = "Locked", lessonsCount = 6),
                LessonChapterEntity(subjectId = "general_knowledge", chapterNumber = "04", chapterTitle = "Continents and Oceans on Earth", status = "Locked", lessonsCount = 4)
            )
            else -> listOf(
                LessonChapterEntity(subjectId = "mathematics", chapterNumber = "01", chapterTitle = "Real Numbers & Sets", status = "Completed", lessonsCount = 4),
                LessonChapterEntity(subjectId = "mathematics", chapterNumber = "02", chapterTitle = "Calculus: Limits & Continuity", status = "In Progress", lessonsCount = 8),
                LessonChapterEntity(subjectId = "mathematics", chapterNumber = "03", chapterTitle = "Euclidean Geometry", status = "Locked", lessonsCount = 6),
                LessonChapterEntity(subjectId = "mathematics", chapterNumber = "04", chapterTitle = "Probability & Stats", status = "Locked", lessonsCount = 5)
            )
        }
    }

    fun getLocalFallbackNote(subjectId: String, chapterNumber: String, defaultTitle: String): NoteEntity {
        val noteId = "${subjectId}_$chapterNumber"
        val content = when (subjectId) {
            "english" -> {
                if (chapterNumber == "01") {
                    """
                    # Chapter 1: Parts of Speech Basics
                    
                    Every single word we speak or write in English belongs to a specific team called **Parts of Speech**. There are **8 essential parts** that help build sentences!
                    
                    ---
                    
                    ## 1. Nouns (The Naming Crew)
                    A noun is the name of a **person**, **place**, **thing**, or **idea**.
                    *   **Common Nouns:** general objects like *book, cat, school, teacher*.
                    *   **Proper Nouns:** specific capitals or names like *Ali, Islamabad, Pakistan*. (Always capitalized!)
                    *   **Example:** "The *teacher* placed the *globe* on the *table*."
                    
                    ## 2. Verbs (The Action Heroes)
                    A verb represents an action, state, or occurrence. You cannot build a proper English sentence without a verb!
                    *   **Physical Action:** *run, write, leap, shout*.
                    *   **State of Being:** *is, am, are, was, were, exist*.
                    *   **Example:** "Ali **wrote** his lessons diligently."
                    
                    ## 3. Adjectives (The Decorators)
                    Adjectives describe nouns or pronouns. They give details about color, size, quantity, or appearance.
                    *   **Colors/Size:** *sweet* mango, *large* classroom, *emerald-green* hills.
                    *   **Example:** "Four **bright** students solved the **complex** puzzle."
                    
                    ---
                    
                    ### Word-Match Grammar Fun:
                    | Word | Part of Speech | Sentence Role |
                    | :--- | :--- | :--- |
                    | Vidyalay | Noun (Proper) | The name of our school |
                    | Learns | Verb | The study action performed |
                    | Smart | Adjective | Describes how the student feels |
                    """.trimIndent()
                } else {
                    """
                    # Chapter 2: Dynamic Sentence Structure
                    
                    Writing proper English sentences requires placing subjects, verbs, and objects in a systematic order.
                    
                    ## S-V-O Structure
                    Most active sentences in English follow the **S-V-O** pattern:
                    1.  **Subject (S):** Who or what performs the action.
                    2.  **Verb (V):** What action is being performed.
                    3.  **Object (O):** Who or what receives the action.
                    
                    ---
                    
                    ## Simple Tenses Breakdown
                    *   **Present Simple:** Describes habitual facts.
                        *   *Format:* Subject + Verb-s/es + Object.
                        *   *Example:* "Amna **reads** her grammar book every evening."
                    *   **Past Simple:** Event finished in the past.
                        *   *Format:* Subject + Verb (2nd Form) + Object.
                        *   *Example:* "We **assembled** a beautiful paper windmill yesterday."
                    *   **Future Simple:** Action that will happen later.
                        *   *Format:* Subject + will + Verb + Object.
                        *   *Example:* "Vidyalay **will launch** school events next month."
                    """.trimIndent()
                }
            }
            "urdu" -> {
                if (chapterNumber == "01") {
                    """
                    # سبق نمبر 1: حمد و نعت کی تفہیم (Hamd-o-Naat Poetry)
                    
                    اردو ادب کی شروعات عموماً پاکیزہ کلام سے ہوتی ہے۔
                    
                    ---
                    
                    ## ۱۔ حمد (H-a-m-d)
                    *حمد* وہ پاک صنفِ شاعری ہے جس میں **اللہ تبارک و تعالیٰ** کی تعریف و توصیف بیان کی جاتی ہے۔ اللہ کی دی ہوئی نعمتوں، کائنات کے نظام، اور خوبصورت پہاڑوں، جھیلوں کا ذکر حمد کا اہم حصہ ہوتا ہے۔
                    
                    ## ۲۔ نعت (N-a-a-t)
                    *نعت* وہ صنفِ شاعری ہے جس میں محسنِ انسانیت، ہمارے پیارے نبی **حضرتِ محمد مصطفیٰ صلی اللہ علیہ وآلہ وسلم** کی تعریف، بلند کردار، اور اسوہ حسنہ کا والہانہ ذکر عقیدت کے ساتھ پیش کیا جائے۔
                    
                    ---
                    
                    ### اہم ذخیرہ الفاظ (Vocabulary Glossary):
                    *   **خالق:** پیدا کرنے والا (اللہتعالیٰ)
                    *   **اسوہ حسنہ:** پیارے نبیؐ کا پاکیزہ طور اور بہترین طریقہ کار
                    *   **رہنما:** صحیح سیدھی راہ دکھانے والی شخصیت
                    """.trimIndent()
                } else {
                    """
                    # سبق نمبر 2: اردو قواعدِ زبان (Urdu Grammar Basics)
                    
                    زبان کی خوبصورتی اور درست ادائیگی کے لیے قواعد سیکھنا انتہائی ناگزیر ہے۔
                    
                    ---
                    
                    ## کلمہ کی تین اہم اقسام:
                    کلام میں ہر با معنی لفظ کو کلمہ کہا جاتا ہے جس کی تین بنیادی قسمیں ہیں:
                    
                    1.  **اسم (Noun):** کسی بھی انسان، چیز، جانور یا جگہ کے نام کو اسم کہتے ہیں۔
                        *مثال:* علی، گھر، فیصل آباد، قلم۔
                    2.  **فعل (Verb):** کسی کام کے کرنے، ہونے یا سہنے کو فعل کہتے ہیں۔ جس میں وقت (زمانہ) پایا جائے۔
                        *مثال:* پڑھتا ہے، لکھا تھا، دوڑیں گے۔
                    3.  **حرف (Preposition/Connector):** ایسا اکیلا لفظ جو خود کوئی معنی نہ دے لیکن اسم اور فعل کو آپس میں جوڑنے کا کام کرے۔
                        *مثال:* پر، سے، کو، کا، کی، میرے۔
                    """.trimIndent()
                }
            }
            "islamiyat" -> {
                if (chapterNumber == "01") {
                    """
                    # Chapter 1: Tauheed & Pillars of Faith
                    
                    Islam is structured on five core pillars, but the foundation rests entirely upon clean, pure beliefs in the heart. These are called **Pillars of Faith (Iman)**.
                    
                    ---
                    
                    ## 1. Belief in Allah (Tauheed)
                    *   **Tauheed** means firmly believing that Allah is **One**, without any partners or equals.
                    *   He has created the heavens, the soils, the sun, and everything in-between.
                    *   He alone is worthy of worship, and we depend on Him entirely for our daily strength, guidance, and exams.
                    
                    ## 2. Belief in Prophets (Risalat)
                    Prophets were select noble human beings sent by Allah to guide mankind onto the path of peace.
                    *   The first Prophet was **Hazrat Adam (A.S.)**.
                    *   The final Prophet is **Hazrat Muhammad (PBUH)**.
                    
                    ## 3. Belief in Angels (Mala'ikah)
                    Angels are light-constructed (Noori) beings who strictly perform tasks assigned by Allah. They never disobey Him.
                    *   Famous angels include **Hazrat Jibrail (A.S.)** who carried divine books to the prophets.
                    """.trimIndent()
                } else {
                    """
                    # Chapter 2: Life of Prophet Muhammad (PBUH)
                    
                    The Life (Seerat) of Prophet Muhammad (PBUH) is the ultimate light guidance for all humans on Earth.
                    
                    ---
                    
                    ## Early Childhood & Perfect Trust
                    *   Born in the holy city of **Makkah** on the 12th of Rabi-ul-Awwal.
                    *   Even before prophethood, he was universally called **Al-Sadiq** (The Truthful) and **Al-Amin** (The Trustworthy) due to his transparent dealings.
                    
                    ## Key Teachings & Manners
                    1.  **Love for Children:** He always greeted young kids with sweet smiles and stroked their hair.
                    2.  **Kindness to Needy:** He shared his simple meals and never spoke harshly.
                    3.  **Respect for Parents:** Emphasized serving mothers and fathers to find divine pleasure.
                    """.trimIndent()
                }
            }
            "general_knowledge" -> {
                if (chapterNumber == "01") {
                    """
                    # Chapter 1: Our Solar System & Planets
                    
                    Welcome to space! Our home galaxy is the **Milky Way**, and inside it sits our solar system, with a glowing bright star at its center called the **Sun**.
                    
                    ---
                    
                    ## Orbit Hierarchy & System Map
                    Eight unique planets orbit (travel around) our Sun in elliptical circular paths:
                    
                     (Sun) ---> [Mercury] -> [Venus] -> [Earth] -> [Mars] -> [Jupiter] -> [Saturn] -> [Uranus] -> [Neptune]
                    
                    ---
                    
                    ## Major Planet Profiles:
                    1.  **Earth (The Water Globe):** The only known planet with water, a balanced atmosphere, and smart life (us!). It takes exactly 365 days to complete one trip around the Sun.
                    2.  **Mars (The Dusty Red planet):** Covered in rust-like iron oxide dust. Scientists are sending robots (rovers) to check if plants could grow there.
                    3.  **Jupiter (The Heavy giant):** The largest planet in our solar system. It is a giant ball of swirling gases and has a massive cyclone spot wider than Earth itself!
                    4.  **Saturn (The Chilled Ring master):** Beautifully framed by hundreds of icy rings made of particles, rocks, and space debris.
                    """.trimIndent()
                } else {
                    """
                    # Chapter 2: Historical Civilizations of Indus
                    
                    Archaeology allows us to peer into deep history. The ancient lands of Pakistan hold some of the oldest pre-planned cities on planet Earth!
                    
                    ---
                    
                    ## Mohenjo-daro & Harappa (Indus Valley System)
                    Over **5,000 years ago**, a highly advanced, peaceful civilization thrived along the banks of the grand **Indus River**.
                    
                    ### Astonishing Historical Facts:
                    *   **Grid Streets:** Unlike old messy cities, Mohenjo-daro houses were built along neat straight roads crossing at right angles.
                    *   **Advanced Drainage:** They carved brick-lined channels underneath pathways to drain household wastewater.
                    *   **No Weapons:** Archaeologists found zero traces of military gear or war shields, indicating they focused heavily on farming, trade, and learning.
                    """.trimIndent()
                }
            }
            else -> {
                if (chapterNumber == "01") {
                    """
                    # Chapter 1: Real Numbers & Sets
                    
                    Mathematics is the dynamic language of the universe. In this chapter, we classify all numerical structures and solve sets.
                    
                    ---
                    
                    ## 1. Number Systems hierarchy
                    *   **Natural Numbers (N):** Counting digits starting from 1, 2, 3, etc.
                    *   **Whole Numbers (W):** Includes zero: 0, 1, 2, 3, etc.
                    *   **Integers (Z):** Negative and positive numbers: ..., -2, -1, 0, 1, 2, ...
                    *   **Real Numbers (R):** All rational and irrational decimals. Can be drawn on an infinite straight line.
                    
                    ---
                    
                    ## 2. Set Theory and Operators
                    A set is a collection of distinct, well-defined items.
                    Let Set A = {2, 4, 6} and Set B = {4, 6, 8}.
                    
                    *   **Union (A U B):** Combines elements of both sets without repeats.
                        *   A U B = {2, 4, 6, 8}
                    *   **Intersection (A n B):** Finds only elements residing in both.
                        *   A n B = {4, 6}
                    """.trimIndent()
                } else {
                    """
                    # Chapter 2: Calculus: Limits & Continuity
                    
                    Calculus is the mathematical branch measuring change. Let's study how functions approach values.
                    
                    ---
                    
                    ## Concept of a Limit
                    A limit determines the y-value a function approaches as its x-value gets infinitely close to a target point.
                    
                    Formula: lim(x -> c) f(x) = L
                    
                    ### Practical Example:
                    Consider the expression f(x) = (x^2 - 4) / (x - 2).
                    We cannot plug in x = 2 directly because dividing by zero is forbidden (0/0). However, if we take the limit:
                    
                    lim(x -> 2) [(x-2)(x+2)] / (x-2) = lim(x -> 2) (x + 2) = 4
                    
                    ---
                    
                    ## Continuity Conditions
                    A function is continuous at a point x = c if and only if:
                    1.  f(c) is defined.
                    2.  lim(x -> c) f(x) exists.
                    3.  lim(x -> c) f(x) = f(c) (No jumps, breaks, or empty holes!)
                    """.trimIndent()
                }
            }
        }
        return NoteEntity(
            id = noteId,
            subjectId = subjectId,
            chapterNumber = chapterNumber,
            title = defaultTitle,
            content = content,
            keywords = when (subjectId) {
                "english" -> "Grammar, S-V-O, Parts of speech, Learning"
                "urdu" -> "حروف, حمد, فعل, قواعد"
                "islamiyat" -> "Tauheed, Beliefs, Pillars, Seerat"
                "general_knowledge" -> "Space, Planets, History, Mohenjo-daro"
                else -> "Math, Sets, Euler, Limits, Proofs"
            },
            totalReadingTimeMinutes = 5 + (chapterNumber.toIntOrNull() ?: 1) * 2,
            isBookmarked = false,
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    /**
     * Upload a quiz attempt to Firebase Firestore.
     * Logs offline if Firestore is unconfigured or offline.
     */
    suspend fun saveQuizAttempt(attempt: QuizAttemptEntity): Boolean {
        val fs = firestore ?: return false
        val docId = "attempt_${attempt.timestamp}_${attempt.subjectId}"
        val data = hashMapOf(
            "subjectId" to attempt.subjectId,
            "chapterNumber" to attempt.chapterNumber,
            "difficulty" to attempt.difficulty,
            "score" to attempt.score,
            "totalQuestions" to attempt.totalQuestions,
            "percentage" to attempt.percentage,
            "timeSpentSeconds" to attempt.timeSpentSeconds,
            "timestamp" to attempt.timestamp
        )

        return try {
            suspendCancellableCoroutine { continuation ->
                fs.collection("quiz_attempts")
                    .document(docId)
                    .set(data)
                    .addOnSuccessListener {
                        continuation.resume(true)
                    }
                    .addOnFailureListener {
                        continuation.resume(false)
                    }
            }
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Failed to save quiz attempt on Firebase.", e)
            false
        }
    }

    /**
     * Upload dynamic student progress parameters to Firebase Firestore.
     */
    suspend fun saveStudentProgress(
        studentName: String,
        classLevel: Int,
        courseCompletion: Int,
        streakDays: Int,
        lastActiveTime: Long = System.currentTimeMillis()
    ): Boolean {
        val fs = firestore ?: return false
        val docId = "progress_${studentName.replace(" ", "_").lowercase()}_class${classLevel}"
        val data = hashMapOf(
            "studentName" to studentName,
            "classLevel" to classLevel,
            "courseCompletion" to courseCompletion,
            "streakDays" to streakDays,
            "lastActiveTime" to lastActiveTime
        )

        return try {
            suspendCancellableCoroutine { continuation ->
                fs.collection("student_progress")
                    .document(docId)
                    .set(data)
                    .addOnSuccessListener {
                        continuation.resume(true)
                    }
                    .addOnFailureListener {
                        continuation.resume(false)
                    }
            }
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Failed to upload student progress to cloud firestore.", e)
            false
        }
    }
}
