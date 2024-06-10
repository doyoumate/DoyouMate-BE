package com.doyoumate.domain.professor.adapter

import com.doyoumate.common.annotation.Client
import com.doyoumate.common.util.getRows
import com.doyoumate.common.util.getValue
import com.doyoumate.domain.global.util.SuwingsRequests
import com.doyoumate.domain.lecture.model.enum.Semester
import com.doyoumate.domain.professor.model.Professor
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.time.LocalDate
import java.util.stream.Collectors

@Client
class ProfessorClient(
    private val webClient: WebClient,
    private val xmlMapper: XmlMapper
) {
    fun getProfessorById(id: String): Mono<Professor> =
        LocalDate.now()
            .let { date ->
                val (year, semester) = date.year to Semester(date)
                webClient.post()
                    .contentType(MediaType.APPLICATION_XML)
                    .bodyValue(SuwingsRequests.getProfessorRequest(id))
                    .retrieve()
                    .bodyToMono<String>()
                    .map { xmlMapper.readTree(it) }
                    .zipWith(getScoreById(id, year, semester))
                    .map { (professor, score) ->
                        with(professor) {
                            Professor(
                                id = id,
                                name = getValue("NM"),
                                phoneNumber = getValue("HP_NO"),
                                email = getValue("E_MAIL"),
                                score = score.takeIf { it != 0f },
                                role = getValue("POSITION_NM")
                            )
                        }
                    }
            }

    private fun getScoreById(id: String, year: Int, semester: Semester): Mono<Float> =
        webClient.post()
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(SuwingsRequests.getScoreRequest(id, year, semester))
            .retrieve()
            .bodyToMono<String>()
            .flatMapMany { xmlMapper.getRows(it) }
            .collect(Collectors.averagingDouble { it.getValue<Double>("EVAL_AVRP") })
            .map { it.toFloat() }
            .defaultIfEmpty(0f)
}
