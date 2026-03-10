package com.example.chess_clock.ViewModel

import android.media.SoundPool
import android.util.Log
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess_clock.AppUtils.ActivatePlayer
import com.example.chess_clock.AppUtils.AppUtil
import com.example.chess_clock.AppUtils.ColorScheme
import com.example.chess_clock.AppUtils.HomeScreenCommand
import com.example.chess_clock.AppUtils.HomeScreenEvent
import com.example.chess_clock.AppUtils.PlayerData
import com.example.chess_clock.AppUtils.PlayerState
import com.example.chess_clock.AppUtils.PlayerType
import com.example.chess_clock.AppUtils.TimeScreenState
import com.example.chess_clock.R
import com.example.chess_clock.model.SelectedClockRepository
import com.example.chess_clock.model.daggerHilt.MyRepositoryImplementation
import com.example.chess_clock.model.daggerHilt.di.AppContext
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.ui.theme.toColorScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val database: MyRepositoryImplementation,
    private val selectedClockRepo: SelectedClockRepository,
    private val themeRepository: com.example.chess_clock.model.ThemeRepository,
) : ViewModel() {

    // ── Sound ────────────────────────────────────────────────────────────────

    private val soundPool = SoundPool.Builder().setMaxStreams(1).build()
    private var clickSoundId: Int = 0

    // ── Current format ───────────────────────────────────────────────────────

    // Tracks the active ClockFormat so we can apply its increment on turn-switch.
    private var currentFormat: ClockFormat = selectedClockRepo.selectedClock.value

    // ── State flows ──────────────────────────────────────────────────────────

    private val isClockInitial = MutableStateFlow(true)

    private val _countDownTime1 = MutableStateFlow(currentFormat.countDown)
    private val _countDownTime2 = MutableStateFlow(currentFormat.countDown)
    private val _microTime1     = MutableStateFlow(99)
    private val _microTime2     = MutableStateFlow(99)

    private val _playerTimerState1 = MutableStateFlow(PlayerState.INACTIVE)
    private val _playerTimerState2 = MutableStateFlow(PlayerState.INACTIVE)

    private val _player1Name = MutableStateFlow("Player 1")
    private val _player2Name = MutableStateFlow("Player 2")

    private val _player1Moves = MutableStateFlow(0)
    private val _player2Moves = MutableStateFlow(0)

    private val _uiState     = MutableStateFlow(TimeScreenState())
    private val _colorScheme1 = MutableStateFlow(ColorScheme())
    private val _colorScheme2 = MutableStateFlow(ColorScheme())
    private val _activePlayer = MutableStateFlow(ActivatePlayer.NONE)

    // Events channel
    private val eventChannel = Channel<HomeScreenEvent>()
    val events = eventChannel.receiveAsFlow()

    // ── Init ─────────────────────────────────────────────────────────────────

    init {
        clickSoundId = soundPool.load(AppContext.getContext(), R.raw.playertap, 1)

        // FIX: observe the shared repository. When the user picks a new clock on
        //      TimerSelection and taps Start, the format updates here automatically
        //      — but ONLY while the clock hasn't started yet (isClockInitial).
        viewModelScope.launch {
            selectedClockRepo.selectedClock.collect { format ->
                currentFormat = format
                // Apply the new format whenever no game is actively running.
                // Previously checked isClockInitial only, which meant a paused
                // or restarted game would ignore the newly selected format.
                if (_activePlayer.value == ActivatePlayer.NONE) {
                    _countDownTime1.value = format.countDown
                    _countDownTime2.value = format.countDown
                    _microTime1.value = 99
                    _microTime2.value = 99
                }
            }
        }
    }

    // ── Combined state ───────────────────────────────────────────────────────

    private val player1Flow = combine(
        _player1Name, _playerTimerState1, _countDownTime1, _player1Moves, _microTime1
    ) { name, state, time, moves, micro ->
        PlayerData(name = name, state = state, mainTime = time, microTime = micro, playerMoves = moves)
    }

    private val player2Flow = combine(
        _player2Name, _playerTimerState2, _countDownTime2, _player2Moves, _microTime2
    ) { name, state, time, moves, micro ->
        PlayerData(name = name, state = state, mainTime = time, microTime = micro, playerMoves = moves)
    }

    private val _currentTheme = themeRepository.selectedTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, com.example.chess_clock.ui.theme.AppTheme.MIDNIGHT_STEEL)

    private val _pausedPlayer = MutableStateFlow(ActivatePlayer.NONE)

    val state = combine(
        _uiState, player1Flow, player2Flow, _activePlayer, isClockInitial, _pausedPlayer
    ) { array ->
        val uiState      = array[0] as TimeScreenState
        val p1           = array[1] as PlayerData
        val p2           = array[2] as PlayerData
        val activePlayer = array[3] as ActivatePlayer
        val clockInitial = array[4] as Boolean
        val pausedPlayer = array[5] as ActivatePlayer
        val theme        = _currentTheme.value
        uiState.copy(
            colorScheme1    = p1.state.toColorScheme(theme),
            colorScheme2    = p2.state.toColorScheme(theme),
            countDownTime1  = AppUtil.formatTime(p1.mainTime),
            countDownTime2  = AppUtil.formatTime(p2.mainTime),
            player_One_Name = p1.name,
            player_Two_Name = p2.name,
            activePlayer    = activePlayer,
            player1State    = p1.state,
            player2State    = p2.state,
            player1Moves    = p1.playerMoves,
            player2Moves    = p2.playerMoves,
            microTime1      = p1.microTime,
            microTime2      = p2.microTime,
            isClockInitial  = clockInitial,
            pausedPlayer    = pausedPlayer,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TimeScreenState())

    // ── Jobs ─────────────────────────────────────────────────────────────────

    private var playerOneJob: Job? = null
    private var playerTwoJob: Job? = null

    // ── Player start ─────────────────────────────────────────────────────────

    private fun startPlayerOne() {
        cancelJobs()

        // FIX: Apply increment to player 2 who just finished their turn.
        //      Only add if the clock was actually running (not the very first tap).
        if (_activePlayer.value == ActivatePlayer.TWO) {
            val increment = currentFormat.increment ?: 0L
            if (increment > 0L) _countDownTime2.value += increment
        }

        if (playerOneJob?.isActive != true) {
            incrementMoves(_activePlayer, _player2Moves)
            setActivePlayer(
                activePlayer        = ActivatePlayer.ONE,
                activePlayerState   = _playerTimerState1,
                inactivePlayerState = _playerTimerState2,
            )
            playerOneJob = viewModelScope.launch(Dispatchers.Default) {
                stopWatch(_countDownTime1, _microTime1, _playerTimerState1, isPlayerOne = true)
            }
        }
    }

    private fun startPlayerTwo() {
        cancelJobs()

        // FIX: Apply increment to player 1 who just finished their turn.
        if (_activePlayer.value == ActivatePlayer.ONE) {
            val increment = currentFormat.increment ?: 0L
            if (increment > 0L) _countDownTime1.value += increment
        }

        if (playerTwoJob?.isActive != true) {
            incrementMoves(_activePlayer, _player1Moves)
            setActivePlayer(
                activePlayer        = ActivatePlayer.TWO,
                activePlayerState   = _playerTimerState2,
                inactivePlayerState = _playerTimerState1,
            )
            playerTwoJob = viewModelScope.launch(Dispatchers.Default) {
                stopWatch(_countDownTime2, _microTime2, _playerTimerState2, isPlayerOne = false)
            }
        }
    }

    // ── Command handler ───────────────────────────────────────────────────────

    fun HomeScreenCommandHandler(command: HomeScreenCommand) {
        when (command) {

            is HomeScreenCommand.PlayerClicked -> {
                // If either player has been flagged, any tap on either card
                // should prompt a restart — not resume or start a new turn.
                val isGameOver = _playerTimerState1.value == PlayerState.DEFEATED
                        || _playerTimerState2.value == PlayerState.DEFEATED
                if (isGameOver) {
                    _uiState.value = _uiState.value.copy(showRestartDialog = true)
                    return
                }

                when (command.playerState) {
                    PlayerState.ACTIVE   -> soundPool.play(clickSoundId, 1f, 1f, 0, 0, 1f)
                    PlayerState.INACTIVE -> command.view.playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT)
                    PlayerState.DEFEATED -> return
                }
                when (command.activatePlayer) {
                    ActivatePlayer.ONE  -> startPlayerOne()
                    ActivatePlayer.TWO  -> startPlayerTwo()
                    ActivatePlayer.NONE -> Unit
                }
            }

            is HomeScreenCommand.OpenSettings ->
                eventChannel.trySend(HomeScreenEvent.NavigateToSettings(command.navController))

            is HomeScreenCommand.OpenHomeSelection ->
                eventChannel.trySend(HomeScreenEvent.NavigateToHomeSelection(command.navController))

            HomeScreenCommand.PauseClockClicked -> pauseClocks()

            HomeScreenCommand.RestartHomeClicked ->
                _uiState.value = _uiState.value.copy(showRestartDialog = true)

            HomeScreenCommand.ConfirmRestartClock -> {
                _uiState.value = _uiState.value.copy(showRestartDialog = false)
                restartTimers()
            }

            HomeScreenCommand.HideRestartHomeDialog ->
                _uiState.value = _uiState.value.copy(showRestartDialog = false)

            is HomeScreenCommand.SetNameClicked -> {
                _uiState.value = _uiState.value.copy(
                    selectedPlayerForNameDialog = command.selectedPlayer,
                    showNameDialog = true
                )
            }

            is HomeScreenCommand.ConfirmSetName -> {
                val trimmed = command.name.trim()
                if (trimmed.isBlank()) {
                    eventChannel.trySend(HomeScreenEvent.ShowInvalidNameSnackBar("😭 Name cannot be empty"))
                    return
                }
                if (command.selectedPlayer == PlayerType.ONE) _player1Name.value = trimmed
                else _player2Name.value = trimmed
                _uiState.value = _uiState.value.copy(showNameDialog = false)
            }

            HomeScreenCommand.HideNameDialog ->
                _uiState.value = _uiState.value.copy(showNameDialog = false)
        }
    }

    // ── Countdown logic ───────────────────────────────────────────────────────

    private suspend fun stopWatch(
        countDownTime: MutableStateFlow<Long>,
        microTime: MutableStateFlow<Int>,
        playerState: MutableStateFlow<PlayerState>,
        isPlayerOne: Boolean,
    ) {
        val tickMs = 10L
        while (playerState.value == PlayerState.ACTIVE && countDownTime.value > 0) {
            delay(tickMs)
            microTime.value -= 1

            if (microTime.value < 0) {
                microTime.value = 99
                countDownTime.value -= 1_000L
            }

            if (countDownTime.value <= 0) {
                countDownTime.value = 0
                microTime.value = 0
                playerState.value = PlayerState.DEFEATED
                _activePlayer.value = ActivatePlayer.NONE
                val name = if (isPlayerOne) _player1Name.value else _player2Name.value
                eventChannel.trySend(HomeScreenEvent.ShowTimeExpiredSnackBar("🤣😭 $name got flagged"))
                break
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun cancelJobs() {
        playerOneJob?.cancel()
        playerTwoJob?.cancel()
    }

    private fun setActivePlayer(
        activePlayer: ActivatePlayer,
        activePlayerState: MutableStateFlow<PlayerState>,
        inactivePlayerState: MutableStateFlow<PlayerState>,
    ) {
        _pausedPlayer.value = ActivatePlayer.NONE  // ← clear on resume
        _activePlayer.value = activePlayer
        isClockInitial.value = false
        activePlayerState.value = PlayerState.ACTIVE
        inactivePlayerState.value = PlayerState.INACTIVE
    }

    private fun incrementMoves(
        activePlayer: MutableStateFlow<ActivatePlayer>,
        playerMoves: MutableStateFlow<Int>,
    ) {
        if (activePlayer.value != ActivatePlayer.NONE) playerMoves.value += 1
    }

    private fun restartTimers() {
        cancelJobs()
        val format = currentFormat
        isClockInitial.value = true
        _pausedPlayer.value = ActivatePlayer.NONE  // ← clear on restart
        _playerTimerState1.value = PlayerState.INACTIVE
        _playerTimerState2.value = PlayerState.INACTIVE
        _player1Moves.value = 0
        _player2Moves.value = 0
        _countDownTime1.value = format.countDown
        _countDownTime2.value = format.countDown
        _microTime1.value = 99
        _microTime2.value = 99
        _activePlayer.value = ActivatePlayer.NONE
    }

    // FIX: set the active player's state back to INACTIVE so the card no
    //      longer looks "active" while the clock is paused. isClockInitial
    //      stays false so the game is still in-progress; both cards become
    //      clickable again because ActivatePlayer is NONE.
    private fun pauseClocks() {
        cancelJobs()
        // Remember who was playing so only they can resume
        _pausedPlayer.value = _activePlayer.value
        if (_playerTimerState1.value == PlayerState.ACTIVE) _playerTimerState1.value = PlayerState.INACTIVE
        if (_playerTimerState2.value == PlayerState.ACTIVE) _playerTimerState2.value = PlayerState.INACTIVE
        _activePlayer.value = ActivatePlayer.NONE
    }

    override fun onCleared() {
        super.onCleared()
        soundPool.release()
    }
}