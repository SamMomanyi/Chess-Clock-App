package com.example.chess_clock.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import com.example.chess_clock.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chess_clock.ViewModel.HomeScreenViewModel
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.ui.theme.ActiveColorScheme
import com.example.chess_clock.ui.theme.InactiveColorScheme


@Composable
fun TimerSelection(
    modifier: Modifier,
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    //begimnning of the UI
    Column(
        modifier = modifier
    ) {

        Image(
            painter = painterResource(
                R.drawable.chess_landscape_image,
            ),
            contentDescription = "top landscapeimage",
            modifier = Modifier
                .weight(0.15F)
                .fillMaxWidth()
        )
        LazyColumn (

            modifier = Modifier.weight(0.75F)
        ){
            items(

            ){

            }
        }
        Row(
            modifier = Modifier.weight(0.1F),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            FloatingActionButton(
                onClick = {

                },
                modifier = Modifier.weight(0.35F),
                contentColor = ActiveColorScheme.contentColor,
                containerColor = ActiveColorScheme.backGroundColor
            ){
                Text(
                    text = "Start",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.35F))

            FloatingActionButton(

                onClick = {
                },
                modifier =  Modifier.weight(0.10f)
                    .size(60.dp),
               // shape = ButtonDefaults.elevatedShape,
                contentColor = InactiveColorScheme.contentColor,
                containerColor = InactiveColorScheme.backGroundColor

            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Default.ModeEdit,
                    contentDescription = "Edit Time"
                )
            }

            Spacer(modifier = Modifier.weight(0.05F))

            FloatingActionButton(

                onClick = {

                },
                modifier =  Modifier.weight(0.10f)
                    .size(60.dp),
                contentColor = InactiveColorScheme.contentColor,
                containerColor = InactiveColorScheme.backGroundColor
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Edit Time"
                )
            }
         }
    }
}



@Composable
fun TimerCard(clockFormat : ClockFormat){

}

