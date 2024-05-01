package com.example.myapplication.ui.view.screen.optical


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.myapplication.ui.view.navigation.Screen

@Composable
fun OpticalLand(
    navController: NavController,
) {
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
            Text(
                text = "Optical Caliper",
                fontSize = 30.sp,
                fontStyle = FontStyle.Normal,
                color = Color.White,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.caliper1),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp)
                .clip(shape = ShapeDefaults.Medium)
        )

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

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                      navController.navigateUp()
                navController.navigate(Screen.OpticalSet.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(text = "Ok", color = Color.White, fontSize = 20.sp)
        }

    }
}

@Preview
@Composable
private fun Test() {
    val navController = rememberNavController()
    OpticalLand(navController = navController)
}