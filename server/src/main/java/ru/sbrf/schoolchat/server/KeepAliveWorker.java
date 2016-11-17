package ru.sbrf.schoolchat.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 17/11/2016.
 */
class KeepAliveWorker implements Runnable {

    private final ChatServer server;

    KeepAliveWorker(ChatServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        final long keepAliveTimeout = server.getKeepAliveTimeout();

        class LongRef {
            private long value;

            LongRef(long value) {
                this.value = value;
            }

            long getValue() {
                return value;
            }

            void setValue(long value) {
                this.value = value;
            }
        }

        final LongRef closestExpiration = new LongRef(Long.MAX_VALUE);

        while (server.isRunning()) {
            closestExpiration.setValue(Long.MAX_VALUE);
            List<Socket> toRemove = new ArrayList<>();

            server.forEachListener(w -> {
                long diff = keepAliveTimeout - w.getLastActivityDeltaMs();
                if (diff <= 0)
                    toRemove.add(w.getSocket());

                else if (diff < closestExpiration.getValue())
                    closestExpiration.setValue(diff);
            });

//            System.out.println("[keep-alive-worker] sockets to kill: " + toRemove.size());

            toRemove.forEach(s -> {
                try {
                    s.close();
                } catch (IOException ignored) {
                }
            });

            closestExpiration.setValue(
                    closestExpiration.getValue() <= 0 || closestExpiration.getValue() > keepAliveTimeout ?
                            keepAliveTimeout : closestExpiration.getValue());

            sleepUtil(closestExpiration.getValue());
        }
    }

    private void sleepUtil(long time) {
        try {
            Thread.sleep((int) time);
        } catch (InterruptedException ignored) {
        }
    }
}
