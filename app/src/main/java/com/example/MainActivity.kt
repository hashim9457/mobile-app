package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("1:123456789012:android:0123456789abcdef")
                    .setApiKey("AIzaSyDummyKeyForFirestoreOfflineWorking")
                    .setProjectId("vidyalay-offline")
                    .build()
                com.google.firebase.FirebaseApp.initializeApp(this, options)
                android.util.Log.d("FirebaseInit", "Firebase manually initialized successfully.")
            }
        } catch (e: Exception) {
            android.util.Log.e("FirebaseInit", "Failed manually initializing FirebaseApp", e)
        }
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigator(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigator(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            is Screen.Splash -> SplashScreen(viewModel)
            is Screen.Onboarding -> OnboardingScreen(viewModel)
            is Screen.Auth -> AuthScreen(viewModel)
            is Screen.MainApp -> MainAppTabsContainer(viewModel)
            is Screen.SubjectDetail -> SubjectDetailScreen(viewModel, screen.subjectId)
            is Screen.Quiz -> QuizScreen(viewModel, screen.subjectId)
            is Screen.QuizResult -> QuizResultsScreen(
                viewModel = viewModel,
                score = screen.score,
                total = screen.total,
                timeMinutes = screen.timeMinutes,
                timeSeconds = screen.timeSeconds
            )
            is Screen.ChapterNotes -> ChapterNotesScreen(
                viewModel = viewModel,
                subjectId = screen.subjectId,
                chapterNumber = screen.chapterNumber,
                chapterTitle = screen.chapterTitle
            )
        }
    }
}

// ==========================================
// 1. SPLASH SCREEN (Vidyalay Offline)
// ==========================================
@Composable
fun SplashScreen(viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1A365D), Color(0xFF002045), Color(0xFF01142A)),
                        center = Offset(size.width / 2f, size.height / 3f),
                        radius = size.maxDimension
                    )
                )
            }
    ) {
        // Futuristic school landscape background mesh layer
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuC6mwKDsqqu5FCQzz4f4ij-rnI6sEnuzO_c1PoaF6MbX2j-ApA9gZQSuPkdYx7VJvwawBLa2BUpdGdUFIxb8kgNzx4Hm71TW1p6paX7Ni72kZ8AvsX2toSzRkfm-0A-gNK5WNssVCnmgjidYzQAohzMLePDFpl28auSrSpaZ8YTFfVp8Ru2Wr0T_XAOCGohk5wPOg9XFruN03AL5PxTxhKsjO8mLlK5QSDij8UcJ7eEbBW7m5nCEIA0Frq1K2y5dlrlQBNJyEZdM3ot",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.25f,
            modifier = Modifier.fillMaxSize()
        )

        // Float Animation for Glass Logo Container
        val infiniteTransition = rememberInfiniteTransition(label = "LogoAnimation")
        val translateY by infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "LogoFloat"
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .offset(y = translateY.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glass Logo Box
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Central Icon
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SecondaryContainer, OnSecondaryContainer)
                            )
                        )
                        .shadow(16.dp, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Graduation Cap Logo",
                        tint = PrimaryColor,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Vidyalay Offline",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.02).sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "EDUCATION WITHOUT INTERNET",
                color = SecondaryContainer,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Ornamental Divider with wifi-off
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Color.White.copy(alpha = 0.4f))
                            )
                        )
                )
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "No-internet",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(16.dp)
                    )
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.White.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                )
            }
        }

        // Action Trigger Button at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { viewModel.navigateTo(Screen.Onboarding) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("enter_classroom_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Enter the Classroom",
                        color = PrimaryColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "VERSION 2.0 • BORDERLESS KNOWLEDGE",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

// ==========================================
// 2. ONBOARDING SCREEN (Learning Anywhere)
// ==========================================
@Composable
fun OnboardingScreen(viewModel: MainViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vidyalay Offline",
                    color = PrimaryColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif
                )
                TextButton(
                    onClick = { viewModel.navigateTo(Screen.Auth) },
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Skip",
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BackgroundColor, Color(0xFFD6E3FF).copy(alpha = 0.5f))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // High-quality rural study landscape illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.25f)
                        .clip(RoundedCornerShape(32.dp))
                        .shadow(12.dp, RoundedCornerShape(32.dp))
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAdsrK8DTovTA6or0isWt4tV6BVrr9SG_azcG9uMwdHgVCuG_f1VAujIz2YDMhKOvPq_CIxoxv4e5773ZJ8GiUO3mHS6M4-pHs4umq_P1FBptryqgrgQXCG_OVG3JF4twT7RCtU7kKrpKiLgUpS5pQJKkJz0WyoGX8kj9OAt4BLPY5uCLXLASW_GFHpHamUsnq4QRii_f0_AbDT1RzEoVE50qU6ksBDW2sCGAlxfxRk50Fu5hd_nND2pMFDXAygF_qWmUULVLF7ccWw",
                        contentDescription = "Student under banyan tree studying offline",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Glassmorphic Content Card
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(32.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Learning Anywhere",
                            color = PrimaryColor,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Access all your Punjab Board lessons even without internet. Download once, study forever.",
                            color = OnSurfaceVariant,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // Page Indicators
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(OutlineVariant)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(OutlineVariant)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Forward Next Button
                        Button(
                            onClick = { viewModel.navigateTo(Screen.Auth) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(20.dp))
                                .testTag("next_onboarding_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Next",
                                    color = PrimaryColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = PrimaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Footer decoration
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.OfflineBolt,
                        contentDescription = null,
                        tint = PrimaryColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Made for Punjab's Future Leaders",
                        color = PrimaryColor.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// ==========================================
// 3. MAIN TAB LAYOUT CONTAINER
// ==========================================
@Composable
fun MainAppTabsContainer(viewModel: MainViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.Home -> HomeScreen(viewModel)
                AppTab.Lessons -> LessonsTabScreen(viewModel)
                AppTab.Badges -> BadgesTabScreen(viewModel)
                AppTab.Profile -> ProfileTabScreen(viewModel)
            }
        }
    }
}

@Composable
fun CustomBottomNavigationBar(selectedTab: AppTab, onTabSelected: (AppTab) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF4FBF6))
            .drawBehind {
                drawLine(
                    color = Color(0xFFDDE5DE),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .navigationBarsPadding()
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple(AppTab.Home, Icons.Default.Home, "Home"),
                Triple(AppTab.Lessons, Icons.Default.MenuBook, "Lessons"),
                Triple(AppTab.Badges, Icons.Default.MilitaryTech, "Badges"),
                Triple(AppTab.Profile, Icons.Default.Person, "Profile")
            )
            tabs.forEach { (tab, icon, label) ->
                val selected = selectedTab == tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_${tab.name.lowercase()}")
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(tab) }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected) Color(0xFFBCEBCE) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (selected) Color(0xFF002114) else Color(0xFF56625B),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        color = if (selected) Color(0xFF006C4C) else Color(0xFF56625B),
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. HOME SCREEN TAB
// ==========================================
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val selectedClass by viewModel.selectedClass.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val subjects by viewModel.subjects.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Avatar with premium border
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor.copy(alpha = 0.1f))
                            .border(2.dp, PrimaryColor.copy(alpha = 0.2f), CircleShape)
                    ) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCOluBFsPnVjjgBpDMzcahmaRkNh9GSP1ZdOK_0zy-PUGw7HJNE3Y1ZM6IODMw4LvFbujbdfm5IzGNXhQlIw-KOVidh1HmzaIkhF4rwH-Hs9fUQsatUMV_vmWxl6SXKAnaWcbdoEKApN8U3jTHj4fbWOO-lk52JfE4vTP9TSfNWNMKZAdErcl9iUXYlJns57z7eWWTYWQ6yD7YSV8J_FsK-pR6R4z1wKUdBuDP9EKuc-4s8BsZlV9-rlEa_b-UILt6D6Xci3UXloqIb",
                            contentDescription = "Profile Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "EduFuture Punjab",
                            color = PrimaryColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Assalam-o-Alaikum",
                            color = OnSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }

                // Interactive Offline mode status indicator
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { viewModel.toggleOfflineMode() }
                        .background(PrimaryColor.copy(alpha = 0.05f))
                        .border(1.dp, PrimaryColor.copy(alpha = 0.15f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val offlineActive = profile?.offlineMode ?: false
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (offlineActive) Icons.Default.WifiOff else Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = if (offlineActive) Color(0xFFDD8D00) else SecondaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (offlineActive) "Offline" else "Offline Ready",
                            color = PrimaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Streak & Level Card (Geometric Balance Theme)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryColor),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "CURRENT LEVEL",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Class 4 Rising Star",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Mint Streak Counter Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SecondaryColor)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = OnSecondaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${profile?.streakDays ?: 12} Days",
                                    color = OnSecondaryContainer,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Next Milestone: ${profile?.nextMilestone ?: "Bronze Sage"}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${profile?.milestonePercentage ?: 85}%",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Track Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = (profile?.milestonePercentage ?: 85) / 100f)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }
        }

        // Circular Bento Completion Overview (Geometric Balance Theme)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1.2f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "COURSE COMPLETION",
                            color = OnSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom Arc Canvas completion chart
                        Box(
                            modifier = Modifier.size(96.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(96.dp)) {
                                drawCircle(
                                    color = Color(0xFFECEEF0),
                                    radius = size.minDimension / 2f,
                                    style = Stroke(width = 8.dp.toPx())
                                )
                                drawArc(
                                    color = SecondaryColor,
                                    startAngle = -90f,
                                    sweepAngle = 270f, // 75%
                                    useCenter = false,
                                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Text(
                                text = "75%",
                                color = PrimaryColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1.8f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LegendRow(color = SecondaryColor, label = "Mathematics: 90%")
                        LegendRow(color = PrimaryColor, label = "Science: 65%")
                        LegendRow(color = GoldAccent, label = "English: 40%")

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"Success is not final... it is the courage to continue that counts.\"",
                            color = OnSurfaceVariant,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Class Selection Filter
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Select Your Class",
                        color = PrimaryColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Change Region",
                        color = OnPrimaryContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.underline()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Classes Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items((1..5).toList()) { num ->
                        val active = num == selectedClass
                        Box(
                            modifier = Modifier
                                .size(width = 84.dp, height = 96.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .shadow(2.dp, RoundedCornerShape(20.dp))
                                .background(
                                    if (active) PrimaryColor else SurfaceContainerLow
                                )
                                .border(
                                    1.dp,
                                    if (active) Color.Transparent else OutlineVariant,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.selectClass(num) }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = when (num) {
                                        1 -> Icons.Outlined.ChildCare
                                        2 -> Icons.Outlined.AutoStories
                                        3 -> Icons.Outlined.Science
                                        4 -> Icons.Default.Star
                                        else -> Icons.Outlined.Architecture
                                    },
                                    contentDescription = null,
                                    tint = if (active) Color.White else when (num) {
                                        1 -> Color(0xFF006C4C)
                                        2 -> Color(0xFFC2410C)
                                        3 -> Color(0xFF006C4C)
                                        4 -> GoldAccent
                                        else -> Color(0xFFBA1A1A)
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Class $num",
                                    color = if (active) Color.White else PrimaryColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Resume Learning Section
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "Resume Learning",
                    color = PrimaryColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Horizontal Course Cards
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(subjects) { subject ->
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .width(280.dp)
                                .height(160.dp)
                                .shadow(4.dp, RoundedCornerShape(24.dp))
                                .clickable {
                                    viewModel.setSubjectId(subject.id)
                                    viewModel.navigateTo(Screen.SubjectDetail(subject.id))
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = subject.imageUrl,
                                    contentDescription = subject.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Gradient Overlay for text contrast
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    PrimaryColor.copy(alpha = 0.85f)
                                                )
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = subject.category.uppercase(),
                                        color = SecondaryContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = subject.title,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                // Play icon badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(16.dp)
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Resume",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Beautiful Subjects Grid Section for Selected Class
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Class $selectedClass Subjects",
                color = PrimaryColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        val chunkedSubjects = subjects.chunked(2)
        items(chunkedSubjects) { rowSubjects ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowSubjects.forEach { subj ->
                    val iconVector = when (subj.iconName) {
                        "translate" -> Icons.Outlined.Translate
                        "edit_note" -> Icons.Outlined.EditNote
                        "star" -> Icons.Outlined.Star
                        "public" -> Icons.Outlined.Public
                        else -> Icons.Outlined.Calculate
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                        border = BorderStroke(1.dp, OutlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .shadow(3.dp, RoundedCornerShape(20.dp))
                            .clickable {
                                viewModel.setSubjectId(subj.id)
                                viewModel.navigateTo(Screen.SubjectDetail(subj.id))
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryColor.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = subj.title,
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                
                                if (subj.isOfflineReady) {
                                    Icon(
                                        imageVector = Icons.Default.OfflinePin,
                                        contentDescription = "Offline Available",
                                        tint = SecondaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = subj.title,
                                color = PrimaryColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = subj.category,
                                color = OnSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Progress track
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryColor.copy(alpha = 0.12f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = (subj.progress / 100f).coerceIn(0f, 1f))
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(PrimaryColor)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${subj.progress}%",
                                    color = PrimaryColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                if (rowSubjects.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun LegendRow(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = PrimaryColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Modifier Extension for cleaner underline style
fun Modifier.underline(): Modifier = drawBehind {
    val strokeWidth = 1.dp.toPx()
    val y = size.height - strokeWidth
    drawLine(
        color = PrimaryColor,
        start = Offset(0f, y),
        end = Offset(size.width, y),
        strokeWidth = strokeWidth
    )
}

// ==========================================
// 5. LESSONS TAB SCREEN
// ==========================================
@Composable
fun LessonsTabScreen(viewModel: MainViewModel) {
    val subjects by viewModel.subjects.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        item {
            Text(
                text = "Your Journey Today",
                color = PrimaryColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Step into the future of learning. Every subject is a new pathway to leadership. Complete your lessons to secure golden badges.",
                color = OnSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Subjects micro-lessons grid
        items(subjects) { subject ->
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp))
                    .clickable {
                        viewModel.setSubjectId(subject.id)
                        viewModel.navigateTo(Screen.SubjectDetail(subject.id))
                    }
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        AsyncImage(
                            model = subject.imageUrl,
                            contentDescription = subject.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (subject.isOfflineReady) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .clip(CircleShape)
                                    .background(SecondaryContainer.copy(alpha = 0.9f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.OfflinePin,
                                        contentDescription = null,
                                        tint = OnSecondaryContainer,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Offline Ready",
                                        color = OnSecondaryContainer,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Content text
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = subject.category.uppercase(),
                                color = OnSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = subject.title,
                                color = PrimaryColor,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Horizontal progress slider
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Progress: ",
                                    color = OnSurfaceVariant,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${subject.progress}%",
                                    color = PrimaryColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainer)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = subject.progress / 100f)
                                        .fillMaxHeight()
                                        .clip(CircleShape)
                                        .background(PrimaryColor)
                                )
                            }
                        }

                        // Subject Badge icon container
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PrimaryColor.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (subject.iconName) {
                                    "translate" -> Icons.Outlined.Translate
                                    "edit_note" -> Icons.Outlined.EditNote
                                    "star" -> Icons.Outlined.Star
                                    "public" -> Icons.Outlined.Public
                                    else -> Icons.Outlined.Calculate
                                },
                                contentDescription = null,
                                tint = PrimaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Science Lab Coming Soon Placeholder block
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundColor),
                border = BorderStroke(2.dp, PrimaryColor.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = PrimaryColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Science Lab",
                        color = PrimaryColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Coming Next Month",
                        color = OnSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// 6. BADGES TAB SCREEN
// ==========================================
@Composable
fun BadgesTabScreen(viewModel: MainViewModel) {
    val badges by viewModel.badges.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Hall of Excellence Hero
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HALL OF EXCELLENCE",
                        color = SecondaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your Journey to Greatness",
                        color = PrimaryColor,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Every lesson mastered is a step toward leadership. Collect badges to unlock new opportunities and showcase your dedication to excellence.",
                        color = OnSurfaceVariant,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Floating rotating badge animation
                    val infiniteTransition = rememberInfiniteTransition(label = "BadgeSpin")
                    val rotationAngle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(15000, easing = LinearEasing)
                        ),
                        label = "BadgeSpinAngle"
                    )

                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .drawBehind {
                                drawCircle(
                                    color = GoldAccent.copy(alpha = 0.15f),
                                    radius = size.minDimension / 1.8f
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer rotating dashed ring
                        Canvas(modifier = Modifier.size(160.dp).rotate(rotationAngle)) {
                            drawCircle(
                                color = PrimaryColor.copy(alpha = 0.2f),
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                        floatArrayOf(15f, 15f), 0f
                                    )
                                )
                            )
                        }

                        // Central Gold Badge Card
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(GoldAccent, GoldLight, GoldDark)
                                    )
                                )
                                .shadow(8.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PrimaryColor.copy(alpha = 0.05f))
                            .border(1.dp, PrimaryColor.copy(alpha = 0.1f), CircleShape)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Newest Achievement!",
                            color = PrimaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Champions featured badge (District Vanguard)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Featured Achievements",
                color = PrimaryColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // District Vanguard and Stats bento grid items
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val featuredBadge = badges.find { it.id == "district_vanguard" }
                if (featuredBadge != null) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                        border = BorderStroke(1.dp, OutlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(24.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(PrimaryColor, OnPrimaryContainer)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = featuredBadge.title,
                                    color = PrimaryColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = featuredBadge.description,
                                    color = OnSurfaceVariant,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(SecondaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = featuredBadge.unlockedAt ?: "",
                                            color = OnSecondaryContainer,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = featuredBadge.studentsCount,
                                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Stats Bento Card
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val unlockedCount = badges.count { it.status == "UNLOCKED" }
                        Text(
                            text = "$unlockedCount / ${badges.size}",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "BADGES COLLECTED",
                            color = SecondaryContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = unlockedCount.toFloat() / badges.size)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(SecondaryContainer)
                                )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Complete 3 more lessons to unlock 'Silver Scientist'",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Grid of Badges
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Badges Gallery",
                color = PrimaryColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Grid display of remaining gallery badges
        item {
            val galleryBadges = badges.filter { it.id != "district_vanguard" }
            FlowRowLayout(
                spacingDp = 10.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                galleryBadges.forEach { badge ->
                    val isUnlocked = badge.status == "UNLOCKED"
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) SurfaceContainerLow else SurfaceContainer.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isUnlocked) OutlineVariant else OutlineVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .width(106.dp)
                            .shadow(1.dp, RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isUnlocked) {
                                            when (badge.tier.uppercase()) {
                                                "GOLD TIER" -> Brush.radialGradient(listOf(GoldLight, GoldAccent))
                                                "SILVER TIER" -> Brush.radialGradient(listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8)))
                                                else -> Brush.radialGradient(listOf(Color(0xFFFDBA74), Color(0xFFB45309)))
                                            }
                                        } else {
                                            Brush.radialGradient(listOf(Color(0xFFECEEF0), Color(0xFFE6E8EA)))
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isUnlocked) {
                                        when (badge.iconName) {
                                            "star" -> Icons.Default.Star
                                            "history_edu" -> Icons.Default.HistoryEdu
                                            else -> Icons.Default.Eco
                                        }
                                    } else {
                                        Icons.Default.Lock
                                    },
                                    contentDescription = null,
                                    tint = if (isUnlocked) Color.White else OnSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = badge.title,
                                color = if (isUnlocked) PrimaryColor else OnSurfaceVariant.copy(alpha = 0.4f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = badge.tier,
                                color = if (isUnlocked) GoldDark else OnSurfaceVariant.copy(alpha = 0.3f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// Simple FlowRow helper to align badges in columns responsive dynamically
@Composable
fun FlowRowLayout(
    spacingDp: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val spacing = spacingDp.roundToPx()
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(looseConstraints) }

        val containerWidth = constraints.maxWidth
        var totalHeight = 0
        var currentX = 0
        var currentY = 0
        var currentRowHeight = 0

        placeables.forEach { placeable ->
            if (currentX + placeable.width > containerWidth && currentX > 0) {
                currentX = 0
                currentY += currentRowHeight + spacing
                currentRowHeight = 0
            }
            currentX += placeable.width + spacing
            currentRowHeight = maxOf(currentRowHeight, placeable.height)
        }
        totalHeight = currentY + currentRowHeight

        layout(
            width = containerWidth,
            height = totalHeight.coerceAtLeast(0)
        ) {
            var xPos = 0
            var yPos = 0
            var rowHeight = 0
            placeables.forEach { placeable ->
                if (xPos + placeable.width > containerWidth && xPos > 0) {
                    xPos = 0
                    yPos += rowHeight + spacing
                    rowHeight = 0
                }
                placeable.placeRelative(xPos, yPos)
                xPos += placeable.width + spacing
                rowHeight = maxOf(rowHeight, placeable.height)
            }
        }
    }
}

// ==========================================
// 7. PROFILE TAB SCREEN
// ==========================================
@Composable
fun ProfileTabScreen(viewModel: MainViewModel) {
    val profile by viewModel.profile.collectAsState()
    val quizAttempts by viewModel.quizAttempts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        item {
            Text(
                text = "Leader Profile",
                color = PrimaryColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Leader card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(3.dp, SecondaryColor, CircleShape)
                    ) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCOluBFsPnVjjgBpDMzcahmaRkNh9GSP1ZdOK_0zy-PUGw7HJNE3Y1ZM6IODMw4LvFbujbdfm5IzGNXhQlIw-KOVidh1HmzaIkhF4rwH-Hs9fUQsatUMV_vmWxl6SXKAnaWcbdoEKApN8U3jTHj4fbWOO-lk52JfE4vTP9TSfNWNMKZAdErcl9iUXYlJns57z7eWWTYWQ6yD7YSV8J_FsK-pR6R4z1wKUdBuDP9EKuc-4s8BsZlV9-rlEa_b-UILt6D6Xci3UXloqIb",
                            contentDescription = "Leader Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = profile?.studentName ?: "Ali Hashim",
                            color = PrimaryColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Class ${profile?.classSelected ?: 4} • ${profile?.villageName ?: "Punjab Region"}",
                            color = OnBackground.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SecondaryColor)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STUDENT RANK #4",
                                color = OnSecondaryContainer,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // Phase 7 Student Progress System — Interactive Educational Analytics & Progress Dashboard
        item {
            val subjects by viewModel.subjects.collectAsState()
            val totalChaptersCount = subjects.size * 4
            val completedChapters = subjects.map { (it.progress * 4) / 100 }.sum()
            val overallClassProgress = if (subjects.isNotEmpty()) {
                subjects.map { it.progress }.average().toInt()
            } else {
                profile?.courseCompletion ?: 0
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Class Progress & Analytics",
                    color = PrimaryColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Class ${profile?.classSelected ?: 4}",
                    color = SecondaryColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(24.dp))
                    .testTag("progress_analytics_dashboard_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Part 1: Class Completion Gauge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Class Completion Rate",
                                color = PrimaryColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Overall academic journey in current syllabus",
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "$completedChapters Completed Chapters",
                                color = SecondaryColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Weekly Study Target: 80% Min MCQ Pass Rate",
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        // Circular Canvas Donut Chart for Progress Visualizer
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(90.dp)
                        ) {
                            Canvas(modifier = Modifier.size(80.dp)) {
                                val strokeWidthValue = 8.dp.toPx()
                                // Background Arc
                                drawArc(
                                    color = OutlineVariant.copy(alpha = 0.5f),
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidthValue, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                )
                                // Progress Arc
                                drawArc(
                                    color = SecondaryColor,
                                    startAngle = -90f,
                                    sweepAngle = (overallClassProgress.toFloat() / 100f) * 360f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidthValue, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$overallClassProgress%",
                                    color = PrimaryColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Done",
                                    color = OnSurfaceVariant,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = OutlineVariant, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Part 2: Subjects Breakdown with Average Quiz Scores
                    Text(
                        text = "Subject Performance & Quiz Scores",
                        color = PrimaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (subjects.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No subjects synchronized yet. Go below to sync offline cache.",
                                color = OnSurfaceVariant,
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    } else {
                        subjects.forEach { subject ->
                            val subjectAttempts = quizAttempts.filter { it.subjectId == subject.id }
                            val averageScore = if (subjectAttempts.isNotEmpty()) {
                                subjectAttempts.map { it.percentage }.average().toInt()
                            } else {
                                0
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(SecondaryColor.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when (subject.iconName) {
                                                    "calculate", "star" -> Icons.Default.Calculate
                                                    "science" -> Icons.Default.Science
                                                    "menu_book", "auto_stories", "book" -> Icons.Default.MenuBook
                                                    else -> Icons.Default.MenuBook
                                                },
                                                contentDescription = null,
                                                tint = SecondaryColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = subject.title,
                                            color = PrimaryColor,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (subjectAttempts.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (averageScore >= 80) SecondaryColor.copy(alpha = 0.12f)
                                                        else GoldAccent.copy(alpha = 0.12f)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "$averageScore% Avg (${subjectAttempts.size} Quiz)",
                                                    color = if (averageScore >= 80) SecondaryColor else GoldDark,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = "No quizes taken",
                                                color = OnSurfaceVariant.copy(alpha = 0.6f),
                                                fontSize = 11.sp,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${subject.progress}% Completed",
                                            color = PrimaryColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { subject.progress.toFloat() / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(CircleShape),
                                    color = if (subject.progress == 100) SecondaryColor else PrimaryColor,
                                    trackColor = OutlineVariant.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Settings items
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Preferences",
                color = PrimaryColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Toggle Offline Switch setting
        item {
            val offlineCount = profile?.offlineMode ?: false
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = GoldDark
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Simulate Offline Mode",
                                color = PrimaryColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Forces app to run in fully offline cached view.",
                                color = OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = offlineCount,
                        onCheckedChange = { viewModel.toggleOfflineMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SecondaryColor
                        )
                    )
                }
            }
        }

        // Full Offline Database Sync Control
        item {
            val syncing by viewModel.syncing.collectAsState()
            val syncStatus by viewModel.syncStatus.collectAsState()
            val syncProgress by viewModel.syncProgress.collectAsState()
            val selectedClass by viewModel.selectedClass.collectAsState()

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("offline_sync_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SecondaryColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = SecondaryColor
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Offline Local Sync",
                                    color = PrimaryColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Download Class $selectedClass notes, chapters & quizzes.",
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.triggerFullOfflineSync() },
                            enabled = !syncing,
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("sync_offline_now_btn")
                        ) {
                            if (syncing) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Sync Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (syncing || syncStatus.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = syncStatus,
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${(syncProgress * 100).toInt()}%",
                                    color = PrimaryColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { syncProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = SecondaryColor,
                                trackColor = OutlineVariant
                            )
                        }
                    }
                }
            }
        }

        // Quiz score profile stat card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SecondaryColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = SecondaryColor
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Algebra Ace Streak",
                            color = PrimaryColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${profile?.streakDays ?: 12} Day learning milestones achieved",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Display Recent Quiz Attempts
        if (quizAttempts.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Quiz Scores",
                        color = PrimaryColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${quizAttempts.size} Total",
                        color = OnSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(quizAttempts.reversed().take(5)) { attempt ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    border = BorderStroke(1.dp, OutlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (attempt.percentage >= 80.0) SecondaryColor.copy(alpha = 0.1f)
                                        else GoldAccent.copy(alpha = 0.1f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${attempt.score}/${attempt.totalQuestions}",
                                    color = if (attempt.percentage >= 80.0) SecondaryColor else GoldDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${attempt.subjectId.replaceFirstChar { it.uppercase() }} Ch ${attempt.chapterNumber}",
                                    color = PrimaryColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${attempt.difficulty} • ${(attempt.percentage).toInt()}% Correct",
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Sync Status Pill
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (attempt.isSyncedToFirebase) SecondaryContainer.copy(alpha = 0.15f)
                                    else SurfaceContainer
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (attempt.isSyncedToFirebase) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = if (attempt.isSyncedToFirebase) OnSecondaryContainer else OnSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (attempt.isSyncedToFirebase) "Cloud Sync" else "Offline",
                                    color = if (attempt.isSyncedToFirebase) OnSecondaryContainer else OnSurfaceVariant,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Log out account card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                border = BorderStroke(1.dp, OutlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { viewModel.logoutStudent() }
                    .testTag("logout_btn")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Log Out Icon",
                            tint = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Sign Out / Switch Profile",
                            color = Color.Red,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Logs out of ${profile?.studentName ?: "this profile"} securely.",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. SUBJECT DETAIL MODULE SCREEN
// ==========================================
@Composable
fun SubjectDetailScreen(viewModel: MainViewModel, subjectId: String) {
    val subjects by viewModel.subjects.collectAsState()
    val subject = subjects.find { it.id == subjectId }
    val chapters by viewModel.selectedSubjectChapters.collectAsState()

    var showQuizDialog by remember { mutableStateOf(false) }
    var quizDialogChapterNum by remember { mutableStateOf("01") }
    var quizDialogChapterTitle by remember { mutableStateOf("General Assessment") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.MainApp) },
                        modifier = Modifier.testTag("back_to_tabs_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryColor
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = subject?.title ?: "Subject Detail",
                        color = PrimaryColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Cloud Status
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SecondaryContainer.copy(alpha = 0.3f))
                        .border(1.dp, SecondaryColor.copy(alpha = 0.1f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = OnSecondaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Offline Ready",
                            color = OnSecondaryContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundColor)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Subject header image
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .shadow(6.dp, RoundedCornerShape(28.dp))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAXnxa5Vgb2OPpSlKBYCYBqeUYb3LIk9B2pKQL-yWCbFBCD6cQBiwc3aUwluJlhNlncAYcFAF_gwC7i1FVAA-ps8JX7-FyZCzotQieOvvCECdZ9H0d6DXuUPtPmu6IwRe-UwCR0HaJPGoGXlOwd8SrOng1hV6fqtrH7dmo-RYtGxHRFp7W6zRoz2wJKys_IgR_k4rtlOp6hoRVKQlZAQ3Oo2hkpFXwzUGyKHyUcl5gb4M1erB1UAX9mxbFT4-Igd0gzXdMhXN-ZZO9S",
                            contentDescription = "Abstract Mathematics formula math structure",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, PrimaryColor.copy(alpha = 0.85f))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Standard XII".uppercase(),
                                color = GoldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Mastering Calculus & Geometry",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(0.9f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(0.7f)
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.65f)
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(SecondaryContainer)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "65% Done",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Concept Notes & Smart Quiz Bento layout
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Concept Notes Card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                        border = BorderStroke(1.dp, OutlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .height(180.dp)
                            .shadow(2.dp, RoundedCornerShape(24.dp))
                            .clickable {
                                val firstCh = chapters.firstOrNull()
                                val chNum = firstCh?.chapterNumber ?: "01"
                                val chTitle = firstCh?.chapterTitle ?: "Introduction"
                                viewModel.selectChapterNotes(subjectId, chNum, chTitle)
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimaryColor.copy(alpha = 0.05f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Description,
                                        contentDescription = null,
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Concept Notes",
                                    color = PrimaryColor,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Detailed, visual summaries.",
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }

                            Text(
                                text = "View 24 Modules >",
                                color = PrimaryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Smart Quiz Card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryColor),
                        modifier = Modifier
                            .weight(1f)
                            .height(180.dp)
                            .shadow(2.dp, RoundedCornerShape(24.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(GoldLight, GoldAccent)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Smart Quiz",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Adaptive assessment.",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }

                            Button(
                                onClick = {
                                    quizDialogChapterNum = "01"
                                    quizDialogChapterTitle = "General Assessment"
                                    showQuizDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .testTag("start_quiz_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Start",
                                        color = PrimaryColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.OfflineBolt,
                                        contentDescription = null,
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Chapters title
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Curriculum Chapters",
                        color = PrimaryColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "12 Chapters Total",
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Chapters List representation
            items(chapters) { ch ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(
                        1.dp,
                        when (ch.status) {
                            "Completed" -> SecondaryColor.copy(alpha = 0.15f)
                            "In Progress" -> PrimaryColor.copy(alpha = 0.15f)
                            else -> Color.Transparent
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            viewModel.selectChapterNotes(subjectId, ch.chapterNumber, ch.chapterTitle)
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ch.chapterNumber,
                                    color = PrimaryColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = ch.chapterTitle,
                                    color = if (ch.status == "Locked") OnSurfaceVariant.copy(alpha = 0.5f) else PrimaryColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${ch.status} • ${ch.lessonsCount} Lessons",
                                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (ch.status != "Locked") {
                                IconButton(
                                    onClick = {
                                        quizDialogChapterNum = ch.chapterNumber
                                        quizDialogChapterTitle = ch.chapterTitle
                                        showQuizDialog = true
                                    },
                                    modifier = Modifier.testTag("chapter_quiz_choose_level_btn_${ch.chapterNumber}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = "Take Quiz",
                                        tint = SecondaryColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Icon(
                                imageVector = when (ch.status) {
                                    "Completed" -> Icons.Default.CheckCircle
                                    "In Progress" -> Icons.Default.PlayCircleFilled
                                    else -> Icons.Default.Lock
                                },
                                contentDescription = ch.status,
                                tint = when (ch.status) {
                                    "Completed" -> SecondaryColor
                                    "In Progress" -> PrimaryColor
                                    else -> OnSurfaceVariant.copy(alpha = 0.3f)
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        if (showQuizDialog) {
            AlertDialog(
                onDismissRequest = { showQuizDialog = false },
                title = {
                    Text(
                        text = "Select Quiz Difficulty",
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Chapter $quizDialogChapterNum: $quizDialogChapterTitle",
                            color = OnSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Easy Difficulty Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.startQuiz(subjectId, quizDialogChapterNum, quizDialogChapterTitle, "Easy")
                                    showQuizDialog = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SecondaryColor.copy(alpha = 0.05f)),
                            border = BorderStroke(1.dp, SecondaryColor.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SecondaryColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("E", color = SecondaryColor, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Easy Level", fontWeight = FontWeight.Bold, color = PrimaryColor, fontSize = 14.sp)
                                    Text("10 MCQs • 30s per question", color = OnSurfaceVariant, fontSize = 11.sp)
                                }
                            }
                        }

                        // Medium Difficulty Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.startQuiz(subjectId, quizDialogChapterNum, quizDialogChapterTitle, "Medium")
                                    showQuizDialog = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryColor.copy(alpha = 0.05f)),
                            border = BorderStroke(1.dp, PrimaryColor.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("M", color = PrimaryColor, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Medium Level", fontWeight = FontWeight.Bold, color = PrimaryColor, fontSize = 14.sp)
                                    Text("15 MCQs • 30s per question", color = OnSurfaceVariant, fontSize = 11.sp)
                                }
                            }
                        }

                        // Hard Difficulty Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.startQuiz(subjectId, quizDialogChapterNum, quizDialogChapterTitle, "Hard")
                                    showQuizDialog = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, GoldAccent)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(GoldDark.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("H", color = GoldDark, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Hard Level", fontWeight = FontWeight.Bold, color = PrimaryColor, fontSize = 14.sp)
                                    Text("20 MCQs • 30s per question", color = OnSurfaceVariant, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showQuizDialog = false }) {
                        Text("Cancel", color = PrimaryColor)
                    }
                }
            )
        }
    }
}

// ==========================================
// 9. RE-USABLE INTERACTIVE QUIZ PAGE
// ==========================================
@Composable
fun QuizScreen(viewModel: MainViewModel, subjectId: String) {
    val quizQuestions by viewModel.quizQuestions.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val answers by viewModel.selectedAnswers.collectAsState()
    val activeQuestion = quizQuestions.getOrNull(currentQuestionIndex)
    val totalCount = quizQuestions.size

    val quizSubjectId by viewModel.quizSubjectId.collectAsState()
    val quizChapterNumber by viewModel.quizChapterNumber.collectAsState()
    val quizChapterTitle by viewModel.quizChapterTitle.collectAsState()
    val quizDifficulty by viewModel.quizDifficulty.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val subject = subjects.find { it.id == quizSubjectId }
    val subjectTitle = subject?.title ?: "Subject"

    if (activeQuestion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryColor)
        }
        return
    }

    // 30 seconds timer per question with dynamic reset on index change
    var timeRemainingSeconds by remember(currentQuestionIndex) { mutableStateOf(30) }
    var totalTimeSpentSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(currentQuestionIndex) {
        while (timeRemainingSeconds > 0) {
            kotlinx.coroutines.delay(1000)
            timeRemainingSeconds--
            totalTimeSpentSeconds++
        }
        // Auto-advance or submit when per-question time limit expires
        if (currentQuestionIndex < totalCount - 1) {
            viewModel.nextQuestion(totalTimeSpentSeconds)
        } else {
            viewModel.submitQuiz(totalTimeSpentSeconds)
        }
    }

    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.SubjectDetail(subjectId)) },
                        modifier = Modifier.testTag("back_from_quiz_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Exit Quiz",
                            tint = PrimaryColor
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$subjectTitle Ch $quizChapterNumber Quiz",
                        color = PrimaryColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Header status pill
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SurfaceContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Offline Mode",
                            color = OnSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Timer + Progress Headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${quizSubjectId.uppercase()} • CHAPTER $quizChapterNumber (${quizDifficulty.uppercase()})",
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of $totalCount",
                        color = PrimaryColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Floating timer box
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, PrimaryColor.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = PrimaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = timeFormatted,
                            color = PrimaryColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Slider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainer)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (currentQuestionIndex + 1).toFloat() / totalCount)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(listOf(SecondaryColor, SecondaryContainer))
                        )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Question Card Glassmorphic container
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(28.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = activeQuestion.question,
                        color = OnBackground,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )

                    activeQuestion.formula?.let { formula ->
                        if (formula.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            // Algebraic Formula styling container
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PrimaryColor.copy(alpha = 0.03f))
                                    .border(1.dp, PrimaryColor.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = formula,
                                    color = PrimaryColor,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Option Buttons A B C D
                    val activeSelected = answers[currentQuestionIndex]

                    QuizOptionRow(
                        letter = "A",
                        text = activeQuestion.optionA,
                        selected = activeSelected == "A",
                        onClick = { viewModel.selectAnswer("A") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    QuizOptionRow(
                        letter = "B",
                        text = activeQuestion.optionB,
                        selected = activeSelected == "B",
                        onClick = { viewModel.selectAnswer("B") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    QuizOptionRow(
                        letter = "C",
                        text = activeQuestion.optionC,
                        selected = activeSelected == "C",
                        onClick = { viewModel.selectAnswer("C") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    QuizOptionRow(
                        letter = "D",
                        text = activeQuestion.optionD,
                        selected = activeSelected == "D",
                        onClick = { viewModel.selectAnswer("D") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation quiz bottom actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.prevQuestion() },
                    enabled = currentQuestionIndex > 0
                ) {
                    Text(
                        text = "Skip",
                        color = if (currentQuestionIndex > 0) PrimaryColor else OnSurfaceVariant.copy(alpha = 0.3f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = { viewModel.nextQuestion(totalTimeSpentSeconds) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
                    modifier = Modifier.testTag("next_question_quiz_btn")
                ) {
                    Text(
                        text = if (currentQuestionIndex == totalCount - 1) "Submit Quiz" else "Next Question",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Potential Badge hints preview card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldAccent.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GoldDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Potential Badge",
                            color = PrimaryColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Complete this quiz with 90% accuracy or higher to earn 'Algebra Ace'",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizOptionRow(
    letter: String,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) PrimaryColor.copy(alpha = 0.05f) else Color.Transparent
            )
            .border(
                2.dp,
                if (selected) PrimaryColor else SurfaceContainer,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) PrimaryColor else SurfaceContainerHigh
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    color = if (selected) Color.White else PrimaryColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                color = PrimaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ==========================================
// 10. QUIZ RESULTS BREAKDOWN SCREEN
// ==========================================
@Composable
fun QuizResultsScreen(
    viewModel: MainViewModel,
    score: Int,
    total: Int,
    timeMinutes: Int,
    timeSeconds: Int
) {
    val scorePercentage = (score * 100) / total

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quiz Results",
                    color = PrimaryColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                IconButton(
                    onClick = { viewModel.navigateTo(Screen.MainApp) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Results",
                        tint = PrimaryColor
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Score Circular Progress
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .shadow(6.dp, CircleShape)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(156.dp)) {
                    drawCircle(
                        color = Color(0xFFECEEF0),
                        radius = size.minDimension / 2f,
                        style = Stroke(width = 12.dp.toPx())
                    )
                    drawArc(
                        color = if (scorePercentage >= 80) SecondaryColor else GoldAccent,
                        startAngle = -90f,
                        sweepAngle = (scorePercentage / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$scorePercentage%",
                        color = PrimaryColor,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "QUIZ SCORE",
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Badge Earned Award box
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, GoldAccent.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(GoldLight, GoldAccent))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (scorePercentage >= 80) "Master Achiever" else "Keep Practicing!",
                        color = PrimaryColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (scorePercentage >= 80) {
                            "Outstanding performance! You've earned the Premium Gold Badge for algebraic mastery across this lesson topic."
                        } else {
                            "You are very close to securing the master badge. Re-play the questions to increase your accuracy to 90%!"
                        },
                        color = OnSurfaceVariant,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats grid details items
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ResultStatItem(
                    icon = Icons.Default.CheckCircle,
                    color = SecondaryColor,
                    title = "Correct Answers",
                    valText = "$score / $total"
                )
                ResultStatItem(
                    icon = Icons.Default.Cancel,
                    color = Color(0xFFBA1A1A),
                    title = "Incorrect Answers",
                    valText = "${total - score} / $total"
                )
                ResultStatItem(
                    icon = Icons.Default.Timer,
                    color = PrimaryColor,
                    title = "Time Spent",
                    valText = String.format("%02d:%02d", timeMinutes, timeSeconds)
                )
                ResultStatItem(
                    icon = Icons.Default.TrendingUp,
                    color = GoldDark,
                    title = "District Rank",
                    valText = "#4 Top 5%"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Replay quiz controls buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.startQuiz("mathematics") },
                    shape = CircleShape,
                    border = BorderStroke(2.dp, PrimaryColor),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("review_answers_result_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Replay Quiz",
                        color = PrimaryColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { viewModel.navigateTo(Screen.MainApp) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = CircleShape,
                    contentPadding = PaddingValues(vertical = 12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("back_to_dashboard_result_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dashboard",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ResultStatItem(
    icon: ImageVector,
    color: Color,
    title: String,
    valText: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = PrimaryColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = valText,
                color = PrimaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun AuthScreen(viewModel: MainViewModel) {
    var isRegisterMode by remember { mutableStateOf(false) }
    
    // Form Inputs
    var name by remember { mutableStateOf("") }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var villageName by remember { mutableStateOf("") }
    var classLevel by remember { mutableStateOf(4) } // Default Class 4
    
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "School Icon",
                    tint = PrimaryColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Vidyalay Offline",
                    color = PrimaryColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BackgroundColor, Color(0xFFD6E3FF).copy(alpha = 0.4f))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Welcome and title
                Text(
                    text = if (isRegisterMode) "Create Your Profile" else "Welcome Back",
                    color = OnBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = if (isRegisterMode) 
                        "Join thousands of rural students learning without internet limits" 
                    else 
                        "Sign in to continue your customized offline lessons",
                    color = OnSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Tab switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainer)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isRegisterMode) Color.White else Color.Transparent)
                            .clickable { 
                                isRegisterMode = false 
                                errorMessage = ""
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            color = if (!isRegisterMode) PrimaryColor else OnSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isRegisterMode) Color.White else Color.Transparent)
                            .clickable { 
                                isRegisterMode = true 
                                errorMessage = ""
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign Up / Register",
                            fontWeight = FontWeight.Bold,
                            color = if (isRegisterMode) PrimaryColor else OnSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Card with credentials form
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    border = BorderStroke(1.dp, OutlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isRegisterMode) {
                            // Student Name field
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Student Full Name",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = OnBackground
                                )
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_name_input"),
                                    placeholder = { Text("e.g. Ali Hashim") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryColor) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryColor,
                                        unfocusedBorderColor = OutlineVariant
                                    )
                                )
                            }
                            
                            // Village Name
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Your Village / Town Name",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = OnBackground
                                )
                                OutlinedTextField(
                                    value = villageName,
                                    onValueChange = { villageName = it },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_village_input"),
                                    placeholder = { Text("e.g. Sahiwal Village") },
                                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryColor) },
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryColor,
                                        unfocusedBorderColor = OutlineVariant
                                    )
                                )
                            }
                        }
                        
                        // Email or Phone Number
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Email Address or Parents' Phone Number",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = OnBackground
                            )
                            OutlinedTextField(
                                value = emailOrPhone,
                                onValueChange = { emailOrPhone = it },
                                modifier = Modifier.fillMaxWidth().testTag("auth_email_phone_input"),
                                placeholder = { Text("e.g. 03211234567 or student@email.com") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryColor) },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryColor,
                                    unfocusedBorderColor = OutlineVariant
                                )
                            )
                        }
                        
                        // Select Class/Grade
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Select Class / Selection Grade",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = OnBackground
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                (1..5).forEach { grade ->
                                    val isSelected = classLevel == grade
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) PrimaryColor else SurfaceContainer)
                                            .border(1.dp, if (isSelected) Color.Transparent else OutlineVariant, RoundedCornerShape(8.dp))
                                            .clickable { classLevel = grade }
                                            .testTag("grade_chip_$grade"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Class $grade",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) Color.White else OnBackground
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Choose PIN or Password
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isRegisterMode) "Choose a Password / Learning PIN" else "Password / Learning PIN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = OnBackground
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                                placeholder = { Text("•••••• (Choose a security password)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryColor) },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryColor,
                                    unfocusedBorderColor = OutlineVariant
                                )
                            )
                        }
                        
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Action Trigger Button
                        Button(
                            onClick = {
                                if (isLoading) return@Button
                                errorMessage = ""
                                
                                when {
                                    isRegisterMode && name.trim().isEmpty() -> {
                                        errorMessage = "Student Full Name is required!"
                                    }
                                    isRegisterMode && villageName.trim().isEmpty() -> {
                                        errorMessage = "Your Village / Town name is required!"
                                    }
                                    emailOrPhone.trim().isEmpty() -> {
                                        errorMessage = "Please enter Email or Phone contact!"
                                    }
                                    password.trim().isEmpty() || password.length < 4 -> {
                                        errorMessage = "Password or PIN must be at least 4 digits/characters!"
                                    }
                                    else -> {
                                        isLoading = true
                                        // Simulate beautiful cloud verification latency before storing in Room DB
                                        scope.launch {
                                            kotlinx.coroutines.delay(1200)
                                            isLoading = false
                                            if (isRegisterMode) {
                                                viewModel.registerStudent(
                                                    name = name.trim(),
                                                    emailOrPhone = emailOrPhone.trim(),
                                                    classLevel = classLevel,
                                                    village = villageName.trim()
                                                )
                                            } else {
                                                viewModel.loginStudent(
                                                    emailOrPhone = emailOrPhone.trim(),
                                                    classLevel = classLevel
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("auth_submit_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text = if (isRegisterMode) "Create Account & Enter" else "Sign In & Enter Classroom",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // Guest mode
                        TextButton(
                            onClick = {
                                viewModel.registerStudent(
                                    name = "Ali Hashim",
                                    emailOrPhone = "alihashim7227@gmail.com",
                                    classLevel = classLevel,
                                    village = "Punjab Region"
                                )
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Continue as Guest (No account needed)",
                                color = PrimaryColor,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Secondary visual elements detailing connection assurance
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = PrimaryColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Vidyalay's database runs completely offline on your phone (Room DB). Once internet is available, we sync progress automatically with Google Cloud Firestore.",
                        color = OnSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

// ==========================================
// 12. OFFLINE NOTES STUDY SYSTEM SCREEN
// ==========================================
@Composable
fun ChapterNotesScreen(
    viewModel: MainViewModel,
    subjectId: String,
    chapterNumber: String,
    chapterTitle: String
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activeNote by viewModel.activeNote.collectAsState()
    val scrollState = rememberScrollState()

    // Real Speech Synthesis Engine for Educational accessibility (reads the content aloud)
    var ttsEngine by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    var isReadingAloud by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val engine = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                // Confirmed
            }
        }
        ttsEngine = engine
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    // Automatically trigger cache/load on enter
    LaunchedEffect(subjectId, chapterNumber) {
        viewModel.selectChapterNotes(subjectId, chapterNumber, chapterTitle)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            ttsEngine?.stop()
                            isReadingAloud = false
                            viewModel.navigateTo(Screen.SubjectDetail(subjectId))
                        },
                        modifier = Modifier.testTag("back_to_subjects_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryColor
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Chapter $chapterNumber",
                        color = PrimaryColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Bookmark Feature
                activeNote?.let { note ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // TTS Trigger Button
                        IconButton(
                            onClick = {
                                if (isReadingAloud) {
                                    ttsEngine?.stop()
                                    isReadingAloud = false
                                } else {
                                    val textToSpeak = note.content
                                        .replace("#", "")
                                        .replace("*", "")
                                        .replace("|", " ")
                                    ttsEngine?.speak(
                                        textToSpeak,
                                        android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                                        null,
                                        "NotesReader"
                                    )
                                    isReadingAloud = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isReadingAloud) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Read Aloud",
                                tint = if (isReadingAloud) SecondaryColor else PrimaryColor
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleNoteBookmark(note) },
                            modifier = Modifier.testTag("notes_bookmark_toggle")
                        ) {
                            Icon(
                                imageVector = if (note.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark notes",
                                tint = if (note.isBookmarked) GoldAccent else PrimaryColor
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .widthIn(max = 620.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Header Meta Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Offline verification status tag
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SecondaryColor.copy(alpha = 0.08f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.OfflinePin,
                                        contentDescription = null,
                                        tint = SecondaryColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Saved Offline",
                                        color = SecondaryColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Dynamic reading time estimation
                            Text(
                                text = "${activeNote?.totalReadingTimeMinutes ?: 5} Min Read",
                                color = OnSurfaceVariant.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = chapterTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryColor,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Render quick-study search keywords chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val keywords = activeNote?.keywords?.split(",") ?: listOf("Offline", "Curriculum", "Self Study")
                            keywords.take(3).forEach { kw ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimaryColor.copy(alpha = 0.05f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = kw.trim(),
                                        color = PrimaryColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Long text Note article rendering block
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, OutlineVariant)
                ) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        activeNote?.let { note ->
                            SimpleMarkdownViewer(markdownText = note.content)
                        } ?: Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = PrimaryColor)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Loading visual outlines...",
                                color = OnSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleMarkdownViewer(markdownText: String) {
    val lines = markdownText.split("\n")
    Column(modifier = Modifier.fillMaxWidth()) {
        lines.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("# ") -> {
                    Text(
                        text = trimmed.removePrefix("# "),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryColor,
                        modifier = Modifier.padding(vertical = 12.dp),
                        lineHeight = 28.sp
                    )
                }
                trimmed.startsWith("## ") -> {
                    Text(
                        text = trimmed.removePrefix("## "),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        modifier = Modifier.padding(vertical = 8.dp),
                        lineHeight = 22.sp
                    )
                }
                trimmed.startsWith("### ") -> {
                    Text(
                        text = trimmed.removePrefix("### "),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryColor,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
                trimmed.startsWith("* ") || trimmed.startsWith("- ") -> {
                    val bulletText = if (trimmed.startsWith("* ")) trimmed.removePrefix("* ") else trimmed.removePrefix("- ")
                    Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
                        Text("•  ", color = SecondaryColor, fontWeight = FontWeight.Bold)
                        Text(
                            text = bulletText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
                trimmed.startsWith("---") -> {
                    HorizontalDivider(
                        color = OutlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                trimmed.startsWith("|") -> {
                    if (!trimmed.contains("---")) {
                        val cells = trimmed.split("|").map { it.trim() }.filter { it.isNotEmpty() }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerLow)
                                .border(1.dp, OutlineVariant)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            cells.forEach { cell ->
                                Text(
                                    text = cell,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (trimmed.contains("Grammar") || cells.firstOrNull() == "Word") FontWeight.Bold else FontWeight.Normal,
                                    color = PrimaryColor,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                trimmed.isNotEmpty() -> {
                    val parts = trimmed.split("**")
                    if (parts.size > 1) {
                        val annotatedText = androidx.compose.ui.text.buildAnnotatedString {
                            parts.forEachIndexed { idx, part ->
                                if (idx % 2 == 1) {
                                    withStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold, color = PrimaryColor)) {
                                        append(part)
                                    }
                                } else {
                                    append(part)
                                }
                            }
                        }
                        Text(
                            text = annotatedText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 4.dp),
                            lineHeight = 22.sp
                        )
                    } else {
                        Text(
                            text = trimmed,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 4.dp),
                            lineHeight = 22.sp
                        )
                    }
                }
                else -> {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


