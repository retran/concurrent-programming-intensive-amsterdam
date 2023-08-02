@file:Suppress("FoldInitializerAndIfToElvis", "DuplicatedCode")

package day2

import kotlinx.atomicfu.*

class MSQueueWithLinearTimeRemove<E> : QueueWithRemove<E> {
    private val head: AtomicRef<Node>
    private val tail: AtomicRef<Node>

    init {
        val dummy = Node(null)
        head = atomic(dummy)
        tail = atomic(dummy)
    }

    override fun enqueue(element: E) {
        val node = Node(element)
        while (true) {
            val localTail = tail.value
            if (localTail.next.compareAndSet(null, node)) {
                if (localTail.extractedOrRemoved) {
                    localTail.remove()
                }

                tail.compareAndSet(localTail, node)

                return
            } else {
                tail.compareAndSet(localTail, localTail.next.value!!)
            }
        }
    }

    override fun dequeue(): E? {
        while (true) {
            val localHead = head.value
            val next = localHead.next.value

            if (next == null) {
                return null
            }

            if (head.compareAndSet(localHead, next) && next.markExtractedOrRemoved()) {
                return next.element
            };
        }
    }

    override fun remove(element: E): Boolean {
        // Traverse the linked list, searching the specified
        // element. Try to remove the corresponding node if found.
        // DO NOT CHANGE THIS CODE.
        var node = head.value
        while (true) {
            val next = node.next.value
            if (next == null) return false
            node = next
            if (node.element == element && node.remove()) return true
        }
    }

    /**
     * This is an internal function for tests.
     * DO NOT CHANGE THIS CODE.
     */
    override fun validate() {
        check(tail.value.next.value == null) {
            "tail.next must be null"
        }
        var node = head.value
        // Traverse the linked list
        while (true) {
            if (node !== head.value && node !== tail.value) {
                check(!node.extractedOrRemoved) {
                    "Removed node with element ${node.element} found in the middle of this queue"
                }
            }
            node = node.next.value ?: break
        }
    }

    // TODO: Node is an inner class for accessing `head` in `remove()`
    private inner class Node(
        var element: E?
    ) {
        val next = atomic<Node?>(null)

        /**
         * TODO: Both [dequeue] and [remove] should mark
         * TODO: nodes as "extracted or removed".
         */
        private val _extractedOrRemoved = atomic(false)
        val extractedOrRemoved get() = _extractedOrRemoved.value

        fun markExtractedOrRemoved(): Boolean = _extractedOrRemoved.compareAndSet(false, true)

        /**
         * Removes this node from the queue structure.
         * Returns `true` if this node was successfully
         * removed, or `false` if it has already been
         * removed by [remove] or extracted by [dequeue].
         */
        fun remove(): Boolean {
            val removed = markExtractedOrRemoved()

            val prevNode = findPrev()

            if (prevNode == null) {
                return removed
            }

            val nextNode = next.value

            if (nextNode == null) {
                return removed
            }

            prevNode.next.compareAndSet(this, nextNode)

            if (nextNode.extractedOrRemoved) {
                nextNode.remove()
            }

            return removed
        }

        private fun findPrev() : Node? {
            var prevNode = head.value

            if (prevNode == this) {
                return null
            }

            while (true) {
                val next = prevNode.next.value
                if (next == null) {
                    return null
                }

                if (next == this) {
                    break
                }

                prevNode = next
            }

            return prevNode
        }
    }
}
