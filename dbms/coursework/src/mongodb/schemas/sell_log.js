var mongoose = require('mongoose');
var table = 'sell_log';
var schema = mongoose.Schema({
	name: {type: String, index: {unique: true} },
	product_id: {type: mongoose.Schema.Types.ObjectId, ref: 'products'},
	shop_id: {type: mongoose.Schema.Types.ObjectId, ref: 'shops'},
	amount: Number,
	date: Date,
});

module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
