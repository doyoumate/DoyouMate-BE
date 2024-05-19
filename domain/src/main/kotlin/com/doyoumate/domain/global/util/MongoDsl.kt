package com.doyoumate.domain.global.util

import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.mapping.toDotPath
import org.springframework.data.mongodb.core.query.Update
import kotlin.reflect.KProperty

infix fun <T> Update.set(update: Pair<KProperty<T>, T>) {
    update.also { (field, value) -> set(field.toDotPath(), value) }
}

infix fun <T> Update.setOnInsert(update: Pair<KProperty<T>, T>) {
    update.also { (field, value) -> setOnInsert(field.toDotPath(), value) }
}

infix fun <T> KProperty<T>.sortBy(direction: Direction): Sort = Sort.by(direction, this.toDotPath())
