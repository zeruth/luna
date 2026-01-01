package runescript

import io.luna.game.event.impl.LoginEvent
import me.filby.neptune.serverscript.compiler.ServerScriptCompiler
import me.filby.neptune.serverscript.compiler.ServerScriptCompilerCLI
import nullpops.events.GlobalEventBus

class RuneScriptManager {

    companion object {
        init {
            GlobalEventBus.subscribe<LoginEvent> {
                println("E-Login: ${it.payload.plr.username}")
            }
        }
    }

    fun compile() {
        ServerScriptCompilerCLI.main(emptyArray())
    }
}