package day2

import day1.*
import kotlinx.atomicfu.*

class FAABasedQueueSimplified<E> : Queue<E> {
    private val infiniteArray = atomicArrayOfNulls<Any?>(4096) // conceptually infinite array
    private val enqIdx = atomic(0)
    private val deqIdx = atomic(0)

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

    override fun enqueue(element: E) {
        while (true) {
            val i = enqIdx.getAndIncrement();
            if (infiniteArray[i].compareAndSet(null, element)) {
                return
            }
        }

        // TODO: Increment the counter atomically via Fetch-and-Add.
        // TODO: Use `getAndIncrement()` function for that.
        //  val i = enqIdx.value
        //  enqIdx.value = i + 1
        // TODO: Atomically install the element into the cell
        // TODO: if the cell is not poisoned.
        // infiniteArray[i].value = element
    }

    @Suppress("UNCHECKED_CAST")
    override fun dequeue(): E? {
        while (true) {
            if (!shouldTryToDeque()) {
                return null
            }

            val i = deqIdx.getAndIncrement();

            if (infiniteArray[i].compareAndSet(null, POISONED)) {
                continue
            }

            return infiniteArray[i].value as E?
        }



        // Is this queue empty?
         if (enqIdx.value <= deqIdx.value) return null
        // TODO: Increment the counter atomically via Fetch-and-Add.
        // TODO: Use `getAndIncrement()` function for that.
        // val i = deqIdx.value
        // deqIdx.value = i + 1
        // TODO: Try to retrieve an element if the cell contains an
        // TODO: element, poisoning the cell if it is empty.
        // return infiniteArray[i].value as E
    }
}

// TODO: poison cells with this value.
private val POISONED = Any()
