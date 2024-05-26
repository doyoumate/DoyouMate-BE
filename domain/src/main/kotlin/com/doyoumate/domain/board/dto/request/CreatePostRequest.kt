package com.doyoumate.domain.board.dto.request

import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap

data class CreatePostRequest(
    val boardId: String,
    val title: String,
    val content: String,
    val images: List<FilePart>
) {
    companion object {
        operator fun invoke(multipartData: MultiValueMap<String, Part>): CreatePostRequest =
            with(multipartData) {
                CreatePostRequest(
                    boardId = (get("boardId")?.first() as FormFieldPart).value(),
                    title = (get("title")?.first() as FormFieldPart).value(),
                    content = (get("content")?.first() as FormFieldPart).value(),
                    images = (get("images") ?: emptyList()) as List<FilePart>
                )
            }
    }
}
