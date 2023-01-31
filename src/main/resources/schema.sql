--Author
CREATE TABLE Author(
id INT auto_increment PRIMARY KEY,
first_name VARCHAR(100) not null,
last_name VARCHAR(100) not null,
email VARCHAR(255) not null,
username VARCHAR(100) not null
);

--Post
CREATE TABLE Post(
id INT auto_increment PRIMARY KEY,
version INT,
title VARCHAR(255) not null,
content TEXT not null,
published_on TIMESTAMP not null,
updated_on TIMESTAMP null default null,
author INT,
FOREIGN key(author) REFERENCES Author(id)
);

--Comment
CREATE TABLE Comment(
post int not null,
name VARCHAR(255) not null,
content TEXT not null,
published_on TIMESTAMP not null,
updated_on TIMESTAMP null default null,
FOREIGN KEY (post) REFERENCES Post(id)
);

