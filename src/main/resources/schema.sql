--post
CREATE TABLE Post(
id INT auto_increment PRIMARY KEY,
version INT,
title VARCHAR(255) not null,
content text not null,
published_on TIMESTAMP not null,
updated_on TIMESTAMP
);

