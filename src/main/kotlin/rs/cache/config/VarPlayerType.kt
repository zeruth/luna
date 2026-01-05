package rs.cache.config

import rs.cache.ConfigType
import rs.io.JagFile
import rs.io.Packet
import java.io.File

class VarPlayerType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<VarPlayerType?> = emptyArray()

        val SCOPE_TEMP = 0
        val SCOPE_PERM = 1

        // engine-level client <-> server varp
        var RUN = 0

        fun load() {
            val server = Packet.load(dir.resolve("server/varp.dat").toFile())
            val jag = JagFile.load(dir.resolve("client/config").toFile())
            parse(server, jag)
        }

        fun parse(server: Packet, jag: JagFile) {
            val count = server.g2()
            val client = jag.read("varp.dat")!!
            client.position(2)

            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = VarPlayerType(id)
                config.decodeType(server)
                config.decodeType(client)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }

                if (config.clientcode == 7) {
                    // unused in client so my best guess is that this was used to find the engine varp
                    RUN = config.id
                }
            }
            println("Loaded ${configs.size} VarPlayerTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): VarPlayerType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var clientcode = 0

    var scope = SCOPE_TEMP
    var type = ScriptVarType.INT
    var protect = true
    var transmit = false

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> scope = dat.g1()
            2 -> type = dat.g1()
            4 -> protect = false
            5 -> clientcode = dat.g2()
            6 -> transmit = true
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }
    }
}