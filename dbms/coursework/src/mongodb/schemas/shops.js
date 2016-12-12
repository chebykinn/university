var mongoose = require('mongoose');
var table = 'shops';
var schema = mongoose.Schema({
	street: {
		type: String,
		index: {unique: true},
		lowercase: true,
		required: true
	},
});
module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
