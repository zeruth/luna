package rs.io

import ext.ArrayListExt.ensure
import ext.ArrayListExt.length
import ext.RandomAccessFile
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists

class FileStream(val dir: Path, val createNew: Boolean = false, val readOnly: Boolean = false) {/*
    var dat: RandomAccessFile? = null
    var idx = ArrayList<RandomAccessFile?>()

    var discardPacket = false
    var packed = ArrayList<Array<Array<Byte>>?>()

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

        dat = randomAccessFile(datPath, readOnly)

        idx.ensure(5)
        packed.ensure(5)

        for (i in 0..4) {
            this.idx[i] = randomAccessFile(dir.resolve("main_file_cache.idx$i"), readOnly);
            //this.packed[i] = emptyArray<Byte>()
        }
    }

    fun count(index: Int): Int {
        if (index < 0 || index > idx.length() || idx[index] == null)
            return 0

        return idx[index]!!.length().toInt() / 6
    }

    fun read(archive: Int, file: Int, decompress: Boolean = false): Array<Byte>? {
        if (dat == null)
            return null

        if (archive < 0 || archive > idx.length() || idx[archive] == null)
            return null

        if (file < 0 || file > count(archive))
            return null

        packed[archive]?.get(file)?.let {
            return it
        }

        val idx = idx[archive]
        idx!!.seek((file * 6).toLong())
    }*/
}
/*

fun randomAccessFile(path: Path, readOnly: Boolean): RandomAccessFile {
    return RandomAccessFile(path.toFile(), if (readOnly) "r" else "rw")
}*/
