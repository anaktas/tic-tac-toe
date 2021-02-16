package com.sevenlayer.tictactoe.factories;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;

/**
 * Factory for the async worker generation.
 *
 * @author Anastasios Daris (t.daris@7linternational.com)
 */
public class AsyncFactory {
    /**
     * Current available CPU count,
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * Core pool size ({@link AsyncFactory#CPU_COUNT} + 1)
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;

    /**
     * Maximum pool size ({@link AsyncFactory#CPU_COUNT} * 2 + 1)
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    /**
     * Maximum keep alive time (1 second)
     */
    private static final int KEEP_ALIVE = 1;

    /**
     * Creates an {@link Executor}.
     *
     * @return {@link Executor}
     */
    public static Executor generateExecutor() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128), generateThreadFactory());
    }

    /**
     * Generates a {@link ThreadFactory}.
     *
     * @return {@link ThreadFactory}
     */
    public static ThreadFactory generateThreadFactory() {
        return new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "TaskThread #" + mCount.getAndIncrement());
            }
        };
    }
}
