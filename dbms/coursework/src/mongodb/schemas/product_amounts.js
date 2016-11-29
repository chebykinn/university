var mongoose = require('mongoose');
var table = 'product_amounts';

var schema = mongoose.Schema({
	product_id: {type: mongoose.Schema.Types.ObjectId, ref: 'products'},
	shop_id: {type: mongoose.Schema.Types.ObjectId, ref: 'shops'},
	amount: {type: Number, min: 0},
});

schema.index({ product_id: 1, shop_id: 1}, { unique: true });
module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
