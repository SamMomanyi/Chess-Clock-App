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
import com.example.chess_clock.ui.theme.toColorScheme
import com.example.chess_clock.AppUtils.HomeScreenCommand
import com.example.chess_clock.AppUtils.HomeScreenEvent
import com.example.chess_clock.AppUtils.PlayerData
import com.example.chess_clock.AppUtils.PlayerState
import com.example.chess_clock.AppUtils.PlayerType
import com.example.chess_clock.AppUtils.TimeScreenState
import com.example.chess_clock.R
import com.example.chess_clock.model.daggerHilt.di.AppContext
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.model.database.clocks.ClocksDatabase
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class clockViewModel @Inject constructor(
    private val database: ClocksDatabase,
) : ViewModel() {

    val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .build()

    private var clickSoundId: Int = 0

    init {
        clickSoundId = soundPool.load(AppContext.getContext(), R.raw.playertap, 1)
    }

    //call one suspend function with the active player, only switch state in the UI

    private val timeFormats: List<ClockFormat> = AppUtil.predefinedClockFormats
    val timeFormat = timeFormats.get(index = 0)

    //represents the current state of the UI HomeScreen
    //seems we will also need two states of countDownTime for player one and two since they are remembered

    //player variables
    private val _countDownTime1 = MutableStateFlow(timeFormat.countDown)
    private val _countDownTime2 = MutableStateFlow(timeFormat.countDown)

    private val _delayTime = MutableStateFlow(timeFormat.delay)

    private val _microTime1 = MutableStateFlow<Int>(0)
    private val _microTime2 = MutableStateFlow<Int>(0)

    private val _playerTimerState1 = MutableStateFlow<PlayerState>(PlayerState.INACTIVE)
    private val _playerTimerState2 = MutableStateFlow<PlayerState>(PlayerState.INACTIVE)
//initial values to be shown in the UI before update since state overrides the current player_one_name value
    private val _player1Name = MutableStateFlow<String>("player1")
    private val _player2Name = MutableStateFlow<String>("player2")

    private val _player1Moves = MutableStateFlow<Int>(0)
    private val _player2Moves = MutableStateFlow<Int>(0)


    private val _uiState = MutableStateFlow<TimeScreenState>(TimeScreenState())
    private val _colorScheme1 = MutableStateFlow<ColorScheme>(ColorScheme())
    private val _colorScheme2 = MutableStateFlow<ColorScheme>(ColorScheme())

    //at the start of the game when no player is active , it could help us check if _activePlayer = NONE , then allow a click
    //even tho clicks only happen when a player is active.
    private val _activePlayer = MutableStateFlow<ActivatePlayer>(ActivatePlayer.NONE)

    //event variables to send events to UI
    private val eventChannel = Channel<HomeScreenEvent>()
    val events = eventChannel.receiveAsFlow()


    val player1Flow = combine(
        _player1Name, _playerTimerState1, _countDownTime1, _player1Moves, _microTime1
    ) { name, state, countdowntime, moves, microtime ->
        PlayerData(
            name = name,
            state = state,
            mainTime = countdowntime,
            microTime = microtime,
            playerMoves = moves
        )
    }
    val player2Flow = combine(
        _player2Name, _playerTimerState2, _countDownTime2, _player2Moves, _microTime2
    ) { name, state, countdowntime, moves, microtime ->
        PlayerData(
            name = name,
            state = state,
            mainTime = countdowntime,
            microTime = microtime,
            playerMoves = moves
        )
    }

    val state = combine(
        _uiState,
        player1Flow,
        player2Flow,
        _activePlayer

    ) { uiState, p1, p2, activePlayer ->

        val (p1Name, p1State, t1, mt1, pm1) = p1
        val (p2Name, p2State, t2, mt2, pm2) = p2

        uiState.copy(
            colorScheme1 = p1State.toColorScheme(),
            colorScheme2 = p2State.toColorScheme(),
            countDownTime1 = AppUtil.formatTime(t1),
            countDownTime2 = AppUtil.formatTime(t2),
            player_One_Name = p1Name,
            player_Two_Name = p2Name,
            activePlayer = activePlayer,
            player1State = p1State,
            player2State = p2State,
            player1Moves = pm1,
            player2Moves = pm2,
            microTime1 = mt1,
            microTime2 = mt2
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TimeScreenState())


    //jobs to use in suspend countDown Coroutine
    private var playerOneJob: Job? = null
    private var playerTwoJob: Job? = null

    //sound variables


    private fun startPlayerOne() {

        cancelJobs()
        Log.e("startPlayerone", "i'm alive 1 ")
        if (playerOneJob?.isActive != true) {
            IncrementMoves(
                activePlayer = _activePlayer,
                playerMoves = _player2Moves
            )
            setActivePlayer(
                activePlayer = ActivatePlayer.ONE,
                activePlayerState = _playerTimerState1,
                inactivePlayerState = _playerTimerState2,
                activeColor = _colorScheme1,
                inactiveColor = _colorScheme2,
            )
            Log.e("startPlayerone", "i'm alive 2")
//            _playerTimerState1.value = PlayerState.ACTIVE
//            _colorScheme1.value = _playerTimerState1.value.toColorScheme()
            try {
                playerOneJob = viewModelScope.launch(Dispatchers.Default) {
                    stopWatch(_countDownTime1, _microTime1, _playerTimerState1)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    AppContext.getContext(),
                    "Timer 1 is failing to start",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Check1", "Timer 1 is failing to start")
            }
        }
    }

    private fun startPlayerTwo() {
        cancelJobs()
        Log.e("startPlayertwo", "i'm alive 1 ")
        if (playerTwoJob?.isActive != true) {
            Log.e("startPlayertwo", "i'm alive 2 ")
            IncrementMoves(
                activePlayer = _activePlayer,
                playerMoves = _player1Moves
            )
            setActivePlayer(
                activePlayer = ActivatePlayer.TWO,
                activePlayerState = _playerTimerState2,
                inactivePlayerState = _playerTimerState1,
                activeColor = _colorScheme2,
                inactiveColor = _colorScheme1
            )
            try {
                playerTwoJob = viewModelScope.launch(Dispatchers.Default) {
                    stopWatch(_countDownTime2, _microTime2, _playerTimerState2)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    AppContext.getContext(),
                    "Timer 2 is failing to start",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Check2", "Timer 2 is failing to start")
            }
        }
    }

    //for switching which countDown time is being listened to and being
    //decremented in the UI


    fun CommandHandler(Command: HomeScreenCommand) {
        when (Command) {
            //we could always only listen for any state change from the UI then use it to alternate the jobs
            is HomeScreenCommand.PlayerClicked -> {
                Log.e("PlayerCLicked", "I was clicked 2 ")
                when (Command.playerState) {
                    PlayerState.ACTIVE -> {
                        soundPool.play(clickSoundId, 1f, 1f, 0, 0, 1f)
                    }

                    PlayerState.INACTIVE -> {
                        Command.view.playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT)
                    }

                    PlayerState.DEFEATED -> {
                        Unit
                    }
                }

                when (Command.activatePlayer) {
                    ActivatePlayer.ONE -> {
                        startPlayerOne()
                    }

                    ActivatePlayer.TWO -> {
                        startPlayerTwo()
                    }

                    ActivatePlayer.NONE -> TODO()
                }
            }

            is HomeScreenCommand.OpenSettings -> {
                eventChannel.trySend(HomeScreenEvent.NavigateToSettings(navController = Command.navController))
            }

            is HomeScreenCommand.OpenTimerSelection -> {
                eventChannel.trySend(HomeScreenEvent.NavigateToTimerSelection(navController = Command.navController))
            }

            HomeScreenCommand.RestartTimerClicked -> {
                _uiState.update {
                    it.copy(
                        showRestartDialog = true
                    )
                }
            }

            HomeScreenCommand.ConfirmRestartClock -> {
                _uiState.update {
                    it.copy(
                        showRestartDialog = false
                    )
                }
                restartTimers()
            }

            HomeScreenCommand.HideRestartTimerDialog -> {
                _uiState.update {
                    it.copy(
                        showRestartDialog = false
                    )
                }
            }

            is HomeScreenCommand.SetNameClicked -> {
                //check which player has clicked to show the name dialog
                if (Command.selectedPlayer == PlayerType.ONE) {
                    _uiState.update {
                        it.copy(
                            selectedPlayerForNameDialog = PlayerType.ONE
                        )
                    }
                }
                else {
                    _uiState.update {
                        it.copy(
                            selectedPlayerForNameDialog = PlayerType.TWO
                        )
                    }
                }
                _uiState.update {
                    it.copy(
                        showNameDialog = true
                    )
                }
            }

            is HomeScreenCommand.ConfirmSetName -> {
                //choose which player to equate the name to
                if (Command.selectedPlayer == PlayerType.ONE) {
                    _player1Name.value = Command.name
                }
                else {
                    _player2Name.value = Command.name
                }
                if (Command.name.isBlank()) {
                    eventChannel.trySend(HomeScreenEvent.ShowInvalidNameSnackBar("😭Name Cannot be Empty"))
                    return
                } else if (Command.selectedPlayer == PlayerType.ONE) {
                    _player1Name.value = Command.name
                } else {
                    _player2Name.value = Command.name
                }
                _uiState.update {
                    it.copy(
                        showNameDialog = false
                    )
                }
            }

            HomeScreenCommand.HideNameDialog -> {
                _uiState.update {
                    it.copy(
                        showNameDialog = false
                    )
                }
            }

            HomeScreenCommand.PauseClockClicked -> {
                Unit
            }

        }
    }

    //helper functions for the startPlayer one and Two
    private suspend fun stopWatch(
        countDownTime: MutableStateFlow<Long>, //either countdowntime one or two,
        microTime: MutableStateFlow<Int>,
        playerState: MutableStateFlow<PlayerState>
    ) {
        val delayTicker = 10L
        while (playerState.value == PlayerState.ACTIVE && countDownTime.value > 0) {
            delay(delayTicker) //delay for one millisecond
            microTime.value -= 1

            if (microTime.value < 0) {
                microTime.value = 99
                countDownTime.value -= 1000L
            }
            //When time runs out
            if (countDownTime.value <= 0) {
                countDownTime.value = 0
                microTime.value = 0
                playerState.value = PlayerState.DEFEATED
                _activePlayer.value = ActivatePlayer.NONE
                //show snackbar Using events // it think it works since the viewModel is aware of this
                val expiredMessage = if (countDownTime == _countDownTime1) {
                    "🤣😭${_player1Name.value} got flagged"
                } else {
                    "🤣😭${_player2Name.value} got flagged"
                }
                eventChannel.trySend(HomeScreenEvent.ShowTimeExpiredSnackBar(expiredMessage))
                break
            }
        }

    }

    private fun cancelJobs() {
        playerOneJob?.cancel()
        playerTwoJob?.cancel()
    }

    private fun setActivePlayer(
        activePlayer: ActivatePlayer,
        activePlayerState: MutableStateFlow<PlayerState>,
        inactivePlayerState: MutableStateFlow<PlayerState>,
        activeColor: MutableStateFlow<ColorScheme>,
        inactiveColor: MutableStateFlow<ColorScheme>
    ) {
        _activePlayer.value = activePlayer
        activePlayerState.value = PlayerState.ACTIVE
        inactivePlayerState.value = PlayerState.INACTIVE
        activeColor.value = activePlayerState.value.toColorScheme()
        inactiveColor.value = inactivePlayerState.value.toColorScheme()
    }

    private fun IncrementMoves(
        activePlayer: MutableStateFlow<ActivatePlayer>,
        playerMoves: MutableStateFlow<Int>
    ) {
        if (activePlayer.value != ActivatePlayer.NONE) {
            playerMoves.value += 1
        }
    }

    private fun restartTimers() {
        cancelJobs()
        _playerTimerState2.value = PlayerState.INACTIVE
        _playerTimerState1.value = PlayerState.INACTIVE
        _countDownTime1.value = timeFormat.countDown
        _countDownTime2.value = timeFormat.countDown
        _activePlayer.value = ActivatePlayer.NONE
    }

    override fun onCleared() {
        super.onCleared()
        soundPool.release()
    }
}







