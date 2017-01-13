var host = 'chebykin.org';
var redis_port = '4242';
var mongo_port = '1337';
var CachemanRedis = require('cacheman-redis');
const assert = require('assert');

var options = {
  host: host,
  port: redis_port,
};
var cache = new CachemanRedis(options);


let key = "ololo", value = Date.now();
    cache.get(key, function (err, data) {
		if( err ) throw err;
		console.log(data);
		if( data == null ){
			cache.set(key, value, function (err, data) {
				if( err ) throw err;
				console.log(data);
			});
		}
    });

