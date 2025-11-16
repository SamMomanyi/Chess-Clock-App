package com.example.chess_clock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess_clock.AppUtils.HomeScreenCommand
import com.example.chess_clock.AppUtils.PlayerType
import com.example.chess_clock.AppUtils.TimeScreenState

@Composable
fun editPlayerNameDialog(
    onCommand: (HomeScreenCommand) -> Unit,
    state: TimeScreenState,
    playerType: PlayerType,

    ) {

    val context = LocalContext.current
    var playerName : String by  remember { mutableStateOf(
        if (playerType == PlayerType.ONE) {
            state.player_One_Name
        } else {
            state.player_Two_Name
        }
    )
    }

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = {

        },
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                text = "enter name"
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    value = playerName,
                    onValueChange = {
                        playerName = it
                    },
                    placeholder = {
                        Text(
                            text = "playername",
                            maxLines = 1
                        )
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCommand(
                        HomeScreenCommand.ConfirmSetName(
                            selectedPlayer = playerType,
                            name = playerName
                        )
                    )
                },
            ){
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onCommand (
                        HomeScreenCommand.HideNameDialog
                    )
                }
            ){
                Text("Cancel")
            }
        }
    )
}

@Composable
fun restartClockDialog(
    onCommand: (HomeScreenCommand) -> Unit,
    state: TimeScreenState,
) {

    val context = LocalContext.current

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = {

        },
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                text = "RESTART CLOCK"
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onCommand (
                        HomeScreenCommand.ConfirmRestartClock
                    )
                }
            ){
                Text("Restart")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onCommand (
                        HomeScreenCommand.HideRestartTimerDialog
                    )
                }
            ){
                Text("Cancel")
            }
        }
    )
}