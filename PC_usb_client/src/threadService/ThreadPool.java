package threadService;

/**
 * Created by CM-WANGMIN on 2017/5/5.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadPool extends Thread {
    private static final Logger logger = Logger.getLogger(ThreadPool.class.getName());
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        public Thread newThread(Runnable runnable) {
            ThreadPool.thread = new ThreadPool(runnable);
            ThreadPool.thread.setName("ThreadPool");
            ThreadPool.thread.setDaemon(Thread.currentThread().isDaemon());
            return ThreadPool.thread;
        }
    };
    private static ThreadPool thread;
    private static ExecutorService service;
    private static int counter = 0;

    private ThreadPool(Runnable runnable) {
        super(runnable);
    }

    public static boolean isCurrent() {
        return currentThread() == thread;
    }

    public static void exec(Runnable task) {
        if(isCurrent()) {
            task.run();
        } else {
            nextTick(task);
        }

    }

    public static void nextTick(final Runnable task) {
        Class var2 = ThreadPool.class;
        ExecutorService executor;
        synchronized(ThreadPool.class) {
            ++counter;
            if(service == null) {
                service = Executors.newSingleThreadExecutor(THREAD_FACTORY);
            }

            executor = service;
        }

        executor.execute(new Runnable() {
            public void run() {
                boolean var10 = false;

                try {
                    var10 = true;
                    task.run();
                    var10 = false;
                } catch (Throwable var13) {
                    ThreadPool.logger.log(Level.SEVERE, "Task threw exception", var13);
                    throw var13;
                } finally {
                    if(var10) {
                        Class var4 = ThreadPool.class;
                        synchronized(ThreadPool.class) {
                            ThreadPool.counter--;
                            if(ThreadPool.counter == 0) {
                                ThreadPool.service.shutdown();
                                ThreadPool.service = null;
                                ThreadPool.thread = null;
                            }

                        }
                    }
                }

                Class t = ThreadPool.class;
                synchronized(ThreadPool.class) {
                    ThreadPool.counter--;
                    if(ThreadPool.counter == 0) {
                        ThreadPool.service.shutdown();
                        ThreadPool.service = null;
                        ThreadPool.thread = null;
                    }

                }
            }
        });
    }
}
