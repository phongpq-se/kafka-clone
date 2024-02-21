package kafka.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author phongpq
 */
public abstract class AbstractServerThread implements Runnable {

    protected final Selector selector;

    {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

    /**
     * Record that the thread startup is complete
     */
    protected void startupComplete() {
        startupLatch.countDown();
    }

    public void wakeup() {
        selector.wakeup();
    }

    public void close(SocketChannel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close(SelectionKey key) {
        if (key != null) {
            key.attach(null);
            close((SocketChannel) key.channel());
        }
    }
}
