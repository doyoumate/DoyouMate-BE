package com.doyoumate.domain.board.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Board(
    @Id
    val id: String? = null,
    val name: String,
    val deletedDate: LocalDateTime? = null
)
