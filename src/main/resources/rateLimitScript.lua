local counter = tonumber(redis.call("GET", KEYS[1]))
if counter == nil then
    redis.call("SET", KEYS[1], ARGV[2], "EX", ARGV[1])
    return false
elseif counter > 0 then
    redis.call("DECR", KEYS[1])
    return false
end
return true