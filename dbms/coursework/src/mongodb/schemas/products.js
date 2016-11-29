var mongoose = require('mongoose');
var table = 'products';
var schema = mongoose.Schema({
	name: String,
	type_id: {type: mongoose.Schema.Types.ObjectId, ref: 'product_types'}
});

module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
