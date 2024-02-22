package com.doyoumate.domain.lecture.model.enum

enum class Section(
    val sectionName: String,
    val id: Int
) {
    SOCIAL_SCIENCES("사회과학영역", 3),
    NATURAL_SCIENCES("자연과학영역", 4),
    GENERAL_ELECTIVES("일반선택영역", 7),
    HUMANITIES("인문학영역", 8),
    CULTURE_AND_ARTS("문화예술영역", 9),
    COMPUTER_UTILIZATION("컴퓨터활용", 11),
    CHARACTER_EDUCATION("인성교양", 12),
    BASIC_EDUCATION("기초교양", 13),
    CORE_EDUCATION("핵심교양", 14),
    HUMANITIES_AND_ARTS("인문예술영역", 15),
    DIGITAL_LITERACY("디지털 리터러시영역", 16),
    MVP_PLUS("MVP+", 17);

    companion object {
        operator fun invoke(sectionName: String): Section =
            entries.first { it.sectionName == sectionName }

        operator fun invoke(id: Int): Section =
            entries.first { it.id == id }
    }
}
