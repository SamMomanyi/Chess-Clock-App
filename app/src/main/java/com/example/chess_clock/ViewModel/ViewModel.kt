package com.example.chess_clock.ViewModel

import android.media.SoundPool
import android.util.Log
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess_clock.AppUtils.ActivatePlayer
import com.example.chess_clock.AppUtils.AppUtil
import com.example.chess_clock.AppUtils.ColorScheme
import com.example.chess_clock.ui.theme.toColorScheme
import com.example.chess_clock.AppUtils.HomeScreenCommand
import com.example.chess_clock.AppUtils.HomeScreenEvent
import com.example.chess_clock.AppUtils.PlayerState
import com.example.chess_clock.AppUtils.TimeScreenState
import com.example.chess_clock.R
import com.example.chess_clock.model.daggerHilt.di.AppContext
import com.example.chess_clock.model.database.clocks.ClockFormat
import com.example.chess_clock.model.database.clocks.ClocksDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class clockViewModel @Inject constructor(
    private val database: ClocksDatabase,
) : ViewModel() {

    val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .build()

    private  var clickSoundId: Int = 0

    init{
        clickSoundId = soundPool.load(AppContext.getContext(),R.raw.playertap,1)
    }

    //call one suspend function with the active player, only switch state in the UI

    private val timeFormats: List<ClockFormat> = AppUtil.predefinedClockFormats
    var timeFormat = timeFormats.get(index = 1)

    //seems we will also need two states of countDownTime for player one and two since they are remembered


    private val _countDownTime1 = MutableStateFlow(timeFormat.countDown)
    private val _countDownTime2 = MutableStateFlow(timeFormat.countDown)

    private val _delayTime = MutableStateFlow(timeFormat.delay)

    private val _playerTimerState1 = MutableStateFlow<PlayerState>(PlayerState.INACTIVE)
    private val _playerTimerState2 = MutableStateFlow<PlayerState>(PlayerState.INACTIVE)

    private val _player1Name = MutableStateFlow("")
    private val _player2Name = MutableStateFlow("")

    private val _player1Moves = MutableStateFlow<Int>(0)
    private val _player2Moves = MutableStateFlow<Int>(0)

    //represents the current state of the UI HomeScreen
    private val _uiState = MutableStateFlow<TimeScreenState>(TimeScreenState())
    private val _colorScheme1 = MutableStateFlow<ColorScheme>(ColorScheme())
    //val _colorScheme1 = playerTimerState1.value.toColorScheme()
    private val _colorScheme2 = MutableStateFlow<ColorScheme>(ColorScheme())
   // val _colorScheme2 = playerTimerState2.value.toColorScheme()
    //at the start of the game when no player is active , it could help us check if _activePlayer = NONE , then allow a click
    //even tho clicks only happen when a player is active.
    private val _activePlayer = MutableStateFlow<ActivatePlayer>(ActivatePlayer.NONE)
    //we could have a flatMap latest that listens to the active player, then we could-
    //only have one startPlayer functions that switches the timerFlow being listened to

    //flatMap latest helps in changing which timer flow is being observed


    val player1Flow = combine(_player1Name,_playerTimerState1,_countDownTime1,_player1Moves){
        name, state, time -> Triple(name, state, time)

    }
    val player2Flow = combine (_player2Name,_playerTimerState2,_countDownTime2,_player2Moves){
        name, state, time -> Triple(name, state, time)
    }

    val state = combine(

        _uiState,
        player1Flow,
        player2Flow,
        _activePlayer

    ) {  uiState, p1, p2, activePlayer  ->
        val(p1Name,p1State,t1) = p1
        val(p2Name,p2State,t2) = p2

        uiState.copy(
            colorScheme1 = p1State.toColorScheme(),
            colorScheme2 = p2State.toColorScheme(),
            countDownTime1 = t1,
            countDownTime2 = t2,
            player_One_Name = p1Name,
            player_Two_Name = p2Name,
            activePlayer = activePlayer,
            player1State = p1State,
            player2State = p2State
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),TimeScreenState())


    //jobs to use in suspend countDown Coroutine
    private var playerOneJob: Job? = null
    private var playerTwoJob: Job? = null

    //sound variables





    private fun startPlayerOne() {
//        _playerTimerState2.value = PlayerState.INACTIVE
//        _colorScheme2.value = _playerTimerState2.value.toColorScheme()
        cancelJobs()
        Log.e("startPlayerone", "i'm alive 1 ")
        if (playerOneJob?.isActive != true) {
            setActivePlayer(
               activePlayer =  _playerTimerState1,
                inactivePlayer = _playerTimerState2,
                activeColor = _colorScheme1,
                inactiveColor = _colorScheme2
            )
            Log.e("startPlayerone", "i'm alive 2")
//            _playerTimerState1.value = PlayerState.ACTIVE
//            _colorScheme1.value = _playerTimerState1.value.toColorScheme()
            try {
                playerOneJob = viewModelScope.launch(Dispatchers.Default) {
                    stopWatch(_countDownTime1, _playerTimerState1)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    AppContext.getContext(),
                    "Timer 1 is failing to start",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Check1","Timer 1 is failing to start")
            }
        }

    }

    private fun startPlayerTwo() {

//        _playerTimerState1.value = PlayerState.INACTIVE
//        _colorScheme1.value = _playerTimerState1.value.toColorScheme()
        cancelJobs()
        Log.e("startPlayertwo", "i'm alive 1 ")
        if (playerTwoJob?.isActive != true) {
            Log.e("startPlayertwo", "i'm alive 2 ")
//            _playerTimerState2.value = PlayerState.ACTIVE
//            _colorScheme2.value = _playerTimerState2.value.toColorScheme()
            setActivePlayer(
                activePlayer =  _playerTimerState2,
                inactivePlayer = _playerTimerState1,
                activeColor = _colorScheme2,
                inactiveColor = _colorScheme1
            )
            try {
                playerTwoJob = viewModelScope.launch(Dispatchers.Default) {
                    stopWatch(_countDownTime2, _playerTimerState2)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    AppContext.getContext(),
                    "Timer 2 is failing to start",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Check2","Timer 2 is failing to start")
            }
        }
    }


    private suspend fun stopWatch(
        countDownTime: MutableStateFlow<Long>, //either countdowntime one or two
        playerState: MutableStateFlow<PlayerState>
    ) {
        while (playerState.value == PlayerState.ACTIVE && countDownTime.value > 0) {
            delay(1000L)
            countDownTime.value -= 1000L
        }
        if (countDownTime.value <= 0) {
            playerState.value = PlayerState.DEFEATED
        }

    }

    //for switching which countDown time is being listened to and being
    //decremented in the UI


    fun CommandHandler(Command: HomeScreenCommand) {
        when (Command) {


            HomeScreenCommand.OpenSettings -> {

            }

            is HomeScreenCommand.OpenTimerSelection -> {

            }

            HomeScreenCommand.RestartTimer -> {

            }
            //this is for changing the button name
            is HomeScreenCommand.SetName -> {
                when (Command.activatePlayer) {
                    //this is means it is player 2 , whoose player dialog has been clicked
                    ActivatePlayer.ONE -> {
                        _player2Name.value = Command.playerName
                    }
                    //this means it is player 1
                    ActivatePlayer.TWO -> {
                        _player1Name.value = Command.playerName
                    }

                    ActivatePlayer.NONE -> TODO()
                }
            }


            //we could always only listen for any state change from the UI then use it to alternate the jobs
            is HomeScreenCommand.PlayerClicked -> {
         Log.e("PlayerCLicked","I was clicked 2 ")
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
                        _activePlayer.value = ActivatePlayer.ONE
                        startPlayerOne()
                    }

                    ActivatePlayer.TWO -> {
                        _activePlayer.value = ActivatePlayer.TWO
                        startPlayerTwo()
                    }

                    ActivatePlayer.NONE -> TODO()
                }
            }
        }
    }

    fun EventHandler(Event : HomeScreenEvent){

    }

    //helper functions for the startPlayer one and Two
    private fun cancelJobs(){
        playerOneJob?.cancel()
        playerTwoJob?.cancel()
    }

    private fun setActivePlayer(
        activePlayer : MutableStateFlow<PlayerState>,
        inactivePlayer : MutableStateFlow<PlayerState>,
        activeColor : MutableStateFlow<ColorScheme>,
        inactiveColor : MutableStateFlow<ColorScheme>

    ){
        activePlayer.value = PlayerState.ACTIVE
        inactivePlayer.value = PlayerState.INACTIVE
        activeColor.value = activePlayer.value.toColorScheme()
        inactiveColor.value = inactivePlayer.value.toColorScheme()
    }

    override fun onCleared() {
        super.onCleared()
        soundPool.release()
    }
}







