package com.doyoumate.domain.lecture.adapter

import com.doyoumate.common.annotation.Client
import com.doyoumate.common.util.getRow
import com.doyoumate.common.util.getRows
import com.doyoumate.common.util.getValue
import com.doyoumate.domain.global.util.SuwingsRequests
import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.model.Plan
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
                    .map { Lecture(node, it) }
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
            .map { Plan(it) }
}
