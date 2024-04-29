package com.doyoumate.domain.global.util

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo

fun query(init: QueryDsl.() -> Unit): Query =
    QueryDsl()
        .apply(init)
        .query

fun update(init: UpdateDsl.() -> Unit): Update =
    UpdateDsl()
        .apply(init)
        .update

class QueryDsl {
    val query = Query()

    infix fun String.isEqualTo(value: Any?) {
        value?.let { query.addCriteria(where(this).isEqualTo(it)) }
    }

    infix fun String.like(value: String?) {
        value?.let { query.addCriteria(where(this).regex(value, "i")) }
    }

    infix fun String.sortBy(direction: Sort.Direction) {
        query.with(Sort.by(direction, this))
    }

    fun paging(pageable: Pageable) {
        query.with(pageable)
    }
}

class UpdateDsl {
    val update = Update()

    infix fun String.set(value: Any?) {
        value?.let { update.set(this, it) }
    }

    infix fun String.setOnInsert(value: Any?) {
        value?.let { update.setOnInsert(this, it) }
    }
}
