package com.doyoumate.domain.fixture

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDate

const val ID = "id"
val CREATED_DATE = LocalDate.now()!!
const val PAGE = 0
const val SIZE = 1

fun createPageable(
    page: Int = PAGE,
    size: Int = SIZE
): Pageable =
    PageRequest.of(page, size)
