package rs.io

import luna.util.Compression
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

        for (i in 0 until fileCount) {
            fileHash[i] = src.g4s()
            val hashMatch = KNOWN_HASHES.containsValue(fileHash[i])
            if (hashMatch) {
                fileName[i] =  HASH_TO_NAME[fileHash[i]]
            }

            fileUnpackedSize[i] = src.g3()
            filePackedSize[i] = src.g3()

            filePos[i] = pos
            pos += this.filePackedSize[i]
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

        val KNOWN_NAMES = listOf(
            // title
            "index.dat",
            "logo.dat",
            "p11.dat",
            "p12.dat",
            "b12.dat",
            "q8.dat",
            "runes.dat",
            "title.dat",
            "titlebox.dat",
            "titlebutton.dat",
            // seen in 274
            "p11_full.dat",
            "p12_full.dat",
            "b12_full.dat",
            "q8_full.dat",

            // config
            "flo.dat",
            "flo.idx",
            "idk.dat",
            "idk.idx",
            "loc.dat",
            "loc.idx",
            "npc.dat",
            "npc.idx",
            "obj.dat",
            "obj.idx",
            "seq.dat",
            "seq.idx",
            "spotanim.dat",
            "spotanim.idx",
            "varp.dat",
            "varp.idx",
            // seen in 254
            "varbit.dat",
            "varbit.idx",
            // seen in 274
            "mesanim.dat",
            "mesanim.idx",
            "mes.dat",
            "mes.idx",
            "param.dat",
            "param.idx",
            "hunt.dat",
            "hunt.idx",

            // interface
            "data",

            // media
            "backbase1.dat",
            "backbase2.dat",
            "backhmid1.dat",
            "backhmid2.dat",
            "backleft1.dat",
            "backleft2.dat",
            "backright1.dat",
            "backright2.dat",
            "backtop1.dat",
            "backvmid1.dat",
            "backvmid2.dat",
            "backvmid3.dat",
            "chatback.dat",
            "combatboxes.dat",
            "combaticons.dat",
            "combaticons2.dat",
            "combaticons3.dat",
            "compass.dat",
            "cross.dat",
            "gnomeball_buttons.dat",
            "headicons.dat",
            "hitmarks.dat",
            "invback.dat",
            "leftarrow.dat",
            "magicoff.dat",
            "magicoff2.dat",
            "magicon.dat",
            "magicon2.dat",
            "mapback.dat",
            "mapdots.dat",
            "mapfunction.dat",
            "mapscene.dat",
            "miscgraphics.dat",
            "miscgraphics2.dat",
            "miscgraphics3.dat",
            "prayerglow.dat",
            "prayeroff.dat",
            "prayeron.dat",
            "redstone1.dat",
            "redstone2.dat",
            "redstone3.dat",
            "rightarrow.dat",
            "scrollbar.dat",
            "sideicons.dat",
            "staticons.dat",
            "staticons2.dat",
            "steelborder.dat",
            "steelborder2.dat",
            "sworddecor.dat",
            "tradebacking.dat",
            "wornicons.dat",
            "backtop2.dat",
            "mapflag.dat",
            "mapmarker.dat",
            "mod_icons.dat",
            "mapedge.dat",
            "blackmark.dat",
            "button_brown.dat",
            "button_brown_big.dat",
            "button_red.dat",
            "chest.dat",
            "coins.dat",
            "headicons_hint.dat",
            "headicons_pk.dat",
            "headicons_prayer.dat",
            "key.dat",
            "keys.dat",
            "leftarrow_small.dat",
            "letter.dat",
            "number_button.dat",
            "overlay_duel.dat",
            "overlay_multiway.dat",
            "pen.dat",
            "rightarrow_small.dat",
            "startgame.dat",
            "tex_brown.dat",
            "tex_red.dat",
            "titlescroll.dat",

            // models
            "base_head.dat",
            "base_label.dat",
            "base_type.dat",
            "frame_del.dat",
            "frame_head.dat",
            "frame_tran1.dat",
            "frame_tran2.dat",
            "ob_axis.dat",
            "ob_face1.dat",
            "ob_face2.dat",
            "ob_face3.dat",
            "ob_face4.dat",
            "ob_face5.dat",
            "ob_head.dat",
            "ob_point1.dat",
            "ob_point2.dat",
            "ob_point3.dat",
            "ob_point4.dat",
            "ob_point5.dat",
            "ob_vertex1.dat",
            "ob_vertex2.dat",

            // versionlist
            "anim_crc",
            "anim_index",
            "anim_version",
            "map_crc",
            "map_index",
            "map_version",
            "midi_crc",
            "midi_index",
            "midi_version",
            "model_crc",
            "model_index",
            "model_version",

            // textures
            *Array(50) { "$it.dat" },

            // wordenc
            "badenc.txt",
            "domainenc.txt",
            "fragmentsenc.txt",
            "tldlist.txt",

            // sounds
            "sounds.dat",

            // worldmap
            "labels.dat",
            "floorcol.dat",
            "underlay.dat",
            "overlay.dat",
            "size.dat"
        )

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