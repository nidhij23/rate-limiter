public class SlidingWindowLogRateLimiter implements RateLimiter {

    private final CounterStore store;
    private final long windowSizeMillis;
    private final int limit;

    public SlidingWindowLogRateLimiter(CounterStore store, long windowSizeMillis, int limit) {
        this.store = store;
        this.windowSizeMillis = windowSizeMillis;
        this.limit = limit;
    }

    @Override
    public synchronized boolean allowRequest(String key, int hits) {

        long now = System.currentTimeMillis();
        long threshold = now - windowSizeMillis;

        store.removeOlderThan(key, threshold);

        if (store.getTimestamps(key).size() + hits > limit)
            return false;

        for (int i = 0; i < hits; i++) {
            store.addTimestamp(key, now);
        }

        return true;
    }
}
