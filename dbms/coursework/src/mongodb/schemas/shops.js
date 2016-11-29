var mongoose = require('mongoose');
var table = 'shops';
var schema = mongoose.Schema({
	street: String,
});
module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
