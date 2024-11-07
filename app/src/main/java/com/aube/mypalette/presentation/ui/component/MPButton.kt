package com.aube.mypalette.presentation.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.MPIconButton(icon: Int, contentDescription: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .heightIn(max = 60.dp, min = 60.dp)
            .weight(1f)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(contentDescription),
            Modifier.padding(5.dp)
        )
        Text(text = stringResource(contentDescription))
    }
}

@Composable
fun RowScope.MPSmallButton(contentDescription: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .heightIn(max = 40.dp, min = 40.dp)
            .weight(1f)
    ) {
        Text(text = stringResource(contentDescription))
    }
}