package com.doyoumate.domain.lecture.model.enum

enum class Type(
    val typeName: String,
    val id: Int
) {
    GENERAL_EDUCATION_REQUIRED("교양필수", 11),
    GENERAL_EDUCATION_ELECTIVE("교양선택", 12),
    MINOR_ELECTIVES("마이크로전공", 75),
    MAJOR_FOUNDATION("전공기초", 21),
    MAJOR_REQUIRED("전공필수", 22),
    MAJOR_ELECTIVE("전공선택", 23),
    MINOR_REQUIRED("부전공필수", 31),
    MINOR_ELECTIVE("부전공선택", 32),
    MINOR_FOUNDATION("부전공기초", 33),
    DOUBLE_MAJOR_REQUIRED("복수전공필수", 41),
    DOUBLE_MAJOR_FOUNDATION("복수전공기초", 43),
    GENERAL_ELECTIVE("일반선택", 51),
    TEACHING_REQUIRED("교직필수", 61),
    LINKED_REQUIRED("연계필수", 71),
    LINKED_ELECTIVE("연계선택", 72),
    ACADEMIC_FOUNDATION("학문기초", 81),
    CHAPEL("채플", 99);

    companion object {
        operator fun invoke(id: Int): Type =
            entries.first { it.id == id }
    }
}
