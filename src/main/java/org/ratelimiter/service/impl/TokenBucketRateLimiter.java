public class TokenBucketRateLimiter implements RateLimiter {

    private final CounterStore store;
    private final int capacity;
    private final int refillRatePerSec;

    public TokenBucketRateLimiter(CounterStore store, int capacity, int refillRatePerSec) {
        this.store = store;
        this.capacity = capacity;
        this.refillRatePerSec = refillRatePerSec;
    }

    @Override
    public synchronized boolean allowRequest(String key, int hits) {

        String tokensKey = key + ":tokens";
        String lastRefillKey = key + ":lastRefill";

        long now = System.currentTimeMillis();

        long tokens = store.get(tokensKey);
        long lastRefill = store.get(lastRefillKey);

        if (lastRefill == 0) lastRefill = now;

        long seconds = (now - lastRefill) / 1000;
        long refill = seconds * refillRatePerSec;

        tokens = Math.min(capacity, tokens + refill);

        if (tokens < hits) {
            store.set(tokensKey, tokens, Long.MAX_VALUE);
            store.set(lastRefillKey, now, Long.MAX_VALUE);
            return false;
        }

        tokens -= hits;

        store.set(tokensKey, tokens, Long.MAX_VALUE);
        store.set(lastRefillKey, now, Long.MAX_VALUE);

        return true;
    }
}
