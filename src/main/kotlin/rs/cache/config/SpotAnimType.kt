package rs.cache.config

import rs.cache.ConfigType
import rs.io.JagFile
import rs.io.Packet
import java.io.File

class SpotAnimType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<SpotAnimType?> = emptyArray()

        fun load() {
            val server = Packet.load(dir.resolve("server/spotanim.dat").toFile())
            val jag = JagFile.load(dir.resolve("client/config").toFile())
            parse(server, jag)
        }

        fun parse(server: Packet, jag: JagFile) {
            val count = server.g2()
            val client = jag.read("spotanim.dat")!!
            client.position(2)

            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = SpotAnimType(id)
                config.decodeType(server)
                config.decodeType(client)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }
            println("Loaded ${configs.size} SpotAnimTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): SpotAnimType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var model = 0
    var anim = -1
    var hasalpha = false
    var recol_s = arrayOfNulls<Byte>(6)
    var recol_d = arrayOfNulls<Byte>(6)
    var resizeh = 128
    var resizev = 128
    var orientation = 0
    var ambient = 0
    var contrast = 0

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> model = dat.g2()
            2 -> anim = dat.g2()
            3 -> hasalpha = true
            4 -> resizeh = dat.g2()
            5 -> resizev = dat.g2()
            6 -> orientation = dat.g2()
            7 -> ambient = dat.g1()
            8 -> contrast = dat.g1()
            40,41,42,43,44,45,46,47,48,49 -> recol_s[code - 40] = dat.g2().toByte()
            50,51,52,53,54,55,56,57,58,59 -> recol_d[code - 50] = dat.g2().toByte()
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }
    }
}