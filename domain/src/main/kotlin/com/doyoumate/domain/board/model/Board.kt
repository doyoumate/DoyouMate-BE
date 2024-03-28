package com.doyoumate.domain.board.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Board(
    @Id
    val id: String? = null,
    val name: String
)
