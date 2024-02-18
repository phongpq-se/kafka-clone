package kafka.network;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.Socket;

/**
 * @author phongpq
 */
@AllArgsConstructor
public class Processor extends AbstractServerThread {

    private final int id;

    @Override
    public void run() {
        while (isRunning()) {
            try {
                Thread.sleep(1000);
                System.out.println("Processor " + id + " is running");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // do something
        }
    }

}
