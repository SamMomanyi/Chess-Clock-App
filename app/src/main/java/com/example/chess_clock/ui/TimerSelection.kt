package com.example.chess_clock.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chess_clock.ViewModel.clockViewModel
import com.example.chess_clock.model.database.clocks.ClockFormat


@Composable
fun TimerSelection(
    modifier: Modifier,
    navController: NavController,
    viewModel: clockViewModel = hiltViewModel()
) {
    //begimnning of the UI
    Column(
        modifier = modifier
    ) {

        Image(
            painter = painterResource(
                res.drawable.chess_landscape_image
            ),
            contentDescription = "chess landscape",
            modifier = Modifier.weight(0.2f)
        )

        LazyColumn (){

        }

        Box(
            modifier = Modifier.weight(0.2F),
            contentAlignment = Alignment.BottomStart,

        ) {
            FloatingActionButton(

                onClick = {
                },
                shape = ButtonDefaults.elevatedShape,
                contentColor = Color.White,
                containerColor = Color.Yellow

            ) {
                Text(
                    text = "Start"
                )
            }

            FloatingActionButton(

                onClick = {
                },
                shape = ButtonDefaults.elevatedShape,
                contentColor = Color.White,
                containerColor = Color.Blue

            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Time"
                )
            }


            FloatingActionButton(

                onClick = {
                },
                shape = ButtonDefaults.elevatedShape,
                contentColor = Color.White,
                containerColor = Color.Blue

            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Time"
                )
            }
         }
    }
}

@Composable
fun TimerCard(clockFormat : ClockFormat){

}

