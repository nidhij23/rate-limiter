// Mongo runs this as root automatically during init

db = db.getSiblingDB('ratelimiter');

db.createCollection("rules");

db.rules.insertMany([
    {
        _id: 'login_rule_global',
        enabled: true,
        algorithm: 'fixed_window',
        window_seconds: 60,
        identifiers: [
            {
                type: 'userId',
                limit: 5
            },
            {
                type: 'ip',
                limit: 50
            }
        ],
        action: {
            on_limit: 'BLOCK',
            status_code: 429,
            message: 'Too many login attempts'
        }
    },
    {
        _id: 'login_limit',
        enabled: true,
        algorithm: 'fixed_window',
        window_seconds: 60,
        identifiers: [
            {
                type: 'userId',
                limit: 5
            },
            {
                type: 'ip',
                limit: 50
            }
        ],
        action: {
            on_limit: 'BLOCK',
            status_code: 429,
            message: 'Too many login attempts'
        }
    }
]);

print("Rate limit demo rules inserted");
