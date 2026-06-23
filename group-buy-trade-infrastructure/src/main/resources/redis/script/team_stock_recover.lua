local recoveredKey = KEYS[1]
local recoverBizKey = KEYS[2]

local ttlMillis = tonumber(ARGV[1])

if ttlMillis == nil then
    return 0
end

if ttlMillis <= 0 then
    return 0
end

local locked = redis.call('set', recoverBizKey, '1', 'PX', ttlMillis, 'NX')

if locked == false then
    return 0
end

redis.call('incr', recoveredKey)
redis.call('pexpire', recoveredKey, ttlMillis)

return 1