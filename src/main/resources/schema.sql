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
content text not null,
published_on TIMESTAMP not null,
updated_on TIMESTAMP NULL DEFAULT NULL,
author INT,
FOREIGN key(author) REFERENCES Author(id)
);

