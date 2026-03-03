package com.example.chess_clock.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.chess_clock.ViewModel.AddTimerCommand
import com.example.chess_clock.ViewModel.AddTimerEvent
import com.example.chess_clock.ViewModel.AddTimerViewModel
import com.example.chess_clock.ui.ObserverAsEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimerScreen(
    navController: NavController,
    viewModel: AddTimerViewModel = hiltViewModel(),
) {
    val form by viewModel.formState.collectAsStateWithLifecycle()

    // Navigate back once the save completes
    ObserverAsEvents(viewModel.events) { event ->
        when (event) {
            AddTimerEvent.SavedAndNavigateBack -> navController.popBackStack()
            is AddTimerEvent.ShowError -> { /* could show a snackbar */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Clock", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // ── Name ──────────────────────────────────────────────────────────
            SectionLabel("Clock name")
            OutlinedTextField(
                value         = form.name,
                onValueChange = { viewModel.onCommand(AddTimerCommand.NameChanged(it)) },
                modifier      = Modifier.fillMaxWidth(),
                label         = { Text("e.g. Bullet: 1|0") },
                singleLine    = true,
                isError       = form.nameError != null,
                supportingText = form.nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            // ── Time ──────────────────────────────────────────────────────────
            SectionLabel("Starting time")
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value         = form.minutes,
                    onValueChange = { viewModel.onCommand(AddTimerCommand.MinutesChanged(it)) },
                    modifier      = Modifier.weight(1f),
                    label         = { Text("Minutes") },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError       = form.timeError != null,
                )
                OutlinedTextField(
                    value         = form.seconds,
                    onValueChange = { viewModel.onCommand(AddTimerCommand.SecondsChanged(it)) },
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

            // ── Increment ─────────────────────────────────────────────────────
            SectionLabel("Increment (seconds added after each move)")
            OutlinedTextField(
                value         = form.incrementSec,
                onValueChange = { viewModel.onCommand(AddTimerCommand.IncrementChanged(it)) },
                modifier      = Modifier.fillMaxWidth(),
                label         = { Text("Increment (0 = none)") },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            // ── Delay ─────────────────────────────────────────────────────────
            SectionLabel("Delay (optional, leave blank for none)")
            OutlinedTextField(
                value         = form.delaySec,
                onValueChange = { viewModel.onCommand(AddTimerCommand.DelayChanged(it)) },
                modifier      = Modifier.fillMaxWidth(),
                label         = { Text("Delay in seconds") },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Buttons ───────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick   = { navController.popBackStack() },
                    modifier  = Modifier.weight(1f),
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick  = { viewModel.onCommand(AddTimerCommand.Save) },
                    modifier = Modifier.weight(1f),
                    enabled  = !form.isSaving,
                ) {
                    Text(if (form.isSaving) "Saving…" else "Save Clock")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text       = text,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        color      = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}