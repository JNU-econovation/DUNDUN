package com.project981.dundun.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project981.dundun.domain.GetMarkerListByDistanceAndGeoUseCase
import com.project981.dundun.model.dto.MarkerDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel : ViewModel() {
    private val useCase = GetMarkerListByDistanceAndGeoUseCase()
    private val _markerList = MutableLiveData<List<MarkerDTO>>(listOf())
    val markerList: LiveData<List<MarkerDTO>>
        get() = _markerList
    private var cluster = Cluster(listOf())
    private var isChecking = false

    fun getMarkerInfo(
        pixelDist: Double,
        distance: Double,
        latitude: Double,
        longitude: Double,
        callback: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase(distance, latitude, longitude) {result ->
                result.onSuccess {
                    cluster = Cluster(it)
                    _markerList.postValue(cluster.clustering(pixelDist))
                    CoroutineScope(Dispatchers.Main).launch {
                        callback(Result.success(Unit))
                    }
                }.onFailure {
                    callback(Result.failure(it))
                }
            }
        }
    }

    fun getClusterInfo(d : Double) {
        if(isChecking) return

        isChecking = true
        val temp = cluster.clustering(d)
        if(temp.equals(_markerList.value).not()){
            _markerList.value = temp
        }
        isChecking = false
    }
}