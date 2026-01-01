package rs.engine.script

import io.luna.game.event.impl.LoginEvent
import me.filby.neptune.serverscript.compiler.ServerScriptCompilerCLI
import nullpops.events.GlobalEventBus
import rs.cache.config.DbTableType
import rs.io.Packet
import java.io.File

/**
 * Decoder for Server RuneScript Binaries.
 * Reads script.dat and script.idx with lookup
 */
object RuneScriptProvider {

    lateinit var loginScript: ScriptFile
    init {
        DbTableType.load()

        GlobalEventBus.subscribe<LoginEvent> {
            println(loginScript.name())
            val state = RuneScriptRunner.init(loginScript, it.payload.plr)
            val result = ScriptState.of(RuneScriptRunner.execute(state))
            println(result)
        }
    }

    private val dir = File("./data/scripts_bin/").toPath()

    private lateinit var scripts: Array<ScriptFile?>

    private val scriptLookup = HashMap<Int, ScriptFile>()

    private val scriptNames = HashMap<String, Int>()

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

        println("Compiled/Loaded $loaded RuneScript Server Binaries in ${System.currentTimeMillis() - start} ms")
        return loaded
    }
}