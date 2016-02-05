package com.log4j2.performance;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    static {
        System.setProperty("AsyncLogger.RingBufferSize", String.valueOf(1024 * 1024));
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("AsyncLogger.ThreadNameStrategy", "CACHED");// 如果在线程池中通过Thread.setName()，这里需要修改为UNCACHED
        System.setProperty("log4j.Clock", "CachedClock");
    }
    private static ThreadLocal<Logger> threadLocalLogger = new ThreadLocal<Logger>() {
        protected Logger initialValue() {
            return LogManager.getLogger(Main.class.toString() + Thread.currentThread().getId());
        }
    };

    public static void main(String[] args) {
        Logger logger = threadLocalLogger.get();
        System.out.println(logger + "|" + Thread.currentThread().getId());
        logger.info("Start");
        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                long start = System.currentTimeMillis();
                for (int i = 0; i < 1; i++) {
                    Logger logger = threadLocalLogger.get();
                    System.out.println(logger.hashCode() + "|" + Thread.currentThread().getId());
                    logger.info(MessageFormat.format("Performance metric:{0}", 10000));
                }
                long end = System.currentTimeMillis();
                System.out.println("T1:" + (end - start));

            }
        }) {
        };
        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                long start = System.currentTimeMillis();
                Logger logger = threadLocalLogger.get();
                System.out.println(logger.hashCode() + "|" + Thread.currentThread().getId());
                for (int i = 0; i < 1; i++) {
                    logger.info(MessageFormat.format("Performance metric:{0}", 10000));
                }
                long end = System.currentTimeMillis();
                System.out.println("T2:" + (end - start));
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }) {
        };
        Thread t3 = new Thread(new Runnable() {

            @Override
            public void run() {
                long start = System.currentTimeMillis();
                Logger logger = threadLocalLogger.get();
                System.out.println(logger.hashCode() + "|" + Thread.currentThread().getId());
                for (int i = 0; i < 1; i++) {
                    logger.info(MessageFormat.format("Performance metric:{0}", 10000));
                }
                long end = System.currentTimeMillis();
                System.out.println("T3:" + (end - start));
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }) {
        };
        t1.start();
        t2.start();
        t3.start();
    }

}
