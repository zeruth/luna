package rs.util

open class Linkable {

    var key: Long = 0L
    var next: Linkable? = null
    var prev: Linkable? = null

    fun unlink() {
        if (prev != null) {
            prev?.next = next
            next?.prev = prev
            next = null
            prev = null
        }
    }
}
