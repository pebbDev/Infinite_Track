package com.example.infinite_track.domain.use_case.wfa

import com.example.infinite_track.domain.model.wfa.WfaRecommendation
import com.example.infinite_track.domain.repository.WfaRepository
import javax.inject.Inject

/**
 * Use case for getting WFA (Work From Anywhere) recommendations
 */
class GetWfaRecommendationsUseCase @Inject constructor(
    private val wfaRepository: WfaRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double
    ): Result<List<WfaRecommendation>> {
        return wfaRepository.getRecommendations(latitude, longitude)
    }
}
