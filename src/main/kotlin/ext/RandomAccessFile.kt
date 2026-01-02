package ext

import rs.io.Packet
import java.io.File
import java.io.RandomAccessFile

class RandomAccessFile(
    path: String,
    readOnly: Boolean = false
) : RandomAccessFile(path, if (readOnly) "r" else "rw") {

    private val raf: RandomAccessFile
    var pos: Long = 0L

    init {
        val file = File(path)
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }

        raf = RandomAccessFile(file, if (readOnly) "r" else "rw")
    }

    val length: Long
        get() = raf.length()

    fun gdata(length: Int): ByteArray {
        val buffer = ByteArray(length)
        raf.seek(pos)
        raf.readFully(buffer)
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

        raf.seek(pos)
        raf.write(data)
        pos += data.size
    }

    override fun close() {
        raf.close()
    }
}
