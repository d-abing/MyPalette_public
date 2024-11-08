package com.aube.mypalette.presentation.ui.screens.my_palette.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.presentation.ui.theme.MyPaletteTheme
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.ui.theme.Sizes
import com.aube.mypalette.utils.colorToHexString
import com.aube.mypalette.utils.isColorBright

@Composable
fun ColorGrid(
    modifier: Modifier,
    colorList: List<ColorEntity>,
    onColorSelected: (ColorEntity) -> Unit,
    onTextClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minSize = Sizes.colorCardSize)
    ) {
        items(
            items = colorList,
            key = { colorItem -> colorItem.id }
        ) { colorItem ->
            GalleryColorItem(
                colorItem = colorItem,
                onColorSelected = {
                    onColorSelected(it)
                },
                onTextClick = {
                    onTextClick(it)
                }
            )
        }
    }
}


@Composable
fun GalleryColorItem(
    colorItem: ColorEntity,
    onColorSelected: (ColorEntity) -> Unit,
    onTextClick: (String) -> Unit,
) {
    var isClicking by remember { mutableStateOf(false) }
    val actualSize = calculateColumnWidth(Sizes.colorCardSize)

    Card(
        modifier = Modifier
            .padding(Paddings.small)
            .width(Sizes.colorCardSize)
            .height(actualSize)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onColorSelected(colorItem)
                    },
                    onTap = {
                        isClicking = !isClicking
                    }
                )
            },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(colorItem.color)
        ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (isClicking) {
                val hexString = colorToHexString(colorItem.color)
                Text(
                    modifier = Modifier.clickable { onTextClick(hexString) },
                    fontSize = 12.sp,
                    text = hexString,
                    color = if (isColorBright(colorItem.color)) Color.Black else Color.White
                )
            }
        }
    }
}

@Composable
fun calculateColumnWidth(minSize: Dp): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp // 화면의 너비를 dp로 가져옴
    val actualScreenWidth = screenWidth - (Paddings.medium * 2) // 패딩을 제외한 실제 화면 너비 계산
    val columnCount = (actualScreenWidth / minSize).toInt() // 생성될 열의 개수 계산
    val paddingWidth = (columnCount + 1) * Paddings.small // 패딩의 총 너비 계산
    return (actualScreenWidth - paddingWidth) / columnCount // 각 열의 너비 계산
}

@Preview
@Composable
private fun ColorGridPreview() {
    MyPaletteTheme {
        ColorGrid(
            modifier = Modifier
                .padding(Paddings.medium),
            colorList =  listOf(
                ColorEntity(color = -8890344),
                ColorEntity(color = -4165512),
                ColorEntity(color = -16220080),
                ColorEntity(color = -16213968),
                ColorEntity(color = -1525656),
            ),
            onColorSelected = {},
            onTextClick = {},
        )
    }
}

@Preview
@Composable
private fun GalleryColorItemPreview() {
    MyPaletteTheme {
        GalleryColorItem(
            colorItem = ColorEntity(color = -8890344),
            onColorSelected = {},
            onTextClick = {},
        )
    }
}