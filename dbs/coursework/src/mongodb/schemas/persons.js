var mongoose = require('mongoose');
var table = 'persons';
var schema = mongoose.Schema({
	last_name: {type: String, required: true},
	first_name: {type: String, required: true},
	second_name: String,
	date_of_birth: {type: String, required: true},
	sex: {type: String, validate: /M|F/},
	place_of_birth: {type: String, required: true},
	address: {type: String, required: true},
	phone: {type: String, required: true},
	photo: Buffer,
	passport: {
		type: String,
		unique: true,
		validate: /\d{4},\d{6}/,
		required: true
	},
	position: {
		name: {type: String, required: true},
		description: String,
		shop_id: {
			type: Number,
			required: true
		},
		salary: {type: Number, required: true},
	}
});

module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
