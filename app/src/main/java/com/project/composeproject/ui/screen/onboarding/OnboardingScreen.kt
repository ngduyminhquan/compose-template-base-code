package com.project.composeproject.ui.screen.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue
import com.project.composeproject.R
import com.project.composeproject.ui.theme.DefaultFontFamily
import com.project.composeproject.ui.theme.OnboardingButtonBorder
import com.project.composeproject.ui.theme.OnboardingButtonText
import com.project.composeproject.ui.theme.OnboardingIndicatorActive
import com.project.composeproject.ui.theme.OnboardingIndicatorInactive
import com.project.composeproject.ui.theme.OnboardingSubtitle
import com.project.composeproject.ui.theme.OnboardingTitle
import com.project.composeproject.ui.utils.onSingleClick
import kotlinx.coroutines.launch
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp

@Composable
fun OnboardingScreen() {
    val onboardingItems = remember {
        listOf(
            OnboardingItem(
                imageResId = R.drawable.img_onboarding_1,
                titleResId = R.string.onboarding_title_1,
                subtitleResId = R.string.onboarding_subtitle_1
            ),
            OnboardingItem(
                imageResId = R.drawable.img_onboarding_2,
                titleResId = R.string.onboarding_title_2,
                subtitleResId = R.string.onboarding_subtitle_2
            ),
            OnboardingItem(
                imageResId = R.drawable.img_onboarding_3,
                titleResId = R.string.onboarding_title_3,
                subtitleResId = R.string.onboarding_subtitle_3
            ),
            OnboardingItem(
                imageResId = R.drawable.img_onboarding_4,
                titleResId = R.string.onboarding_title_4,
                subtitleResId = R.string.onboarding_subtitle_4
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { onboardingItems.size })
    val coroutineScope = rememberCoroutineScope()

    OnboardingContent(
        pagerState = pagerState,
        items = onboardingItems,
        onNextClick = {
            coroutineScope.launch {
                val nextPage = pagerState.currentPage + 1
                if (nextPage < onboardingItems.size) {
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        },
        onStartClick = {
            // TODO: Tạm thời chưa xử lý nút START
        }
    )
}

@Composable
private fun OnboardingContent(
    pagerState: PagerState,
    items: List<OnboardingItem>,
    onNextClick: () -> Unit,
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) { page ->
            OnboardingPage(
                item = items[page],
                pagerState = pagerState,
                onNextClick = onNextClick,
                onStartClick = onStartClick,
                modifier = Modifier.graphicsLayer {
                    val pageOffset = pagerState
                        .getOffsetDistanceInPages(page)
                        .absoluteValue
                        .coerceIn(0f, 1f)
                    val scale = 1f - (pageOffset * 0.1f)
                    val alpha = 1f - (pageOffset * 0.6f)

                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                }
            )
        }
    }
}

@Composable
private fun OnboardingPage(
    item: OnboardingItem,
    pagerState: PagerState,
    onNextClick: () -> Unit,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = item.imageResId),
            contentDescription = stringResource(id = item.titleResId),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.sdp))

        Text(
            text = stringResource(id = item.titleResId),
            color = OnboardingTitle,
            fontSize = 18.ssp,
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.sdp)
        )

        Spacer(modifier = Modifier.height(8.sdp))

        Text(
            text = stringResource(id = item.subtitleResId),
            color = OnboardingSubtitle,
            fontSize = 14.ssp,
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.sdp)
        )

        Spacer(modifier = Modifier.height(24.sdp))

        OnboardingBottomBar(
            pagerState = pagerState,
            onNextClick = onNextClick,
            onStartClick = onStartClick
        )
    }
}

@Composable
private fun OnboardingBottomBar(
    pagerState: PagerState,
    onNextClick: () -> Unit,
    onStartClick: () -> Unit
) {
    val isLastPage = pagerState.currentPage == pagerState.pageCount - 1
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.sdp, vertical = 24.sdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                val color =
                    if (isSelected) OnboardingIndicatorActive else OnboardingIndicatorInactive
                val width = if (isSelected) 14.sdp else 8.sdp

                Box(
                    modifier = Modifier
                        .height(7.sdp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Box(
            modifier = Modifier
                .border(
                    width = 0.5.sdp,
                    color = OnboardingButtonBorder,
                    shape = RoundedCornerShape(50)
                )
                .clip(RoundedCornerShape(50))
                .onSingleClick(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (isLastPage) onStartClick() else onNextClick()
                }
                .padding(horizontal = 16.sdp, vertical = 4.sdp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = if (isLastPage) R.string.onboarding_start else R.string.onboarding_next),
                color = OnboardingButtonText,
                fontSize = 10.ssp,
                fontFamily = DefaultFontFamily,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen()
}
