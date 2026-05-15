package com.project.composeproject.ui.screen.home

import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.composeproject.R
import com.project.composeproject.databinding.ScreenHomeBinding
import com.project.composeproject.domain.model.SourceType
import com.project.composeproject.ui.screen.home.model.HomeChannelUiModel
import com.project.composeproject.ui.screen.home.model.HomePlaylistFilter
import com.project.composeproject.ui.screen.home.model.HomeSectionUiModel
import com.project.composeproject.ui.screen.home.model.HomeSourceCardUiModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AndroidView(
        factory = { context ->
            ScreenHomeBinding.inflate(LayoutInflater.from(context)).apply {
                bindStaticContent()
                bindInteractions(viewModel::onIntent)
                startBannerAutoAdvance(viewModel::onIntent)
            }.root
        },
        update = { root ->
            ScreenHomeBinding.bind(root).render(state, viewModel::onIntent)
        }
    )
}

private fun ScreenHomeBinding.bindStaticContent() {
    val content = contentState
    val context = root.context
    bindSourceCard(cardFile.root, R.drawable.ic_file, context.getString(R.string.home_file), context.getString(R.string.home_one_channel))
    bindSourceCard(cardDevice.root, R.drawable.ic_media, context.getString(R.string.home_device), context.getString(R.string.home_two_videos))
    bindSourceCard(cardUrl.root, R.drawable.ic_link, context.getString(R.string.home_url), context.getString(R.string.home_three_channels))
    bindSourceCard(cardStream.root, R.drawable.ic_stream, context.getString(R.string.home_stream), context.getString(R.string.home_four_channels))

    bindSection(content.getChildAt(1), context.getString(R.string.home_recent), emptyList(), {})
    bindSection(sectionFavorite.root, context.getString(R.string.home_favorite), emptyList(), {})
    bindSection(sectionPlaylist.root, context.getString(R.string.home_playlist), emptyList(), {})

    bindBottomItem(navHome.root, R.drawable.ic_home, R.string.home_nav_home, true)
    bindBottomItem(navChannel.root, R.drawable.ic_channel, R.string.home_nav_channel, false)
    bindBottomItem(navFavorite.root, R.drawable.ic_favorite, R.string.home_nav_favorite, false)
    bindBottomItem(navSettings.root, R.drawable.ic_settings, R.string.home_nav_settings, false)

    bindAddAction(actionPlaySingle.root, R.drawable.ic_add_stream, R.string.home_play_single_url)
    bindAddAction(actionImportUrl.root, R.drawable.ic_add_link, R.string.home_import_playlist_url)
    bindAddAction(actionImportDevice.root, R.drawable.ic_add_media, R.string.home_import_device)
    bindAddAction(actionUploadFile.root, R.drawable.ic_add_file, R.string.home_upload_m3u)

    emptyState.visibility = View.GONE
    contentState.visibility = View.VISIBLE
}

private fun ScreenHomeBinding.bindInteractions(dispatch: (HomeIntent) -> Unit) {
    btnFilterAll.setOnClickListener { dispatch(HomeIntent.OnFilterSelected(HomePlaylistFilter.ALL)) }
    btnFilterUrl.setOnClickListener { dispatch(HomeIntent.OnFilterSelected(HomePlaylistFilter.URL)) }
    btnFilterStream.setOnClickListener { dispatch(HomeIntent.OnFilterSelected(HomePlaylistFilter.STREAM)) }
    btnFilterFile.setOnClickListener { dispatch(HomeIntent.OnFilterSelected(HomePlaylistFilter.FILE)) }
    btnFilterDevice.setOnClickListener { dispatch(HomeIntent.OnFilterSelected(HomePlaylistFilter.DEVICE)) }

    btnAdd.setOnClickListener { dispatch(HomeIntent.OnAddClicked) }
    addOverlayScrim.setOnClickListener { dispatch(HomeIntent.OnAddOverlayDismissed) }
    listOf(actionPlaySingle.root, actionImportUrl.root, actionImportDevice.root, actionUploadFile.root).forEach {
        it.setOnClickListener { dispatch(HomeIntent.OnAddOverlayDismissed) }
    }
}

private fun ScreenHomeBinding.render(state: HomeUiState, dispatch: (HomeIntent) -> Unit) {
    ivBanner.setImageResource(bannerRes(state.bannerIndex))
    renderTabs(state.selectedFilter)
    renderSourceCards(state.sourceCards, dispatch)
    emptyState.visibility = if (state.hasPlaylists) View.GONE else View.VISIBLE
    contentState.visibility = if (state.hasPlaylists) View.VISIBLE else View.GONE
    showAddOverlay(state.isAddOverlayOpen)
    if (!state.hasPlaylists) return

    state.recentSection?.let { bindSection(contentState.getChildAt(1), it.titleWithCount(), it.channels, dispatch) }
    state.favoriteSection?.let { bindSection(sectionFavorite.root, it.titleWithCount(), it.channels, dispatch) }
    renderPlaylistSections(state.playlistSections, dispatch)
}

private fun ScreenHomeBinding.renderTabs(selected: HomePlaylistFilter) {
    val items = mapOf(
        HomePlaylistFilter.ALL to btnFilterAll,
        HomePlaylistFilter.URL to btnFilterUrl,
        HomePlaylistFilter.STREAM to btnFilterStream,
        HomePlaylistFilter.FILE to btnFilterFile,
        HomePlaylistFilter.DEVICE to btnFilterDevice,
    )
    items.forEach { (filter, button) ->
        button.setBackgroundResource(if (filter == selected) R.drawable.bg_home_selected else android.R.color.transparent)
    }
}

private fun ScreenHomeBinding.renderSourceCards(cards: List<HomeSourceCardUiModel>, dispatch: (HomeIntent) -> Unit) {
    val byType = cards.associateBy { it.sourceType }
    bindSourceCard(cardFile.root, R.drawable.ic_file, byType[SourceType.FILE]?.title ?: "File", byType[SourceType.FILE]?.channelText() ?: "0 channels")
    bindSourceCard(cardDevice.root, R.drawable.ic_media, byType[SourceType.DEVICE]?.title ?: "Device", byType[SourceType.DEVICE]?.channelText() ?: "0 channels")
    bindSourceCard(cardUrl.root, R.drawable.ic_link, byType[SourceType.URL]?.title ?: "URL", byType[SourceType.URL]?.channelText() ?: "0 channels")
    bindSourceCard(cardStream.root, R.drawable.ic_stream, byType[SourceType.STREAM]?.title ?: "Stream", byType[SourceType.STREAM]?.channelText() ?: "0 channels")

    cardFile.root.setOnClickListener { dispatch(HomeIntent.OnSourceCardClicked(SourceType.FILE)) }
    cardDevice.root.setOnClickListener { dispatch(HomeIntent.OnSourceCardClicked(SourceType.DEVICE)) }
    cardUrl.root.setOnClickListener { dispatch(HomeIntent.OnSourceCardClicked(SourceType.URL)) }
    cardStream.root.setOnClickListener { dispatch(HomeIntent.OnSourceCardClicked(SourceType.STREAM)) }
    cardFile.btnSourceMore.setOnClickListener { it.showHomePopup(R.string.home_edit_playlist, R.drawable.ic_edit, R.string.home_delete_playlist, R.drawable.ic_delete, dispatch) }
    cardDevice.btnSourceMore.setOnClickListener { it.showHomePopup(R.string.home_edit_playlist, R.drawable.ic_edit, R.string.home_delete_playlist, R.drawable.ic_delete, dispatch) }
    cardUrl.btnSourceMore.setOnClickListener { it.showHomePopup(R.string.home_edit_playlist, R.drawable.ic_edit, R.string.home_delete_playlist, R.drawable.ic_delete, dispatch) }
    cardStream.btnSourceMore.setOnClickListener { it.showHomePopup(R.string.home_edit_playlist, R.drawable.ic_edit, R.string.home_delete_playlist, R.drawable.ic_delete, dispatch) }
}

private fun ScreenHomeBinding.renderPlaylistSections(sections: List<HomeSectionUiModel>, dispatch: (HomeIntent) -> Unit) {
    val staticChildCount = 4
    while (contentState.childCount > staticChildCount) {
        contentState.removeViewAt(staticChildCount)
    }
    if (sections.isEmpty()) {
        sectionPlaylist.root.visibility = View.GONE
        return
    }
    sectionPlaylist.root.visibility = View.VISIBLE
    bindSection(sectionPlaylist.root, sections.first().titleWithCount(), sections.first().channels, dispatch)
    sections.drop(1).forEach { section ->
        val sectionRoot = LayoutInflater.from(root.context).inflate(R.layout.view_home_section, contentState, false)
        bindSection(sectionRoot, section.titleWithCount(), section.channels, dispatch)
        contentState.addView(sectionRoot)
    }
}

private fun ScreenHomeBinding.startBannerAutoAdvance(dispatch: (HomeIntent) -> Unit) {
    val handler = Handler(Looper.getMainLooper())
    val advance = object : Runnable {
        override fun run() {
            dispatch(HomeIntent.OnBannerAutoAdvance)
            handler.postDelayed(this, BANNER_DELAY_MS)
        }
    }
    root.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) {
            handler.postDelayed(advance, BANNER_DELAY_MS)
        }

        override fun onViewDetachedFromWindow(view: View) {
            handler.removeCallbacks(advance)
        }
    })
}

private fun ScreenHomeBinding.showAddOverlay(show: Boolean) {
    addOverlayScrim.visibility = if (show) View.VISIBLE else View.GONE
    addOverlayActions.visibility = if (show) View.VISIBLE else View.GONE
    if (show) {
        addOverlayActions.translationY = addOverlayActions.resources.getDimension(R.dimen.home_gap_lg)
        addOverlayActions.alpha = 0f
        addOverlayActions.animate().translationY(0f).alpha(1f).setDuration(180L).start()
    } else {
        addOverlayActions.animate().translationY(addOverlayActions.resources.getDimension(R.dimen.home_gap_lg)).alpha(0f).setDuration(140L).start()
    }
}

private fun bindSourceCard(root: View, icon: Int, title: String, count: String) {
    root.findViewById<ImageView>(R.id.iv_source_icon).setImageResource(icon)
    root.findViewById<TextView>(R.id.tv_source_title).text = title
    root.findViewById<TextView>(R.id.tv_source_count).text = count
}

private fun bindSection(root: View, title: String, channels: List<HomeChannelUiModel>, dispatch: (HomeIntent) -> Unit) {
    root.findViewById<TextView>(R.id.tv_section_title).text = title
    val row = root.findViewById<LinearLayout>(R.id.video_row)
    row.removeAllViews()
    channels.forEach { channel ->
        val card = LayoutInflater.from(root.context).inflate(R.layout.view_home_video_card, row, false)
        bindVideoCard(card, channel, dispatch)
        row.addView(card)
    }
}

private fun bindBottomItem(root: View, icon: Int, label: Int, selected: Boolean) {
    val context = root.context
    val tint = ContextCompat.getColor(context, if (selected) R.color.home_primary else R.color.home_text_secondary)
    root.findViewById<ImageView>(R.id.iv_nav_icon).apply {
        setImageResource(icon)
        setColorFilter(tint)
        contentDescription = context.getString(label)
    }
    root.findViewById<TextView>(R.id.tv_nav_label).apply {
        setText(label)
        setTextColor(tint)
    }
}

private fun bindAddAction(root: View, icon: Int, label: Int) {
    root.findViewById<ImageView>(R.id.iv_action_icon).setImageResource(icon)
    root.findViewById<TextView>(R.id.tv_action_label).setText(label)
}

private fun bindVideoCard(root: View, channel: HomeChannelUiModel, dispatch: (HomeIntent) -> Unit) {
    root.findViewById<TextView>(R.id.tv_video_title).text = channel.title
    root.findViewById<ImageView>(R.id.iv_video_thumb).contentDescription = channel.title
    root.setOnClickListener { dispatch(HomeIntent.OnChannelClicked(channel.id)) }
    root.findViewById<ImageButton>(R.id.btn_video_favorite).apply {
        isSelected = channel.isFavorited
        setColorFilter(ContextCompat.getColor(context, if (channel.isFavorited) R.color.home_primary else R.color.white))
        setOnClickListener { dispatch(HomeIntent.OnFavoriteClicked(channel.id, channel.isFavorited)) }
    }
    root.findViewById<ImageButton>(R.id.btn_video_more).setOnClickListener { anchor ->
        dispatch(HomeIntent.OnChannelMenuClicked(channel.id))
        anchor.showHomePopup(R.string.home_edit_channel, R.drawable.ic_edit, R.string.home_delete_channel, R.drawable.ic_delete, dispatch)
    }
}

private fun View.showHomePopup(firstLabel: Int, firstIcon: Int, secondLabel: Int, secondIcon: Int, dispatch: (HomeIntent) -> Unit) {
    val context = context
    lateinit var popupWindow: PopupWindow
    val menu = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        background = ContextCompat.getDrawable(context, R.drawable.bg_home_menu)
        addView(menuRow(firstIcon, firstLabel) {
            dispatch(if (firstLabel == R.string.home_edit_channel) HomeIntent.OnEditChannelClicked else HomeIntent.OnEditPlaylistClicked)
            popupWindow.dismiss()
        })
        addView(menuRow(secondIcon, secondLabel) {
            dispatch(if (secondLabel == R.string.home_delete_channel) HomeIntent.OnDeleteChannelClicked else HomeIntent.OnDeletePlaylistClicked)
            popupWindow.dismiss()
        })
    }
    popupWindow = PopupWindow(menu, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true).apply {
        elevation = resources.getDimension(R.dimen.home_gap_sm)
        isOutsideTouchable = true
        showAsDropDown(this@showHomePopup)
    }
}

private fun View.menuRow(icon: Int, label: Int, onClick: () -> Unit): View {
    return LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
        setPadding(
            resources.getDimensionPixelSize(R.dimen.home_gap_md),
            resources.getDimensionPixelSize(R.dimen.home_gap_sm),
            resources.getDimensionPixelSize(R.dimen.home_gap_md),
            resources.getDimensionPixelSize(R.dimen.home_gap_sm)
        )
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)
        setOnClickListener { onClick() }
        addView(ImageView(context).apply {
            setImageResource(icon)
            layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.home_icon_sm),
                resources.getDimensionPixelSize(R.dimen.home_icon_sm)
            )
        })
        addView(TextView(context).apply {
            setText(label)
            setTextColor(ContextCompat.getColor(context, R.color.home_text_primary))
            textSize = 12f
            typeface = resources.getFont(R.font.inter_regular)
            setPadding(resources.getDimensionPixelSize(R.dimen.home_gap_sm), 0, 0, 0)
        })
    }
}

private fun HomeSourceCardUiModel.channelText(): String = "$channelCount channels"

private fun HomeSectionUiModel.titleWithCount(): String = "$title ($totalCount)"

private fun bannerRes(index: Int): Int {
    return when (index % 4) {
        0 -> R.drawable.img_home_banner_1
        1 -> R.drawable.img_home_banner_2
        2 -> R.drawable.img_home_banner_3
        else -> R.drawable.img_home_banner_4
    }
}

private const val BANNER_DELAY_MS = 5_000L
