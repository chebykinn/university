var mongoose = require('mongoose');
var table = 'person_shop';

var schema = mongoose.Schema({
	person_id: {type: mongoose.Schema.Types.ObjectId, ref: 'persons'},
	shop_id: {type: mongoose.Schema.Types.ObjectId, ref: 'shops'},
});

schema.index({ person_id: 1, shop_id: 1}, { unique: true });
module.exports = {
	schema: schema,
	model: mongoose.model(table, schema)
}
