package com.doyoumate.domain.global.util

import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

fun query(init: QueryDsl.() -> Unit): Query =
    QueryDsl()
        .apply(init)
        .build()

class QueryDsl {
    private val query = Query()

    infix fun String.isEqualTo(value: Any?) {
        value?.let {
            query.addCriteria(where(this).isEqualTo(it))
        }
    }

    infix fun String.like(value: String?) {
        value?.let {
            query.addCriteria(where(this).regex(value, "i"))
        }
    }

    fun paging(pageable: Pageable) {
        query.with(pageable)
    }

    fun build(): Query = query
}
