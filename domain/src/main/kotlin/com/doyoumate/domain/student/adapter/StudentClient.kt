package com.doyoumate.domain.student.adapter

import com.doyoumate.common.annotation.Client
import com.doyoumate.common.util.*
import com.doyoumate.domain.global.util.SuwingsRequests
import com.doyoumate.domain.student.model.Student
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Client
class StudentClient(
    private val webClient: WebClient,
    private val xmlMapper: XmlMapper,
) {
    fun getStudentByStudentNumber(studentNumber: String): Mono<Student> =
        getProfileByStudentNumber(studentNumber)
            .filter { it.getValue<String>("SCHREG_STAT_CHANGE_NM") != "제적" }
            .flatMap {
                Mono.zip(
                    Mono.just(it),
                    getPhoneNumberByStudentNumber(studentNumber),
                    getGpaByStudentNumber(studentNumber)
                        .defaultIfEmpty(0.0f),
                    getRankByStudentNumber(studentNumber)
                        .defaultIfEmpty(0)
                )
            }
            .map { (profile, phoneNumber, gpa, rank) -> Student(profile, phoneNumber, gpa, rank) }

    private fun getProfileByStudentNumber(studentNumber: String): Mono<JsonNode> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getProfileRequest(studentNumber))
            .retrieve()
            .bodyToMono<String>()
            .flatMap { xmlMapper.getRow(it) }

    private fun getGpaByStudentNumber(studentNumber: String): Mono<Float> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getGpaRequest(studentNumber))
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

    private fun getRankByStudentNumber(studentNumber: String): Mono<Int> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getRankRequest(studentNumber))
            .retrieve()
            .bodyToMono<String>()
            .flatMap { xmlMapper.getRow(it) }
            .filter { it.getValue<String>("MJR_RANK").isNotBlank() }
            .map { it.getValue("MJR_RANK") }
}
