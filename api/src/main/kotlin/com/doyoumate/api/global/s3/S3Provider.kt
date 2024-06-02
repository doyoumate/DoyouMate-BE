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
import software.amazon.awssdk.services.s3.model.Delete
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest
import software.amazon.awssdk.services.s3.model.ObjectIdentifier
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI
import kotlin.math.pow

@Component
class S3Provider(
    private val s3Client: S3AsyncClient,
    @Value("\${aws.s3.bucket}")
    private val bucket: String,
    @Value("\${aws.cloudFront.domain}")
    private val domain: String
) {
    fun upload(key: String, filePart: FilePart): Mono<String> =
        getPutObjectRequest(key, filePart)
            .zipWith(filePart.content().toByteArray())
            .filter { (_, bytes) -> (bytes.size / (2.0.pow(20))) <= 1 }
            .switchIfEmpty(Mono.error(ImageOverSizeException()))
            .flatMap { (request, bytes) ->
                Mono.fromFuture(
                    s3Client.putObject(request, AsyncRequestBody.fromBytes(bytes))
                )
            }
            .map { createUri(key) }

    fun deleteAll(uris: List<URI>): Mono<Void> =
        Mono.just(uris)
            .filter { it.isNotEmpty() }
            .map { getDeleteObjectsRequest(uris.map { getObjectKey(it) }) }
            .flatMap { Mono.fromFuture(s3Client.deleteObjects(it)) }
            .then()

    private fun getPutObjectRequest(key: String, filePart: FilePart): Mono<PutObjectRequest> =
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

    private fun getDeleteObjectsRequest(keys: List<String>): DeleteObjectsRequest =
        DeleteObjectsRequest.builder()
            .bucket(bucket)
            .delete(Delete.builder()
                .objects(
                    keys.map {
                        ObjectIdentifier
                            .builder()
                            .key(it)
                            .build()
                    }
                )
                .build()
            )
            .build()

    private fun createUri(key: String): String = "https://${domain}/${key}"

    private fun getObjectKey(uri: URI): String = uri.path.trimStart('/')
}
