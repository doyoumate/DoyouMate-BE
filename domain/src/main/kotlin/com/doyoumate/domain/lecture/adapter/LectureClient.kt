package com.doyoumate.domain.lecture.adapter

import com.doyoumate.common.annotation.Client
import com.doyoumate.common.util.getRows
import com.doyoumate.common.util.getValue
import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import java.time.LocalDate

@Client
class LectureClient(
    private val webClient: WebClient,
    private val xmlMapper: XmlMapper
) {
    fun getLecturesByYearAndSemester(year: Int, semester: Semester): Flux<Lecture> =
        """
            <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ac.ac03.ac03_20040305_m_M0_F0_xda" con="sudev">
            	<FCLT_GSCH_DIV_CD value="1"/>
            	<OPEN_YY value="$year"/>
            	<OPEN_SHTM_CD value="${semester.id}"/>
            	<LANG_GUBUN value="K"/>
            </rqM0_F0>
        """.let {
            webClient.post()
                .contentType(MediaType.APPLICATION_XML)
                .bodyValue(it)
                .retrieve()
                .bodyToMono<String>()
        }.flatMapMany { xmlMapper.getRows(it) }
            .map {
                it.run {
                    Lecture(
                        id = getValue<String>("EDUCUR_CORS_NO") + getValue<String>("LECT_NO"),
                        year = getValue("OPEN_YY"),
                        grade = getValue("EDUCUR_CORS_SHYS_CD"),
                        semester = Semester(getValue<Int>("OPEN_SHTM_CD")),
                        major = getValue("ORGN4_NM"),
                        name = getValue("SBJT_NM"),
                        professor = getValue("FNM"),
                        room = getValue("LT_ROOM_NM"),
                        date = getValue("LTTM"),
                        credit = getValue("LCTPT"),
                        section = getValue<String>("CTNCCH_FLD_DIV_CD")
                            .run { if (isBlank()) null else Section(toInt()) }
                    )
                }
            }

    fun getAppliedLectureIdsByStudentNumber(studentNumber: String): Flux<String> =
        LocalDate.now()
            .let {
                """
                    <rqM2_F0 task="system.commonTask" action="comSelect" xda="academic.al.al04.al04_20050409_m_M2_F0_xda" con="sudev">
                        <FCLT_GSCH_DIV_CD value="1"/>
                        <YY value="${it.year}"/>   
                        <SHTM_CD value="${Semester(it).id}"/>   
                        <STUNO value="$studentNumber"/>
                    </rqM2_F0>
                """
            }
            .let {
                webClient.post()
                    .contentType(MediaType.APPLICATION_XML)
                    .bodyValue(it)
                    .retrieve()
                    .bodyToMono<String>()
            }
            .flatMapMany { xmlMapper.getRows(it) }
            .map { it.getValue<String>("EDUCUR_CORS_NO") + it.getValue<String>("LECT_NO") }

    fun getPreAppliedLectureIdsByStudentNumber(studentNumber: String): Flux<String> =
        LocalDate.now()
            .let {
                """
                    <rqM2_F0 task="system.commonTask" action="comSelect" xda="academic.al.al04.al04_20050403_m_M2_F0_xda" con="sudev">
                        <FCLT_GSCH_DIV_CD value="1"/>
                        <YY value="${it.year}"/>   
                        <SHTM_CD value="${Semester(it).id}"/>   
                        <STUNO value="$studentNumber"/>
                    </rqM2_F0>
                """
            }
            .let {
                webClient.post()
                    .contentType(MediaType.APPLICATION_XML)
                    .bodyValue(it)
                    .retrieve()
                    .bodyToMono<String>()
            }
            .flatMapMany { xmlMapper.getRows(it) }
            .map { it.getValue<String>("EDUCUR_CORS_NO") + it.getValue<String>("LECT_NO") }
}
