package util

import nullpops.events.GlobalEventBus

class EventBusBridge {
    companion object {
        val instance = GlobalEventBus
    }
}