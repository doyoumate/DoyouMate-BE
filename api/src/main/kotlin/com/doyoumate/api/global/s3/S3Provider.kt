package com.doyoumate.api.global.s3

import com.doyoumate.common.util.component1
import com.doyoumate.common.util.component2
import com.doyoumate.common.util.toByteArray
import com.doyoumate.domain.board.exception.ImageOverSizeException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import kotlin.math.pow

@Component
class S3Provider(
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}")
    private val bucket: String,
    @Value("\${aws.s3.region}")
    private val region: String
) {
    fun upload(key: String, filePart: FilePart): Mono<String> =
        getPutObjectRequest(bucket, key, filePart)
            .zipWith(filePart.content().toByteArray())
            .filter { (_, bytes) -> (bytes.size / (2.0.pow(20))) <= 1 }
            .switchIfEmpty(Mono.error(ImageOverSizeException()))
            .flatMap { (request, bytes) ->
                Mono.fromFuture(
                    s3Client.putObject(request, AsyncRequestBody.fromBytes(bytes))
                )
            }
            .map { createUri(bucket, region, key) }

    private fun getPutObjectRequest(bucket: String, key: String, filePart: FilePart): Mono<PutObjectRequest> =
        DataBufferUtils
            .join(filePart.content())
            .map {
                PutObjectRequest.builder()
                    .contentType(filePart.headers().contentType!!.toString())
                    .contentLength(it.readableByteCount().toLong())
                    .key(key)
                    .bucket(bucket)
                    .build()
            }

    private fun createUri(bucket: String, region: String, key: String) =
        "https://${bucket}.s3.${region}.amazonaws.com/${key}"
}
