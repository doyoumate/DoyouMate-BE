package com.doyoumate.api.lecture.service

import com.doyoumate.domain.lecture.dto.response.FilterResponse
import com.doyoumate.domain.lecture.dto.response.LectureResponse
import com.doyoumate.domain.lecture.exception.LectureNotFoundException
import com.doyoumate.domain.lecture.model.MarkedLectureStudent
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.lecture.repository.CustomLectureRepository
import com.doyoumate.domain.lecture.repository.LectureRepository
import com.doyoumate.domain.lecture.repository.MarkedLectureStudentRepository
import com.github.jwt.security.JwtAuthentication
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val customLectureRepository: CustomLectureRepository,
    private val markedLectureStudentRepository: MarkedLectureStudentRepository
) {
    fun getLectureById(id: String): Mono<LectureResponse> =
        lectureRepository.findById(id)
            .switchIfEmpty(Mono.error(LectureNotFoundException()))
            .map { LectureResponse(it) }

    fun getLectures(): Flux<LectureResponse> =
        lectureRepository.findAll()
            .map { LectureResponse(it) }

    fun getLecturesByIds(ids: List<String>): Flux<LectureResponse> =
        lectureRepository.findAllByIdIn(ids)
            .map { LectureResponse(it) }

    fun searchLectures(
        year: Int?,
        grade: Int?,
        semester: Semester?,
        major: String?,
        name: String,
        credit: Int?,
        section: Section?,
        pageable: Pageable
    ): Flux<LectureResponse> =
        customLectureRepository.searchLectures(year, grade, semester, major, name, credit, section, pageable)
            .map { LectureResponse(it) }

    fun getFilter(): Mono<FilterResponse> =
        lectureRepository.getFilter()
            .map { FilterResponse(it) }

    fun markLectureById(id: String, authentication: JwtAuthentication): Mono<Void> =
        markedLectureStudentRepository.findByLectureIdAndStudentId(id, authentication.id)
            .flatMap {
                markedLectureStudentRepository.deleteById(it.id!!)
                    .thenReturn(true)
            }
            .switchIfEmpty(Mono.defer {
                markedLectureStudentRepository.save(
                    MarkedLectureStudent(
                        lectureId = id,
                        studentId = authentication.id
                    )
                ).thenReturn(true)
            })
            .then()
}
