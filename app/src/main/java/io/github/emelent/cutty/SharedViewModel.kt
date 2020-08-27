package io.github.emelent.cutty

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.example.cutty.NavMainDirections
import com.microsoft.signalr.HubConnection
import io.github.emelent.cutty.fragments.EnterUrlFragmentDirections
import io.github.emelent.cutty.fragments.TrimVideoFragmentDirections
import io.github.emelent.cutty.fragments.WakeServerFragmentDirections
import io.github.emelent.cutty.net.CuttyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel(
    private val cuttyRepo: CuttyRepository,
    val hubConnection: HubConnection
) : ViewModel() {

    var seekPos = Pair(0L, 100L)
    private var videoUrl = ""

    private val _navigationEvent = SingleLiveEvent<NavDirections>()
    val navigationEvent: LiveData<NavDirections> = _navigationEvent



    val loading = MutableLiveData<Boolean>()


    fun goToEnterUrl(): NavDirections {
        return NavMainDirections.actionGlobalEnterUrlFragment()
    }

    fun goToDownloadUrl() {
        _navigationEvent.postValue(TrimVideoFragmentDirections.actionTrimVideoFragmentToDownloadFragment())
    }

    fun goToVideoTrimmer(audioUrl: String, videoUrl: String) {
        _navigationEvent.postValue(
            EnterUrlFragmentDirections.actionEnterUrlFragmentToTrimVideoFragment(
            audioUrl,
            videoUrl
        ))
    }


    fun requestAvUrl(url: String) {
        loading.value = true
        videoUrl = url
        hubConnection.send("RequestAvUrls", videoUrl)
    }

    fun requestTrimVideo() {
        val startTime = seekPos.first.toHhMmSs()
        val duration = (seekPos.second - seekPos.first).toHhMmSs()
        loading.value = true
        println("Player::SharedViewModel::request trim($videoUrl, $startTime, $duration)")
        hubConnection.send("RequestTrimVideo", videoUrl, startTime, duration)
        _navigationEvent.postValue(TrimVideoFragmentDirections.actionTrimVideoFragmentToDownloadFragment())
    }

    fun wakeServer() {
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)
            cuttyRepo.wakeServer()
            hubConnection.start().blockingAwait()
            loading.postValue(false)
            _navigationEvent.postValue(WakeServerFragmentDirections.actionWakeServerFragmentToEnterUrlFragment())
        }
    }

    fun setVideoUrl(url: String) {
        videoUrl = url
    }


    class Factory(
        private val cuttyRepo: CuttyRepository,
        private val hubConnection: HubConnection
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(cuttyRepo, hubConnection) as T
        }
    }
}