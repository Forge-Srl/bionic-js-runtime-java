package bionic.js;

import jjbridge.api.runtime.JSReference;
import jjbridge.api.value.JSFunction;

import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

final class TimeoutHandler
{
    private final ScheduledExecutorService scheduler;
    private final HashMap<Long, ScheduledFuture<?>> handlers;
    private final AtomicLong lastTimeoutId;

    TimeoutHandler(long startingId, ScheduledExecutorService scheduler)
    {
        this.scheduler = scheduler;
        this.handlers = new HashMap<>();
        this.lastTimeoutId = new AtomicLong(startingId);
    }

    long newTimeoutId()
    {
        return lastTimeoutId.incrementAndGet();
    }

    boolean exists(long timeoutId)
    {
        return handlers.containsKey(timeoutId);
    }

    void remove(long timeoutId)
    {
        ScheduledFuture<?> handler = handlers.remove(timeoutId);
        if (handler == null)
        {
            return;
        }
        handler.cancel(false);
    }

    long runDelayed(JSFunction<?> function, JSReference functionReference, long delay)
    {
        long timeoutId = newTimeoutId();
        ScheduledFuture<?> handler = scheduler.schedule(() ->
        {
            function.invoke(functionReference);
            remove(timeoutId);
        }, delay, TimeUnit.MILLISECONDS);
        handlers.put(timeoutId, handler);
        return timeoutId;
    }

    long runAtFixedRate(JSFunction<?> function, JSReference functionReference, long delay)
    {
        long timeoutId = newTimeoutId();
        ScheduledFuture<?> handler = scheduler.scheduleAtFixedRate(() ->
                function.invoke(functionReference), delay, delay, TimeUnit.MILLISECONDS);
        handlers.put(timeoutId, handler);
        return timeoutId;
    }
}
