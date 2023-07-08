package com.sheltersdog.core.util

import net.coobird.thumbnailator.Thumbnails
import org.apache.commons.imaging.Imaging
import org.apache.commons.io.FilenameUtils
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun resizeImage(
    file: File,
    resizeFile: File,
    extension: String
) {
    if (file.length() <= 1024 * 1024) {
        Thumbnails.of(file)
            .outputQuality(.5)
            .scale(.3)
            .outputFormat(extension)
            .toFile(resizeFile)

        return
    }
    resizeImageWithChunk(file, resizeFile, extension)
}

private fun resizeImageWithChunk(
    file: File,
    resizeFile: File,
    extension: String
) {
    val imageInfo = Imaging.getImageInfo(file)
    val chunkLength = file.length() / (1024 * 512)
    val chunkHeight = imageInfo.height / chunkLength

    val image = BufferedImage(
        (imageInfo.width * .3).toInt(),
        (imageInfo.height * .3).toInt(),
        BufferedImage.TYPE_INT_RGB
    )

    val graphics = image.graphics as Graphics2D

    var startHeight = 0
    var y = 0
    val baseName = FilenameUtils.getBaseName(file.name)
    for (index in 0..chunkLength) {
        val chunkFile = File(baseName + "_chunk_" + index + "." + extension)
        try {
            val chunkHeightSize =
                if (index == chunkLength) (imageInfo.height - (chunkLength * chunkHeight)).toInt()
                else chunkHeight.toInt()
            if (chunkHeight <= 0) break

            Thumbnails.of(file)
                .sourceRegion(0, startHeight, imageInfo.width, chunkHeightSize)
                .outputQuality(.5)
                .scale(.3)
                .outputFormat(extension)
                .toFile(chunkFile)

            startHeight += chunkHeight.toInt()
            y = drawChunkImage(graphics, y, chunkFile)
        } finally {
            chunkFile.delete()
        }
    }
    graphics.dispose()
    ImageIO.write(image, extension, resizeFile)
}

private fun drawChunkImage(graphics2D: Graphics2D, y: Int, chunkFile: File): Int {
    val chunkImage = ImageIO.read(chunkFile)
    graphics2D.drawImage(chunkImage, 0, y, null)
    return y + chunkImage.height
}
