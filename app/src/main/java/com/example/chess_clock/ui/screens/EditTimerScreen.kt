package com.example.chess_clock.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.chess_clock.ViewModel.EditTimerCommand
import com.example.chess_clock.ViewModel.EditTimerEvent
import com.example.chess_clock.ViewModel.EditTimerViewModel
import com.example.chess_clock.ui.ObserverAsEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTimerScreen(
    navController: NavController,
    viewModel: EditTimerViewModel = hiltViewModel(),
) {
    val form by viewModel.formState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    ObserverAsEvents(viewModel.events) { event ->
        when (event) {
            EditTimerEvent.SavedAndNavigateBack,
            EditTimerEvent.DeletedAndNavigateBack -> navController.popBackStack()
            is EditTimerEvent.ShowError -> { /* could show snackbar */ }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title   = { Text("Delete clock?", fontWeight = FontWeight.Bold) },
            text    = { Text("This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.onCommand(EditTimerCommand.Delete)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Clock", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Delete button in the top bar
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector        = Icons.Default.Delete,
                            contentDescription = "Delete clock",
                            tint               = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        when {
            form.isLoading -> {
                Column(
                    modifier            = Modifier.fillMaxSize().padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) { CircularProgressIndicator() }
            }

            form.notFound -> {
                Column(
                    modifier            = Modifier.fillMaxSize().padding(innerPadding).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("Clock not found.", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { navController.popBackStack() }) { Text("Go back") }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Name ──────────────────────────────────────────────────
                    EditSectionLabel("Clock name")
                    OutlinedTextField(
                        value         = form.name,
                        onValueChange = { viewModel.onCommand(EditTimerCommand.NameChanged(it)) },
                        modifier      = Modifier.fillMaxWidth(),
                        label         = { Text("Name") },
                        singleLine    = true,
                        isError       = form.nameError != null,
                        supportingText = form.nameError?.let {
                            { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    // ── Time ──────────────────────────────────────────────────
                    EditSectionLabel("Starting time")
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedTextField(
                            value         = form.minutes,
                            onValueChange = { viewModel.onCommand(EditTimerCommand.MinutesChanged(it)) },
                            modifier      = Modifier.weight(1f),
                            label         = { Text("Minutes") },
                            singleLine    = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError       = form.timeError != null,
                        )
                        OutlinedTextField(
                            value         = form.seconds,
                            onValueChange = { viewModel.onCommand(EditTimerCommand.SecondsChanged(it)) },
                            modifier      = Modifier.weight(1f),
                            label         = { Text("Seconds") },
                            singleLine    = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError       = form.timeError != null,
                        )
                    }
                    if (form.timeError != null) {
                        Text(form.timeError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    // ── Increment ─────────────────────────────────────────────
                    EditSectionLabel("Increment (seconds per move)")
                    OutlinedTextField(
                        value         = form.incrementSec,
                        onValueChange = { viewModel.onCommand(EditTimerCommand.IncrementChanged(it)) },
                        modifier      = Modifier.fillMaxWidth(),
                        label         = { Text("Increment (0 = none)") },
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    // ── Delay ─────────────────────────────────────────────────
                    EditSectionLabel("Delay (optional, leave blank for none)")
                    OutlinedTextField(
                        value         = form.delaySec,
                        onValueChange = { viewModel.onCommand(EditTimerCommand.DelayChanged(it)) },
                        modifier      = Modifier.fillMaxWidth(),
                        label         = { Text("Delay in seconds") },
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Action buttons ────────────────────────────────────────
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick  = { navController.popBackStack() },
                            modifier = Modifier.weight(1f),
                        ) { Text("Cancel") }

                        Button(
                            onClick  = { viewModel.onCommand(EditTimerCommand.Save) },
                            modifier = Modifier.weight(1f),
                            enabled  = !form.isSaving,
                        ) { Text(if (form.isSaving) "Saving…" else "Save") }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun EditSectionLabel(text: String) {
    Text(
        text       = text,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        color      = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}