package com.project.composeproject.ui.screen.language

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.project.composeproject.utils.LanguageUtils

@Composable
fun LanguageScreen() {

    val displayLanguages = LanguageUtils.displayLanguages
    val context = LocalContext.current
    val activity = LocalActivity.current

    Column(
        modifier = Modifier
            .statusBarsPadding()
    ) {
        displayLanguages.forEach { language ->

            Text(
                text = "${language.code} - ${language.name}",
                modifier = Modifier
                    .padding(20.dp)
                    .clickable(
                        onClick = {
                            LanguageUtils.setCurrentLanguage(context, language.code)
                            activity?.recreate()
                        }
                    )
            )

        }
    }
}