package rs.cache.config

import rs.Environment
import rs.cache.ConfigType
import rs.cache.graphics.AnimFrame
import rs.io.JagFile
import rs.io.Packet
import java.io.File

class ObjType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<ObjType?> = emptyArray()

        fun load() {
            val server = Packet.load(dir.resolve("server/obj.dat").toFile())
            val jag = JagFile.load(dir.resolve("client/config").toFile())
            parse(server, jag)
        }

        fun parse(server: Packet, jag: JagFile) {
            val count = server.g2()
            val client = jag.read("obj.dat")!!
            client.position(2)

            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = ObjType(id)
                config.decodeType(server)
                config.decodeType(client)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            for (id in 0 until count) {
                val config = configs[id]!!

                if (config.certtemplate != -1) {
                    config.toCertificate()
                }

                if (config.dummyitem != 0) {
                    config.tradeable = false
                }

                if (!Environment.NODE_MEMBERS && config.members) {
                    config.tradeable = false
                    config.op = null
                    config.iop = null

                    config.params = config.params.filterKeys { key ->
                        ParamType.get(key)?.autodisable != true
                    }.toMutableMap()
                }
            }
            println("Loaded ${configs.size} ObjTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): ObjType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var model = 0
    var name = ""
    var desc = ""
    var recol_s = emptyArray<Int?>()
    var recol_d = emptyArray<Int?>()
    var zoom2d = 2000
    var xan2d = 0
    var yan2d = 0
    var zan2d = 0
    var xof2d = 0
    var yof2d = 0
    var code9 = false
    var code10 = -1
    var stackable = false
    var cost = 1
    var members = false
    var op: Array<String?>? = null
    var iop: Array<String?>? = null
    var manwear = -1
    var manwear2 = -1
    var manwearOffsetY = 0
    var womanwear = -1
    var womanwear2 = -1
    var womanwearOffsetY = 0
    var manwear3 = -1
    var womanwear3 = -1
    var manhead = -1
    var manhead2 = -1
    var womanhead = -1
    var womanhead2 = -1
    var countobj: Array<Int?>? = null
    var countco: Array<Int?>? = null
    var certlink = -1
    var certtemplate = -1
    var resizex = 128
    var resizey = 128
    var resizez = 128
    var ambient = 0
    var contrast = 0
    var team = 0

    // server-side
    var wearpos = -1
    var wearpos2 = -1
    var wearpos3 = -1
    var weight = 0 // in grams
    var category = -1
    var dummyitem = 0
    var tradeable = true
    var respawnrate = 100 // default to 1-minute

    lateinit var params: ParamMap

    fun toCertificate() {
        val template = get(certtemplate)!!
        this.model = template.model;
        this.zoom2d = template.zoom2d;
        this.xan2d = template.xan2d;
        this.yan2d = template.yan2d;
        this.zan2d = template.zan2d;
        this.xof2d = template.xof2d;
        this.yof2d = template.yof2d;
        this.recol_s = template.recol_s;
        this.recol_d = template.recol_d;

        val link = get(this.certlink)!!
        this.name = link.name;
        this.members = link.members;
        this.cost = link.cost;
        this.tradeable = link.tradeable;

        var article = "a"
        val c = link.name.lowercase().getOrNull(0)
        if (c in listOf('a', 'e', 'i', 'o', 'u')) {
            article = "an"
        }
        this.desc = "Swap this note at any bank for $article ${link.name}."

        this.stackable = true;
    }

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> model = dat.g2()
            2 -> name = dat.gjstr()
            3 -> desc = dat.gjstr()
            4 -> zoom2d = dat.g2()
            5 -> xan2d = dat.g2()
            6 -> yan2d = dat.g2()
            7 -> xof2d = dat.g2s()
            8 -> yof2d = dat.g2s()
            9 -> code9 = true
            10 -> code10 = dat.g2()
            11 -> stackable = true
            12 -> cost = dat.g4s()
            13 -> wearpos = dat.g1()
            14 -> wearpos2 = dat.g1()
            15 -> tradeable = false
            16 -> members = true
            23 -> {
                manwear = dat.g2()
                manwearOffsetY = dat.g1b()
            }
            24 -> manwear2 = dat.g2()
            25 -> {
                womanwear = dat.g2()
                womanwearOffsetY = dat.g1b()
            }
            26 -> womanwear2 = dat.g2()
            27 -> wearpos3 = dat.g1()
            30,31,32,33,34 -> {
                if (op == null) {
                    op = arrayOfNulls(5)
                }
                op!![code - 30] = dat.gjstr()
            }
            35,36,37,38,39 -> {
                if (iop == null) {
                    iop = arrayOfNulls(5)
                }
                iop!![code - 35] = dat.gjstr()
            }
            40 -> {
                val count = dat.g1()
                recol_s = arrayOfNulls(count)
                recol_d = arrayOfNulls(count)

                for (i in 0 until count) {
                    recol_s[i] = dat.g2()
                    recol_d[i] = dat.g2()
                }
            }
            75 -> weight = dat.g2s()
            78 -> manwear3 = dat.g2()
            79 -> womanwear3 = dat.g2()
            90 -> manhead = dat.g2()
            91 -> womanhead = dat.g2()
            92 -> manhead2 = dat.g2()
            93 -> womanhead2 = dat.g2()
            94 -> category = dat.g2()
            95 -> zan2d = dat.g2()
            96 -> dummyitem = dat.g1()
            97 -> certlink = dat.g2()
            98 -> certtemplate = dat.g2()
            100,101,102,103,104,105,106,107,108,109 -> {
                if (this.countobj == null || this.countco == null) {
                    this.countobj = arrayOfNulls(10)
                    this.countco = arrayOfNulls(10)
                }
                this.countobj!![code - 100] = dat.g2()
                this.countco!![code - 100] = dat.g2()
            }
            110 -> resizex = dat.g2()
            111 -> resizey = dat.g2()
            112 -> resizez = dat.g2()
            113 -> ambient = dat.g1b()
            114 -> contrast = dat.g1b()
            115 -> team = dat.g1()
            201 -> respawnrate = dat.g2()
            249 -> params = ParamHelper.decodeParams(dat);
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }


    }
}