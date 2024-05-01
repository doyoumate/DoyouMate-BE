package com.doyoumate.api.lecture.service

import com.doyoumate.domain.lecture.dto.response.FilterResponse
import com.doyoumate.domain.lecture.dto.response.LectureResponse
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.lecture.repository.CustomLectureRepository
import com.doyoumate.domain.lecture.repository.LectureRepository
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import com.github.jwt.security.JwtAuthentication
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val customLectureRepository: CustomLectureRepository,
    private val studentRepository: StudentRepository
) {
    fun getLecturesByIds(ids: Collection<String>): Flux<LectureResponse> =
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
        customLectureRepository.search(year, grade, semester, major, name, credit, section, pageable)
            .map { LectureResponse(it) }

    fun getFilter(): Mono<FilterResponse> =
        lectureRepository.getFilter()
            .map { FilterResponse(it) }

    fun markLectureById(id: String, authentication: JwtAuthentication): Mono<Void> =
        studentRepository.findById(authentication.id)
            .switchIfEmpty(Mono.error(StudentNotFoundException()))
            .map {
                it.copy(markedLecturesIds = it.markedLecturesIds.apply {
                    if (id in it.markedLecturesIds) {
                        remove(id)
                    } else {
                        add(id)
                    }
                })
            }
            .flatMap { studentRepository.save(it) }
            .then()
}
