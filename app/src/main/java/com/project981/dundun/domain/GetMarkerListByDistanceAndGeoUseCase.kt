package com.project981.dundun.domain

import com.project981.dundun.model.dto.MarkerDTO
import com.project981.dundun.model.repository.MainRepository

class GetMarkerListByDistanceAndGeoUseCase {
    suspend operator fun invoke(
        distance: Double,
        latitude: Double,
        longitude: Double,
        callback: (Result<List<MarkerDTO>>) -> Unit
    ) {
        return MainRepository.getMarkerListByDistanceAndGeo(distance,latitude,longitude,callback)
    }
}