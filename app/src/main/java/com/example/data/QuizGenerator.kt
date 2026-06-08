package com.example.data

object QuizGenerator {

    fun generateQuizQuestions(
        subjectId: String,
        chapterNumber: String,
        difficulty: String
    ): List<QuizQuestion> {
        val count = when (difficulty) {
            "Easy" -> 10
            "Medium" -> 15
            "Hard" -> 20
            else -> 10
        }

        val baseQuestions = when (subjectId.lowercase()) {
            "mathematics" -> getMathQuestions(chapterNumber, difficulty)
            "english" -> getEnglishQuestions(chapterNumber, difficulty)
            "urdu" -> getUrduQuestions(chapterNumber, difficulty)
            "islamiyat" -> getIslamiyatQuestions(chapterNumber, difficulty)
            "general_knowledge" -> getGkQuestions(chapterNumber, difficulty)
            else -> getGkQuestions(chapterNumber, difficulty)
        }

        // Ensure we return exactly the requested count
        return if (baseQuestions.size >= count) {
            baseQuestions.take(count)
        } else {
            // If the pool is smaller, pad it dynamically by copying and slightly modifying the copies to make them unique
            val paddedList = baseQuestions.toMutableList()
            var offset = 1
            while (paddedList.size < count) {
                val copySource = baseQuestions[paddedList.size % baseQuestions.size]
                paddedList.add(
                    copySource.copy(
                        id = copySource.id + 100 * offset,
                        question = "${copySource.question} (Set B-${offset})"
                    )
                )
                offset++
            }
            paddedList
        }
    }

    private fun getMathQuestions(chapterNumber: String, difficulty: String): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()
        if (chapterNumber == "01") {
            // Real Numbers & Sets
            list.add(QuizQuestion(1, "Which of the following is a Natural Number?", null, "0", "-5", "4", "2.5", "C"))
            list.add(QuizQuestion(2, "What is the union of Set A = {1, 2} and Set B = {2, 3}?", "A U B", "{1, 2}", "{2, 3}", "{1, 2, 3}", "{1, 3}", "C"))
            list.add(QuizQuestion(3, "Which number is an Integer but NOT a Natural Number?", null, "5", "0.5", "-3", "10", "C"))
            list.add(QuizQuestion(4, "What is the intersection of Set A = {5, 6, 7} and Set B = {7, 8}?", "A n B", "{7}", "{5, 6, 7, 8}", "{5, 6}", "{8}", "A"))
            list.add(QuizQuestion(5, "What does the symbol 'W' represent in mathematics?", null, "Whole Numbers", "Natural Numbers", "Rational Numbers", "Integers", "A"))
            list.add(QuizQuestion(6, "Find the Union of empty set O and Set A = {3, 4}", "O U A", "{3, 4}", "O", "{3}", "{4}", "A"))
            list.add(QuizQuestion(7, "If Set A has 3 elements, what is the count of elements in its power set?", "P(A)", "3", "6", "8", "9", "C"))
            list.add(QuizQuestion(8, "Which of the following numbers is an irrational real number?", null, "3.14", "22/7", "Square Root of 2", "1.5", "C"))
            list.add(QuizQuestion(9, "What is the intersection of two disjoint sets?", null, "The Universal Set", "The Empty Set", "The First Set", "The Second Set", "B"))
            list.add(QuizQuestion(10, "Is 0 a Whole Number?", null, "Yes", "No", "Only in Decimals", "Only in Fractions", "A"))
            
            // Extra questions for Medium & Hard
            list.add(QuizQuestion(11, "Which of the following represents the commutative property under union?", "A U B = ?", "A n B", "B U A", "A - B", "B - A", "B"))
            list.add(QuizQuestion(12, "If U = {1, 2, 3, 4} and A = {2, 3}, what is the complement of A?", "A'", "{1, 4}", "{2, 3}", "U", "Empty Set", "A"))
            list.add(QuizQuestion(13, "What is the difference of Set A = {10, 20} and B = {20, 30}?", "A - B", "{10}", "{30}", "{10, 30}", "{20}", "A"))
            list.add(QuizQuestion(14, "Which set represents the prime numbers less than 10?", null, "{1, 3, 5, 7}", "{2, 3, 5, 7}", "{3, 5, 7}", "{2, 3, 5, 7, 9}", "B"))
            list.add(QuizQuestion(15, "Solve the equation for the natural number x:", "3x - 1 = 8", "x = 3", "x = 4", "x = 2", "x = 5", "A"))
            list.add(QuizQuestion(16, "What is the smallest positive integer?", null, "0", "1", "-1", "2", "B"))
            list.add(QuizQuestion(17, "If Set A = {a, b} and B = {b, a}, then Set A and B are:", null, "Disjoint", "Equal", "Infinite", "Power Sets", "B"))
            list.add(QuizQuestion(18, "Which of the following counts as a rational number?", null, "Square root of 3", "Square root of 9", "Square root of 5", "Pi", "B"))
            list.add(QuizQuestion(19, "What is the Cartesian product cardinality for Set A(2 elements) and Set B(3 elements)?", "n(A x B)", "5", "6", "8", "9", "B"))
            list.add(QuizQuestion(20, "What is the value of 5 + (-3) in integers?", null, "8", "2", "-8", "-2", "B"))
        } else {
            // Calculus: Limits & Continuity & other chapters
            list.add(QuizQuestion(1, "Evaluate the limit of f(x) = x + 3 as x approaches 2", "lim (x->2) (x+3)", "5", "2", "3", "0", "A"))
            list.add(QuizQuestion(2, "Evaluate the limit of constant function f(x) = 15 as x approaches 5", "lim (x->5) 15", "5", "15", "75", "0", "B"))
            list.add(QuizQuestion(3, "Find the value of the following limit:", "lim (x->3) (x^2 - 9)/(x - 3)", "3", "6", "0", "Undefined", "B"))
            list.add(QuizQuestion(4, "A function f(x) is continuous at point c if f(c) equals:", "lim (x->c) f(x) = ?", "0", "f(c)", "1", "Infinity", "B"))
            list.add(QuizQuestion(5, "Find the derivative of f(x) = 4x at x = 2", "f'(x) of 4x", "4", "2", "8", "1", "A"))
            list.add(QuizQuestion(6, "Find the derivative of constant f(x) = 100", "f'(x) of 100", "100", "1", "0", "Undefined", "C"))
            list.add(QuizQuestion(7, "Find the limit as x approaches infinity of f(x) = 1/x", "lim (x->inf) 1/x", "1", "0", "Infinity", "Undefined", "B"))
            list.add(QuizQuestion(8, "Does live limit exist if left-hand limit is 3 and right-hand limit is 5?", "LHL = 3, RHL = 5", "Yes, limit is 4", "Yes, limit is 3", "No, limit does not exist", "Yes, limit is 5", "C"))
            list.add(QuizQuestion(9, "Solve for x in equation:", "2x - 4 = 10", "x = 5", "x = 7", "x = 8", "x = 6", "B"))
            list.add(QuizQuestion(10, "What is the slope of the line y = 3x + 1?", "y = mx + c", "1", "3", "4", "0", "B"))
            
            // Extra questions for Calculus
            list.add(QuizQuestion(11, "Evaluate the limit", "lim (x->0) (sin x)/x", "0", "1", "Undefined", "Infinity", "B"))
            list.add(QuizQuestion(12, "If a function has a dynamic jump at x = 2, is it continuous at x = 2?", null, "Yes", "No", "Only if LHL exists", "Only if RHL exists", "B"))
            list.add(QuizQuestion(13, "Evaluate the limit", "lim (x->4) x^2", "4", "8", "16", "2", "C"))
            list.add(QuizQuestion(14, "Find the derivative of f(x) = x^2 at x = 3", "f'(x) = 2x", "6", "9", "3", "5", "A"))
            list.add(QuizQuestion(15, "Determine if f(x) = |x| has a derivative at x = 0", "f(x) = |x| at x = 0", "Yes, f'(0) = 0", "No, corner point", "Yes, f'(0) = 1", "Yes, f'(0)= -1", "B"))
            list.add(QuizQuestion(16, "What is the value of limit of (3x^2 + 2)/(x^2 - 1) as x approaches infinity?", "lim (x->inf) (3x^2+2)/(x^2-1)", "2", "-1", "3", "Undefined", "C"))
            list.add(QuizQuestion(17, "What is the integral of the derivative of f(x)?", "Integral[ f'(x) dx ]", "f(x) + C", "f'(x)", "f''(x)", "x", "A"))
            list.add(QuizQuestion(18, "Which of the following functions has a vertical asymptote at x = 1?", "f(x) = ?", "x - 1", "1/(x-1)", "x^2", "1/x", "B"))
            list.add(QuizQuestion(19, "Find the rate of change of area of circle with radius r = 5 with respect to time if dr/dt = 2", "dA/dt = 2*pi*r*dr/dt", "10 pi", "20 pi", "5 pi", "15 pi", "B"))
            list.add(QuizQuestion(20, "What is the limit of x^3 as x approaches -2?", "lim (x->-2) x^3", "-8", "8", "-6", "6", "A"))
        }
        return list
    }

    private fun getEnglishQuestions(chapterNumber: String, difficulty: String): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()
        if (chapterNumber == "01") {
            // Parts of Speech Basics
            list.add(QuizQuestion(1, "Which of the following is a common noun?", null, "Vidyalay", "Islamabad", "book", "Ali", "C"))
            list.add(QuizQuestion(2, "Identify the verb in the sentence: 'The horse runs fast.'", null, "horse", "runs", "fast", "The", "B"))
            list.add(QuizQuestion(3, "Which word is an adjective in: 'She wore a beautiful emerald dress.'", null, "She", "beautiful", "dress", "wore", "B"))
            list.add(QuizQuestion(4, "Fill in the blank with a suitable pronoun: 'Asif is absent because ____ is sick.'", null, "he", "she", "it", "they", "A"))
            list.add(QuizQuestion(5, "What part of speech is the word 'silently'?", null, "Noun", "Verb", "Adverb", "Preposition", "C"))
            list.add(QuizQuestion(6, "Which of the following is a proper noun?", null, "country", "river", "Pakistan", "boy", "C"))
            list.add(QuizQuestion(7, "Choose the preposition: 'The ball rolled under the car.'", null, "rolled", "under", "car", "ball", "B"))
            list.add(QuizQuestion(8, "Which word is a conjunction in: 'I wanted to run but I was tired.'", null, "wanted", "but", "tired", "was", "B"))
            list.add(QuizQuestion(9, "What is the plural of 'child'?", null, "childs", "children", "childes", "childrens", "B"))
            list.add(QuizQuestion(10, "Select the abstract noun:", null, "happiness", "table", "water", "Ali", "A"))
            
            // Extra questions for Parts of speech
            list.add(QuizQuestion(11, "Which of the following is a collective noun?", null, "team", "player", "ball", "bat", "A"))
            list.add(QuizQuestion(12, "Identify the auxiliary verb in: 'We are studying English.'", null, "studying", "are", "English", "We", "B"))
            list.add(QuizQuestion(13, "What is the feminine gender of 'actor'?", null, "actress", "actor", "actresses", "actorine", "A"))
            list.add(QuizQuestion(14, "Identify the adverb of place in: 'Put the book here.'", null, "Put", "book", "here", "the", "C"))
            list.add(QuizQuestion(15, "Which sentence contains an transitive verb?", null, "He laughed aloud.", "The baby slept.", "She wrote a letter.", "They run daily.", "C"))
            list.add(QuizQuestion(16, "Select the correct pronoun: 'This pen belongs to me; it is ____.'", null, "my", "mine", "me", "myself", "B"))
            list.add(QuizQuestion(17, "What is the comparative form of the adjective 'good'?", null, "gooder", "best", "better", "more good", "C"))
            list.add(QuizQuestion(18, "Choose the interjection:", null, "Wow!", "And", "Between", "Walk", "A"))
            list.add(QuizQuestion(19, "Identify the noun in: 'Knowledge is power.'", null, "is", "Knowledge", "both Knowledge and power", "none", "C"))
            list.add(QuizQuestion(20, "Which of the following is a count noun?", null, "water", "sand", "apple", "milk", "C"))
        } else {
            // Sentence Structure
            list.add(QuizQuestion(1, "What is the subject in the sentence: 'Amna reads books.'", null, "Amna", "reads", "books", "reads books", "A"))
            list.add(QuizQuestion(2, "What is the object in: 'The boy kicked the soccer ball.'", null, "boy", "kicked", "soccer ball", "The boy", "C"))
            list.add(QuizQuestion(3, "Which sentence layout represents active S-V-O in English?", null, "Subject-Verb-Object", "Verb-Subject-Object", "Object-Subject-Verb", "Subject-Object-Verb", "A"))
            list.add(QuizQuestion(4, "Identify the tense: 'I will visit Lahore next Sunday.'", null, "Present Simple", "Past Simple", "Future Simple", "Present Continuous", "C"))
            list.add(QuizQuestion(5, "Identify the tense: 'She baked a delicious chocolate cake.'", null, "Present Simple", "Past Simple", "Future Simple", "Past Continuous", "B"))
            list.add(QuizQuestion(6, "Which of these is a complete sentence?", null, "Because it was raining.", "The lazy dog.", "He ran home quickly.", "Under the heavy bridge.", "C"))
            list.add(QuizQuestion(7, "Choose the correct verb form: 'She ____ to school every day.'", null, "go", "goes", "going", "gone", "B"))
            list.add(QuizQuestion(8, "Change to negative: 'We solved the math puzzle.'", null, "We are not solve the puzzle.", "We did not solve the puzzle.", "We not solved the puzzle.", "We don't solve the puzzle.", "B"))
            list.add(QuizQuestion(9, "What punctuation mark always ends an interrogative sentence?", null, "Period (.)", "Exclamation (!)", "Question mark (?)", "Comma (,)", "C"))
            list.add(QuizQuestion(10, "Which is a compound sentence?", null, "He came and sat down.", "I like tea but she likes coffee.", "The sun is shining hot.", "When did you arrive?", "B"))
            
            // Extra questions for Sentence structure
            list.add(QuizQuestion(11, "Choose the correct article: 'I saw ____ elephant at the safari.'", null, "a", "an", "the", "no article", "B"))
            list.add(QuizQuestion(12, "What is the predicate in: 'The bright light flickered.'", null, "The bright light", "light flickered", "flickered", "bright light", "C"))
            list.add(QuizQuestion(13, "Identify the tense: 'They have finished their lessons.'", null, "Present Perfect", "Present Simple", "Past Simple", "Past Perfect", "A"))
            list.add(QuizQuestion(14, "Which of these is a run-on sentence?", null, "I wanted tea, so I boiled water.", "I love reading I buy books weekly.", "Although he was late, he came.", "Hello!", "B"))
            list.add(QuizQuestion(15, "Fill sentence: 'Neither of the boys ____ present.'", null, "was", "were", "are", "have", "A"))
            list.add(QuizQuestion(16, "Identify the direct object: 'Ali gave Amna a beautiful flower.'", null, "Ali", "Amna", "flower", "gave", "C"))
            list.add(QuizQuestion(17, "What type of sentence is: 'Close the door immediately.'", null, "Declarative", "Imperative", "Interrogative", "Exclamatory", "B"))
            list.add(QuizQuestion(18, "Choose the correct form: 'The team ____ practicing hard.'", null, "is", "are", "have", "were", "is"))
            list.add(QuizQuestion(19, "Which is passive voice?", null, "Ali painted the wall.", "The wall was painted by Ali.", "Ali is painting the wall.", "Wall was white.", "B"))
            list.add(QuizQuestion(20, "What is the synonym of 'Quick'?", null, "Slow", "Rapid", "Heavy", "Dull", "B"))
        }
        return list
    }

    private fun getUrduQuestions(chapterNumber: String, difficulty: String): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()
        if (chapterNumber == "01") {
            // Poetry & Prose (Hamd & Naat)
            list.add(QuizQuestion(1, "حمد کس نظم کو کہا جاتا ہے؟", null, "جس میں رسول پاکؐ کی تعریف ہو", "جس میں اللہ تعالیٰ کی تعریف ہو", "جس میں اولیاء کی تعریف ہو", "ملک کی تعریف", "B"))
            list.add(QuizQuestion(2, "نعت کس نظم کو کہتے ہیں؟", null, "جس میں دیس کی خوبی ہو", "جس میں اللہ کی بڑائی ہو", "جس میں حضرت محمد مصطفیٰؐ کی تعریف ہو", "موسم کا حال", "C"))
            list.add(QuizQuestion(3, "لفظ 'خالق' کا کیا معنی ہے؟", null, "پیدا کرنے والا", "پڑھا ہوا", "دوست", "مہمان", "A"))
            list.add(QuizQuestion(4, "اسوہ حسنہ کے کیا معنی ہیں؟", null, "برا طریقہ کار", "بہترین طریقہ کار / پاکیزہ زندگی", "بڑی کتاب", "سفر نامہ", "B"))
            list.add(QuizQuestion(5, "اللہ تعالیٰ کی نعمتوں کا شکر کس نظم میں بیان کیا جاتا ہے؟", null, "نعت", "حمد", "غزل", "مرثیہ", "B"))
            list.add(QuizQuestion(6, "پہلے نبی علیہ السلام کون تھے؟", null, "حضرت موسیٰؑ", "حضرت نوحؑ", "حضرت آدمؑ", "حضرت عیسیٰؑ", "C"))
            list.add(QuizQuestion(7, "نبی کریم صلی اللہ علیہ وآلہ وسلم کے آخری ہونے کا عقیدہ کیا کہلاتا ہے؟", null, "عقیدہ رسالت", "عقیدہ توحید", "عقیدہ ختمِ نبوت", "عقیدہ آخرت", "C"))
            list.add(QuizQuestion(8, "لفظ 'رہنما' کا متضاد کیا ہے؟", null, "رہبر", "راہزن / ڈاکو", "دوست", "مسافر", "B"))
            list.add(QuizQuestion(9, "کائنات کا تمام نظام کون چلا رہا ہے؟", null, "انسان", "فرشتے", "اللہ تعالیٰ کی واحد ذات", "سائنسدان", "C"))
            list.add(QuizQuestion(10, "لفظ 'تعریف' کی جمع کیا ہے؟", null, "تعریفیں", "تعارف", "تعاریف", "معروف", "C"))
            
            // Extra Urdu Poetry Questions
            list.add(QuizQuestion(11, "نعتِ رسول مقبولؐ سن کر دل کو کیا حاصل ہوتا ہے؟", null, "بے چینی", "تسکین اور سکون", "غصہ", "تھکاوٹ", "B"))
            list.add(QuizQuestion(12, "لفظ 'بشر' کا مترادف کیا ہے؟", null, "فرشتہ", "انسان", "حیوان", "درخت", "B"))
            list.add(QuizQuestion(13, "عرش کا متضاد کیا ہے؟", null, "آسمان", "فرش / زمین", "بادل", "سورج", "B"))
            list.add(QuizQuestion(14, "حمد و نعت کس صنف کے تحت آتی ہیں؟", null, "نثر", "شاعری", "ناول", "ڈرامہ", "B"))
            list.add(QuizQuestion(15, "اللہ کا آخری کلام کون سی پاک کتاب ہے؟", null, " تورات", "انجیل", "قرآن مجید", "زبور", "C"))
            list.add(QuizQuestion(16, "لفظ 'شمس' کا کیا معنی ہے؟", null, "چاند", "سورج", "تارے", "رات", "B"))
            list.add(QuizQuestion(17, "لفظ 'قمر' کا کیا معنی ہے؟", null, "چاند", "سورج", "ستارہ", "دن", "A"))
            list.add(QuizQuestion(18, "پیارے نبیؐ بچپن سے ہی کس لقب سے مشہور تھے؟", null, "فاتح", "صادق اور امین", "سخی", "شجاع", "B"))
            list.add(QuizQuestion(19, "لفظ 'رحمت' کا متضاد کیا ہے؟", null, "کرم", "زحمت / غضب", "شفقت", "عطا", "B"))
            list.add(QuizQuestion(20, "نعتیہ کلام پڑھنے والے کو کیا کہتے ہیں؟", null, "شاعر", "نعت خواں", "مقرر", "قاری", "B"))
        } else {
            // Urdu Grammar: Fe'al, Ism, Harf
            list.add(QuizQuestion(1, "کسی بھی شخص، جگہ یا چیز کے نام کو کیا کہتے ہیں؟", null, "فعل", "اسم", "حرف", "ضمیر", "B"))
            list.add(QuizQuestion(2, "کسی کام کے کرنے یا ہونے کو کیا کہتے ہیں؟", null, "اسم", "فعل", "حرف", "صفت", "B"))
            list.add(QuizQuestion(3, "وہ لفظ جو اکیلا کوئی معنی نہ دے لیکن الفاظ کو جوڑنے کا کام کرے؟", null, "اسم", "فعل", "حرف", "انشائیہ", "C"))
            list.add(QuizQuestion(4, "مندرجہ ذیل میں سے 'اسم' کی نشاندہی کریں:", null, "پڑھتا ہے", "علی", "پر", "سے", "B"))
            list.add(QuizQuestion(5, "جملہ 'وہ خط لکھتا ہے' میں 'لکھتا ہے' کیا ہے؟", null, "اسم", "فعل", "حرف", "ضمیر", "B"))
            list.add(QuizQuestion(6, "جملہ 'کتاب میز پر ہے' میں 'پر' کیا ہے؟", null, "اسم", "فعل", "حرف", "اسم صفت", "C"))
            list.add(QuizQuestion(7, "اسم کی کتنی بنیادی معنوی اقسام ہیں؟ (معرفہ اور نکرہ)", null, "دو (2)", "تین (3)", "چار (4)", "پانچ (5)", "A"))
            list.add(QuizQuestion(8, "کسی عام چیز، جگہ یا انسان کے نام کو کیا کہتے ہیں؟", null, "اسمِ معرفہ", "اسمِ نکرہ", "اسمِ صفت", "فعلِ ماضی", "B"))
            list.add(QuizQuestion(9, "کسی خاص شخص، جگہ یا چیز کے نام کو کیا کہتے ہیں؟", null, "اسمِ معرفہ", "اسمِ نکرہ", "اسمِ ضمیر", "حرفِ جر", "A"))
            list.add(QuizQuestion(10, "'قلم' اور 'کمرہ' اسم کی کون سی قسم ہے؟", null, "اسمِ معرفہ", "اسمِ نکرہ", "اسمِ صفت", "کوئی نہیں", "B"))
            
            // Extra Urdu Grammar
            list.add(QuizQuestion(11, "'لاہور' اور 'علامہ اقبال' اسم کی کون سی قسم ہے؟", null, "اسمِ نکرہ", "اسمِ معرفہ", "اسمِ صفت", "حرف", "B"))
            list.add(QuizQuestion(12, "گزرا ہوا زمانہ کیا کہلاتا ہے؟", null, "فعلِ حال", "فعلِ مستقبل", "فعلِ ماضی", "فعلِ امر", "C"))
            list.add(QuizQuestion(13, "موجودہ زمانہ کیا کہلاتا ہے؟", null, "فعلِ حال", "فعلِ ماضی", "فعلِ مستقبل", "فعلِ نہی", "A"))
            list.add(QuizQuestion(14, "آنے والا زمانہ کیا کہلاتا ہے؟", null, "فعلِ حال", "فعلِ ماضی", "فعلِ مستقبل", "مضارع", "C"))
            list.add(QuizQuestion(15, "ایسا کلمہ جو اسم کی جگہ استعمال ہو (جیسے وہ، تم):", null, "اسمِ موصول", "اسمِ اشارہ", "اسمِ ضمیر", "اسمِ صفت", "C"))
            list.add(QuizQuestion(16, "اسم کی خوبی یا خامی بیان کرنے والے لفظ کو کیا کہتے ہیں؟", null, "اسمِ صفت", "اسمِ ضمیر", "اسمِ نکرہ", "فعل", "A"))
            list.add(QuizQuestion(17, "جملہ 'گرم چائے سردی دور کرتی ہے' میں 'گرم' کیا ہے؟", null, "اسم", "اسمِ صفت", "فعل", "حرف", "B"))
            list.add(QuizQuestion(18, "واحد لفظ 'شجر' کی جمع کیا ہے؟", null, "شجروں", "اشجار", "شجری", "شجرہ", "B"))
            list.add(QuizQuestion(19, "'سچ بولنا' کا متضاد یا الٹ کیا ہے؟", null, "جھوٹ بولنا", "غصہ کرنا", "خاموش رہنا", "امانت داری", "A"))
            list.add(QuizQuestion(20, "جملہ مکمل کریں: 'بچے میدان میں کھیل رہے ____۔'", null, "ہے", "تھا", "ہیں", "ہوں", "C"))
        }
        return list
    }

    private fun getIslamiyatQuestions(chapterNumber: String, difficulty: String): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()
        if (chapterNumber == "01") {
            // Tauheed & Faith
            list.add(QuizQuestion(1, "What does Tauheed mean?", null, "Belief in many Gods", "Belief in One Allah", "Belief in Angels", "Belief in Holy Books", "B"))
            list.add(QuizQuestion(2, "According to Islam, who is the creator of the whole Universe?", null, "Ancients", "Angels", "Allah Almighty", "Nature itself", "C"))
            list.add(QuizQuestion(3, "Who is the final Prophet of Allah?", null, "Hazrat Musa (A.S.)", "Hazrat Isa (A.S.)", "Hazrat Muhammad (PBUH)", "Hazrat Adam (A.S.)", "C"))
            list.add(QuizQuestion(4, "How many core pillars of Islam are there?", null, "3", "4", "5", "7", "C"))
            list.add(QuizQuestion(5, "What is the first pillar of Islam?", null, "Salah (Prayer)", "Zakat (Charity)", "Shahadah (Declaration of Faith)", "Sawm (Fasting)", "C"))
            list.add(QuizQuestion(6, "Which angel brought the divine messages/revelations to the Prophets?", null, "Hazrat Jibrail (A.S.)", "Hazrat Mikail (A.S.)", "Hazrat Izrail (A.S.)", "Hazrat Israfil (A.S.)", "A"))
            list.add(QuizQuestion(7, "Who was the first prophet of Allah sent to earth?", null, "Hazrat Nuh (A.S.)", "Hazrat Adam (A.S.)", "Hazrat Ibrahim (A.S.)", "Hazrat Yusuf (A.S.)", "B"))
            list.add(QuizQuestion(8, "What are angels made of?", null, "Clay", "Fire", "Light (Noor)", "Water", "C"))
            list.add(QuizQuestion(9, "Which holy book was revealed to Hazrat Musa (A.S.)?", null, "Injeel", "Torah (Tawrat)", "Zaboor", "The Holy Quran", "B"))
            list.add(QuizQuestion(10, "In which language was the Holy Quran revealed?", null, "Hebrew", "Persian", "Arabic", "Urdu", "C"))
            
            // Extra Faith Questions
            list.add(QuizQuestion(11, "What is the second pillar of Islam?", null, "Sawm", "Salah", "Hajj", "Zakat", "B"))
            list.add(QuizQuestion(12, "What is the meaning of 'Al-Rahman'?", null, "The Most Merciful", "The Omnipotent", "The Creator", "The Judge", "A"))
            list.add(QuizQuestion(13, "How many times a day must a Muslim pray Salah?", null, "3", "4", "5", "6", "C"))
            list.add(QuizQuestion(14, "Fasting (Sawm) is mandatory during which Islamic month?", null, "Rabi-ul-Awwal", "Rajab", "Ramadan", "Dhul-Hijjah", "C"))
            list.add(QuizQuestion(15, "To whom was the holy book Zaboor revealed?", null, "Hazrat Dawood (A.S.)", "Hazrat Musa (A.S.)", "Hazrat Isa (A.S.)", "Hazrat Ibrahim (A.S.)", "A"))
            list.add(QuizQuestion(16, "What does 'Al-Amin' mean?", null, "The Truthful", "The Trustworthy / Honest", "The Generous", "The Valiant", "B"))
            list.add(QuizQuestion(17, "What does 'Al-Sadiq' mean?", null, "The Truthful", "The Polite", "The Patient", "The Brave", "A"))
            list.add(QuizQuestion(18, "Which Islamic pillar involves giving a portion of wealth to the poor?", null, "Hajj", "Zakat", "Salah", "Sawm", "B"))
            list.add(QuizQuestion(19, "Where do Muslims go to perform the annual pilgrimage (Hajj)?", null, "Jerusalem", "Makkah", "Madinah", "Cairo", "B"))
            list.add(QuizQuestion(20, "What is the belief in life after death called in Islam?", null, "Iman-bil-Risaalat", "Iman-bil-Akhirah", "Iman-bil-Kutub", "Iman-bil-Mala'ikah", "B"))
        } else {
            // Life of Prophet Muhammad (PBUH) - Seerat
            list.add(QuizQuestion(1, "In which city was Prophet Muhammad (PBUH) born?", null, "Madinah", "Ta'if", "Makkah", "Jerusalem", "C"))
            list.add(QuizQuestion(2, "In which Islamic month was the Prophet (PBUH) born?", null, "Ramadan", "Rabi-ul-Awwal", "Dhul-Hijjah", "Muharram", "B"))
            list.add(QuizQuestion(3, "What was the name of Prophet Muhammad's (PBUH) father?", null, "Hazrat Abu Talib", "Hazrat Abdullah", "Hazrat Abdul Muttalib", "Hazrat Hamza", "B"))
            list.add(QuizQuestion(4, "What was the name of Prophet Muhammad's (PBUH) mother?", null, "Hazrat Aminah", "Hazrat Halimah", "Hazrat Khadijah", "Hazrat Fatimah", "A"))
            list.add(QuizQuestion(5, "Who fostered the Prophet Muhammad (PBUH) during his infancy from the countryside?", null, "Hazrat Aminah", "Hazrat Halimah Saadia", "Hazrat Safiyyah", "Hazrat Fatima", "B"))
            list.add(QuizQuestion(6, "Who took custody of the Prophet (PBUH) after his mother passed away?", null, "His uncle Abu Talib", "His grandfather Abdul Muttalib", "His cousin Hazrat Ali", "Abul Lahab", "B"))
            list.add(QuizQuestion(7, "Who took custody after grandfather Abdul Muttalib passed away?", null, "His uncle Abu Talib", "Abu Jahl", "Hazrat Abbas", "Hazrat Abu Bakr", "A"))
            list.add(QuizQuestion(8, "Who was the first wife of Prophet Muhammad (PBUH)?", null, "Hazrat Aisha (R.A.)", "Hazrat Khadijah (R.A.)", "Hazrat Hafsah (R.A.)", "Hazrat Sawdah (R.A.)", "B"))
            list.add(QuizQuestion(9, "At what age did Hazrat Muhammad (PBUH) receive his first prophethood revelation?", null, "25", "30", "40", "50", "C"))
            list.add(QuizQuestion(10, "In which cave did the Prophet (PBUH) receive the first Quranic revelation?", null, "Cave of Saur", "Cave of Hira", "Cave of Kahf", "Cave of Uhud", "B"))
            
            // Extra Seerat
            list.add(QuizQuestion(11, "What are the first five verses revealed to Prophet Muhammad (PBUH)?", null, "Surah Al-Fatiha", "Surah Al-Alaq (Iqra)", "Surah Al-Ikhlas", "Surah Yaseen", "B"))
            list.add(QuizQuestion(12, "Who was the first woman to accept Islam?", null, "Hazrat Fatima (R.A.)", "Hazrat Khadijah (R.A.)", "Hazrat Aisha (R.A.)", "Hazrat Aminah", "B"))
            list.add(QuizQuestion(13, "Who was the first adult male to accept Islam?", null, "Hazrat Umar (R.A.)", "Hazrat Abu Bakr (R.A.)", "Hazrat Ali (R.A.)", "Hazrat Uthman (R.A.)", "B"))
            list.add(QuizQuestion(14, "Who was the first child to accept Islam?", null, "Hazrat Ali (R.A.)", "Hazrat Bilal (R.A.)", "Hazrat Zaid (R.A.)", "Hazrat Hasan (R.A.)", "A"))
            list.add(QuizQuestion(15, "The migration of Muslims from Makkah to Madinah is called:", null, "Hajj", "Jihad", "Hijrah", "Umrah", "C"))
            list.add(QuizQuestion(16, "What was the old name of Madinah city?", null, "Taif", "Yathrib", "Quba", "Riyadh", "B"))
            list.add(QuizQuestion(17, "What was the name of the Prophet's camel upon Hijrah arrival in Madinah?", null, "Al-Qaswa", "Al-Baiza", "Duldul", "Burraq", "A"))
            list.add(QuizQuestion(18, "Which first mosque was built by the Prophet (PBUH) near Madinah?", null, "Masjid-al-Haram", "Masjid-e-Quba", "Masjid-e-Nabwi", "Masjid-al-Aqsa", "B"))
            list.add(QuizQuestion(19, "In which year of Hijri did the historic Battle of Badr take place?", null, "1 A.H.", "2 A.H.", "3 A.H.", "5 A.H.", "B"))
            list.add(QuizQuestion(20, "What is the Arabic word for the biography of Prophet Muhammad (PBUH)?", null, "Hadith", "Fiqh", "Seerat", "Tafseer", "C"))
        }
        return list
    }

    private fun getGkQuestions(chapterNumber: String, difficulty: String): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()
        if (chapterNumber == "01") {
            // Space & Solar System
            list.add(QuizQuestion(1, "Which star sits at the center of our solar system?", null, "Polaris", "The Sun", "Sirius", "Alpha Centauri", "B"))
            list.add(QuizQuestion(2, "What is the closest planet to the Sun?", null, "Venus", "Mercury", "Earth", "Mars", "B"))
            list.add(QuizQuestion(3, "Which planet is known as the Red Planet due to iron oxide rust?", null, "Jupiter", "Venus", "Mars", "Saturn", "C"))
            list.add(QuizQuestion(4, "What is the largest planet in our solar system?", null, "Earth", "Neptune", "Jupiter", "Saturn", "C"))
            list.add(QuizQuestion(5, "How many planets are there in our Solar System?", null, "7", "8", "9", "10", "B"))
            list.add(QuizQuestion(6, "Which planet is famous for its elaborate icy ring system?", null, "Uranus", "Saturn", "Jupiter", "Mars", "B"))
            list.add(QuizQuestion(7, "What is the natural satellite of the Earth called?", null, "Halley", "The Moon", "Phobos", "Titan", "B"))
            list.add(QuizQuestion(8, "How many days does the Earth take to orbit around the Sun?", null, "30 days", "365.25 days", "24 days", "100 days", "B"))
            list.add(QuizQuestion(9, "Which galaxy is home to our solar system?", null, "Andromeda", "Milky Way", "Triangulum", "Sombrero", "B"))
            list.add(QuizQuestion(10, "Which planet is the hottest because of its thick greenhouse gas atmosphere?", null, "Mercury", "Venus", "Mars", "Uranus", "B"))
            
            // Extra Space
            list.add(QuizQuestion(11, "Which planet is called the Blue Planet?", null, "Earth", "Uranus", "Neptune", "Venus", "A"))
            list.add(QuizQuestion(12, "What is the coldest planet in our solar system?", null, "Mars", "Saturn", "Uranus", "Neptune", "C"))
            list.add(QuizQuestion(13, "Pluto is now officially classified as a:", null, "Gas Giant", "Dwarf Planet", "Asteroid", "Comet", "B"))
            list.add(QuizQuestion(14, "Which planet has a giant cyclone spot called 'The Great Red Spot'?", null, "Saturn", "Mars", "Jupiter", "Neptune", "C"))
            list.add(QuizQuestion(15, "What causes day and night on Earth?", null, "Earth's orbit around the Sun", "Earth's rotation on its own axis", "Moon's shadow", "Sun's movement", "B"))
            list.add(QuizQuestion(16, "What gas is most abundant in the atmosphere of Earth?", null, "Oxygen", "Carbon Dioxide", "Nitrogen", "Argon", "C"))
            list.add(QuizQuestion(17, "How long does light from the Sun take to reach Earth?", null, "8 seconds", "8 minutes", "8 hours", "1 day", "B"))
            list.add(QuizQuestion(18, "Who was the first person to step onto the moon?", null, "Yuri Gagarin", "Neil Armstrong", "Buzz Aldrin", "John Glenn", "B"))
            list.add(QuizQuestion(19, "Which force pulls everything towards the center of the Earth?", null, "Magnetic force", "Frictional force", "Gravitational force", "Centrifugal force", "C"))
            list.add(QuizQuestion(20, "What is a massive chunk of ice and dust orbiting the Sun with a glowing tail?", null, "Asteroid", "Meteor", "Comet", "Black Hole", "C"))
        } else {
            // Earth Geography & Indus Valley
            list.add(QuizQuestion(1, "Where is the ancient archaeological site 'Mohenjo-daro' located?", null, "India", "Iran", "Pakistan", "Egypt", "C"))
            list.add(QuizQuestion(2, "Mohenjo-daro represents which ancient world civilization?", null, "Mesopotamian", "Egyptian", "Indus Valley Civilization", "Chinese Dynasty", "C"))
            list.add(QuizQuestion(3, "How many years ago did the Indus Valley Civilization bloom?", null, "1,000", "2,000", "5,000+", "10,000", "C"))
            list.add(QuizQuestion(4, "Which premium river nourished the Indus Valley Civilization cities?", null, "Ganges River", "Indus River", "Nile River", "Euphrates River", "B"))
            list.add(QuizQuestion(5, "What is the largest ocean on planet Earth?", null, "Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean", "C"))
            list.add(QuizQuestion(6, "Which is the tallest mountain on earth?", null, "K2", "Mount Everest", "Nanga Parbat", "Kilimanjaro", "B"))
            list.add(QuizQuestion(7, "How many continents are there on Earth?", null, "5", "6", "7", "8", "C"))
            list.add(QuizQuestion(8, "Which continent is the largest in terms of area?", null, "Africa", "Asia", "North America", "Europe", "B"))
            list.add(QuizQuestion(9, "What unique archaeological feature was discovered in Mohenjo-daro streets?", null, "Large war catapults", "Underground brick drainage channels", "Gold statues of kings", "Iron pyramids", "B"))
            list.add(QuizQuestion(10, "Which desert is located in East Punjab, Pakistan?", null, "Sahara", "Thar / Cholistan", "Gobi", "Kalahari", "B"))
            
            // Extra Earth Geography Questions
            list.add(QuizQuestion(11, "What is the longest river in the world?", null, "Amazon River", "Nile River", "Yangtze River", "Indus River", "B"))
            list.add(QuizQuestion(12, "Which country has the largest population in the world?", null, "India", "USA", "China", "Russia", "A"))
            list.add(QuizQuestion(13, "What is the capital of Pakistan?", null, "Karachi", "Lahore", "Islamabad", "Peshawar", "C"))
            list.add(QuizQuestion(14, "Which instrument is used to measure earthquakes?", null, "Barometer", "Thermometer", "Seismograph", "Anemometer", "C"))
            list.add(QuizQuestion(15, "What is the smallest continent on Earth?", null, "Europe", "Antarctica", "Australia", "South America", "C"))
            list.add(QuizQuestion(16, "Which country is called the Land of the Rising Sun?", null, "China", "Korea", "Japan", "Thailand", "C"))
            list.add(QuizQuestion(17, "What is the capital city of Punjab province?", null, "Multan", "Faisalabad", "Lahore", "Rawalpindi", "C"))
            list.add(QuizQuestion(18, "Which line divides the Earth into northern and southern hemispheres?", null, "Prime Meridian", "Equator", "Tropic of Cancer", "Tropic of Capricorn", "B"))
            list.add(QuizQuestion(19, "What is the largest hot desert in the world?", null, "Gobi", "Kalahari", "Sahara", "Arabian", "C"))
            list.add(QuizQuestion(20, "What gas do humans breathe in to live?", null, "Carbon Dioxide", "Nitrogen", "Oxygen", "Hydrogen", "C"))
        }
        return list
    }
}
