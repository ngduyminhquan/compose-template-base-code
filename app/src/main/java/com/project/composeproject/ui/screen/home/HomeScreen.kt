package com.project.composeproject.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.composeproject.R
import com.project.composeproject.domain.model.SourceType
import com.project.composeproject.ui.screen.home.model.HomeChannelUiModel
import com.project.composeproject.ui.screen.home.model.HomePlaylistFilter
import com.project.composeproject.ui.screen.home.model.HomeSectionUiModel
import com.project.composeproject.ui.screen.home.model.HomeSourceCardUiModel
import com.project.composeproject.ui.theme.*
import kotlinx.coroutines.delay
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(HomeIntent.LoadHome)
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            viewModel.onIntent(HomeIntent.OnBannerAutoAdvance)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            uiState = uiState,
            onIntent = viewModel::onIntent
        )

        if (uiState.isAddOverlayOpen) {
            AddOverlay(
                onDismiss = { viewModel.onIntent(HomeIntent.OnAddOverlayDismissed) },
                onIntent = viewModel::onIntent
            )
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackground)
    ) {
        TopBar(onIntent = onIntent)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                BannerSection(bannerIndex = uiState.bannerIndex)
            }

            item {
                FilterTabs(
                    selectedFilter = uiState.selectedFilter,
                    onIntent = onIntent
                )
            }

            if (uiState.sourceCards.isEmpty() && 
                uiState.recentSection == null && 
                uiState.favoriteSection == null && 
                uiState.playlistSections.isEmpty()) {
                item {
                    EmptyState(onIntent = onIntent)
                }
            } else {
                if (uiState.sourceCards.isNotEmpty()) {
                    item {
                        SourceCardsSection(
                            sourceCards = uiState.sourceCards,
                            onIntent = onIntent
                        )
                    }
                }

                uiState.recentSection?.let { section ->
                    item {
                        ChannelSection(
                            section = section,
                            onIntent = onIntent
                        )
                    }
                }

                uiState.favoriteSection?.let { section ->
                    item {
                        ChannelSection(
                            section = section,
                            onIntent = onIntent
                        )
                    }
                }

                items(uiState.playlistSections) { section ->
                    ChannelSection(
                        section = section,
                        onIntent = onIntent
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        BottomNavigation(onIntent = onIntent)
    }
}

@Composable
private fun TopBar(onIntent: (HomeIntent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.sdp, vertical = 12.sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_premium),
            contentDescription = stringResource(R.string.home_crown_icon_description),
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(24.sdp)
        )

        Text(
            text = stringResource(R.string.home_app_title),
            fontSize = 16.ssp,
            fontWeight = FontWeight.Bold,
            color = HomeSectionTitle,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.sdp)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_help),
            contentDescription = stringResource(R.string.home_help_icon_description),
            tint = HomeSectionTitle,
            modifier = Modifier
                .size(24.sdp)
                .clickable { onIntent(HomeIntent.OnHelpClicked) }
        )
    }
}

@Composable
private fun BannerSection(bannerIndex: Int) {
    val bannerImages = listOf(
        R.drawable.img_home_banner_1,
        R.drawable.img_home_banner_2,
        R.drawable.img_home_banner_3,
        R.drawable.img_home_banner_4
    )

    Image(
        painter = painterResource(id = bannerImages[bannerIndex % bannerImages.size]),
        contentDescription = "Banner",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.sdp)
    )
}

@Composable
private fun FilterTabs(
    selectedFilter: HomePlaylistFilter,
    onIntent: (HomeIntent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.sdp, vertical = 12.sdp),
        horizontalArrangement = Arrangement.spacedBy(8.sdp)
    ) {
        FilterTab(
            text = stringResource(R.string.home_filter_all),
            isSelected = selectedFilter == HomePlaylistFilter.ALL,
            onClick = { onIntent(HomeIntent.OnFilterSelected(HomePlaylistFilter.ALL)) }
        )
        FilterTab(
            text = stringResource(R.string.home_filter_url),
            isSelected = selectedFilter == HomePlaylistFilter.URL,
            onClick = { onIntent(HomeIntent.OnFilterSelected(HomePlaylistFilter.URL)) }
        )
        FilterTab(
            text = stringResource(R.string.home_filter_stream),
            isSelected = selectedFilter == HomePlaylistFilter.STREAM,
            onClick = { onIntent(HomeIntent.OnFilterSelected(HomePlaylistFilter.STREAM)) }
        )
        FilterTab(
            text = stringResource(R.string.home_filter_file),
            isSelected = selectedFilter == HomePlaylistFilter.FILE,
            onClick = { onIntent(HomeIntent.OnFilterSelected(HomePlaylistFilter.FILE)) }
        )
        FilterTab(
            text = stringResource(R.string.home_filter_device),
            isSelected = selectedFilter == HomePlaylistFilter.DEVICE,
            onClick = { onIntent(HomeIntent.OnFilterSelected(HomePlaylistFilter.DEVICE)) }
        )
    }
}

@Composable
private fun FilterTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 12.ssp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) HomeFilterTabSelected else HomeFilterTabUnselected,
        modifier = Modifier
            .clip(RoundedCornerShape(16.sdp))
            .background(if (isSelected) HomeFilterTabSelected.copy(alpha = 0.1f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.sdp, vertical = 6.sdp)
    )
}

@Composable
private fun EmptyState(onIntent: (HomeIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.sdp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.sdp))
                .background(HomeTutorialBackground)
                .padding(16.sdp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_tutorial_title),
                        fontSize = 18.ssp,
                        fontWeight = FontWeight.Bold,
                        color = HomeTutorialText
                    )
                    Spacer(modifier = Modifier.height(4.sdp))
                    Text(
                        text = stringResource(R.string.home_tutorial_description),
                        fontSize = 14.ssp,
                        color = HomeTutorialText.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(12.sdp))
                    Button(
                        onClick = { onIntent(HomeIntent.OnTutorialGetStartedClicked) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = HomeTutorialBackground
                        ),
                        shape = RoundedCornerShape(8.sdp)
                    ) {
                        Text(
                            text = stringResource(R.string.home_tutorial_button),
                            fontSize = 14.ssp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.img_banner_tutorial_background),
                    contentDescription = "Tutorial",
                    modifier = Modifier.size(100.sdp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.sdp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_empty_playlist),
                contentDescription = stringResource(R.string.home_empty_icon_description),
                tint = HomeFilterTabUnselected,
                modifier = Modifier.size(80.sdp)
            )
            Spacer(modifier = Modifier.height(16.sdp))
            Text(
                text = stringResource(R.string.home_empty_title),
                fontSize = 18.ssp,
                fontWeight = FontWeight.Bold,
                color = HomeSectionTitle
            )
            Spacer(modifier = Modifier.height(8.sdp))
            Text(
                text = stringResource(R.string.home_empty_description),
                fontSize = 14.ssp,
                color = HomeChannelName
            )
        }
    }
}

@Composable
private fun SourceCardsSection(
    sourceCards: List<HomeSourceCardUiModel>,
    onIntent: (HomeIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.sdp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp),
            horizontalArrangement = Arrangement.spacedBy(12.sdp)
        ) {
            sourceCards.forEach { card ->
                SourceCard(
                    card = card,
                    onIntent = onIntent,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SourceCard(
    card: HomeSourceCardUiModel,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val iconRes = when (card.sourceType) {
        SourceType.FILE -> R.drawable.ic_file
        SourceType.DEVICE -> R.drawable.ic_media
        SourceType.URL -> R.drawable.ic_link
        SourceType.STREAM -> R.drawable.ic_stream
    }

    val titleRes = when (card.sourceType) {
        SourceType.FILE -> R.string.home_source_file
        SourceType.DEVICE -> R.string.home_source_device
        SourceType.URL -> R.string.home_source_url
        SourceType.STREAM -> R.string.home_source_stream
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.sdp))
            .background(HomeCardBackground)
            .border(1.dp, HomeSourceCardBorder, RoundedCornerShape(12.sdp))
            .clickable { onIntent(HomeIntent.OnSourceCardClicked(card.sourceType)) }
            .padding(12.sdp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = stringResource(titleRes),
                tint = HomeFilterTabSelected,
                modifier = Modifier.size(32.sdp)
            )
            Spacer(modifier = Modifier.height(8.sdp))
            Text(
                text = stringResource(titleRes),
                fontSize = 12.ssp,
                fontWeight = FontWeight.Bold,
                color = HomeSectionTitle
            )
            Spacer(modifier = Modifier.height(4.sdp))
            Text(
                text = stringResource(R.string.home_source_channels, card.channelCount),
                fontSize = 10.ssp,
                color = HomeChannelName
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_channel_option),
            contentDescription = stringResource(R.string.home_source_menu_description),
            tint = HomeFilterTabUnselected,
            modifier = Modifier
                .size(20.sdp)
                .align(Alignment.TopEnd)
                .clickable { onIntent(HomeIntent.OnSourceMenuClicked(card.sourceType)) }
        )
    }
}

@Composable
private fun ChannelSection(
    section: HomeSectionUiModel,
    onIntent: (HomeIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.sdp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp, vertical = 8.sdp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = section.title,
                    fontSize = 16.ssp,
                    fontWeight = FontWeight.Bold,
                    color = HomeSectionTitle
                )
                Spacer(modifier = Modifier.width(4.sdp))
                Text(
                    text = stringResource(R.string.home_section_count, section.count),
                    fontSize = 14.ssp,
                    color = HomeFilterTabUnselected
                )
            }

            Text(
                text = stringResource(R.string.home_section_view_all),
                fontSize = 14.ssp,
                color = HomeFilterTabSelected,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { 
                    onIntent(HomeIntent.OnSectionViewAllClicked(section.id)) 
                }
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.sdp),
            horizontalArrangement = Arrangement.spacedBy(12.sdp)
        ) {
            items(section.channels) { channel ->
                ChannelItem(
                    channel = channel,
                    onIntent = onIntent
                )
            }
        }
    }
}

@Composable
private fun ChannelItem(
    channel: HomeChannelUiModel,
    onIntent: (HomeIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .width(140.sdp)
            .clip(RoundedCornerShape(12.sdp))
            .background(HomeCardBackground)
            .clickable { onIntent(HomeIntent.OnChannelClicked(channel.id)) }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.sdp)
                    .background(Gray200)
            ) {
                channel.thumbnailUrl?.let { url ->
                    // Placeholder for image loading
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.sdp)
            ) {
                Text(
                    text = channel.name,
                    fontSize = 12.ssp,
                    fontWeight = FontWeight.Medium,
                    color = HomeChannelName,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Icon(
            painter = painterResource(id = if (channel.isFavorited) R.drawable.ic_favorite else R.drawable.ic_favorite),
            contentDescription = stringResource(R.string.home_channel_favorite_description),
            tint = if (channel.isFavorited) HomeFavoriteActive else HomeFavoriteInactive,
            modifier = Modifier
                .size(24.sdp)
                .align(Alignment.TopEnd)
                .padding(8.sdp)
                .clickable { 
                    onIntent(HomeIntent.OnFavoriteClicked(channel.id, channel.isFavorited)) 
                }
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_channel_option),
            contentDescription = stringResource(R.string.home_channel_menu_description),
            tint = HomeFilterTabUnselected,
            modifier = Modifier
                .size(24.sdp)
                .align(Alignment.BottomEnd)
                .padding(8.sdp)
                .clickable { onIntent(HomeIntent.OnChannelMenuClicked(channel.id)) }
        )
    }
}

@Composable
private fun AddOverlay(
    onDismiss: () -> Unit,
    onIntent: (HomeIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeOverlayBackground)
            .clickable(onClick = onDismiss)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.sdp)
                .padding(horizontal = 16.sdp),
            verticalArrangement = Arrangement.spacedBy(12.sdp)
        ) {
            AddButton(
                text = stringResource(R.string.home_add_play_single_url),
                icon = R.drawable.ic_add_stream,
                onClick = { onIntent(HomeIntent.OnPlaySingleUrlClicked) }
            )
            AddButton(
                text = stringResource(R.string.home_add_import_playlist_url),
                icon = R.drawable.ic_add_link,
                onClick = { onIntent(HomeIntent.OnImportPlaylistUrlClicked) }
            )
            AddButton(
                text = stringResource(R.string.home_add_import_from_device),
                icon = R.drawable.ic_add_media,
                onClick = { onIntent(HomeIntent.OnImportFromDeviceClicked) }
            )
            AddButton(
                text = stringResource(R.string.home_add_upload_m3u_file),
                icon = R.drawable.ic_add_file,
                onClick = { onIntent(HomeIntent.OnUploadM3uFileClicked) }
            )
        }
    }
}

@Composable
private fun AddButton(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.sdp))
            .background(HomeAddButtonBackground)
            .clickable(onClick = onClick)
            .padding(16.sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            tint = HomeFilterTabSelected,
            modifier = Modifier.size(24.sdp)
        )
        Spacer(modifier = Modifier.width(12.sdp))
        Text(
            text = text,
            fontSize = 14.ssp,
            fontWeight = FontWeight.Medium,
            color = HomeAddButtonText
        )
    }
}

@Composable
private fun BottomNavigation(onIntent: (HomeIntent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(vertical = 12.sdp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            icon = R.drawable.ic_home,
            label = stringResource(R.string.home_nav_home),
            isSelected = true,
            onClick = { }
        )
        BottomNavItem(
            icon = R.drawable.ic_channel,
            label = stringResource(R.string.home_nav_channel),
            isSelected = false,
            onClick = { onIntent(HomeIntent.OnChannelTabClicked) }
        )
        Box(
            modifier = Modifier
                .size(56.sdp)
                .clip(CircleShape)
                .background(HomeFilterTabSelected)
                .clickable { onIntent(HomeIntent.OnAddClicked) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_more),
                contentDescription = stringResource(R.string.home_nav_add),
                tint = White,
                modifier = Modifier.size(24.sdp)
            )
        }
        BottomNavItem(
            icon = R.drawable.ic_favorite,
            label = stringResource(R.string.home_nav_favorite),
            isSelected = false,
            onClick = { onIntent(HomeIntent.OnFavoriteTabClicked) }
        )
        BottomNavItem(
            icon = R.drawable.ic_settings,
            label = stringResource(R.string.home_nav_settings),
            isSelected = false,
            onClick = { onIntent(HomeIntent.OnSettingsTabClicked) }
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = if (isSelected) HomeBottomNavSelected else HomeBottomNavUnselected,
            modifier = Modifier.size(24.sdp)
        )
        Spacer(modifier = Modifier.height(4.sdp))
        Text(
            text = label,
            fontSize = 10.ssp,
            color = if (isSelected) HomeBottomNavSelected else HomeBottomNavUnselected
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val mockState = HomeUiState(
        sourceCards = listOf(
            HomeSourceCardUiModel(SourceType.FILE, 10),
            HomeSourceCardUiModel(SourceType.DEVICE, 5),
            HomeSourceCardUiModel(SourceType.URL, 8),
            HomeSourceCardUiModel(SourceType.STREAM, 12)
        ),
        recentSection = HomeSectionUiModel(
            id = "recent",
            title = "Recent",
            count = 15,
            channels = listOf(
                HomeChannelUiModel(1, "Demo Video 1", null, false),
                HomeChannelUiModel(2, "Demo Video 2", null, true)
            )
        )
    )

    HomeContent(
        uiState = mockState,
        onIntent = {}
    )
}
