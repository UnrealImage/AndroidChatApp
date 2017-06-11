CREATE TABLE IF NOT EXISTS user(
	user_id varchar(32),
	password varchar(32),
	config varchar(128),
	user_name varchar(32),
	primary key(user_id)
) DEFAULT CHARSET=utf8;
insert into user (user_id, password, config, user_name) values ('000000', '000000', 'config', 'TEST_USER');
CREATE TABLE IF NOT EXISTS msg(
	msg_id int(64) auto_increment,
	from_user varchar(32),
	to_user varchar(32),
	msg varchar(128),
	msg_at datetime DEFAULT CURRENT_TIMESTAMP,
	type int(8),
	sended int(8),
	primary key(msg_id)
) DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS friend(
	friend_id int(64) auto_increment,
	user_id_1 varchar(32),
	user_id_2 varchar(32),
	primary key(friend_id)
) DEFAULT CHARSET=utf8;