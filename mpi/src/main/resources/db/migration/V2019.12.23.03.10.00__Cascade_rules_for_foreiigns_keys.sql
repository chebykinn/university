alter table company_reviews drop constraint company_reviews_bid_id_fk;

alter table company_reviews
	add constraint company_reviews_bid_id_fk
		foreign key (bid_id) references bids
			on delete cascade;

alter table system_reviews drop constraint system_reviews_user_id_fk;

alter table system_reviews
	add constraint system_reviews_user_id_fk
		foreign key (user_id) references users
			on delete cascade;

