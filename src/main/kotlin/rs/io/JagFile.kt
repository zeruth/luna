package rs.io

import luna.util.Compression
import rs.util.KNOWN_NAMES
import java.io.File
import java.util.Locale

class JagFileQueue() {
    var hash = -1
    var name = ""

    var write = false
    var data: ByteArray? = null
    var packedSize = -1
    var unpackedSize = -1

    var delete = false

    var rename = false
    var newHash = -1
    var newName = ""
}

class JagFile(src: Packet) {

    var src: Packet
    var data: ByteArray
    var fileCount = 0
    var fileHash: IntArray
    var fileName: Array<String?>
    var fileUnpackedSize: IntArray
    var filePackedSize: IntArray
    var filePos: IntArray
    var compressWhole = false
    
    init {
        this.src = src
        val unpackedSize = src.g3()
        val packedSize = src.g3()

        if (unpackedSize == packedSize) {
            data = src.data.copyOf()
            compressWhole = false
        } else {
            data = Compression.decompressBZip2(src.data.sliceArray(6..packedSize), unpackedSize, prependHeader = true)
            this.src = Packet(data)
            compressWhole = true
        }

        fileCount = src.g2()

        fileHash = IntArray(fileCount)
        fileName = arrayOfNulls(fileCount)
        fileUnpackedSize = IntArray(fileCount)
        filePackedSize = IntArray(fileCount)
        filePos = IntArray(fileCount)

        var pos = src.position() + fileCount * 10

        for (i in fileHash.indices) {
            val hash = src.g4s()
            fileHash[i] = hash
            fileName[i] = HASH_TO_NAME[hash]

            fileUnpackedSize[i] = src.g3()
            filePackedSize[i] = src.g3()

            filePos[i] = pos
            pos += filePackedSize[i]
        }
    }

    fun read(name: String): Packet? {
        val hash = genHash(name)
        val index = fileHash.indexOf(hash)
        return if (index != -1) get(index) else null
    }

    fun get(index: Int): Packet =
        if (compressWhole) {
            Packet(data.copyOfRange(filePos[index], filePos[index] + filePackedSize[index]))
        } else {
            Packet(
                Compression.decompressBZip2(
                    data.copyOfRange(filePos[index], filePos[index] + filePackedSize[index]), prependHeader = true
                )
            )
        }

    companion object {
        fun load(path: File): JagFile {
            val file = JagFile(Packet.load(path))
            println("Loaded JagFile ${path.path}")
            return file
        }

        val KNOWN_HASHES: Map<String, Int> =
            KNOWN_NAMES.associateWith { genHash(it) }

        val HASH_TO_NAME: Map<Int, String> =
            KNOWN_HASHES.entries.associate { (name, hash) -> hash to name }

        fun genHash(name: String): Int {
            var hash = 0
            val upper = name.uppercase(Locale.ROOT)

            for (ch in upper) {
                hash = (hash * 61 + ch.code - 32)
            }

            return hash
        }
    }
}