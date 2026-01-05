package rs.cache.config

import rs.cache.ConfigType
import rs.engine.entity.BlockWalk
import rs.engine.entity.MoveRestrict
import rs.engine.entity.NpcMode
import rs.engine.entity.NpcStat
import rs.io.JagFile
import rs.io.Packet
import java.io.File

class NpcType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<NpcType?> = emptyArray()

        fun load() {
            val server = Packet.load(dir.resolve("server/npc.dat").toFile())
            val jag = JagFile.load(dir.resolve("client/config").toFile())
            parse(server, jag)
        }

        fun parse(server: Packet, jag: JagFile) {
            val count = server.g2()
            val client = jag.read("npc.dat")!!
            client.position(2)

            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = NpcType(id)
                config.decodeType(server)
                config.decodeType(client)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            println("Loaded ${configs.size} NpcTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): NpcType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var name = ""
    var desc = ""
    var size = 1
    var models: Array<Int?>? = null
    var heads: Array<Int?>? = null
    var hasanim = false;
    var readyanim = -1;
    var walkanim = -1;
    var walkanim_b = -1;
    var walkanim_r = -1;
    var walkanim_l = -1;
    var hasalpha = false;
    var recol_s: Array<Int?>? = null
    var recol_d: Array<Int?>? = null
    var op: Array<String?>? = null
    var resizex = -1;
    var resizey = -1;
    var resizez = -1;
    var minimap = true;
    var vislevel = -1;
    var resizeh = 128;
    var resizev = 128;
    var alwaysontop = false;
    var ambient = 0;
    var contrast = 0;
    var headicon = -1;
    var turnspeed = 32;
    var multivarbit = -1;
    var multivarp = -1;
    var multinpc: Array<Int?>? = null
    var active = true;

    // server-side
    var regenRate = 100;
    var category = -1;
    var wanderrange = 5;
    var maxrange = 7;
    var huntrange = 0;
    var timer = -1;
    var respawnrate = 100; // default to 1-minute
    var stats = arrayOf(1, 1, 1, 1, 1, 1)
    var moverestrict = MoveRestrict.NORMAL
    var attackrange = 0;
    var huntmode = -1;
    var defaultmode = NpcMode.WANDER
    var members = false;
    var blockwalk = BlockWalk.NPC;
    lateinit var params: ParamMap
    var patrolCoord: Array<Int?>? = null
    var patrolDelay: Array<Int?>? = null
    var givechase = true

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> {
                val count = dat.g1()
                models = arrayOfNulls(count)

                for (i in 0 until count) {
                    models!![i] = dat.g2()
                }
            }
            2 -> name = dat.gjstr()
            3 -> desc = dat.gjstr()
            12 -> size = dat.g1()
            13 -> readyanim = dat.g2()
            14 -> walkanim = dat.g2()
            16 -> hasanim = true
            17 -> {
                walkanim = dat.g2();
                walkanim_b = dat.g2();
                walkanim_r = dat.g2();
                walkanim_l = dat.g2();
            }
            18 -> category = dat.g2()
            30,31,32,33,34,35,36,37,38,39 -> {
                if (op == null) {
                    op = arrayOfNulls(5)
                }

                op!![code - 30] = dat.gjstr();
            }
            40 -> {
                val count = dat.g1()
                recol_s = arrayOfNulls(count)
                recol_d = arrayOfNulls(count)

                for (i in 0 until count) {
                    recol_s!![i] = dat.g2();
                    recol_d!![i] = dat.g2();
                }
            }
            60 -> {
                val count = dat.g1()
                heads = arrayOfNulls(count)

                for (i in 0 until count) {
                    heads!![i] = dat.g2();
                }
            }
            74 -> stats[NpcStat.ATTACK.ordinal] = dat.g2()
            75 -> stats[NpcStat.DEFENCE.ordinal] = dat.g2()
            76 -> stats[NpcStat.STRENGTH.ordinal] = dat.g2()
            77 -> stats[NpcStat.HITPOINTS.ordinal] = dat.g2()
            78 -> stats[NpcStat.RANGED.ordinal] = dat.g2()
            79 -> stats[NpcStat.MAGIC.ordinal] = dat.g2()
            90 -> resizex = dat.g2()
            91 -> resizey = dat.g2()
            92 -> resizez = dat.g2()
            93 -> minimap = false
            95 -> vislevel = dat.g2()
            97 -> resizeh = dat.g2()
            98 -> resizev = dat.g2()
            99 -> alwaysontop = true
            100 -> ambient = dat.g1b()
            101 -> contrast = dat.g1b()
            102 -> headicon = dat.g2()
            103 -> turnspeed = dat.g2()
            106 -> {
                this.multivarbit = dat.g2();
                if (this.multivarbit == 65535) {
                    this.multivarbit = -1;
                }

                this.multivarp = dat.g2();
                if (this.multivarp == 65535) {
                    this.multivarp = -1;
                }

                val count = dat.g1()
                multinpc = arrayOfNulls(count + 1)
                for (i in 0..count) {
                    multinpc!![i] = dat.g2()
                    if (multinpc!![i] == 65535) multinpc!![i] = -1
                }
            }
            107 -> active = false
            200 -> wanderrange = dat.g2()
            201 -> maxrange = dat.g2()
            202 -> huntrange = dat.g1()
            203 -> timer = dat.g2()
            204 -> respawnrate = dat.g2()
            206 -> moverestrict = MoveRestrict.of(dat.g1())
            207 -> attackrange = dat.g2()
            208 -> blockwalk = BlockWalk.of(dat.g1())
            209 -> huntmode = dat.g1()
            210 -> defaultmode = dat.g1()
            211 -> members = true
            212 -> {
                val count = dat.g1()
                patrolCoord = arrayOfNulls(count)
                patrolDelay = arrayOfNulls(count)

                for (i in 0 until count) {
                    patrolCoord!![i] = dat.g4s()
                    patrolDelay!![i] = dat.g1()
                }
            }
            213 -> givechase = false
            249 -> params = ParamHelper.decodeParams(dat)
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }


    }
}