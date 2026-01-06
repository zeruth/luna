package rs.cache.config

import rs.cache.ConfigType
import rs.engine.entity.BlockWalk
import rs.engine.entity.MoveRestrict
import rs.engine.entity.NpcMode
import rs.engine.entity.NpcStat
import rs.io.JagFile
import rs.io.Packet
import java.io.File

class LocType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<LocType?> = emptyArray()

        fun load() {
            val server = Packet.load(dir.resolve("server/loc.dat").toFile())
            val jag = JagFile.load(dir.resolve("client/config").toFile())
            parse(server, jag)
        }

        fun parse(server: Packet, jag: JagFile) {
            val count = server.g2()
            val client = jag.read("loc.dat")!!
            client.position(2)

            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = LocType(id)
                config.decodeType(server)
                config.decodeType(client)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            println("Loaded ${configs.size} LocTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): LocType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var models: Array<Int?>? = null
    var shapes: Array<Byte?>? = null
    var name = ""
    var desc = ""
    var recol_s: Array<Int?>? = null
    var recol_d: Array<Int?>? = null
    var width = 1
    var length = 1
    var blockwalk = true
    var blockrange = true
    var active = -1
    var hillskew = false;
    var sharelight = false;
    var occlude = false;
    var anim = -1;
    var hasalpha = false;
    var wallwidth = 16;
    var ambient = 0;
    var contrast = 0;
    var op: Array<String?>? = null
    var mapfunction = -1;
    var mapscene = -1;
    var mirror = false;
    var shadow = true;
    var resizex = 128;
    var resizey = 128;
    var resizez = 128;
    var forceapproach = 0;
    var offsetx = 0;
    var offsety = 0;
    var offsetz = 0;
    var forcedecor = false;
    var breakroutefinding = false;
    var raiseobject = -1;
    var multivarbit = -1;
    var multivarp = -1;
    var multiloc: Array<Int?>? = null

    var category = -1
    lateinit var params: ParamMap

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> {
                val count = dat.g1()
                models = arrayOfNulls(count)
                shapes = arrayOfNulls(count)

                for (i in 0 until count) {
                    models!![i] = dat.g2()
                    shapes!![i] = dat.g1().toByte()
                }
            }
            2 -> name = dat.gjstr()
            3 -> desc = dat.gjstr()
            5 -> {
                val count = dat.g1()
                models = arrayOfNulls(count)
                shapes = null

                for (i in 0 until count) {
                    models!![i] = dat.g2()
                }
            }
            14 -> width = dat.g1()
            15 -> length = dat.g1()
            17 -> blockwalk = false
            18 -> blockrange = false
            19 -> active = dat.g1()
            21 -> hillskew = true
            22 -> sharelight = true
            23 -> occlude = true
            24 -> {
                anim = dat.g2()

                if (anim == 65535)
                    anim = -1
            }
            25 -> hasalpha = true
            28 -> wallwidth = dat.g1()
            29 -> ambient = dat.g1b()
            30,31,32,33,34 -> {
                if (op == null) {
                    op = arrayOfNulls(5)
                }

                op!![code - 30] = dat.gjstr()
            }
            39 -> contrast = dat.g1b()
            40 -> {
                val count = dat.g1()
                recol_s = arrayOfNulls(count)
                recol_d = arrayOfNulls(count)

                for (i in 0 until count) {
                    recol_s!![i] = dat.g2()
                    recol_d!![i] = dat.g2()
                }
            }
            60 -> mapfunction = dat.g2()
            61 -> category = dat.g2()
            62 -> mirror = true
            64 -> shadow = false
            65 -> resizex = dat.g2()
            66 -> resizey = dat.g2()
            67 -> resizez = dat.g2()
            68 -> mapscene = dat.g2()
            69 -> forceapproach = dat.g1()
            70 -> offsetx = dat.g2s()
            71 -> offsety = dat.g2s()
            72 -> offsetz = dat.g2s()
            73 -> forcedecor = true
            74 -> breakroutefinding = true
            75 -> raiseobject = dat.g1()
            77 -> {
                multivarbit = dat.g2()
                if (multivarbit == 65536) {
                    multivarbit = -1
                }

                multivarp = dat.g2()
                if (multivarp == 65536) {
                    multivarp = -1
                }

                val count = dat.g1()
                multiloc = arrayOfNulls(count + 1)
                for (i in 0 .. count) {
                    multiloc!![i] = dat.g2()
                    if (multiloc!![i] == 65536) {
                        multiloc!![i] = -1
                    }
                }
            }
            249 -> params = ParamHelper.decodeParams(dat)
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }
    }
}