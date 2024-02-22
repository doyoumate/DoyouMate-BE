package com.doyoumate.api.controller

import com.doyoumate.api.config.ApplicationConfiguration
import com.doyoumate.api.config.SecurityTestConfiguration
import com.doyoumate.api.lecture.handler.LectureHandler
import com.doyoumate.api.lecture.router.LectureRouter
import com.doyoumate.api.lecture.service.LectureService
import com.doyoumate.common.controller.ControllerTest
import com.doyoumate.common.dto.ErrorResponse
import com.doyoumate.common.util.*
import com.doyoumate.domain.fixture.ID
import com.doyoumate.domain.fixture.NAME
import com.doyoumate.domain.fixture.createFilterResponse
import com.doyoumate.domain.fixture.createLectureResponse
import com.doyoumate.domain.lecture.dto.response.FilterResponse
import com.doyoumate.domain.lecture.dto.response.LectureResponse
import com.doyoumate.domain.lecture.exception.LectureNotFoundException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.IsolationMode
import io.mockk.every
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono

@ContextConfiguration(classes = [ApplicationConfiguration::class, SecurityTestConfiguration::class])
@Import(LectureRouter::class, LectureHandler::class)
class LectureControllerTest : ControllerTest() {
    @MockkBean
    private lateinit var lectureService: LectureService

    private val lectureResponseFields = listOf(
        "id" bodyDesc "식별자",
        "courseNumber" bodyDesc "강의 번호",
        "code" bodyDesc "교과목코드",
        "year" bodyDesc "연도",
        "grade" bodyDesc "학년",
        "semester" bodyDesc "학기",
        "major" bodyDesc "전공",
        "name" bodyDesc "강의명",
        "professor" bodyDesc "교수",
        "room" bodyDesc "강의실",
        "date" bodyDesc "시간",
        "credit" bodyDesc "학점",
        "section" bodyDesc "영역"
    )

    private val filterResponseFields = listOf(
        "year" bodyDesc "연도",
        "grade" bodyDesc "학년",
        "semester" bodyDesc "학기",
        "major" bodyDesc "전공",
        "name" bodyDesc "강의명",
        "credit" bodyDesc "학점",
        "section" bodyDesc "영역"
    )

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerLeaf

    init {
        describe("getLectureById()는") {
            withMockUser()

            context("식별자에 해당하는 강의가 존재하는 경우") {
                every { lectureService.getLectureById(any()) } returns createLectureResponse()

                it("상태 코드 200과 LectureResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/lecture/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<LectureResponse>()
                        .document("식별자를 통한 강의 단일 조회 성공(200)") {
                            pathParams("id" paramDesc "식별자")
                            responseBody(lectureResponseFields)
                        }
                }
            }

            context("식별자에 해당하는 강의가 존재하지 않는 경우") {
                every { lectureService.getLectureById(any()) } returns Mono.error(LectureNotFoundException())

                it("상태 코드 404와 ErrorResponse를 반환한다.") {
                    webClient
                        .get()
                        .uri("/lecture/{id}", ID)
                        .exchange()
                        .expectStatus()
                        .isEqualTo(404)
                        .expectBody<ErrorResponse>()
                        .document("식별자를 통한 강의 단일 조회 실패(404)") {
                            pathParams("id" paramDesc "식별자")
                            responseBody(errorResponseFields)
                        }
                }
            }
        }

        describe("getLectures()는") {
            withMockUser()

            context("강의가 존재하는 경우") {
                every { lectureService.getLectures() } returns listOf(createLectureResponse())

                it("상태 코드 200과 LectureResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/lecture")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<List<LectureResponse>>()
                        .document("강의 전체 조회 성공(200)") {
                            responseBody(lectureResponseFields.toListFields())
                        }
                }
            }
        }

        describe("searchLectures()는") {
            context("강의가 존재하는 경우") {
                every { lectureService.searchLectures(any(), any(), any(), any(), any(), any(), any()) } returns
                    listOf(createLectureResponse())

                it("상태 코드 200과 LectureResponse들을 반환한다.") {
                    webClient
                        .get()
                        .uri("/lecture?name={name}", NAME)
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<List<LectureResponse>>()
                        .document("강의 검색 성공(200)") {
                            responseBody(lectureResponseFields.toListFields())
                        }
                }
            }
        }

        describe("getFilter()는") {
            context("강의가 존재하는 경우") {
                every { lectureService.getFilter() } returns createFilterResponse()

                it("상태 코드 200과 Filter를 반환한다.") {
                    webClient
                        .get()
                        .uri("/lecture/filter")
                        .exchange()
                        .expectStatus()
                        .isOk
                        .expectBody<FilterResponse>()
                        .document("강의 필터 조회 성공(200)") {
                            responseBody(filterResponseFields)
                        }
                }
            }
        }
    }
}
