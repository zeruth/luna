package rs.cache.config

import rs.cache.ConfigType
import rs.io.JagFile
import rs.io.Packet
import java.io.File

class FloType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<FloType?> = emptyArray()

        fun load() {
            val server = Packet.load(dir.resolve("server/flo.dat").toFile())
            val jag = JagFile.load(dir.resolve("client/config").toFile())
            parse(server, jag)
        }

        fun parse(server: Packet, jag: JagFile) {
            val count = server.g2()
            val client = jag.read("flo.dat")!!
            client.position(2)

            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = FloType(id)
                config.decodeType(server)
                config.decodeType(client)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }
            println("Loaded ${configs.size} FloTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): FloType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var rgb = 0
    var texture = -1
    var overlay = false
    var occlude = true

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> rgb = dat.g3()
            2 -> texture = dat.g1()
            3 -> overlay = true
            5 -> occlude = false
            6 -> debugname = dat.gjstr()
            7 -> dat.g3() //TODO: Investigate
            else -> throw RuntimeException("Unhandled code $code")
        }
    }
}