package customLock;

import java.util.HashMap;
import java.util.Map;

/**
 * A custom reentrant read/write lock that allows:
 * 1) Multiple readers (when there is no writer). Any thread can acquire multiple read locks (if nobody is writing).
 * 2) One writer (when nobody else is writing or reading).
 * 3) A writer is allowed to acquire a read lock while holding the write lock.
 * 4) A writer is allowed to acquire another write lock while holding the write lock.
 * 5) A reader can not acquire a write lock while holding a read lock.
 * <p>
 * Use ReentrantReadWriteLockTest to test this class.
 * The code is modified from the code of Prof. Rollins.
 */
public class ReentrantReadWriteLock {
    // FILL IN CODE:
    // Add instance variables:
    // for each threadId, store the number of read locks and write locks currently held

    private Map<Long, ThreadState> threads;

    public volatile long currentWritingThreadId = 0L;

    private static class ThreadState {
        private int writers = 0;
        private int readers = 0;
    }

    /**
     * Constructor for ReentrantReadWriteLock
     */
    public ReentrantReadWriteLock() {
        // FILL IN CODE: initialize instance variables
        threads = new HashMap<>();
    }

    /**
     * Return true if the current thread holds a read lock.
     *
     * @return true or false
     */
    public synchronized boolean isReadLockHeldByCurrentThread() {
        long c = getCurrentId();
        if (threads.containsKey(c)) {
            return threads.get(c).readers != 0;
        } else {
            return false;
        }
    }

    /**
     * Return true if the current thread holds a write lock.
     *
     * @return true or false
     */
    public synchronized boolean isWriteLockHeldByCurrentThread() {
        long c = getCurrentId();
        if (threads.containsKey(c)) {
            return threads.get(c).writers != 0;
        } else {
            return false;
        }
    }

    /**
     * Non-blocking method that attempts to acquire the read lock. Returns true
     * if successful.
     * Checks conditions (whether it can acquire the read lock), and if they are true,
     * updates readers info.
     * <p>
     * Note that if conditions are false (can not acquire the read lock at the moment), this method
     * does NOT wait, just returns false
     *
     * @return
     */
    public synchronized boolean tryAcquiringReadLock() {
        long c = getCurrentId();
        if (!threads.containsKey(c)) {
            threads.put(c, new ThreadState()); // if no current thread in threads map, put a new one
        }
        if (c == currentWritingThreadId) { // if current thread is writing, allow it to have read locks
            threads.get(c).readers++;
            return true;
        }
//        for (Long id : threads.keySet()) { // Just check if currentWritingThreadId is 0
//            if (threads.get(id).writers != 0) {
//                return false;
//            }
//        }

        if (currentWritingThreadId != 0L) { // current writing thread != 0 means someone's writing
            return false;
        }

        threads.get(c).readers++;
        return true;
    }

    /**
     * Non-blocking method that attempts to acquire the write lock. Returns true
     * if successful.
     * Checks conditions (whether it can acquire the write lock), and if they are true,
     * updates writers info.
     * <p>
     * Note that if conditions are false (can not acquire the write lock at the moment), this method
     * does NOT wait, just returns false
     *
     * @return
     */
    public synchronized boolean tryAcquiringWriteLock() {
        long c = getCurrentId();
        if (!threads.containsKey(c)) { // if no current thread in threads map, put a new one
            threads.put(c, new ThreadState());
        }
        if (c == currentWritingThreadId) { // if current thread is writing, allow it to have another write lock
            threads.get(c).writers++;
            return true;
        }
        for (Long id : threads.keySet()) { // if other thread is reading or writing, return false
            if (threads.get(id).readers != 0 || threads.get(id).writers != 0) {
                return false;
            }
        }
        threads.get(c).writers++; // being this step means no one's reading or writing, allow it to have write lock
        currentWritingThreadId = getCurrentId(); // assign current writing thread as this thread
        return true;
    }

    /**
     * Blocking method that will return only when the read lock has been
     * acquired.
     * Calls tryAcquiringReadLock, and as long as it returns false, waits.
     * Catches InterruptedException.
     */
    public synchronized void lockRead() {
        try {
            while (!tryAcquiringReadLock()) {
                this.wait();
            }
        } catch (InterruptedException e) {
        }
        this.notifyAll();
    }

    /**
     * Releases the read lock held by the calling thread. Other threads might
     * still be holding read locks. If no more readers after unlocking, calls notifyAll().
     */
    public synchronized void unlockRead() {
        long c = Thread.currentThread().getId();
        if (threads.containsKey(c) && threads.get(c).readers > 0) {
            threads.get(c).readers--;
            if (threads.get(c).readers == 0) {
                this.notifyAll();
            }
        }
    }

    /**
     * Blocking method that will return only when the write lock has been
     * acquired.
     * Calls tryAcquiringWriteLock, and as long as it returns false, waits.
     * Catches InterruptedException.
     */
    public synchronized void lockWrite() {
        try {
            while (!tryAcquiringWriteLock()) {
                this.wait();
            }
        } catch (InterruptedException e) {
        }
        this.notifyAll();
    }

    /**
     * Releases the write lock held by the calling thread. The calling thread
     * may continue to hold a read lock.
     * If the number of writers becomes 0, calls notifyAll.
     */

    public synchronized void unlockWrite() {
        long currentThreadId = Thread.currentThread().getId();
        if (threads.containsKey(currentThreadId) && threads.get(currentThreadId).writers > 0) {
            threads.get(currentThreadId).writers--;
            if (threads.get(currentThreadId).writers == 0) {
                currentWritingThreadId = 0L;
                this.notifyAll();
            }
        }
    }

    private long getCurrentId() {
        return Thread.currentThread().getId();
    }
}
