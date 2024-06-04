package com.doyoumate.domain.lecture.adapter

import com.doyoumate.common.annotation.Client
import com.doyoumate.common.util.getRow
import com.doyoumate.common.util.getRows
import com.doyoumate.common.util.getValue
import com.doyoumate.domain.global.util.SuwingsRequests
import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.model.Plan
import com.doyoumate.domain.lecture.model.Ratio
import com.doyoumate.domain.lecture.model.enum.Evaluation
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Client
class LectureClient(
    private val webClient: WebClient,
    private val xmlMapper: XmlMapper
) {
    fun getLecturesByYearAndSemester(year: Int, semester: Semester): Flux<Lecture> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getLecturesRequest(year, semester))
            .retrieve()
            .bodyToMono<String>()
            .flatMapMany { xmlMapper.getRows(it) }
            .delayElements(Duration.ofMillis(10))
            .flatMap { node ->
                getPlan(node)
                    .map {
                        with(node) {
                            Lecture(
                                id = getValue<String>("EDUCUR_CORS_NO") + getValue<String>("LECT_NO"),
                                professorId = getValue<String>("STF_NO"),
                                year = getValue("OPEN_YY"),
                                grade = getValue("EDUCUR_CORS_SHYS_CD"),
                                semester = Semester(getValue<Int>("OPEN_SHTM_CD")),
                                major = getValue("ORGN4_NM"),
                                name = getValue("SBJT_NM"),
                                professorName = getValue("FNM"),
                                room = getValue("LT_ROOM_NM"),
                                date = getValue("LTTM"),
                                credit = getValue("LCTPT"),
                                section = getValue<String>("CTNCCH_FLD_DIV_CD").run {
                                    if (isBlank()) null else Section(toInt())
                                },
                                plan = it
                            )
                        }
                    }
            }

    fun getAppliedLectureIdsByStudentNumber(studentNumber: String): Flux<String> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getAppliedLecturesRequest(studentNumber))
            .retrieve()
            .bodyToMono<String>()
            .flatMapMany { xmlMapper.getRows(it) }
            .map { it.getValue<String>("EDUCUR_CORS_NO") + it.getValue<String>("LECT_NO") }

    fun getPreAppliedLectureIdsByStudentNumber(studentNumber: String): Flux<String> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getPreAppliedLecturesRequest(studentNumber))
            .retrieve()
            .bodyToMono<String>()
            .flatMapMany { xmlMapper.getRows(it) }
            .map { it.getValue<String>("EDUCUR_CORS_NO") + it.getValue<String>("LECT_NO") }

    private fun getPlan(node: JsonNode): Mono<Plan> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getPlanRequest(node))
            .retrieve()
            .bodyToMono<String>()
            .flatMap { xmlMapper.getRow(it) }
            .map {
                Plan(
                    ratio = Ratio(
                        theory = it.getValue<String>("THEORY_WKHS").ifBlank { "0" }.toInt(),
                        practice = it.getValue<String>("PRAC_WKHS").ifBlank { "0" }.toInt()
                    ),
                    overview = it.getValue("LT_PURP_SMRY_CTNT"),
                    objective = it.getValue("LRN_TGET_CTNT"),
                    type = it.getValue("SBJT_CLSF_CD"),
                    evaluation = Evaluation(it.getValue<String>("LSRT_EVAL_DIV_CD")),
                    prerequisites = it.getValue("PRE_LRN_CTNT")
                )
            }
}
