var mongoose = require('mongoose');
var cacheman = require('cacheman-redis');
var schemas = require('./schemas');


//var host = '192.168.1.123';
//var redis_port = '6379';
//var mongo_port = '27017';

var host = 'chebykin.org';
var redis_port = '4242';
var mongo_port = '1337';

var options = {
  host: host,
  port: redis_port,
};

// adding mongoose cachebox
var cache = new cacheman(options);

var uri = 'mongodb://'+host+':'+mongo_port+'/coursework';
var options = { promiseLibrary: require('bluebird') };
mongoose.Promise = require('bluebird');
mongoose.connect(uri, options);
var db = mongoose.connection;

const rl = require('readline-sync');

var funcs = {
	'add': function(splitted_input) {
		var schema = get_schema_by_name(splitted_input[1]);
		if(schema == null) return;
		model = fill_fields(schema, null);
		console.log(model);
		model.save((err) => print_errors(err));
	},
	'read': function(splitted_input) {
		var key = splitted_input.toString();
		cache.get(key, function (err, value) {
			if (err) throw err;
			if( value == null ){
				var schema = get_schema_by_name(splitted_input[1]);
				if(schema == null) return;
				schema.model.find({}).exec(function(err, row){
					cache.set(key, row, function (err, value) {
						if (err) throw err;
						console.log(value);
						console.log("added to cache");
					});
				});
			}else{
				console.log(value);
				console.log("cached");
			}
		});

	},
	'update': function(splitted_input) {
		var schema = get_schema_by_name(splitted_input[1]);
		var field = get_unique_field(schema);
		var value = rl.question("   " + field + " = ");
		var query = {};
		query[field] = value;
		 schema.model.findOne(query, function(err, row){
			console.log(err);
			var model = fill_fields(schema, row);
			console.log(model);
			model.save((err) => print_errors(err));
		});
	},
	'delete': function(splitted_input) {
		var schema = get_schema_by_name(splitted_input[1]);
		var field = get_unique_field(schema);
		var value = rl.question("   " + field + " = ");
		var query = {};
		query[field] = value;
		 schema.model.findOne(query, function(err, row){
			console.log(err);
			row.remove((err) => print_errors(err));
		});
	},
	'help': function(splitted_input) {
		for(key in funcs){
			console.log(key);
		}
	},
	'quit': function(){
		process.exit(0);
	}
}

var err_code = 0;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {

function run(){
	input = rl.question();
	var splitted_input = input.split(/\s+/, 2);
	var func = get_func_by_name(splitted_input[0]);
	if(func != null)
		func(splitted_input);
}

run();
setTimeout(function(){
	console.log('>');
	process.exit(0);
}, 1000);



});

function fill_fields(schema, model) {
	if( model == null ) model = new schema.model;

	schema.schema.eachPath((field) => {
		if( field[0] != '_' ){
			if( field.match(/\./) ){
				var nested = field.split(".");
				var nested_head = nested[0];
				var nested_field = nested[1];
				model[nested_head][nested_field] = rl.question("   " + field + " = ");
			}else model[field] = rl.question("   " + field + " = ");
		}
	});
	return model;
}

function get_unique_field(schema) {
	var tree = schema.schema.tree;
	for (var property in tree) {
		if ( tree.hasOwnProperty(property) ) {
			var field = tree[property];
			if ( (field.hasOwnProperty('index')
				 && field.index.hasOwnProperty('unique')
				 && field.index.unique)
				 || (field.hasOwnProperty('unique') && field.unique) ) {
				return property;
			}
		} }
	return null;
}

function check_fields_num(fields, num) {
	if(fields.length < num) {
		console.log("Too few fields");
		return true;
	} else
		return false;
}

function get_schema_by_name(name) {
	return get_field_by_name(schemas, name, 'Unknown table: ' + name);
}

function get_func_by_name(name) {
	return get_field_by_name(funcs, name, 'Unknown command, see help');
}

function get_field_by_name(container, name, error) {
	var f = container[name];
	//f.set('redisCache', true);
	if(f == null) {
		console.log(error);
	}
	return f;
}

function print_errors(err) {
	if( err != null ) console.log(err.message);
	db.close();
}
