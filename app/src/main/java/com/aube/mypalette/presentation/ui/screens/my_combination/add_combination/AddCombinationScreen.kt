package com.aube.mypalette.presentation.ui.screens.my_combination.add_combination

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aube.mypalette.R
import com.aube.mypalette.data.model.ColorEntity
import com.aube.mypalette.presentation.ui.screens.my_palette.content.calculateColorSize
import com.aube.mypalette.presentation.ui.theme.Paddings
import com.aube.mypalette.presentation.ui.theme.PurpleGrey40
import com.aube.mypalette.presentation.ui.theme.Sizes
import com.aube.mypalette.presentation.viewmodel.ColorViewModel


@Composable
fun AddCombinationScreen(
    newCombination: SnapshotStateList<Int>,
    colorViewModel: ColorViewModel,
    addColor: (Int) -> Unit,
    removeColor: (Int) -> Unit,
    onBackPressed: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Paddings.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NewCombination(newCombination, removeColor)
        MyPaletteColor(colorViewModel, addColor)
    }

    BackHandler {
        onBackPressed()
    }
}

@Composable
fun NewCombination(newCombination: SnapshotStateList<Int>, removeColor: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Sizes.newCombinationRowHeight)
            .border(1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(Paddings.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        newCombination.forEach { colorItem ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color(colorItem))
                    .clickable {
                        removeColor(colorItem)
                    }
            ) {}
        }
    }
}

@Composable
fun MyPaletteColor(colorViewModel: ColorViewModel, addColor: (Int) -> Unit) {
    val colorList by colorViewModel.allColors.observeAsState(emptyList())

    if (colorList.isEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Sizes.colorLazyVerticalGridHeight)
                .border(1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.empty),
                color = PurpleGrey40
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = Sizes.colorCardSize),
            modifier = Modifier
                .fillMaxWidth()
                .height(Sizes.colorLazyVerticalGridHeight)
                .border(1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
                .padding(Paddings.large)
        ) {
            items(colorList) { colorItem ->
                ClickablePaletteColorItem(colorItem, addColor)
            }
        }
    }
}

@Composable
fun ClickablePaletteColorItem(colorItem: ColorEntity, addColor: (Int) -> Unit) {

    val actualSize = calculateColorSize(Sizes.colorCardSize, Paddings.large * 2, Paddings.small)

    Card(
        modifier = Modifier
            .padding(Paddings.small)
            .width(Sizes.colorCardSize)
            .height(actualSize)
            .clip(RoundedCornerShape(12.dp))
            .clickable { addColor(colorItem.color) },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(colorItem.color)
        )
    ) {}
}