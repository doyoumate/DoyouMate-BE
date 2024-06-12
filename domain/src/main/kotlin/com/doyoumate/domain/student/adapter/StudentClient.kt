package com.doyoumate.domain.student.adapter

import com.doyoumate.common.annotation.Client
import com.doyoumate.common.util.getRow
import com.doyoumate.common.util.getRows
import com.doyoumate.common.util.getValue
import com.doyoumate.domain.global.util.SuwingsRequests
import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.student.dto.response.Attendance
import com.doyoumate.domain.student.dto.response.ChapelResponse
import com.doyoumate.domain.student.model.Student
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.kotlin.core.util.function.component3
import reactor.kotlin.core.util.function.component4
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Client
class StudentClient(
    private val webClient: WebClient,
    private val xmlMapper: XmlMapper,
) {
    fun getStudentByStudentNumber(studentNumber: String): Mono<Student> =
        LocalDate.now()
            .let { date ->
                val (year, semester) = date.year to Semester(date)

                getProfileByStudentNumber(studentNumber, year, semester)
                    .filter { it.getValue<String>("SCHREG_STAT_CHANGE_NM") != "제적" }
                    .flatMap {
                        Mono.zip(
                            Mono.just(it),
                            getPhoneNumberByStudentNumber(studentNumber),
                            getGpaByStudentNumber(studentNumber, year, semester)
                                .defaultIfEmpty(0.0f),
                            getRankByStudentNumber(studentNumber, year, semester)
                                .defaultIfEmpty(0)
                        )
                    }
                    .map { (profile, phoneNumber, gpa, rank) ->
                        with(profile) {
                            Student(
                                number = getValue("STUNO"),
                                name = getValue("FNM"),
                                birthDate = LocalDate.parse(
                                    getValue("BIRYMD"),
                                    DateTimeFormatter.ofPattern("yyyyMMdd")
                                ),
                                phoneNumber = phoneNumber,
                                major = getValue("FCLT_NM"),
                                grade = getValue("NOW_SHYS_CD"),
                                semester = Semester(getValue<Int>("NOW_SHTM_CD")),
                                status = "${getValue<String>("SCHREG_STAT_CHANGE_NM")}(${getValue<String>("SCHREG_CHANGE_DTL_NM")})",
                                gpa = gpa.takeIf { it != 0.0f },
                                rank = rank.takeIf { it != 0 }
                            )
                        }
                    }
            }

    fun getAppliedStudentNumbersByLectureId(lectureId: String, year: Int, semester: Semester): Flux<String> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getAppliedStudentsRequest(lectureId, year, semester))
            .retrieve()
            .bodyToMono<String>()
            .flatMapMany { xmlMapper.getRows(it) }
            .map { it.getValue("STUNO") }

    fun getChapelByStudentNumber(studentNumber: String, year: Int, semester: Semester): Mono<ChapelResponse> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getChapelInformationRequest(studentNumber, year, semester))
            .retrieve()
            .bodyToMono<String>()
            .flatMap { xmlMapper.getRow(it) }
            .zipWith(
                getChapelAttendanceByStudentNumber(studentNumber, year, semester)
                    .collectList()
            )
            .map { (node, attendances) ->
                node.run {
                    ChapelResponse(
                        date = "${ChapelResponse.DAYS[getValue<Int>("DAY_CD") - 1]}요일 ${getValue<Int>("LTTM_CD")}교시",
                        room = getValue<String>("SPC_NM"),
                        seat = "(${ChapelResponse.COLUMNS[getValue<Int>("COL_DIV_CD") - 1]})열 ${getValue<String>("CHPL_SEAT_NO")}",
                        attendances = attendances
                    )
                }
            }

    private fun getChapelAttendanceByStudentNumber(studentNumber: String, year: Int, semester: Semester):
        Flux<Attendance> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getChapelAttendanceRequest(studentNumber, year, semester))
            .retrieve()
            .bodyToMono<String>()
            .flatMapMany { xmlMapper.getRows(it) }
            .map {
                Attendance(
                    date = LocalDate.parse(it.getValue("CHPL_DT"), DateTimeFormatter.ofPattern("yyyyMMdd")),
                    isAttended = it.getValue<String>("CHPL_ATTND").run { equals("Y") },
                    isOnline = it.getValue<String>("DIV").run { equals("온라인") }
                )
            }

    private fun getProfileByStudentNumber(studentNumber: String, year: Int, semester: Semester): Mono<JsonNode> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getProfileRequest(studentNumber, year, semester))
            .retrieve()
            .bodyToMono<String>()
            .flatMap { xmlMapper.getRow(it) }

    private fun getGpaByStudentNumber(studentNumber: String, year: Int, semester: Semester): Mono<Float> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getGpaRequest(studentNumber, year, semester))
            .retrieve()
            .bodyToMono<String>()
            .flatMap { xmlMapper.getRow(it) }
            .map { it.getValue<Float>("TOT_AVG_AVRP") }

    private fun getPhoneNumberByStudentNumber(studentNumber: String): Mono<String> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getPhoneNumberRequest(studentNumber))
            .retrieve()
            .bodyToMono<String>()
            .flatMap { xmlMapper.getRow(it) }
            .map { it.getValue("MBPHON_NO") }

    private fun getRankByStudentNumber(studentNumber: String, year: Int, semester: Semester): Mono<Int> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getRankRequest(studentNumber, year, semester))
            .retrieve()
            .bodyToMono<String>()
            .flatMap { xmlMapper.getRow(it) }
            .filter { it.getValue<String>("MJR_RANK").isNotBlank() }
            .map { it.getValue("MJR_RANK") }
}
