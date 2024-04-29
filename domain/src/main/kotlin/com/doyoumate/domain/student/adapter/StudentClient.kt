package com.doyoumate.domain.student.adapter

import com.doyoumate.common.annotation.Client
import com.doyoumate.common.util.*
import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.student.model.Student
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Client
class StudentClient(
    private val webClient: WebClient,
    private val xmlMapper: XmlMapper,
) {
    fun getStudentByNumber(number: String): Mono<Student> =
        getProfileByNumber(number)
            .filter { it.getValue<String>("SCHREG_STAT_CHANGE_NM") != "제적" }
            .flatMap {
                Mono.zip(
                    Mono.just(it),
                    getPhoneNumberByNumber(number),
                    getGpaByNumber(number)
                        .defaultIfEmpty(0.0f),
                    getRankByNumber(number)
                        .defaultIfEmpty(0)
                )
            }
            .map { (profile, phoneNumber, gpa, rank) ->
                profile.run {
                    Student(
                        number = getValue("STUNO"),
                        name = getValue("FNM"),
                        birthDate = LocalDate.parse(getValue("BIRYMD"), DateTimeFormatter.ofPattern("yyyyMMdd")),
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

    private fun getProfileByNumber(number: String): Mono<JsonNode> =
        LocalDate.now()
            .let {
                """
                    <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ar.iframe.ar02_20030201_f_M0_F0_xda" con="enc"> 
            	        <PGM_ID value="90010101"/>
            	        <YY value="${it.year}"/>
            	        <SHTM_CD value="${Semester(it).id}"/>
            	        <LANG_GUBUN value="K"/>
            	        <STUNO value="$number"/>
                    </rqM0_F0>
                 """
            }
            .let {
                webClient.post()
                    .contentType(MediaType.APPLICATION_XML)
                    .bodyValue(it)
                    .retrieve()
                    .bodyToMono<String>()
            }
            .flatMap { xmlMapper.getRow(it) }

    private fun getGpaByNumber(id: String): Mono<Float> =
        LocalDate.now()
            .let {
                """
                    <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ag.ag04.ag04_20060407_m_M0_F0_xda" con="enc"> 
            	        <FCLT_GSCH_DIV value="1"/>
            	        <STUNO value="$id"/>
            	        <LSRT_YY value="${it.year}"/>
                        <LSRT_SHTM_CD value="${Semester(it).id}"/>
                    </rqM0_F0>
                """
            }
            .let {
                webClient.post()
                    .contentType(MediaType.APPLICATION_XML)
                    .bodyValue(it)
                    .retrieve()
                    .bodyToMono<String>()
            }
            .flatMap {
                xmlMapper.getRow(it)
            }
            .map {
                it.getValue<Float>("TOT_AVG_AVRP")
            }

    private fun getPhoneNumberByNumber(number: String): Mono<String> =
        """
            <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ar.iframe.ar02_20030202_d_M0_F0_xda" con="enc"> 
			    <STUNO value="$number"/>
            </rqM0_F0>
        """.let {
            webClient.post()
                .contentType(MediaType.APPLICATION_XML)
                .bodyValue(it)
                .retrieve()
                .bodyToMono<String>()
        }.flatMap {
            xmlMapper.getRow(it)
        }.map {
            it.getValue("MBPHON_NO")
        }

    private fun getRankByNumber(number: String): Mono<Int> =
        LocalDate.now()
            .let {
                if (it.monthValue > 7) {
                    it.year to Semester.FIRST
                } else {
                    it.year - 1 to Semester.SECOND
                }
            }
            .let { (year, semester) ->
                """
                    <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ag.ag02.ag02_20060206_m_M0_F0_xda" con="sudev"> 
            	        <FCLT_GSCH_DIV_CD value="1"/>
            	        <YY value="$year"/> 
            	        <SHTM_CD value="${semester.id}"/> 
            	        <STUNO value="$number"/>
                    </rqM0_F0>
                """
            }.let {
                webClient.post()
                    .contentType(MediaType.APPLICATION_XML)
                    .bodyValue(it)
                    .retrieve()
                    .bodyToMono<String>()
            }
            .flatMap { xmlMapper.getRow(it) }
            .filter { it.getValue<String>("MJR_RANK").isNotBlank() }
            .map { it.getValue("MJR_RANK") }
}
