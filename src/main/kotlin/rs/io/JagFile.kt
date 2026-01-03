package rs.io

import java.io.File

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

class JagFile() {
    constructor(src: Packet) : this() {
        this.src = src
    }

    var src: Packet? = null
    var data: ByteArray? = null
    var fileCount = 0
    var fileHash: IntArray? = null
    var fileName: Array<String>? = null
    var fileUnpackedSize: IntArray? = null
    var filePackedSize: IntArray? = null
    var filePos: IntArray? = null
    var compressWhole = false
    
    init {
        src?.let { src ->
            val unpackedSize = src.g3()
            val packedSize = src.g3()

            if (unpackedSize == packedSize) {

            }
        }
    }
    companion object {
        fun load(path: File): JagFile {
            return JagFile(Packet.load(path))
        }
    }
}