public class FixedWindowRateLimiter implements RateLimiter {

    private final CounterStore store;
    private final long windowSizeMillis;
    private final int limit;

    public FixedWindowRateLimiter(CounterStore store, long windowSizeMillis, int limit) {
        this.store = store;
        this.windowSizeMillis = windowSizeMillis;
        this.limit = limit;
    }

    @Override
    public synchronized boolean allowRequest(String key, int hits) {

        long current = store.increment(key, hits);

        if (current == hits) {
            store.set(key, current, windowSizeMillis);
        }

        return current <= limit;
    }
}
