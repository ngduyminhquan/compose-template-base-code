package com.project.composeproject.ui.screen.language

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.project.composeproject.R
import com.project.composeproject.ui.theme.DefaultFontFamily
import com.project.composeproject.ui.theme.LanguageBackground
import com.project.composeproject.ui.theme.LanguageItemBackground
import com.project.composeproject.ui.theme.LanguagePrimary
import com.project.composeproject.ui.theme.LanguageSecondary
import com.project.composeproject.utils.LanguageItem
import com.project.composeproject.utils.LanguageUtils
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp

@Composable
fun LanguageScreen() {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val languages = remember { LanguageUtils.displayLanguages }
    var selectedLanguageCode by remember { mutableStateOf(LanguageUtils.getCurrentLanguage(context).code) }

    LanguageContent(
        languages = languages,
        selectedLanguageCode = selectedLanguageCode,
        onLanguageClick = { selectedLanguageCode = it },
        onDoneClick = {
            LanguageUtils.setCurrentLanguage(context, selectedLanguageCode)
            activity?.recreate()
        }
    )
}

@Composable
private fun LanguageContent(
    languages: List<LanguageItem>,
    selectedLanguageCode: String,
    onLanguageClick: (String) -> Unit,
    onDoneClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LanguageBackground)
            .statusBarsPadding()
    ) {
        LanguageTopBar(onDoneClick = onDoneClick)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.sdp)
        ) {
            items(
                items = languages,
                key = { it.code }
            ) { language ->
                LanguageItemRow(
                    language = language,
                    selected = language.code == selectedLanguageCode,
                    onClick = { onLanguageClick(language.code) }
                )
            }
        }
    }
}

@Composable
private fun LanguageTopBar(onDoneClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.sdp)
            .padding(horizontal = 12.sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.language_title),
            color = LanguagePrimary,
            fontSize = 17.ssp,
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.selectable(
                selected = false,
                onClick = onDoneClick
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_language_done),
                contentDescription = stringResource(R.string.language_done_icon_description),
                tint = LanguagePrimary,
                modifier = Modifier.size(24.sdp)
            )
            Spacer(modifier = Modifier.width(2.sdp))
            Text(
                text = stringResource(R.string.language_done),
                color = LanguagePrimary,
                fontSize = 13.ssp,
                fontFamily = DefaultFontFamily,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun LanguageItemRow(
    language: LanguageItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.sdp, end = 12.sdp, bottom = 10.sdp)
            .clip(shape)
            .background(LanguageItemBackground)
            .then(
                if (selected) {
                    Modifier.border(1.sdp, LanguagePrimary, shape)
                } else {
                    Modifier
                }
            )
            .selectable(
                selected = selected,
                onClick = onClick
            )
            .padding(12.sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = LanguagePrimary,
                unselectedColor = LanguageSecondary
            )
        )
        Spacer(modifier = Modifier.width(8.sdp))
        Text(
            text = language.name,
            color = LanguagePrimary,
            fontSize = 16.ssp,
            fontFamily = DefaultFontFamily,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 504)
@Composable
private fun LanguageScreenPreview() {
    LanguageContent(
        languages = listOf(
            LanguageItem("en", "English"),
            LanguageItem("de", "German"),
            LanguageItem("ar", "Arabic"),
            LanguageItem("hi", "Hindi"),
            LanguageItem("es", "Spanish"),
            LanguageItem("zh", "Mandarin"),
            LanguageItem("fr", "French")
        ),
        selectedLanguageCode = "en",
        onLanguageClick = {},
        onDoneClick = {}
    )
}
