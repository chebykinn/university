var mongoose = require('mongoose');
var table = 'shops';
var schema = mongoose.Schema({
	street: {
		type: String,
		required: true
	},
	number: {
		type: Number,
		required: true,
	},
});
schema.index({street: 1, number: 1}, {unique: true});
module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
