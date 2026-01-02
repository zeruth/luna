package rs.engine.script

import io.luna.game.event.impl.LoginEvent
import me.filby.neptune.runescript.compiler.codegen.script.RuneScript
import me.filby.neptune.serverscript.compiler.ServerScriptCompilerCLI
import nullpops.events.GlobalEventBus
import rs.cache.config.DbTableType
import rs.cache.config.InvType
import rs.engine.script.test.FakeScriptFile
import rs.io.Packet
import java.io.File

/**
 * Decoder for Server RuneScript Binaries.
 * Reads script.dat and script.idx with lookup
 */
object RuneScriptProvider {

    fun RuneScript.target(): String {
        return "[$trigger,$name$]"
    }

    lateinit var loginScript: ScriptFile
    init {
        println("---Lost-City RuneScript (377)---")
        DbTableType.load()
        InvType.load()
        println("--------------------------------")
        GlobalEventBus.subscribe<LoginEvent> {
            println(loginScript.name())
            val state = RuneScriptRunner.init(loginScript, it.payload.plr)
            val result = ScriptState.of(RuneScriptRunner.execute(state))
            println(result)
        }
    }

    private val dir = File("./data/scripts_bin/").toPath()


    private val scriptLookup = HashMap<Int, ScriptFile>()

    private val scriptNames = HashMap<String, Int>()

    var scripts: Array<ScriptFile?> = emptyArray()

    @JvmStatic
    fun parse(): Int {
        val start = System.currentTimeMillis()
        ServerScriptCompilerCLI.main(emptyArray())

        val datPath = dir.resolve("script.dat")
        val idxPath = dir.resolve("script.idx")

        if (!datPath.toFile().exists() || !idxPath.toFile().exists()) {
            throw IllegalArgumentException("Both script.dat and script.idx must exist in $dir")
        }

        val dat = Packet.load(datPath.toFile())
        val idx = Packet.load(idxPath.toFile())

        if (dat.length() < 1 || idx.length() < 1) {
            throw IllegalArgumentException("Invalid RuneScript Blob")
        }

        val entries = dat.g4s()

        idx.move(4)

        val version = dat.g4s()

        if (version != 25) {
            throw IllegalArgumentException("Invalid RuneScript Compiler version or corrupt script bundle, version: $version")
        }

        var loaded = 0

        scripts = arrayOfNulls<ScriptFile?>(entries)

        for (id in 0 until entries) {
            val size = idx.g4s()
            if (size == 0) {
                continue
            }

            val data = ByteArray(size)
            dat.gdata(data, 0, size)
            val script = ScriptFile.decode(id, Packet(data))
            scripts[id] = script
            scriptNames[script.name()] = id

            if (script.info!!.lookupKey.toLong() != 0xffffffff) {
                scriptLookup[script.info!!.lookupKey] = script
            }

            loaded++
        }

        loginScript = scripts.filterNotNull().first { it.name().contains("login") }
        return loaded
    }

    fun get(id: Int): ScriptFile? {
        if (scripts.isEmpty())
            return FakeScriptFile.simple(opcodes = intArrayOf(-1)) as ScriptFile
        return scripts[id]
    }
}