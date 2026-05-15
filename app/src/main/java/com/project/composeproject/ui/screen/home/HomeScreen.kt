package com.project.composeproject.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.composeproject.R
import com.project.composeproject.domain.model.SourceType
import com.project.composeproject.ui.screen.home.model.HomeChannelUiModel
import com.project.composeproject.ui.screen.home.model.HomePlaylistFilter
import com.project.composeproject.ui.screen.home.model.HomeSectionUiModel
import com.project.composeproject.ui.screen.home.model.HomeSourceCardUiModel
import com.project.composeproject.ui.theme.DefaultFontFamily
import com.project.composeproject.ui.theme.HomeBackground
import com.project.composeproject.ui.theme.HomeBorder
import com.project.composeproject.ui.theme.HomeHeart
import com.project.composeproject.ui.theme.HomeIconTint
import com.project.composeproject.ui.theme.HomeOverlay
import com.project.composeproject.ui.theme.HomePrimary
import com.project.composeproject.ui.theme.HomePrimaryDark
import com.project.composeproject.ui.theme.HomePrimarySoft
import com.project.composeproject.ui.theme.HomeSurface
import com.project.composeproject.ui.theme.HomeTextPrimary
import com.project.composeproject.ui.theme.HomeTextSecondary
import com.project.composeproject.ui.theme.HomeTutorialEnd
import com.project.composeproject.ui.theme.HomeTutorialStart
import com.project.composeproject.ui.theme.White
import com.project.composeproject.ui.utils.onSingleClick
import kotlinx.coroutines.delay
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        while (true) {
            delay(HOME_BANNER_INTERVAL_MILLIS)
            viewModel.onIntent(HomeIntent.OnBannerAutoAdvance)
        }
    }

    HomeScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            HomeTopBar(onHelpClick = { onIntent(HomeIntent.OnHelpClicked) })

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 12.sdp, top = 8.sdp, end = 12.sdp, bottom = 96.sdp),
                verticalArrangement = Arrangement.spacedBy(12.sdp),
            ) {
                item {
                    HomeBannerPager(
                        bannerIndex = uiState.bannerIndex,
                    )
                }

                item {
                    HomeFilterRow(
                        selectedFilter = uiState.selectedFilter,
                        onFilterSelected = { onIntent(HomeIntent.OnFilterSelected(it)) },
                    )
                }

                if (uiState.hasPlaylistContent) {
                    item {
                        SourceCardGrid(
                            sourceCards = uiState.sourceCards,
                            openedMenuSourceId = uiState.openedPlaylistSourceMenuId,
                            onCardClick = { onIntent(HomeIntent.OnSourceCardClicked(it)) },
                            onMenuClick = { onIntent(HomeIntent.OnSourceMenuClicked(it)) },
                            onMenuDismiss = { onIntent(HomeIntent.OnPlaylistMenuDismissed) },
                            onEditClick = { onIntent(HomeIntent.OnEditPlaylistClicked) },
                            onDeleteClick = { onIntent(HomeIntent.OnDeletePlaylistClicked) },
                        )
                    }

                    uiState.recentSection?.let { section ->
                        item(section.id) {
                            ChannelSection(
                                section = section,
                                openedChannelMenuId = uiState.openedChannelMenuId,
                                onViewAllClick = { onIntent(HomeIntent.OnSectionViewAllClicked(section.id)) },
                                onChannelClick = { onIntent(HomeIntent.OnChannelClicked(it)) },
                                onFavoriteClick = { id, isFavorited -> onIntent(HomeIntent.OnFavoriteClicked(id, isFavorited)) },
                                onMenuClick = { onIntent(HomeIntent.OnChannelMenuClicked(it)) },
                                onMenuDismiss = { onIntent(HomeIntent.OnChannelMenuDismissed) },
                                onEditClick = { onIntent(HomeIntent.OnEditChannelClicked) },
                                onDeleteClick = { onIntent(HomeIntent.OnDeleteChannelClicked) },
                            )
                        }
                    }

                    uiState.favoriteSection?.let { section ->
                        item(section.id) {
                            ChannelSection(
                                section = section,
                                openedChannelMenuId = uiState.openedChannelMenuId,
                                onViewAllClick = { onIntent(HomeIntent.OnSectionViewAllClicked(section.id)) },
                                onChannelClick = { onIntent(HomeIntent.OnChannelClicked(it)) },
                                onFavoriteClick = { id, isFavorited -> onIntent(HomeIntent.OnFavoriteClicked(id, isFavorited)) },
                                onMenuClick = { onIntent(HomeIntent.OnChannelMenuClicked(it)) },
                                onMenuDismiss = { onIntent(HomeIntent.OnChannelMenuDismissed) },
                                onEditClick = { onIntent(HomeIntent.OnEditChannelClicked) },
                                onDeleteClick = { onIntent(HomeIntent.OnDeleteChannelClicked) },
                            )
                        }
                    }

                    items(uiState.playlistSections, key = { it.id }) { section ->
                        ChannelSection(
                            section = section,
                            openedChannelMenuId = uiState.openedChannelMenuId,
                            onViewAllClick = { onIntent(HomeIntent.OnSectionViewAllClicked(section.id)) },
                            onChannelClick = { onIntent(HomeIntent.OnChannelClicked(it)) },
                            onFavoriteClick = { id, isFavorited -> onIntent(HomeIntent.OnFavoriteClicked(id, isFavorited)) },
                            onMenuClick = { onIntent(HomeIntent.OnChannelMenuClicked(it)) },
                            onMenuDismiss = { onIntent(HomeIntent.OnChannelMenuDismissed) },
                            onEditClick = { onIntent(HomeIntent.OnEditChannelClicked) },
                            onDeleteClick = { onIntent(HomeIntent.OnDeleteChannelClicked) },
                        )
                    }
                } else {
                    item {
                        TutorialCard(
                            onClick = { onIntent(HomeIntent.OnTutorialGetStartedClicked) },
                        )
                    }

                    item {
                        EmptyPlaylistCard()
                    }
                }
            }
        }

        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            isAddOverlayOpen = uiState.isAddOverlayOpen,
            onHomeClick = {},
            onChannelClick = { onIntent(HomeIntent.OnChannelTabClicked) },
            onAddClick = { onIntent(HomeIntent.OnAddClicked) },
            onFavoriteClick = { onIntent(HomeIntent.OnFavoriteTabClicked) },
            onSettingsClick = { onIntent(HomeIntent.OnSettingsTabClicked) },
        )

        AddOverlay(
            visible = uiState.isAddOverlayOpen,
            onDismiss = { onIntent(HomeIntent.OnAddOverlayDismissed) },
            onPlaySingleClick = { onIntent(HomeIntent.OnPlaySingleUrlClicked) },
            onImportUrlClick = { onIntent(HomeIntent.OnImportPlaylistUrlClicked) },
            onImportDeviceClick = { onIntent(HomeIntent.OnImportFromDeviceClicked) },
            onUploadFileClick = { onIntent(HomeIntent.OnUploadM3uFileClicked) },
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = HomePrimary,
                strokeWidth = 2.sdp,
            )
        }
    }
}

@Composable
private fun HomeTopBar(onHelpClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.sdp, vertical = 8.sdp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_premium),
            contentDescription = null,
            modifier = Modifier.size(18.sdp),
        )

        Text(
            text = stringResource(id = R.string.home_title),
            color = HomePrimary,
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.ssp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.sdp),
        )

        Box(
            modifier = Modifier
                .size(24.sdp)
                .clip(CircleShape)
                .onSingleClick(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onHelpClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_help),
                contentDescription = stringResource(id = R.string.home_help_description),
                modifier = Modifier.size(18.sdp),
            )
        }
    }
}

@Composable
private fun HomeBannerPager(bannerIndex: Int) {
    val banners = remember {
        listOf(
            R.drawable.img_home_banner_1,
            R.drawable.img_home_banner_2,
            R.drawable.img_home_banner_3,
            R.drawable.img_home_banner_4,
        )
    }
    Image(
        painter = painterResource(id = banners[bannerIndex % banners.size]),
        contentDescription = stringResource(id = R.string.home_banner_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.sdp))
            .aspectRatio(2.08f),
    )
}

@Composable
private fun HomeFilterRow(
    selectedFilter: HomePlaylistFilter,
    onFilterSelected: (HomePlaylistFilter) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.sdp),
    ) {
        items(HomePlaylistFilter.entries, key = { it.name }) { filter ->
            val selected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.sdp))
                    .background(if (selected) HomePrimarySoft else HomeSurface)
                    .border(width = 1.sdp, color = if (selected) HomePrimary else HomeBorder, shape = RoundedCornerShape(12.sdp))
                    .onSingleClick(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onFilterSelected(filter) }
                    .padding(horizontal = 12.sdp, vertical = 9.sdp),
            ) {
                Text(
                    text = filter.toLabel(),
                    color = if (selected) HomePrimary else HomeTextSecondary,
                    fontFamily = DefaultFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.ssp,
                )
            }
        }
    }
}

@Composable
private fun SourceCardGrid(
    sourceCards: List<HomeSourceCardUiModel>,
    openedMenuSourceId: Long?,
    onCardClick: (SourceType) -> Unit,
    onMenuClick: (SourceType) -> Unit,
    onMenuDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.sdp)) {
        sourceCards.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.sdp)) {
                rowItems.forEach { item ->
                    SourceCard(
                        modifier = Modifier.weight(1f),
                        item = item,
                        menuExpanded = openedMenuSourceId == item.id,
                        onClick = { onCardClick(item.sourceType) },
                        onMenuClick = { onMenuClick(item.sourceType) },
                        onMenuDismiss = onMenuDismiss,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                    )
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SourceCard(
    modifier: Modifier,
    item: HomeSourceCardUiModel,
    menuExpanded: Boolean,
    onClick: () -> Unit,
    onMenuClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.sdp))
                .background(HomeSurface)
                .border(1.sdp, HomeBorder, RoundedCornerShape(14.sdp))
                .onSingleClick(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                )
                .padding(12.sdp),
            verticalArrangement = Arrangement.spacedBy(10.sdp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.sdp)
                        .clip(RoundedCornerShape(8.sdp))
                        .background(HomePrimarySoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = item.sourceType.toSourceIcon()),
                        contentDescription = stringResource(id = R.string.home_source_icon_description),
                        modifier = Modifier.size(16.sdp),
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.ic_channel_option),
                        contentDescription = stringResource(id = R.string.home_more_description),
                        modifier = Modifier
                            .size(18.sdp)
                            .clickable { onMenuClick() },
                    )
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = onMenuDismiss,
                        containerColor = White,
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.home_playlist_menu_edit)) },
                            leadingIcon = {
                                Image(painterResource(id = R.drawable.ic_edit), contentDescription = null)
                            },
                            onClick = onEditClick,
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.home_playlist_menu_delete)) },
                            leadingIcon = {
                                Image(painterResource(id = R.drawable.ic_delete), contentDescription = null)
                            },
                            onClick = onDeleteClick,
                        )
                    }
                }
            }

            Text(
                text = item.title,
                color = HomeTextPrimary,
                fontFamily = DefaultFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.ssp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = item.subtitle,
                color = HomeTextSecondary,
                fontFamily = DefaultFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.ssp,
            )
        }
    }
}

@Composable
private fun TutorialCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.sdp))
            .background(Brush.horizontalGradient(listOf(HomeTutorialStart, HomeTutorialEnd)))
            .padding(14.sdp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.sdp),
            ) {
                Text(
                    text = stringResource(id = R.string.home_tutorial_title),
                    color = White,
                    fontFamily = DefaultFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.ssp,
                )
                Text(
                    text = stringResource(id = R.string.home_tutorial_description),
                    color = White,
                    fontFamily = DefaultFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.ssp,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(White)
                        .onSingleClick(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick,
                        )
                        .padding(horizontal = 12.sdp, vertical = 5.sdp),
                ) {
                    Text(
                        text = stringResource(id = R.string.home_get_started),
                        color = HomePrimaryDark,
                        fontFamily = DefaultFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.ssp,
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.img_banner_tutorial_background),
                contentDescription = stringResource(id = R.string.home_tutorial_illustration_description),
                modifier = Modifier
                    .width(92.sdp)
                    .height(66.sdp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun EmptyPlaylistCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.sdp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_empty_playlist),
            contentDescription = stringResource(id = R.string.home_empty_icon_description),
            modifier = Modifier.size(42.sdp),
        )
        Spacer(modifier = Modifier.height(10.sdp))
        Text(
            text = stringResource(id = R.string.home_empty_title),
            color = HomeTextPrimary,
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.ssp,
        )
        Spacer(modifier = Modifier.height(4.sdp))
        Text(
            text = stringResource(id = R.string.home_empty_description),
            color = HomeTextSecondary,
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.ssp,
        )
    }
}

@Composable
private fun ChannelSection(
    section: HomeSectionUiModel,
    openedChannelMenuId: Long?,
    onViewAllClick: () -> Unit,
    onChannelClick: (Long) -> Unit,
    onFavoriteClick: (Long, Boolean) -> Unit,
    onMenuClick: (Long) -> Unit,
    onMenuDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.sdp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.home_section_count, section.title, section.itemCount),
                color = HomeTextPrimary,
                fontFamily = DefaultFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.ssp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(id = R.string.home_view_all),
                color = HomeTextSecondary,
                fontFamily = DefaultFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.ssp,
                modifier = Modifier.onSingleClick(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onViewAllClick,
                ),
            )
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.sdp)) {
            items(section.items, key = { it.id }) { item ->
                ChannelCard(
                    item = item,
                    menuExpanded = openedChannelMenuId == item.id,
                    onClick = { onChannelClick(item.id) },
                    onFavoriteClick = { onFavoriteClick(item.id, item.isFavorited) },
                    onMenuClick = { onMenuClick(item.id) },
                    onMenuDismiss = onMenuDismiss,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                )
            }
        }
    }
}

@Composable
private fun ChannelCard(
    item: HomeChannelUiModel,
    menuExpanded: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onMenuClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier.width(116.sdp),
        verticalArrangement = Arrangement.spacedBy(6.sdp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.36f)
                .clip(RoundedCornerShape(14.sdp))
                .background(HomeSurface)
                .onSingleClick(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_home_banner_1),
                contentDescription = stringResource(id = R.string.home_video_thumbnail_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.sdp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                CircleIconButton(
                    iconRes = if (item.isFavorited) R.drawable.ic_favorite else R.drawable.ic_favorite,
                    contentDescription = stringResource(id = R.string.home_heart_description),
                    tint = if (item.isFavorited) HomePrimary else HomeHeart,
                    onClick = onFavoriteClick,
                )
                Box {
                    CircleIconButton(
                        iconRes = R.drawable.ic_channel_option,
                        contentDescription = stringResource(id = R.string.home_more_description),
                        tint = HomeTextSecondary,
                        onClick = onMenuClick,
                    )
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = onMenuDismiss,
                        containerColor = White,
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.home_channel_menu_edit)) },
                            leadingIcon = {
                                Image(painterResource(id = R.drawable.ic_edit), contentDescription = null)
                            },
                            onClick = onEditClick,
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.home_channel_menu_delete)) },
                            leadingIcon = {
                                Image(painterResource(id = R.drawable.ic_delete), contentDescription = null)
                            },
                            onClick = onDeleteClick,
                        )
                    }
                }
            }
        }
        Text(
            text = item.title,
            color = HomeTextPrimary,
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.ssp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CircleIconButton(
    iconRes: Int,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(22.sdp)
            .clip(CircleShape)
            .background(White.copy(alpha = 0.92f))
            .onSingleClick(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(12.sdp),
            colorFilter = ColorFilter.tint(tint),
        )
    }
}

@Composable
private fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    isAddOverlayOpen: Boolean,
    onHomeClick: () -> Unit,
    onChannelClick: () -> Unit,
    onAddClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(HomeSurface)
            .navigationBarsPadding()
            .padding(horizontal = 10.sdp, vertical = 8.sdp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomNavItem(R.drawable.ic_home, R.string.home_nav_home, true, onHomeClick)
        BottomNavItem(R.drawable.ic_channel, R.string.home_nav_channel, false, onChannelClick)
        AddNavButton(isOpen = isAddOverlayOpen, onClick = onAddClick)
        BottomNavItem(R.drawable.ic_favorite, R.string.home_nav_favorite, false, onFavoriteClick)
        BottomNavItem(R.drawable.ic_settings, R.string.home_nav_settings, false, onSettingsClick)
    }
}

@Composable
private fun BottomNavItem(
    iconRes: Int,
    textRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.onSingleClick(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = stringResource(id = textRes),
            modifier = Modifier.size(18.sdp),
        )
        Spacer(modifier = Modifier.height(4.sdp))
        Text(
            text = stringResource(id = textRes),
            color = if (selected) HomePrimary else HomeIconTint,
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.ssp,
        )
    }
}

@Composable
private fun AddNavButton(isOpen: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(46.sdp)
            .clip(CircleShape)
            .background(if (isOpen) HomePrimaryDark else HomePrimary)
            .onSingleClick(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_add_more),
            contentDescription = stringResource(id = R.string.home_plus_description),
            modifier = Modifier.size(18.sdp),
        )
    }
}

@Composable
private fun AddOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    onPlaySingleClick: () -> Unit,
    onImportUrlClick: () -> Unit,
    onImportDeviceClick: () -> Unit,
    onUploadFileClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200, easing = LinearEasing)),
        exit = fadeOut(animationSpec = tween(200, easing = LinearEasing)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HomeOverlay)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 72.sdp),
                verticalArrangement = Arrangement.spacedBy(10.sdp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.sdp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AddOverlayButton(R.drawable.ic_add_stream, R.string.home_add_play_single, onPlaySingleClick)
                        AddOverlayButton(R.drawable.ic_add_link, R.string.home_add_import_url, onImportUrlClick)
                        AddOverlayButton(R.drawable.ic_add_media, R.string.home_add_import_device, onImportDeviceClick)
                        AddOverlayButton(R.drawable.ic_add_file, R.string.home_add_upload_file, onUploadFileClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddOverlayButton(
    iconRes: Int,
    textRes: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.sdp))
            .background(HomeSurface)
            .onSingleClick(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.sdp, vertical = 12.sdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.sdp),
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = stringResource(id = textRes),
            modifier = Modifier.size(16.sdp),
        )
        Text(
            text = stringResource(id = textRes),
            color = HomeTextPrimary,
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.ssp,
        )
    }
}

@Composable
private fun HomePlaylistFilter.toLabel(): String {
    return stringResource(
        id = when (this) {
            HomePlaylistFilter.ALL -> R.string.home_filter_all
            HomePlaylistFilter.URL -> R.string.home_filter_url
            HomePlaylistFilter.STREAM -> R.string.home_filter_stream
            HomePlaylistFilter.FILE -> R.string.home_filter_file
            HomePlaylistFilter.DEVICE -> R.string.home_filter_device
        },
    )
}

private fun SourceType.toSourceIcon(): Int {
    return when (this) {
        SourceType.URL -> R.drawable.ic_link
        SourceType.STREAM -> R.drawable.ic_stream
        SourceType.FILE -> R.drawable.ic_file
        SourceType.DEVICE -> R.drawable.ic_media
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F7FB)
@Composable
private fun HomeScreenEmptyPreview() {
    HomeScreen(
        uiState = HomeUiState(),
        onIntent = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F7FB)
@Composable
private fun HomeScreenContentPreview() {
    HomeScreen(
        uiState = HomeUiState(
            selectedFilter = HomePlaylistFilter.ALL,
            bannerIndex = 1,
            sourceCards = listOf(
                HomeSourceCardUiModel(1, SourceType.FILE, stringResource(id = R.string.home_source_file), stringResource(id = R.string.home_channel_count, 1), 1),
                HomeSourceCardUiModel(2, SourceType.DEVICE, stringResource(id = R.string.home_source_device), stringResource(id = R.string.home_channel_count, 2), 2),
                HomeSourceCardUiModel(3, SourceType.URL, stringResource(id = R.string.home_source_url), stringResource(id = R.string.home_channel_count, 3), 3),
                HomeSourceCardUiModel(4, SourceType.STREAM, stringResource(id = R.string.home_source_stream), stringResource(id = R.string.home_channel_count, 4), 4),
            ),
            recentSection = previewSection(id = "recent", title = stringResource(id = R.string.home_recent_title)),
            favoriteSection = previewSection(id = "favorite", title = stringResource(id = R.string.home_favorite_title)),
            playlistSections = listOf(previewSection(id = "playlist_1", title = "Playlist 1")),
        ),
        onIntent = {},
    )
}

private fun previewSection(id: String, title: String): HomeSectionUiModel {
    return HomeSectionUiModel(
        id = id,
        title = title,
        itemCount = 3,
        items = List(3) { index ->
            HomeChannelUiModel(
                id = index.toLong(),
                title = "Demo Video ${index + 1}",
                thumbnailUrl = "",
                isFavorited = index != 1,
            )
        },
    )
}

private const val HOME_BANNER_INTERVAL_MILLIS = 5_000L
