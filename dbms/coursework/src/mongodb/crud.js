var mongoose = require('mongoose');
var readline = require('readline');
var schemas = require('./schemas');

var rl = readline.createInterface({
	input: process.stdin,
	output: process.stdout
});

mongoose.connect('mongodb://192.168.1.123/coursework');
var db = mongoose.connection;

var funcs = { 
	'add': function(splitted_input) {


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

rl.on('line', (input) => {
	var splitted_input = input.split(/\s+/);
	if(splitted_input[0] in funcs) {
		funcs[splitted_input[0]](splitted_input);
	} else {
		console.log('Unknown command, see help');
	}

});
