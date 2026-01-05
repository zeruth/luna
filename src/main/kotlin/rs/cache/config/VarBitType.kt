package rs.cache.config

import rs.cache.ConfigType
import rs.io.JagFile
import rs.io.Packet
import java.io.File

class VarBitType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<VarBitType?> = emptyArray()

        fun load() {
            val server = Packet.load(dir.resolve("server/varbit.dat").toFile())
            val jag = JagFile.load(dir.resolve("client/config").toFile())
            parse(server, jag)
        }

        fun parse(server: Packet, jag: JagFile) {
            val count = server.g2()
            val client = jag.read("varbit.dat")!!
            client.position(2)

            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = VarBitType(id)
                config.decodeType(server)
                config.decodeType(client)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }
            println("Loaded ${configs.size} VarBitTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): VarBitType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var basevar = -1
    var startbit = -1
    var endbit = -1

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> {
                basevar = dat.g2()
                startbit = dat.g1()
                this.endbit = dat.g1()
            }
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }
    }
}