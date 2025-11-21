package com.example.chess_clock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chess_clock.ViewModel.clockViewModel
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.ui.theme.ActiveColorScheme
import com.example.chess_clock.ui.theme.InactiveColorScheme


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


        LazyColumn (
            modifier = Modifier.weight(0.9F)
        ){

        }

        Row(
            modifier = Modifier.weight(0.1F),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

//            ExtendedFloatingActionButton(
//                onClick = { /* do something */ },
//                text = { Text(text = "Extended FAB") },
//                icon = { Icon(Icons. Filled. Add, "Localized description") },
//            )

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

