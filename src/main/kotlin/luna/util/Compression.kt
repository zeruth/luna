package luna.util

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream

object Compression {

    fun decompressBZip2(
        compressed: ByteArray,
        decompressedLength: Int? = null,
        prependHeader: Boolean = false,
        containsDecompressedLength: Boolean = false
    ): ByteArray {
        var data = compressed.copyOf()
        var prepend = prependHeader
        var length = decompressedLength ?: -1

        if (containsDecompressedLength) {
            require(data.size >= 4) { "Compressed array too short to contain length header" }

            length = ((data[0].toInt() and 0xFF) shl 24) or
                    ((data[1].toInt() and 0xFF) shl 16) or
                    ((data[2].toInt() and 0xFF) shl 8)  or
                    (data[3].toInt() and 0xFF)

            data[0] = 'B'.code.toByte()
            data[1] = 'Z'.code.toByte()
            data[2] = 'h'.code.toByte()
            data[3] = '1'.code.toByte()

            prepend = false
        }

        if (prepend) {
            val header = byteArrayOf('B'.code.toByte(), 'Z'.code.toByte(), 'h'.code.toByte(), '1'.code.toByte())
            data = header + data
        }

        return ByteArrayInputStream(data).use { input ->
            BZip2CompressorInputStream(input).use { bz2 ->
                val output = ByteArrayOutputStream(length.coerceAtLeast(0))
                bz2.copyTo(output)
                output.toByteArray()
            }
        }
    }

    fun decompressGZipSync(data: ByteArray): ByteArray {
        return GZIPInputStream(ByteArrayInputStream(data)).use { gzip ->
            buffer(gzip)
        }
    }

    fun buffer(gzip: InputStream) : ByteArray {
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(4096)
        var read: Int
        while (gzip.read(buffer).also { read = it } > 0) {
            out.write(buffer, 0, read)
        }
        return out.toByteArray()
    }
}