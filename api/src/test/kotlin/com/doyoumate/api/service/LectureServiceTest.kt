package com.doyoumate.api.service

import com.doyoumate.api.lecture.service.LectureService
import com.doyoumate.common.util.getResult
import com.doyoumate.common.util.returns
import com.doyoumate.domain.fixture.ID
import com.doyoumate.domain.fixture.createLecture
import com.doyoumate.domain.fixture.createLectureResponse
import com.doyoumate.domain.lecture.exception.LectureNotFoundException
import com.doyoumate.domain.lecture.repository.LectureRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import reactor.core.publisher.Mono
import reactor.kotlin.test.expectError

class LectureServiceTest : BehaviorSpec() {
    private val lectureRepository = mockk<LectureRepository>()

    private val lectureService = LectureService(
        lectureRepository = lectureRepository
    )

    init {
        Given("강의가 존재하는 경우") {
            val lecture = createLecture()
                .also {
                    every { lectureRepository.findById(any<String>()) } returns it
                    every { lectureRepository.findAll() } returns listOf(it)
                }

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
