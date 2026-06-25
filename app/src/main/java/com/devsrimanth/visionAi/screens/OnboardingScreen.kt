package com.devsrimanth.visionAi.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devsrimanth.visionAi.R

/**
 * One-time onboarding shown only on first app launch.
 * 3 slides explaining the app features.
 * Completion status saved via UserPreferences (DataStore).
 */

private data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String
)

private val pages = listOf(
    OnboardingPage(
        emoji = "",
        title = "Welcome to VisionAI",
        description = "Real-time AI vision running entirely on your device. No internet required."
    ),
    OnboardingPage(
        emoji = "📦",
        title = "Object Detection",
        description = "Point your camera at anything — cups, phones, people, cars. AI detects and labels them instantly with bounding boxes."
    ),
    OnboardingPage(
        emoji = "🧍",
        title = "Pose Detection",
        description = "See your body's skeleton tracked live — 33 body landmarks detected in real time using MediaPipe."
    )
)

@Composable
fun OnboardingScreen(onOnboardingComplete: () -> Unit) {

    var currentPage by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0A),
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            /** Skip button — top right */
            Box(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = onOnboardingComplete,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text("Skip", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                }
            }

            /** Page content */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                if(pages[currentPage].emoji.isNotEmpty()) {
                    Text(
                        text = pages[currentPage].emoji,
                        fontSize = 80.sp
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier.size(72.dp)
                    )
                }


                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = pages[currentPage].title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pages[currentPage].description,
                    fontSize = 15.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                /** Page indicator dots */
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    pages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentPage) 24.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (index == currentPage)
                                        Color.White else Color.White.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                /** Next / Get Started button */
                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onOnboardingComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (currentPage < pages.size - 1) "Next →" else "Get Started ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}