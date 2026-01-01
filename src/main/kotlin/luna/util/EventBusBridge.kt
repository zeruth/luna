package luna.util

import nullpops.events.GlobalEventBus

/**
 * Provides an easy-to-access instance for Java classes
 */
class EventBusBridge {
    companion object {
        val instance = GlobalEventBus
    }
}