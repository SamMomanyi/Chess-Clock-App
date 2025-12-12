package com.example.chess_clock.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material.icons.filled.NearbyError
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import com.example.chess_clock.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chess_clock.AppUtils.HomeScreenCommand
import com.example.chess_clock.AppUtils.TimerSelectionCommands
import com.example.chess_clock.ViewModel.HomeScreenViewModel
import com.example.chess_clock.ViewModel.TimerSelectionViewModel
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.model.database.clocks.DatabaseResponse
import com.example.chess_clock.ui.theme.ActiveColorScheme
import com.example.chess_clock.ui.theme.InactiveColorScheme


@Composable
fun TimerSelection(
    modifier: Modifier,
    navController: NavController,
    viewModel: TimerSelectionViewModel = hiltViewModel()
) {


    val  state = viewModel.state
    val clocks = viewModel.dbResponse.collectAsState()
    val onCommand = viewModel::TimerSelectionCommandHandler

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
        ) {

            when (val result = clocks.value) {

                is DatabaseResponse.Success -> {
                    if(result.data.isEmpty()){
                        item{
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No clock found", color = Color.White)
                                Text("Click + to add one",color = Color.White)
                            }
                        }
                    }
                    else {
                        items(result.data) { clocks -> TimerCard(clocks, onCommand) }
                    }}

                DatabaseResponse.isLoading -> {
                   item{ CircularProgressIndicator() }
                }

                is DatabaseResponse.Failed -> {
                    item { FailedScreen(result.message ?: "Failed to load Clocks") }
                }

            }
        }
        Row(
            modifier = Modifier.weight(0.1F),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            FloatingActionButton(
                onClick = {
                    onCommand(TimerSelectionCommands.StartButtonClicked)
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
                    onCommand(TimerSelectionCommands.EditTimerClicked)
                },
                modifier =  Modifier
                    .weight(0.10f)
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
                    onCommand(TimerSelectionCommands.EditTimerClicked)
                },
                modifier =  Modifier
                    .weight(0.10f)
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
fun TimerCard(clock : ClockFormat,onCommand: (TimerSelectionCommands) -> Unit) {
    Card(
        modifier = Modifier
            .padding(
                start = 10.dp,
                top = 3.dp,
                end = 10.dp
            )
            .fillMaxWidth()
            .height(65.dp),
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(
            contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceVariant),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp,
            pressedElevation = 20.dp
        )

    ) {

        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .weight(0.2f)
                            .padding(
                                start = 13.5.dp
                            ),
                        text = clock.name,
                        style = TextStyle(
                            color = Color.LightGray,
                            fontWeight = FontWeight.Light
                        )
                    )
                    Row(
                        modifier = Modifier
                            .weight(0.8f)
                            .padding(
                                top = 8.dp,
                                start = 3.dp
                            )
                    ) {
                        Text(
                            text = "${clock.countDown}",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                fontSize = 20.sp
                            )
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = "${clock.delay}",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                fontSize = 20.sp
                            )
                        )
                    }
                }



            }
        }
    }
}

    @Composable
    fun FailedScreen(message: String) {

        ExtendedFloatingActionButton(
            onClick = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.NearbyError,
                    contentDescription = "error icon"
                )
            },
            text = {
                Text(message)
            },
            containerColor = Color.Cyan,
            contentColor = Color.White
        )

    }