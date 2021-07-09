package bionic.js;

import jjbridge.api.runtime.JSReference;
import jjbridge.api.value.JSFunction;

import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

final class TimeoutHandler
{
    private final ScheduledExecutorService scheduler;
    private final HashMap<Integer, ScheduledFuture<?>> handlers;
    private final AtomicInteger lastTimeoutId;

    TimeoutHandler(int startingId, ScheduledExecutorService scheduler)
    {
        this.scheduler = scheduler;
        this.handlers = new HashMap<>();
        this.lastTimeoutId = new AtomicInteger(startingId);
    }

    int newTimeoutId()
    {
        return lastTimeoutId.incrementAndGet();
    }

    boolean exists(int timeoutId)
    {
        return handlers.containsKey(timeoutId);
    }

    void remove(int timeoutId)
    {
        ScheduledFuture<?> handler = handlers.remove(timeoutId);
        if (handler == null)
        {
            return;
        }
        handler.cancel(false);
    }

    int runDelayed(JSFunction<?> function, JSReference functionReference, int delay)
    {
        int timeoutId = newTimeoutId();
        ScheduledFuture<?> handler = scheduler.schedule(() ->
        {
            function.invoke(functionReference);
            remove(timeoutId);
        }, delay, TimeUnit.MILLISECONDS);
        handlers.put(timeoutId, handler);
        return timeoutId;
    }

    int runAtFixedRate(JSFunction<?> function, JSReference functionReference, int delay)
    {
        int timeoutId = newTimeoutId();
        ScheduledFuture<?> handler = scheduler.scheduleAtFixedRate(() ->
                function.invoke(functionReference), delay, delay, TimeUnit.MILLISECONDS);
        handlers.put(timeoutId, handler);
        return timeoutId;
    }
}
