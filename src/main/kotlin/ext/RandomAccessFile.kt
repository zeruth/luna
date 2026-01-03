package ext

import rs.io.Packet
import java.io.File
import java.io.RandomAccessFile
import java.nio.file.Path

class RandomAccessFile(
    path: Path,
    readOnly: Boolean = false
) : RandomAccessFile(path.toFile().absolutePath, if (readOnly) "r" else "rw") {
    var pos: Long = 0L

    init {
        val file = path.toFile()
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
    }

    fun gdata(length: Int): ByteArray {
        val buffer = ByteArray(length)
        seek(pos)
        readFully(buffer)
        pos += length
        return buffer
    }

    fun gPacket(length: Int): Packet {
        return Packet(gdata(length))
    }

    fun pdata(buffer: Any) {
        val data: ByteArray = when (buffer) {
            is Packet -> buffer.data
            is ByteArray -> buffer
            else -> error("Unsupported buffer type: ${buffer::class}")
        }

        seek(pos)
        write(data)
        pos += data.size
    }

    override fun close() {
        close()
    }
}
