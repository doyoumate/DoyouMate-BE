package com.doyoumate.domain.lecture.model

import com.doyoumate.domain.lecture.model.enum.Section
import com.doyoumate.domain.lecture.model.enum.Semester

data class Filter(
    val year: Set<Int>,
    val grade: Set<Int>,
    val semester: Set<Semester>,
    val major: Set<String>,
    val credit: Set<Int>,
    val section: Set<Section>
)
