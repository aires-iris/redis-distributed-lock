package com.aires.test;


import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @desc:
 * @author: fan zhengxiang
 * @create: 2021/1/26 11:28
 */
public class LockBaseOnAQS {


    private Syncer syncer = new Syncer();


    public void lock() {
        syncer.acquire(1);
    }

    public void unlock() {
        syncer.release(1);
    }


    private static class Syncer extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int acquire) {

            Thread currentThread = Thread.currentThread();
            int c = getState();

            if (c == 0) {
                if (hasQueuedPredecessors() && compareAndSetState(0, acquire)) {
                    // 获取锁之后将线程设置为自己
                    setExclusiveOwnerThread(currentThread);
                    return true;
                }
            }
        /*    else if (currentThread == getExclusiveOwnerThread()) {

                // 如果是当前线程持有锁,就直接重入
//                setState(c + acquire);
                return true;
            }*/

            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            Thread thread = Thread.currentThread();
            if (thread == getExclusiveOwnerThread()) {
                setState(getState() - arg);
                setExclusiveOwnerThread(null);
                return true;
            }
            setState(0);
            return true;
        }
    }
}
