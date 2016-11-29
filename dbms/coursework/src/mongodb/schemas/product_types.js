var mongoose = require('mongoose');
var table = 'product_types';
var schema = mongoose.Schema({
	title: String,
	description: String,
});

module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}

