public class SlidingWindowCounterRateLimiter implements RateLimiter {

    private final CounterStore store;
    private final long windowSizeMillis;
    private final int limit;

    public SlidingWindowCounterRateLimiter(CounterStore store, long windowSizeMillis, int limit) {
        this.store = store;
        this.windowSizeMillis = windowSizeMillis;
        this.limit = limit;
    }

    @Override
    public synchronized boolean allowRequest(String key, int hits) {

        long now = System.currentTimeMillis();
        long currentWindow = now / windowSizeMillis;

        String currentKey = key + ":" + currentWindow;
        String previousKey = key + ":" + (currentWindow - 1);

        long currentCount = store.increment(currentKey, hits);
        long previousCount = store.get(previousKey);

        double weight = (double)(windowSizeMillis - (now % windowSizeMillis)) / windowSizeMillis;
        double total = currentCount + (previousCount * weight);

        return total <= limit;
    }
}
