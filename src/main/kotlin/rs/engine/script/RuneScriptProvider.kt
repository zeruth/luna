package rs.engine.script

import io.luna.game.event.impl.LoginEvent
import me.filby.neptune.runescript.compiler.codegen.script.RuneScript
import me.filby.neptune.serverscript.compiler.ServerScriptCompilerCLI
import nullpops.events.GlobalEventBus
import rs.cache.config.CategoryType
import rs.cache.config.DbRowType
import rs.cache.config.DbTableType
import rs.cache.config.EnumType
import rs.cache.config.HuntType
import rs.cache.config.InvType
import rs.cache.config.MesAnimType
import rs.cache.config.ParamType
import rs.cache.config.StructType
import rs.cache.config.VarNpcType
import rs.cache.config.VarSharedType
import rs.engine.OnDemand
import rs.engine.script.test.FakeScriptFile
import rs.io.FileStream
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
        println("---Lost-City (377)---")
        CategoryType.load()
        DbRowType.load()
        DbTableType.load()
        EnumType.load()
        HuntType.load()
        InvType.load()
        MesAnimType.load()
        ParamType.load()
        StructType.load()
        VarNpcType.load()
        VarSharedType.load()
        println(".............Cache..............")
        OnDemand
        val cache = FileStream(File("./data/pack/").toPath())
        val index = 2
        val count = cache.count(index)
        for (file in 0 until count) {
            cache.read(index, file) ?: throw RuntimeException("could not read Config archive")
        }
        println("Loaded $count Configs")

        println("......Compiling RuneScript......")
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

        println("Lost-City Engine loaded in ${System.currentTimeMillis() - start}ms")
        println("--------------------------------")
        return loaded
    }

    fun get(id: Int): ScriptFile? {
        if (scripts.isEmpty())
            return FakeScriptFile.simple(opcodes = intArrayOf(-1)) as ScriptFile
        return scripts[id]
    }
}