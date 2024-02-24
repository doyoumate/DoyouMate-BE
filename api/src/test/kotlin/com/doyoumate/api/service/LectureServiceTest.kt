package com.doyoumate.api.service

import com.doyoumate.api.lecture.service.LectureService
import com.doyoumate.common.util.getResult
import com.doyoumate.common.util.returns
import com.doyoumate.domain.fixture.*
import com.doyoumate.domain.lecture.exception.LectureNotFoundException
import com.doyoumate.domain.lecture.repository.CustomLectureRepository
import com.doyoumate.domain.lecture.repository.LectureRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import reactor.core.publisher.Mono
import reactor.kotlin.test.expectError

class LectureServiceTest : BehaviorSpec() {
    private val lectureRepository = mockk<LectureRepository>()

    private val customLectureRepository = mockk<CustomLectureRepository>()

    private val lectureService = LectureService(
        lectureRepository = lectureRepository,
        customLectureRepository = customLectureRepository
    )

    init {
        Given("강의가 존재하는 경우") {
            val lecture = createLecture()
                .also {
                    every { lectureRepository.findById(any<String>()) } returns it
                    every { lectureRepository.findAll() } returns listOf(it)
                    every {
                        customLectureRepository.searchLectures(
                            any(), any(), any(), any(), any(), any(), any(), any()
                        )
                    } returns listOf(it)
                }
            every { lectureRepository.getFilter() } returns createFilter()

            When("식별자를 통해 특정 강의를 조회하면") {
                val result = lectureService.getLectureById(ID)
                    .getResult()

                Then("해당 강의가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createLectureResponse(lecture))
                        .verifyComplete()
                }
            }

            When("모든 강의를 조회하면") {
                val result = lectureService.getLectures()
                    .getResult()

                Then("모든 강의가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createLectureResponse(lecture))
                        .verifyComplete()
                }
            }

            When("특정 강의를 검색하면") {
                val result = lecture.run {
                    lectureService.searchLectures(year, grade, semester, major, name, credit, section, createPageable())
                }.getResult()

                Then("해당 강의가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createLectureResponse(lecture))
                        .verifyComplete()
                }
            }

            When("필터를 조회하면") {
                val result = lectureService.getFilter()
                    .getResult()

                Then("강의의 각 필드에 대한 필터가 조회된다.") {
                    result.expectSubscription()
                        .expectNext(createFilterResponse())
                        .verifyComplete()
                }
            }
        }

        Given("강의가 존재하지 않는 경우") {
            every { lectureRepository.findById(any<String>()) } returns Mono.empty()

            When("식별자를 통해 특정 강의를 조회하면") {
                val result = lectureService.getLectureById(ID)
                    .getResult()

                Then("예외가 발생한다.") {
                    result.expectSubscription()
                        .expectError<LectureNotFoundException>()
                        .verify()
                }
            }
        }
    }
}
