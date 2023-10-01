local index
local counter
local result = false
for i = 2, #ARGV, 2 do
    index = i / 2
    counter = tonumber(redis.call("GET", KEYS[index]))
    if counter == nil then
        redis.call("SET", KEYS[index], ARGV[i], "EX", ARGV[i - 1])
    elseif counter > 0 then
        redis.call("DECR", KEYS[index])
    else
        result = true
    end
end
return result