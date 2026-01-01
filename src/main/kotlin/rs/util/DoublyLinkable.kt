package rs.util

open class DoublyLinkable : Linkable() {

    var next2: DoublyLinkable? = null
    var prev2: DoublyLinkable? = null

    fun unlink2() {
        if (prev2 != null) {
            prev2?.next2 = next2
            next2?.prev2 = prev2
            next2 = null
            prev2 = null
        }
    }
}
