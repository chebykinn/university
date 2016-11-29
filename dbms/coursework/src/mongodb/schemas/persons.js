var mongoose = require('mongoose');
var table = 'persons';
var schema = mongoose.Schema({
	last_name: String,
	first_name: String,
	second_name: String,
	date_of_birth: String,
	sex: {type: String, validate: /M|F/},
	place_of_birth: String,
	address: String,
	phone: String,
	photo: Buffer,
	passport: String,
	position: {
		name: String,
		description: String,
		shop_id: {type: mongoose.Schema.Types.ObjectId, ref: 'shops'},
		salary: Number,
	}
});

module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
