package com.doyoumate.domain.lecture.adapter

import com.doyoumate.common.annotation.Client
import com.doyoumate.common.util.getRows
import com.doyoumate.common.util.getValue
import com.doyoumate.domain.lecture.model.Lecture
import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Flux
import java.time.LocalDate

@Client
class LectureClient(
    private val webClient: WebClient,
    private val xmlMapper: XmlMapper,
    @Value("\${api.uri}")
    private val uri: String
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
                .uri(uri)
                .contentType(MediaType.APPLICATION_XML)
                .bodyValue(it)
                .retrieve()
                .bodyToMono<String>()
        }.flatMapMany {
            xmlMapper.getRows(it)
        }.map {
            it.get("ROW")
                .run {
                    Lecture(
                        id = getValue("LECT_NO"),
                        courseNumber = getValue("EDUCUR_CORS_NO"),
                        code = getValue("SBJT_CD"),
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
}
