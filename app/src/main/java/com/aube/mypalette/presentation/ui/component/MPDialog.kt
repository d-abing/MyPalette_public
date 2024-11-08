package com.aube.mypalette.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aube.mypalette.R
import com.aube.mypalette.presentation.ui.theme.MyPaletteTheme
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.ui.theme.Sizes

@Composable
fun MPDialog(
    bodyText: String,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onCancelClick() },
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxWidth()
                        .padding(
                            start = Paddings.extra,
                            end = Paddings.extra,
                        )
                ) {
                    Text(
                        text = bodyText,
                        modifier = Modifier
                            .padding(
                                top = Paddings.extra,
                                bottom = Paddings.extra
                            )
                            .align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Sizes.buttonRowHeight)
                    ) {
                        MPSmallButton(R.string.cancel) {
                            onCancelClick()
                        }
                        Spacer(Modifier.width(10.dp))
                        MPSmallButton(R.string.delete) {
                            onDeleteClick()
                        }
                    }
                }
            }
        }
    }

}

@Preview
@Composable
private fun MPDialogPreview() {
    MyPaletteTheme {
        MPDialog(
            bodyText = stringResource(id = R.string.delete_message),
            onDeleteClick = {},
            onCancelClick = {}
        )
    }
}