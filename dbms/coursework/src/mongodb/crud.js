var mongoose = require('mongoose');
var rl = require('readline-sync');
var schemas = require('./schemas');

mongoose.connect('mongodb://chebykinn.ru:1337/coursework');
var db = mongoose.connection;

var funcs = { 
	'add': function(input) {
		var splitted_input = input.split(/\s+/, 3);
		if(check_fields_num(splitted_input, 2))
			return;
		var schema = get_schema_by_name(splitted_input[1]);
		if(schema == null) 
			return;
		model = fill_fields(schema);
		console.log(model);
		model.save((err) => print_errors(err));
	},
	'read': function(splitted_input) {
		console.log('read');
	},
	'update': function(splitted_input) {
		console.log('update');
	},
	'delete': function(splitted_input) {
		console.log('delete');
	},
	'help': function(splitted_input) {
		console.log('help');
	}
}

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
	while(true) {
		input = rl.question();
		if(input == "q")
			break;
		var splitted_input = input.split(/\s+/, 1);
		var func = get_command_by_name(splitted_input[0]);
		if(func != null)
			func(input);
	}
});

function fill_fields(schema) {
	model = new schema.model;
	schema.schema.eachPath((field) => {
		model[String(field)] = rl.question("   " + field + " = ");
	});
	return model;
}

function check_fields_num(fields, num) {
	if(fields.length < num) {
		console.log("To few fields");
		return true;
	} else 
		return false;
}

function get_schema_by_name(name) {
	return get_field_by_name(schemas, name, 'Unknown table: ' + name);
}

function get_command_by_name(name) {
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
	console.log(err.message);
};
