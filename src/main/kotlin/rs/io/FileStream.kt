package rs.io

import ext.RandomAccessFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import kotlin.io.path.*

class FileStream(
    val dir: Path,
    createNew: Boolean = false,
    readOnly: Boolean = false
) {

    private var dat: RandomAccessFile? = null
    private val idx: Array<RandomAccessFile?> = arrayOfNulls(5)

    var discardPacked = false
    private val packed: Array<Array<ByteArray?>?> = arrayOfNulls(5)

    init {
        dir.createDirectories()

        val datPath = dir.resolve("main_file_cache.dat")
        if (createNew || !datPath.exists()) {
            datPath.createFile()
            for (i in 0 until 5) {
                dir.resolve("main_file_cache.idx$i").createFile()
            }
        }

        dat = RandomAccessFile(datPath, readOnly)

        for (i in 0 until 5) {
            idx[i] = RandomAccessFile(dir.resolve("main_file_cache.idx$i"), readOnly)
            packed[i] = null
        }
    }

    fun count(archive: Int): Int {
        val index = idx.getOrNull(archive) ?: return 0
        return (index.length() / 6).toInt()
    }

    fun read(archive: Int, file: Int, decompress: Boolean = false): ByteArray? {
        val dat = dat ?: return null
        val index = idx.getOrNull(archive) ?: return null

        if (file !in 0 until count(archive)) return null

        val cache = packed[archive] ?: arrayOfNulls<ByteArray>(count(archive)).also {
            packed[archive] = it
        }

        cache[file]?.let { return it }

        index.pos = (file * 6).toLong()
        val idxHeader = index.gPacket(6)

        val size = idxHeader.g3()
        var sector = idxHeader.g3()

        if (size !in 1..2_000_000) return null
        if (sector <= 0 || sector > dat.length() / 520) return null

        val data = Packet(ByteArray(size))

        var part = 0
        while (data.position() < size && sector != 0) {
            dat.pos = (sector * 520).toLong()

            val remaining = minOf(512, size - data.position())
            val header = dat.gPacket(remaining + 8)

            val sectorFile = header.g2()
            val sectorPart = header.g2()
            val nextSector = header.g3()
            val sectorIndex = header.g1()

            if (
                sectorFile != file ||
                sectorPart != part ||
                sectorIndex != archive + 1) return null

            if (nextSector < 0 || nextSector > dat.length() / 520) return null

            data.pdata(header.data, header.position(), remaining)
            sector = nextSector
            part++
        }

        val result = data.data

        if (!decompress && !discardPacked) {
            cache[file] = result
        }

        return if (archive == 0) {
            data.data
        } else {
            gunzipSync(data.data)
        }
    }

    fun gunzipSync(data: ByteArray): ByteArray {
        return GZIPInputStream(ByteArrayInputStream(data)).use { gzip ->
            ByteArrayOutputStream().use { out ->
                val buffer = ByteArray(4096)
                var read: Int
                while (gzip.read(buffer).also { read = it } > 0) {
                    out.write(buffer, 0, read)
                }
                out.toByteArray()
            }
        }
    }
}
