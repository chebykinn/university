var mongoose = require('mongoose');
var table = 'position_salary';

var schema = mongoose.Schema({
	position_id: {type: mongoose.Schema.Types.ObjectId, ref: 'positions'},
	shop_id: {type: mongoose.Schema.Types.ObjectId, ref: 'shops'},
	salary: {type: Number, min: 0},
});

schema.index({ position_id: 1, shop_id: 1}, { unique: true });
module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
