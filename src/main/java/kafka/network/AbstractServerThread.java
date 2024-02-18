package kafka.network;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author phongpq
 */
public abstract class AbstractServerThread implements Runnable {
    private final AtomicBoolean alive = new AtomicBoolean(true);

    private final CountDownLatch startupLatch = new CountDownLatch(1);

    /**
     * Is the server still running?
     */
    protected Boolean isRunning() {
        return alive.get();
    }

    /**
     * Wait for the thread to completely start up
     */
    public void awaitStartup() throws InterruptedException {
        startupLatch.await();
    }
}
