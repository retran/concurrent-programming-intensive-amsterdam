package day2

import day1.*
import kotlinx.atomicfu.*

class FAABasedQueue<E> : Queue<E> {
    private val enqIdx = atomic(0)
    private val deqIdx = atomic(0)

    private val head = atomic(Segment(0))
    private val tail = atomic(head.value)

    private fun shouldTryToDeque(): Boolean {
        while (true) {
            val curDeqIdx = deqIdx.value
            val curEnqIdx = enqIdx.value
            if (curDeqIdx != deqIdx.value) {
                continue
            }
            return curDeqIdx <= curEnqIdx
        }
    }

    private fun moveTailForward(start: Segment) {
        while (true) {
            val localTail = tail.value;
            val localEnqIdx = enqIdx.value

            if (localTail.id == localEnqIdx % 64 || localTail.next.value == null) {
                return
            }

            tail.compareAndSet(localTail, localTail.next.value!!)
        }
    }

    private fun moveHeadForward(start: Segment) {
        while (true) {
            val localHead = head.value;
            val localDeqIdx = deqIdx.value

            if (localHead.id == localDeqIdx % 64 || localHead.next.value == null) {
                return
            }

            head.compareAndSet(localHead, localHead.next.value!!)
        }
    }

    private fun findSegment(start: Segment, i: Int) : Segment {
        var current = start;
        while (true) {
            if (current.id == i) {
                return current
            }
            current.next.compareAndSet(null, Segment(current.id + 1))
            current = current.next.value!!;
        }
    }

    override fun enqueue(element: E) {
        while (true) {
            val localTail = tail.value
            val i = enqIdx.getAndIncrement();
            val segment = findSegment(localTail, i % 64)
            moveTailForward(segment)
            if (segment.array[i % 64].compareAndSet(null, element)) {
                return
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun dequeue(): E? {
        while (true) {
            if (!shouldTryToDeque()) {
                return null
            }

            val localHead = head.value
            val i = deqIdx.getAndIncrement();
            val segment = findSegment(localHead, i % 64)
            moveHeadForward(segment)

            if (segment.array[i % 64].compareAndSet(null, POISONED)) {
                continue
            }

            return segment.array[i % 64].value as E?
        }
    }

    class Segment(val id: Int) {
        val array = atomicArrayOfNulls<Any?>(64)
        val next = atomic<Segment?>(null)
    }
}

private val POISONED = Any()
