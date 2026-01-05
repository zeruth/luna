package rs.cache.config

import rs.cache.ConfigType
import rs.cache.graphics.AnimFrame
import rs.io.JagFile
import rs.io.Packet
import java.io.File

class SeqType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<SeqType?> = emptyArray()

        fun load() {

            // adds some startup time but we need it for seqlength
            if (AnimFrame.instances.isEmpty()) {
                AnimFrame.load();
            }

            val server = Packet.load(dir.resolve("server/seq.dat").toFile())
            val jag = JagFile.load(dir.resolve("client/config").toFile())
            parse(server, jag)
        }

        fun parse(server: Packet, jag: JagFile) {
            val count = server.g2()
            val client = jag.read("seq.dat")!!
            client.position(2)

            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = SeqType(id)
                config.decodeType(server)
                config.decodeType(client)
                config.postDecode()

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }
            println("Loaded ${configs.size} SeqTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): SeqType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var frameCount = 0
    var frames = emptyArray<Int?>()
    var iframes = emptyArray<Int?>()
    var delay = emptyArray<Int?>()
    var loops = -1
    var walkmerge: Array<Int?>? = emptyArray<Int?>()
    var stretches = false
    var priority = 5
    var replaceheldleft = -1
    var replaceheldright = -1
    var maxloops = 99
    var preanim_move = -1
    var postanim_move = -1
    var duplicatebehavior = 0

    // precalculated for seqlength
    var duration = 0

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> {
                frameCount = dat.g1()
                frames = arrayOfNulls(frameCount)
                iframes = arrayOfNulls(frameCount)
                delay = arrayOfNulls(frameCount)

                for (i in 0 until frameCount) {
                    frames[i] = dat.g2()
                    iframes[i] = dat.g2()
                    if (iframes[i] == 65535) {
                        iframes[i] = -1
                    }

                    delay[i] = dat.g2()
                    if (delay[i] == 0) {
                        delay[i] = AnimFrame.instances[this.frames[i]!!]!!.delay;
                    }

                    if (delay[i] == 0) {
                        delay[i] = 1;
                    }

                    this.duration += this.delay[i]!!
                }
            }
            2 -> loops = dat.g2()
            3 -> {
                val count = dat.g1()
                walkmerge = arrayOfNulls(count + 1)
                for (i in 0 until count) {
                    walkmerge!![i] = dat.g1();
                }
                walkmerge!![count] = 9999999;
            }
            4 -> stretches = true
            5 -> priority = dat.g1()
            6 -> replaceheldleft = dat.g2()
            7 -> replaceheldright = dat.g2()
            8 -> maxloops = dat.g1()
            9 -> preanim_move = dat.g1()
            10 -> postanim_move = dat.g1()
            11 -> duplicatebehavior = dat.g1()
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }
    }

    override fun postDecode() {
        if (frameCount == 0) {
            frameCount = 1
            frames = arrayOfNulls(frameCount)
            frames[0] = -1;
            iframes = arrayOfNulls(frameCount)
            iframes[0] = -1;
            delay = arrayOfNulls(frameCount)
            delay[0] = -1;
        }

        if (preanim_move == -1) {
            if (walkmerge == null) {
                preanim_move = 0;
            } else {
                preanim_move = 2;
            }
        }

        if (this.postanim_move == -1) {
            if (this.walkmerge == null) {
                postanim_move = 0;
            } else {
                postanim_move = 2;
            }
        }
    }
}