var mongoose = require('mongoose');
module.exports = {
	schema: mongoose.Schema({
		last_name: String,
		first_name: String,
		second_name: String,
		date_of_birth: String,
		sex: {type: String, validate: /M|F/},
		place_of_birth: String,
		address: String,
		phone: String,
		photo: Buffer,
		passport: String,
		position_id: Number
	})
}
