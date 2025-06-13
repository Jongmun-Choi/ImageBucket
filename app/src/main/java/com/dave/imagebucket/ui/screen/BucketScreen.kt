package com.dave.imagebucket.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dave.imagebucket.data.model.ImageItem
import com.dave.imagebucket.ui.components.ImageItemCard
import com.dave.imagebucket.viewmodel.BucketViewModel
import androidx.compose.foundation.lazy.grid.items

@Composable
fun BucketScreen(viewModel: BucketViewModel = hiltViewModel()) {

    val bucketItems by viewModel.bucketItems.collectAsState()

    if (bucketItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("저장된 이미지가 없습니다.\n이미지를 검색하여 이미지를 저장해주세요.")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(bucketItems, key = { it.thumbnailUrl }) { item ->
                // Convert BucketItem to SearchResult for the card
                val searchResult = ImageItem(
                    thumbnailUrl = item.thumbnailUrl,
                    dateTime = item.dateTime,
                    isSaved = true
                )
                ImageItemCard(
                    item = searchResult,
                    onSaveClick = { viewModel.removeItem(item) }
                )
            }
        }
    }

}