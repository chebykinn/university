var mongoose = require('mongoose');
var readline = require('readline');
var schemas = require('./schemas');

var rl = readline.createInterface({
	input: process.stdin,
	output: process.stdout
});

mongoose.connect('mongodb://192.168.1.123/coursework');
var db = mongoose.connection;

rl.on('line', (input) => {
	var splitted_input = input.split(/\s+/);
	console.log(splitted_input);
});

var funcs = { 
	'add': function() {
	},
	'read': function() {
	},
	'update': function() {
	},
	'delete': function() {
	}
}
