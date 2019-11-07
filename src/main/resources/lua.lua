
local lock = redis.call('setnx','rate_lock',1)
local limitTimeMillis = redis.call('get',KEYS[1])
redis.call('expire','rate_lock',limitTimeMillis/1000)

if  1 ~= lock then
	return 0
end

local current = tonumber(ARGV[1])
local start = redis.call('get','default_startLimitTimeMillis')
local a = current - start
if  a >= tonumber(limitTimeMillis) then
	redis.call('set','default_startLimitTimeMillis',current)
	redis.call('set','default_currentTime',1)
	redis.call('del','rate_lock')
	return 1
end

local currentTime = redis.call('get','default_currentTime')
print(currentTime)
local time = redis.call('get',KEYS[2])
if tonumber(currentTime) >= tonumber(time) then
    redis.call('del','rate_lock')
	return 2
end
redis.call('incr','default_currentTime')
redis.call('del','rate_lock')
return 1


