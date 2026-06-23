local reservedKey = KEYS[1]
local recoveredKey = KEYS[2]

local targetCount = tonumber(ARGV[1])
local currentLockCount = tonumber(ARGV[2])
local ttlMillis = tonumber(ARGV[3])

if targetCount == nil or currentLockCount == nil or ttlMillis == nil then
    return 0
end

if ttlMillis <= 0 then
    return 0
end

local recoveredCount = tonumber(redis.call('get', recoveredKey) or '0')
local reservedCount = tonumber(redis.call('get', reservedKey) or '0')

if reservedCount == 0 then
    reservedCount = currentLockCount + recoveredCount
    redis.call('set', reservedKey, reservedCount)
end

local maxReserved = targetCount + recoveredCount

if reservedCount >= maxReserved then
    redis.call('pexpire', reservedKey, ttlMillis)
    redis.call('pexpire', recoveredKey, ttlMillis)
    return 0
end

redis.call('incr', reservedKey)
redis.call('pexpire', reservedKey, ttlMillis)
redis.call('pexpire', recoveredKey, ttlMillis)

return 1