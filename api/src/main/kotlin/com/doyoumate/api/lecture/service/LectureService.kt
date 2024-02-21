package com.doyoumate.api.lecture.service

import com.doyoumate.domain.lecture.dto.response.LectureResponse
import com.doyoumate.domain.lecture.exception.LectureNotFoundException
import com.doyoumate.domain.lecture.repository.LectureRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class LectureService(
    private val lectureRepository: LectureRepository
) {
    fun getLectureById(id: String): Mono<LectureResponse> =
        lectureRepository.findById(id)
            .switchIfEmpty(Mono.error(LectureNotFoundException()))
            .map { LectureResponse(it) }

    fun getLectures(): Flux<LectureResponse> =
        lectureRepository.findAll()
            .map { LectureResponse(it) }
}