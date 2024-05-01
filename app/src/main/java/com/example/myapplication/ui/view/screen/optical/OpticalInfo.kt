package com.example.myapplication.ui.view.screen.optical

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.utils.CirclePath
import com.example.myapplication.utils.common.endOffsetForPage
import com.example.myapplication.utils.common.offsetForPage
import com.example.myapplication.utils.common.startOffsetForPage
import com.example.myapplication.utils.Instance
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun OpticalInfo(navController: NavController) {
    val state = rememberPagerState { Instance.opticalModel.count() }
    val (offsetY, setOffSetY) = remember { mutableFloatStateOf(1f) }


    LaunchedEffect(state) {
        var currentPage = state.currentPage
        while (currentPage < state.pageCount) {
            delay(3000) // Wait for 3 seconds
            state.animateScrollToPage(currentPage + 1)
            currentPage++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.2f)
                        .clickable {
                            navController.popBackStack()
                        },
                    colorFilter = ColorFilter.tint(color = Color.White)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "Optical Caliper",
                    fontSize = 30.sp,
                    modifier = Modifier.weight(2f),
                    fontStyle = FontStyle.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalPager(
            modifier = Modifier
                .pointerInteropFilter {
                    setOffSetY(it.y)
                    false
                }
                .padding(16.dp)
                .clip(
                    RoundedCornerShape(18.dp)
                ),
            state = state
        ) { page ->
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .graphicsLayer {
                    val pageOffset = state.offsetForPage(page)
                    translationX = size.width * pageOffset

                    val endOffset = state.endOffsetForPage(page)
                    shadowElevation = 20f
                    shape = CirclePath(
                        progress = 1f - endOffset.absoluteValue,
                        origin = Offset(
                            size.width,
                            offsetY
                        )
                    )
                    clip = true

                    val abslouteOffset = state.offsetForPage(page).absoluteValue
                    val scale = 1f + (abslouteOffset.absoluteValue * .3f)

                    scaleX = scale
                    scaleY = scale

                    val startOffset = state.startOffsetForPage(page)
                    alpha = (2f - startOffset) / 2
                }) {
                val locationModel = Instance.opticalModel[page]
                Image(
                    painter = painterResource(id = locationModel.image),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.land),
            fontSize = 11.sp,
            color = Color.Black,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(20.dp),
            fontWeight = FontWeight.Medium,
            letterSpacing = TextUnit.Unspecified,
            lineHeight = 25.sp,
            textAlign = TextAlign.Center
        )
    }
}
@Preview
@Composable
private fun Test() {
    val navController = rememberNavController()
    OpticalInfo(navController = navController)
}