package com.doyoumate.api.lecture.service

import com.doyoumate.domain.lecture.adapter.LectureClient
import com.doyoumate.domain.lecture.dto.response.FilterResponse
import com.doyoumate.domain.lecture.dto.response.LectureResponse
import com.doyoumate.domain.lecture.exception.LectureNotFoundException
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.lecture.repository.CustomLectureRepository
import com.doyoumate.domain.lecture.repository.LectureRepository
import com.doyoumate.domain.student.exception.StudentNotFoundException
import com.doyoumate.domain.student.repository.StudentRepository
import com.github.jwt.security.DefaultJwtAuthentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
    private val customLectureRepository: CustomLectureRepository,
    private val studentRepository: StudentRepository
) {
    fun getRelatedLecturesById(id: String): Flux<LectureResponse> =
        lectureRepository.findById(id)
            .switchIfEmpty(Mono.error(LectureNotFoundException()))
            .flatMapMany { lectureRepository.findAllByNameLikeAndYearLessThanOrderByYearDesc(it.name, it.year) }
            .map { LectureResponse(it) }

    fun searchLecturePage(
        year: Int?,
        grade: Int?,
        semester: Semester?,
        major: String?,
        name: String,
        credit: Int?,
        section: Section?,
        lastId: String?,
        size: Int
    ): Flux<LectureResponse> =
        customLectureRepository.searchPage(year, grade, semester, major, name, credit, section, lastId, size)
            .map { LectureResponse(it) }

    fun getFilter(): Mono<FilterResponse> =
        lectureRepository.getFilter()
            .map { FilterResponse(it) }

    fun markLectureById(id: String, authentication: DefaultJwtAuthentication): Mono<Void> =
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
