var mongoose = require('mongoose');
var schemas = require('./schemas');

var uri = 'mongodb://chebykin.org:1337/coursework';
var options = { promiseLibrary: require('bluebird') };
mongoose.Promise = require('bluebird');
mongoose.connect(uri, options);
var db = mongoose.connection;


//const readline = require('readline');
const rl = require('readline-sync');
//const rl = readline.createInterface({
		//input: process.stdin,
		//output: process.stdout,
		//prompt: '> '
//});

var funcs = {
	'add': function(splitted_input) {
		var schema = get_schema_by_name(splitted_input[1]);
		if(schema == null) return;
		model = fill_fields(schema, null);
		console.log(model);
		model.save((err) => print_errors(err));
	},
	'read': function(splitted_input) {
		var schema = get_schema_by_name(splitted_input[1]);
		if(schema == null) return;
		schema.model.find({}).exec(function(err, row){
			console.log(row);
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

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
	//while(true) {
		input = rl.question();
		//if(input == "q")
			//break;
		var splitted_input = input.split(/\s+/, 2);
		var func = get_func_by_name(splitted_input[0]);
		if(func != null)
			func(splitted_input);
	//}
	//rl.on('line', (line) => {
		//if( !is_question ){
			//switch(line.trim()) {
				//case 'q': case 'quit':
					//funcs['quit']();
				//break;
				//case '': break;
				//default:
					//console.log('test');
					//var input = line.trim();
					//var splitted_input = input.split(/\s+/, 2);
					//var func = get_func_by_name(splitted_input[0]);
					//if(func != null)
						//func(splitted_input);
				//break;
			//}
			//rl.prompt();
		//}else{
		//}
	//}).on('close', () => {
		//funcs['quit']();
	//});

});

function fill_fields(schema, model) {
	if( model == null ) model = new schema.model;

	schema.schema.eachPath((field) => {
		if( !field.match(/^_/) ){
			model[field] = rl.question("   " + field + " = ");
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
		}
	}
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
	if(f == null) {
		console.log(error);
	}
	return f;
}

function print_errors(err) {
	if( err != null ) console.log(err.message);
};
