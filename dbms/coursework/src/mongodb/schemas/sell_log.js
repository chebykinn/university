var mongoose = require('mongoose');
var table = 'sell_log';
var schema = mongoose.Schema({
	name: {type: String, index: {unique: true} },
	product_id: { type: Number, unique: true },
	shop_id: { type: Number, unique: true },
	amount: Number,
	date: Date,
});

module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
