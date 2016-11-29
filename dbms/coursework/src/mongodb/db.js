// getting-started.js
var mongoose = require('mongoose');
var schemas = require('./schemas');
//var persons = require('./persons.js');
//var positions = require('./positions.js');

mongoose.connect('mongodb://192.168.1.123/coursework');
var db = mongoose.connection;

var Person = mongoose.model('persons', schemas.persons.schema);
var Position = mongoose.model('positions', schemas.positions.schema);

var pp = new Position;

pp.name = "dima lalka";
pp.description = "aga";

var p = new Person;
p.last_name = "kek";
p.first_name = "lol";
p.second_name = "lal";
p.date_of_birth = "123";
p.sex = "M";
p.place_of_birth = "asd";
p.address = "asdasdasd";
p.phone = "123123123123";
p.photo = new Buffer(0);
p.passport = "123123,12313";
p.position_id = pp;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
	// we're connected!
	console.log('kek');
	pp.save();
	p.save();
});
