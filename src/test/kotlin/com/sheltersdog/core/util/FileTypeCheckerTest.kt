package com.sheltersdog.core.util

import com.sheltersdog.core.model.FileType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart

class FileTypeCheckerTest {

    @MethodSource
    @ParameterizedTest(name = "type: {0} :: mediaType: {1}")
    fun fileTypeCheckTest(
        type: FileType, mediaType: MediaType,
        test: (expected: Boolean) -> Unit,
    ) {
        val filePart = mock(FilePart::class.java)
        val headers = HttpHeaders()
        headers.contentType = mediaType
        Mockito.`when`(filePart.headers()).thenReturn(headers)
        test(fileTypeCheck(type, filePart))
    }

    @MethodSource
    @ParameterizedTest(name = "filename: {0} :: expectedExtension: {1}")
    fun getFileExtensionWithFilenameTest(filename: String, expectedExtension: String) {
        val filePart = mock(FilePart::class.java)
        Mockito.`when`(filePart.filename()).thenReturn(filename)
        assertEquals(getFileExtension(filePart), expectedExtension)
    }

    @MethodSource
    @ParameterizedTest(name = "mediaType: {0} :: expectedExtension: {1}")
    fun getFileExtensionWithMediaTypeTest(type: MediaType, expectedExtension: String) {
        val filePart = mock(FilePart::class.java)
        Mockito.`when`(filePart.filename()).thenReturn(null)

        val headers = HttpHeaders()
        headers.contentType = type
        Mockito.`when`(filePart.headers()).thenReturn(headers)

        assertEquals(getFileExtension(filePart), expectedExtension)
    }

    companion object {
        @JvmStatic
        fun fileTypeCheckTest() = listOf(
            Arguments.of(
                FileType.IMAGE,
                MediaType.IMAGE_JPEG,
                { expected: Boolean -> assertEquals(expected, true) }
            ),
            Arguments.of(
                FileType.IMAGE,
                MediaType.IMAGE_PNG,
                { expected: Boolean -> assertEquals(expected, true) }
            ),
            Arguments.of(
                FileType.IMAGE,
                MediaType.IMAGE_GIF,
                { expected: Boolean -> assertEquals(expected, false) }
            ),
            Arguments.of(
                FileType.IMAGE,
                MediaType.TEXT_XML,
                { expected: Boolean -> assertEquals(expected, false) }
            ),
            Arguments.of(
                FileType.IMAGE,
                MediaType.APPLICATION_JSON,
                { expected: Boolean -> assertEquals(expected, false) }
            ),
            Arguments.of(
                FileType.IMAGE,
                MediaType.APPLICATION_JSON,
                { expected: Boolean -> assertEquals(expected, false) }
            ),
            Arguments.of(
                FileType.VIDEO,
                MediaType.valueOf("video/mp4"),
                { expected: Boolean -> assertEquals(expected, true) }
            ),
            Arguments.of(
                FileType.VIDEO,
                MediaType.TEXT_HTML,
                { expected: Boolean -> assertEquals(expected, false) }
            ),
        )
        @JvmStatic
        fun getFileExtensionWithFilenameTest() = listOf(
            Arguments.of("ihaiwlef.jpeg", "jpeg"),
            Arguments.of("ihaiwlef.png", "png"),
            Arguments.of("png.ihaiwlef.png", "png"),
            Arguments.of("png.iha.i.wlef.png", "png"),
            Arguments.of("png.iha.i.wlef.png", "png"),
            Arguments.of("png.iha.i.wlef.png", "png"),
        )

        @JvmStatic
        val fileExtensionWithMediaTypeTest = listOf(
            Arguments.of(MediaType.IMAGE_PNG, "png"),
            Arguments.of(MediaType.IMAGE_JPEG, "jpeg"),
            Arguments.of(MediaType.valueOf("image/jpg"), "jpg"),
            Arguments.of(MediaType.valueOf("video/mp4"), "mp4"),
            Arguments.of(MediaType.TEXT_HTML, ""),
        )
    }
}