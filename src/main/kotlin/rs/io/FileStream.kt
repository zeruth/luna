package rs.io

import ext.RandomAccessFile
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists

class FileStream(val dir: Path, val createNew: Boolean = false, val readOnly: Boolean = false) {
    var dat: RandomAccessFile? = null
    var idx: Array<RandomAccessFile?> = arrayOfNulls(5)

    var discardPacked = false
    var packed: Array<Array<Array<Byte?>?>?> = arrayOfNulls(5)

    init {
        if (!dir.exists()) {
            dir.createDirectories()
        }

        val datPath = dir.resolve("main_file_cache.dat")
        if (createNew || !datPath.exists()) {
            datPath.createFile()

            for (i in 0..4) {
                val idxPath = dir.resolve("main_file_cache.idx$i")
                idxPath.createFile()
            }
        }

        dat = RandomAccessFile(datPath, readOnly)

        for (i in 0..4) {
            idx[i] = RandomAccessFile(dir.resolve("main_file_cache.idx$i"), readOnly);
            packed[i] = arrayOfNulls<Array<Byte?>?>(0)
        }
    }

    fun count(index: Int): Int {
        if (index < 0 || index > idx.size || idx[index] == null)
            return 0
        val count = idx[index]!!.length().toInt() / 6
        return count
    }

    fun read(archive: Int, file: Int, decompress: Boolean = false): Array<Byte?>? {
        val dat = dat ?: return null

        if (archive < 0 || archive > idx.size || idx[archive] == null) return null
        if (file < 0 || file > count(archive)) return null

        if (packed[archive]!!.isEmpty())
            packed[archive] = arrayOfNulls<Array<Byte?>?>(count(archive))

        packed[archive]?.get(file)?.let {
            return it
        }

        val idx = idx[archive]
        idx!!.pos = (file * 6).toLong()
        val idxHeader = idx.gPacket(6)

        val size = idxHeader.g3()
        var sector = idxHeader.g3()

        if (size > 2000000) return null
        if (sector <= 0 || sector > dat.length() / 520) return null

        val data = Packet(ByteArray(size))
        for (part in 0 until size) {
            if (sector == 0) break

            dat.pos = (sector * 520).toLong()

            var available = size - data.position()
            if (available > 512)
                available = 512

            val header = dat.gPacket(available + 8)
            val sectorFile = header.g2()
            val sectorPart = header.g2()
            val nextSector = header.g3()
            val sectorIndex = header.g1()

            if (file != sectorFile || part != sectorPart || archive != sectorIndex - 1) return null
            if (nextSector < 0 || nextSector > dat.length() / 520) return null

            data.pdata(header.data, header.position(), available);

            sector = nextSector;
        }

        if (!decompress) {
            if (!discardPacked) {
                this.packed[archive]?.set(file, data.data.toTypedArray() as Array<Byte?>?)
            }

            return data.data.toTypedArray() as Array<Byte?>?
        }

        if (archive == 0) {
            return data.data.toTypedArray() as Array<Byte?>?
        } else {
            return data.data.toTypedArray() as Array<Byte?>?
        }
    }
}