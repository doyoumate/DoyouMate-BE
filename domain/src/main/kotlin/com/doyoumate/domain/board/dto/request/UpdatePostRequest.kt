package com.doyoumate.domain.board.dto.request

import com.doyoumate.domain.board.model.Board
import com.doyoumate.domain.board.model.Post
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap

data class UpdatePostRequest(
    val boardId: String,
    val title: String,
    val content: String,
    val images: List<FilePart>,
    val isImageUpdated: Boolean
) {
    companion object {
        operator fun invoke(multipartData: MultiValueMap<String, Part>): UpdatePostRequest =
            with(multipartData) {
                UpdatePostRequest(
                    boardId = (get("boardId")!!.first() as FormFieldPart).value(),
                    title = (get("title")!!.first() as FormFieldPart).value(),
                    content = (get("content")!!.first() as FormFieldPart).value(),
                    images = (get("images") ?: emptyList()) as List<FilePart>,
                    isImageUpdated = (get("isImageUpdated")!!.first() as FormFieldPart).value()
                        .toBoolean()
                )
            }
    }

    fun updateEntity(post: Post, board: Board, images: List<String>): Post =
        post.copy(
            board = board,
            title = title,
            content = content,
            images = images
        )
}
