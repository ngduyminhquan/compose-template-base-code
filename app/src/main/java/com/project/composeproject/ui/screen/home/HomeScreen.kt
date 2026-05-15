package com.project.composeproject.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.composeproject.R
import com.project.composeproject.domain.model.SourceType
import com.project.composeproject.ui.screen.home.model.HomeChannelUiModel
import com.project.composeproject.ui.screen.home.model.HomePlaylistFilter
import com.project.composeproject.ui.screen.home.model.HomeSectionUiModel
import com.project.composeproject.ui.screen.home.model.HomeSourceCardUiModel
import com.project.composeproject.ui.theme.Gray200
import com.project.composeproject.ui.theme.Gray500
import com.project.composeproject.ui.theme.HomeBackground
import com.project.composeproject.ui.theme.HomeEmptyText
import com.project.composeproject.ui.theme.HomeOverlayBackground
import com.project.composeproject.ui.theme.HomeSectionTitle
import com.project.composeproject.ui.theme.HomeTopBarTitle
import com.project.composeproject.ui.theme.HomeTutorialBackground
import com.project.composeproject.ui.theme.HomeTutorialText
import com.project.composeproject.ui.theme.Indigo100
import com.project.composeproject.ui.theme.Indigo500
import com.project.composeproject.ui.theme.Indigo600
import com.project.composeproject.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(HomeIntent.LoadHome)
    }

    HomeScreenContent(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    HomeTopBar(onHelpClicked = { onIntent(HomeIntent.OnHelpClicked) })
                }
                item {
                    HomeBanner(
                        bannerIndex = uiState.bannerIndex,
                        onAutoAdvance = { onIntent(HomeIntent.OnBannerAutoAdvance) }
                    )
                }
                item {
                    HomeTabs(
                        selectedFilter = uiState.selectedFilter,
                        onFilterSelected = { onIntent(HomeIntent.OnFilterSelected(it)) }
                    )
                }

                val isPlaylistEmpty =
                    uiState.sourceCards.isEmpty() && uiState.playlistSections.isEmpty()

                if (isPlaylistEmpty) {
                    item {
                        HomeEmptyState(
                            onGetStartedClicked = { onIntent(HomeIntent.OnTutorialGetStartedClicked) }
                        )
                    }
                } else {
                    item {
                        HomeSourceCards(
                            sourceCards = uiState.sourceCards,
                            openedMenuId = uiState.openedPlaylistSourceMenuId,
                            onCardClicked = { onIntent(HomeIntent.OnSourceCardClicked(it)) },
                            onMenuClicked = { onIntent(HomeIntent.OnSourceMenuClicked(it)) },
                            onEditPlaylist = { onIntent(HomeIntent.OnEditPlaylistClicked) },
                            onDeletePlaylist = { onIntent(HomeIntent.OnDeletePlaylistClicked) },
                            onDismissMenu = { onIntent(HomeIntent.OnEditPlaylistClicked) /* Trick to dismiss */ }
                        )
                    }

                    uiState.recentSection?.let { section ->
                        item {
                            HomeSection(
                                section = section,
                                openedMenuId = uiState.openedChannelMenuId,
                                onChannelClicked = { onIntent(HomeIntent.OnChannelClicked(it)) },
                                onFavoriteClicked = { id, isFav ->
                                    onIntent(
                                        HomeIntent.OnFavoriteClicked(
                                            id,
                                            isFav
                                        )
                                    )
                                },
                                onMenuClicked = { onIntent(HomeIntent.OnChannelMenuClicked(it)) },
                                onViewAllClicked = {
                                    onIntent(
                                        HomeIntent.OnSectionViewAllClicked(
                                            section.id
                                        )
                                    )
                                },
                                onEditChannel = { onIntent(HomeIntent.OnEditChannelClicked) },
                                onDeleteChannel = { onIntent(HomeIntent.OnDeleteChannelClicked) },
                                onDismissMenu = { onIntent(HomeIntent.OnEditChannelClicked) }
                            )
                        }
                    }

                    uiState.favoriteSection?.let { section ->
                        item {
                            HomeSection(
                                section = section,
                                openedMenuId = uiState.openedChannelMenuId,
                                onChannelClicked = { onIntent(HomeIntent.OnChannelClicked(it)) },
                                onFavoriteClicked = { id, isFav ->
                                    onIntent(
                                        HomeIntent.OnFavoriteClicked(
                                            id,
                                            isFav
                                        )
                                    )
                                },
                                onMenuClicked = { onIntent(HomeIntent.OnChannelMenuClicked(it)) },
                                onViewAllClicked = {
                                    onIntent(
                                        HomeIntent.OnSectionViewAllClicked(
                                            section.id
                                        )
                                    )
                                },
                                onEditChannel = { onIntent(HomeIntent.OnEditChannelClicked) },
                                onDeleteChannel = { onIntent(HomeIntent.OnDeleteChannelClicked) },
                                onDismissMenu = { onIntent(HomeIntent.OnEditChannelClicked) }
                            )
                        }
                    }

                    items(uiState.playlistSections, key = { it.id }) { section ->
                        HomeSection(
                            section = section,
                            openedMenuId = uiState.openedChannelMenuId,
                            onChannelClicked = { onIntent(HomeIntent.OnChannelClicked(it)) },
                            onFavoriteClicked = { id, isFav ->
                                onIntent(
                                    HomeIntent.OnFavoriteClicked(
                                        id,
                                        isFav
                                    )
                                )
                            },
                            onMenuClicked = { onIntent(HomeIntent.OnChannelMenuClicked(it)) },
                            onViewAllClicked = { onIntent(HomeIntent.OnSectionViewAllClicked(section.id)) },
                            onEditChannel = { onIntent(HomeIntent.OnEditChannelClicked) },
                            onDeleteChannel = { onIntent(HomeIntent.OnDeleteChannelClicked) },
                            onDismissMenu = { onIntent(HomeIntent.OnEditChannelClicked) }
                        )
                    }
                }
            }

            HomeBottomNavigation(
                onHomeClicked = { },
                onChannelClicked = { onIntent(HomeIntent.OnChannelTabClicked) },
                onAddClicked = { onIntent(HomeIntent.OnAddClicked) },
                onFavoriteClicked = { onIntent(HomeIntent.OnFavoriteTabClicked) },
                onSettingsClicked = { onIntent(HomeIntent.OnSettingsTabClicked) }
            )
        }

        if (uiState.isAddOverlayOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HomeOverlayBackground)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onIntent(HomeIntent.OnAddOverlayDismissed) }
            )
        }

        AnimatedVisibility(
            visible = uiState.isAddOverlayOpen,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AddOverlayContent(
                onPlaySingle = { onIntent(HomeIntent.OnPlaySingleUrlClicked) },
                onImportUrl = { onIntent(HomeIntent.OnImportPlaylistUrlClicked) },
                onImportDevice = { onIntent(HomeIntent.OnImportFromDeviceClicked) },
                onUploadM3u = { onIntent(HomeIntent.OnUploadM3uFileClicked) }
            )
        }
    }
}

@Composable
fun HomeTopBar(onHelpClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_premium),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(id = R.string.home_title),
            color = HomeTopBarTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_help),
            contentDescription = null,
            tint = Gray500,
            modifier = Modifier
                .size(24.dp)
                .clickable { onHelpClicked() }
        )
    }
}

@Composable
fun HomeBanner(bannerIndex: Int, onAutoAdvance: () -> Unit) {
    LaunchedEffect(bannerIndex) {
        delay(5000)
        onAutoAdvance()
    }

    val banners = listOf(
        R.drawable.img_home_banner_1,
        R.drawable.img_home_banner_2,
        R.drawable.img_home_banner_3,
        R.drawable.img_home_banner_4
    )
    val imageRes = banners.getOrNull(bannerIndex) ?: R.drawable.img_home_banner_1

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
fun HomeTabs(
    selectedFilter: HomePlaylistFilter,
    onFilterSelected: (HomePlaylistFilter) -> Unit
) {
    val tabs = listOf(
        HomePlaylistFilter.ALL to R.drawable.ic_all,
        HomePlaylistFilter.URL to R.drawable.ic_link,
        HomePlaylistFilter.STREAM to R.drawable.ic_stream,
        HomePlaylistFilter.FILE to R.drawable.ic_file,
        HomePlaylistFilter.DEVICE to R.drawable.ic_media
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .background(White, RoundedCornerShape(12.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEach { (filter, iconRes) ->
            val isSelected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Indigo100 else Color.Transparent)
                    .clickable { onFilterSelected(filter) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = filter.name,
                    tint = if (isSelected) Indigo600 else Gray500,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun HomeEmptyState(onGetStartedClicked: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(HomeTutorialBackground)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.home_tutorial_title),
                        color = HomeTutorialText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.home_tutorial_desc),
                        color = HomeTutorialText.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onGetStartedClicked,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Indigo600
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_tutorial_btn),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.img_banner_tutorial_background),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_empty_playlist),
                contentDescription = null,
                tint = Gray500,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.home_empty_title),
                color = HomeSectionTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Add new playlist by click ",
                    color = HomeEmptyText,
                    fontSize = 14.sp
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Indigo500, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_more),
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Text(
                    text = " button",
                    color = HomeEmptyText,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HomeSourceCards(
    sourceCards: List<HomeSourceCardUiModel>,
    openedMenuId: Long?,
    onCardClicked: (SourceType) -> Unit,
    onMenuClicked: (SourceType) -> Unit,
    onEditPlaylist: () -> Unit,
    onDeletePlaylist: () -> Unit,
    onDismissMenu: () -> Unit
) {
    val types = listOf(SourceType.FILE, SourceType.DEVICE, SourceType.URL, SourceType.STREAM)

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SourceCard(
                sourceType = SourceType.FILE,
                iconRes = R.drawable.ic_file,
                cards = sourceCards,
                modifier = Modifier.weight(1f),
                openedMenuId = openedMenuId,
                onCardClicked = onCardClicked,
                onMenuClicked = onMenuClicked,
                onEdit = onEditPlaylist,
                onDelete = onDeletePlaylist,
                onDismiss = onDismissMenu
            )
            SourceCard(
                sourceType = SourceType.DEVICE,
                iconRes = R.drawable.ic_media,
                cards = sourceCards,
                modifier = Modifier.weight(1f),
                openedMenuId = openedMenuId,
                onCardClicked = onCardClicked,
                onMenuClicked = onMenuClicked,
                onEdit = onEditPlaylist,
                onDelete = onDeletePlaylist,
                onDismiss = onDismissMenu
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SourceCard(
                sourceType = SourceType.URL,
                iconRes = R.drawable.ic_link,
                cards = sourceCards,
                modifier = Modifier.weight(1f),
                openedMenuId = openedMenuId,
                onCardClicked = onCardClicked,
                onMenuClicked = onMenuClicked,
                onEdit = onEditPlaylist,
                onDelete = onDeletePlaylist,
                onDismiss = onDismissMenu
            )
            SourceCard(
                sourceType = SourceType.STREAM,
                iconRes = R.drawable.ic_stream,
                cards = sourceCards,
                modifier = Modifier.weight(1f),
                openedMenuId = openedMenuId,
                onCardClicked = onCardClicked,
                onMenuClicked = onMenuClicked,
                onEdit = onEditPlaylist,
                onDelete = onDeletePlaylist,
                onDismiss = onDismissMenu
            )
        }
    }
}

@Composable
fun SourceCard(
    sourceType: SourceType,
    iconRes: Int,
    cards: List<HomeSourceCardUiModel>,
    modifier: Modifier = Modifier,
    openedMenuId: Long?,
    onCardClicked: (SourceType) -> Unit,
    onMenuClicked: (SourceType) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val cardData = cards.find { it.sourceType == sourceType }
    val count = cardData?.channelCount ?: 0
    val isMenuOpen = openedMenuId == sourceType.ordinal.toLong()

    Box(
        modifier = modifier
            .background(White, RoundedCornerShape(12.dp))
            .clickable { onCardClicked(sourceType) }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Indigo600,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sourceType.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeSectionTitle
                )
                Text(
                    text = "$count channels",
                    fontSize = 12.sp,
                    color = Gray500
                )
            }
            Box {
                Icon(
                    painter = painterResource(id = R.drawable.ic_channel_option),
                    contentDescription = null,
                    tint = Gray500,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onMenuClicked(sourceType) }
                )
                DropdownMenu(
                    expanded = isMenuOpen,
                    onDismissRequest = onDismiss
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.home_menu_edit_playlist)) },
                        onClick = onEdit,
                        leadingIcon = {
                            Icon(
                                painterResource(id = R.drawable.ic_edit),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.home_menu_delete_playlist)) },
                        onClick = onDelete,
                        leadingIcon = {
                            Icon(
                                painterResource(id = R.drawable.ic_delete),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeSection(
    section: HomeSectionUiModel,
    openedMenuId: Long?,
    onChannelClicked: (Long) -> Unit,
    onFavoriteClicked: (Long, Boolean) -> Unit,
    onMenuClicked: (Long) -> Unit,
    onViewAllClicked: () -> Unit,
    onEditChannel: () -> Unit,
    onDeleteChannel: () -> Unit,
    onDismissMenu: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${section.title} (${section.count})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HomeSectionTitle
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onViewAllClicked() }
            ) {
                Text(
                    text = stringResource(id = R.string.home_view_all),
                    fontSize = 14.sp,
                    color = Gray500
                )
                Spacer(modifier = Modifier.width(4.dp))
                // A simple arrow right icon (can use default or just skip if no custom icon provided, using > symbol for simplicity)
                Text(">", fontSize = 14.sp, color = Gray500)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(section.items, key = { it.id }) { item ->
                ChannelItem(
                    item = item,
                    isMenuOpen = openedMenuId == item.id,
                    onClick = { onChannelClicked(item.id) },
                    onFavorite = { onFavoriteClicked(item.id, item.isFavorited) },
                    onMenuClick = { onMenuClicked(item.id) },
                    onEdit = onEditChannel,
                    onDelete = onDeleteChannel,
                    onDismiss = onDismissMenu
                )
            }
        }
    }
}

@Composable
fun ChannelItem(
    item: HomeChannelUiModel,
    isMenuOpen: Boolean,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    onMenuClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Gray200) // Placeholder background
            .clickable(onClick = onClick)
    ) {
        // Thumbnail would go here. Using a placeholder image for now as requested by UI design.
        Image(
            painter = painterResource(id = R.drawable.img_onboarding_1), // Mock thumbnail
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient or dark overlay for text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite),
                    contentDescription = null,
                    tint = if (item.isFavorited) Color.Red else White,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onFavorite() }
                )
                Box {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_channel_option),
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onMenuClick() }
                    )
                    DropdownMenu(
                        expanded = isMenuOpen,
                        onDismissRequest = onDismiss
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.home_menu_edit_channel)) },
                            onClick = onEdit,
                            leadingIcon = {
                                Icon(
                                    painterResource(id = R.drawable.ic_edit),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.home_menu_delete_channel)) },
                            onClick = onDelete,
                            leadingIcon = {
                                Icon(
                                    painterResource(id = R.drawable.ic_delete),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                    }
                }
            }
            Text(
                text = item.name,
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HomeBottomNavigation(
    onHomeClicked: () -> Unit,
    onChannelClicked: () -> Unit,
    onAddClicked: () -> Unit,
    onFavoriteClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter)
                .background(White),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = R.drawable.ic_home,
                label = "Home",
                isSelected = true,
                onClick = onHomeClicked
            )
            BottomNavItem(
                icon = R.drawable.ic_channel,
                label = "Channel",
                isSelected = false,
                onClick = onChannelClicked
            )
            Spacer(modifier = Modifier.width(64.dp)) // Space for center FAB
            BottomNavItem(
                icon = R.drawable.ic_favorite,
                label = "Favorite",
                isSelected = false,
                onClick = onFavoriteClicked
            )
            BottomNavItem(
                icon = R.drawable.ic_settings,
                label = "Settings",
                isSelected = false,
                onClick = onSettingsClicked
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(64.dp)
                .background(HomeBackground, CircleShape)
                .padding(8.dp)
                .background(Indigo500, CircleShape)
                .clickable { onAddClicked() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_more),
                contentDescription = "Add",
                tint = White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BottomNavItem(icon: Int, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = if (isSelected) Indigo600 else Gray500,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (isSelected) Indigo600 else Gray500,
            fontSize = 12.sp
        )
    }
}

@Composable
fun AddOverlayContent(
    onPlaySingle: () -> Unit,
    onImportUrl: () -> Unit,
    onImportDevice: () -> Unit,
    onUploadM3u: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 90.dp
            ), // Padding bottom to stay above the bottom nav + button
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AddOverlayButton(
            icon = R.drawable.ic_stream,
            text = stringResource(id = R.string.home_add_play_single),
            onClick = onPlaySingle
        )
        AddOverlayButton(
            icon = R.drawable.ic_add_link,
            text = stringResource(id = R.string.home_add_import_url),
            onClick = onImportUrl
        )
        AddOverlayButton(
            icon = R.drawable.ic_add_media,
            text = stringResource(id = R.string.home_add_import_device),
            onClick = onImportDevice
        )
        AddOverlayButton(
            icon = R.drawable.ic_add_file,
            text = stringResource(id = R.string.home_add_upload_m3u),
            onClick = onUploadM3u
        )
    }
}

@Composable
fun AddOverlayButton(icon: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(White, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Indigo600,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = HomeSectionTitle,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeEmptyState() {
    HomeEmptyState(onGetStartedClicked = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeSourceCards() {
    val mockCards = listOf(
        HomeSourceCardUiModel(1, SourceType.FILE, "File", 1),
        HomeSourceCardUiModel(2, SourceType.DEVICE, "Device", 2),
        HomeSourceCardUiModel(3, SourceType.URL, "Url", 3),
        HomeSourceCardUiModel(4, SourceType.STREAM, "Stream", 4)
    )
    HomeSourceCards(
        sourceCards = mockCards,
        openedMenuId = null,
        onCardClicked = {},
        onMenuClicked = {},
        onEditPlaylist = {},
        onDeletePlaylist = {},
        onDismissMenu = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeSection() {
    val mockItems = listOf(
        HomeChannelUiModel(1, "Demo Video 1", "", true),
        HomeChannelUiModel(2, "Demo Video 2", "", false)
    )
    val section = HomeSectionUiModel("1", "Recent", 2, mockItems)
    HomeSection(
        section = section,
        openedMenuId = null,
        onChannelClicked = {},
        onFavoriteClicked = { _, _ -> },
        onMenuClicked = {},
        onViewAllClicked = {},
        onEditChannel = {},
        onDeleteChannel = {},
        onDismissMenu = {}
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PreviewHomeScreenWithData() {
    val mockCards = listOf(
        HomeSourceCardUiModel(1, SourceType.FILE, "File", 1),
        HomeSourceCardUiModel(2, SourceType.DEVICE, "Device", 2),
        HomeSourceCardUiModel(3, SourceType.URL, "Url", 3),
        HomeSourceCardUiModel(4, SourceType.STREAM, "Stream", 4)
    )
    val mockItems = listOf(
        HomeChannelUiModel(1, "Demo Video 1", "", true),
        HomeChannelUiModel(2, "Demo Video 2", "", false)
    )
    val uiState = HomeUiState(
        sourceCards = mockCards,
        recentSection = HomeSectionUiModel("1", "Recent(3)", 3, mockItems),
        favoriteSection = HomeSectionUiModel("2", "Favorite(1)", 1, mockItems),
        playlistSections = listOf(HomeSectionUiModel("3", "Playlist 1(3)", 3, mockItems))
    )
    HomeScreenContent(uiState = uiState, onIntent = {})
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PreviewHomeScreenEmpty() {
    HomeScreenContent(uiState = HomeUiState(), onIntent = {})
}
