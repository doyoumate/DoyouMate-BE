package com.doyoumate.domain.professor.dto.response

import com.doyoumate.domain.professor.model.Professor

data class ProfessorResponse(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val score: Float?,
    val role: String
) {
    companion object {
        operator fun invoke(professor: Professor): ProfessorResponse =
            with(professor) {
                ProfessorResponse(
                    id = id,
                    name = name,
                    phoneNumber = phoneNumber,
                    email = email,
                    score = score,
                    role = role
                )
            }
    }
}
