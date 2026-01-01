package runescript

import java.io.File
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.inputStream

/**
 * Simple decoder for [JagFileScriptWriter].
 * Reads script.dat and script.idx into a map of id -> ByteArray
 */
class RuneScriptParser {

    private val dir = File("./data/scripts_bin/").toPath()

    fun read(): Map<Int, ByteArray?> {
        val datPath = dir.resolve("script.dat")
        val idxPath = dir.resolve("script.idx")

        if (!datPath.toFile().exists() || !idxPath.toFile().exists()) {
            throw IllegalArgumentException("Both script.dat and script.idx must exist in $dir")
        }

        datPath.inputStream().use { dat ->
            idxPath.inputStream().use { idx ->

                // Read number of entries (first 4 bytes)
                val datEntries = dat.readIntBE()
                val idxEntries = idx.readIntBE()
                val version = dat.readIntBE() // skip version in dat

                if (version != 25) {
                    throw IllegalArgumentException("Invalid RuneScript Compiler version or corrupt script bundle")
                }

                val map = mutableMapOf<Int, ByteArray?>()

                for (id in 0 until datEntries) {
                    val size = idx.readIntBE()
                    if (size == 0) {
                        map[id] = null
                        continue
                    }

                    val data = ByteArray(size)
                    val read = dat.readNBytes(size)
                    if (read.size != size) throw IllegalStateException("Unexpected EOF for id $id")
                    map[id] = read
                }


                println("Loaded $datEntries RuneScript Server Binaries")
                return map
            }
        }
    }

    private fun InputStream.readIntBE(): Int {
        val b1 = read()
        val b2 = read()
        val b3 = read()
        val b4 = read()
        if (b4 == -1) throw IllegalStateException("Unexpected EOF")
        return (b1 shl 24) or (b2 shl 16) or (b3 shl 8) or b4
    }
}
