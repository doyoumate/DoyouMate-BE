package com.doyoumate.api.lecture.service

import com.doyoumate.domain.lecture.dto.response.FilterResponse
import com.doyoumate.domain.lecture.dto.response.LectureResponse
import com.doyoumate.domain.lecture.exception.LectureNotFoundException
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.lecture.repository.CustomLectureRepository
import com.doyoumate.domain.lecture.repository.LectureRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val customLectureRepository: CustomLectureRepository
) {
    fun getLectureById(id: String): Mono<LectureResponse> =
        lectureRepository.findById(id)
            .switchIfEmpty(Mono.error(LectureNotFoundException()))
            .map { LectureResponse(it) }

    fun getLectures(): Flux<LectureResponse> =
        lectureRepository.findAll()
            .map { LectureResponse(it) }

    fun searchLectures(
        year: Int?,
        grade: Int?,
        semester: Semester?,
        major: String?,
        name: String,
        credit: Int?,
        section: Section?
    ): Flux<LectureResponse> =
        customLectureRepository.searchLectures(year, grade, semester, major, name, credit, section)
            .map { LectureResponse(it) }

    fun getFilter(): Mono<FilterResponse> =
        lectureRepository.getFilter()
            .map { FilterResponse(it) }
}
