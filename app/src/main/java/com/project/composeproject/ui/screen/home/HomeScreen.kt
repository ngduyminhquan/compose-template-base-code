package com.project.composeproject.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.composeproject.R
import com.project.composeproject.ui.screen.home.components.AddMenuOverlay
import com.project.composeproject.ui.screen.home.components.ChannelSourceCards
import com.project.composeproject.ui.screen.home.components.HomeBanner
import com.project.composeproject.ui.screen.home.components.HomeBottomNav
import com.project.composeproject.ui.screen.home.components.HomeEmptyState
import com.project.composeproject.ui.screen.home.components.HomeTabs
import com.project.composeproject.ui.screen.home.components.HomeTopBar
import com.project.composeproject.ui.screen.home.components.HorizontalMediaList

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(HomeIntent.LoadHome)
    }

    HomeContent(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit
) {
    Scaffold(
        bottomBar = {
            HomeBottomNav(
                onHomeClicked = {},
                onChannelClicked = { onIntent(HomeIntent.OnChannelTabClicked) },
                onAddClicked = { onIntent(HomeIntent.OnAddClicked) },
                onFavoriteClicked = { onIntent(HomeIntent.OnFavoriteTabClicked) },
                onSettingsClicked = { onIntent(HomeIntent.OnSettingsTabClicked) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFAFAFA))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HomeTopBar(
                    onHelpClicked = { onIntent(HomeIntent.OnHelpClicked) }
                )

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        HomeBanner(bannerIndex = uiState.bannerIndex)
                    }

                    item {
                        HomeTabs(
                            selectedFilter = uiState.selectedFilter,
                            onFilterSelected = { onIntent(HomeIntent.OnFilterSelected(it)) }
                        )
                    }

                    if (uiState.sourceCards.isEmpty() && uiState.playlistSections.isEmpty()) {
                        item {
                            HomeEmptyState(
                                onGetStartedClicked = { onIntent(HomeIntent.OnTutorialGetStartedClicked) },
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    } else {
                        item {
                            ChannelSourceCards(
                                cards = uiState.sourceCards,
                                onCardClicked = {
                                    onIntent(
                                        HomeIntent.OnSourceCardClicked(
                                            it
                                        )
                                    )
                                },
                                onMenuClicked = {
                                    onIntent(
                                        HomeIntent.OnSourceMenuClicked(
                                            it
                                        )
                                    )
                                }
                            )
                        }

                        uiState.recentSection?.let { section ->
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalMediaList(
                                    section = section,
                                    onViewAllClicked = {
                                        onIntent(
                                            HomeIntent.OnSectionViewAllClicked(
                                                it
                                            )
                                        )
                                    },
                                    onChannelClicked = {
                                        onIntent(
                                            HomeIntent.OnChannelClicked(
                                                it
                                            )
                                        )
                                    },
                                    onFavoriteClicked = { id, isFavorited ->
                                        onIntent(
                                            HomeIntent.OnFavoriteClicked(id, isFavorited)
                                        )
                                    },
                                    onChannelMenuClicked = {
                                        onIntent(
                                            HomeIntent.OnChannelMenuClicked(
                                                it
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        uiState.favoriteSection?.let { section ->
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalMediaList(
                                    section = section,
                                    onViewAllClicked = {
                                        onIntent(
                                            HomeIntent.OnSectionViewAllClicked(
                                                it
                                            )
                                        )
                                    },
                                    onChannelClicked = {
                                        onIntent(
                                            HomeIntent.OnChannelClicked(
                                                it
                                            )
                                        )
                                    },
                                    onFavoriteClicked = { id, isFavorited ->
                                        onIntent(
                                            HomeIntent.OnFavoriteClicked(id, isFavorited)
                                        )
                                    },
                                    onChannelMenuClicked = {
                                        onIntent(
                                            HomeIntent.OnChannelMenuClicked(
                                                it
                                            )
                                        )
                                    }
                                )
                            }
                        }

                        items(
                            uiState.playlistSections.size,
                            key = { uiState.playlistSections[it].id }) { index ->
                            val section = uiState.playlistSections[index]
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalMediaList(
                                section = section,
                                onViewAllClicked = {
                                    onIntent(
                                        HomeIntent.OnSectionViewAllClicked(
                                            it
                                        )
                                    )
                                },
                                onChannelClicked = {
                                    onIntent(
                                        HomeIntent.OnChannelClicked(
                                            it
                                        )
                                    )
                                },
                                onFavoriteClicked = { id, isFavorited ->
                                    onIntent(
                                        HomeIntent.OnFavoriteClicked(id, isFavorited)
                                    )
                                },
                                onChannelMenuClicked = {
                                    onIntent(
                                        HomeIntent.OnChannelMenuClicked(
                                            it
                                        )
                                    )
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            // Playlist Source Popup Menu
            if (uiState.openedPlaylistSourceMenuId != null) {
                // Simplified menu, usually requires a proper anchor
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000))
                        .clickable { onIntent(HomeIntent.OnHelpClicked) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_edit_playlist),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onIntent(HomeIntent.OnEditPlaylistClicked) }
                                .padding(16.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.home_delete_playlist),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onIntent(HomeIntent.OnDeletePlaylistClicked) }
                                .padding(16.dp)
                        )
                    }
                }
            }

            // Channel Popup Menu
            if (uiState.openedChannelMenuId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000))
                        .clickable { onIntent(HomeIntent.OnHelpClicked) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.home_edit_channel),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onIntent(HomeIntent.OnEditChannelClicked) }
                                .padding(16.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.home_delete_channel),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onIntent(HomeIntent.OnDeleteChannelClicked) }
                                .padding(16.dp)
                        )
                    }
                }
            }

            if (uiState.isAddOverlayOpen) {
                AddMenuOverlay(
                    onDismissRequest = { onIntent(HomeIntent.OnAddOverlayDismissed) },
                    onPlaySingleUrlClicked = { onIntent(HomeIntent.OnPlaySingleUrlClicked) },
                    onImportPlaylistUrlClicked = { onIntent(HomeIntent.OnImportPlaylistUrlClicked) },
                    onImportFromDeviceClicked = { onIntent(HomeIntent.OnImportFromDeviceClicked) },
                    onUploadM3uFileClicked = { onIntent(HomeIntent.OnUploadM3uFileClicked) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeContent(
        uiState = HomeUiState(),
        onIntent = {}
    )
}