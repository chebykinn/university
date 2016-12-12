// getting-started.js
var mongoose = require('mongoose');
var schemas = require('./schemas');

mongoose.connect('mongodb://chebykinn.ru:1337/coursework');
var db = mongoose.connection;

//var Person = mongoose.model('persons', schemas.persons.schema);
//var Position = mongoose.model('positions', schemas.positions.schema);
//var Shop = mongoose.model('positions', schemas.positions.schema);

var sh = new schemas.shops.model;
sh.street = "Lol st.";

var pr = new schemas.products.model;

pr.name = "product one";
pr.type = {
	name: "type one",
	description: "descdesc",
},
pr.sell_info = {
	shop_id: sh,
	price: 100,
	amount: 100,
}

var p = new schemas.persons.model;
p.last_name = "kek";
p.first_name = "lol";
p.second_name = "lal";
p.date_of_birth = "123";
p.sex = "M";
p.place_of_birth = "asd";
p.address = "asdasdasd";
p.phone = "123123123123";
p.photo = new Buffer(0);
p.passport = "1231,182313";
	p.position.name = "test";
	p.position.description = "test desc";
	p.position.shop_id = sh;
	p.position.salary = 1000;

db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
	// we're connected!
	console.log('kek');
	console.log(sh);
	sh.save((err) => print_errors(err));
	pr.save((err) => print_errors(err));
	p.save((err) => print_errors(err));
});

function print_errors(err) {
	console.log(err.message);
};
