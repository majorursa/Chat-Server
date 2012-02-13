CREATE TABLE account (
       id INT NOT NULL AUTO_INCREMENT, 
       PRIMARY KEY(id),
       name VARCHAR(32), 
       password varchar(32)
);
insert into account (name, password) values ('tom',SHA1('tom11'));
insert into account (name, password) values ('david',SHA1('david22'));
insert into account (name, password) values ('beth',SHA1('beth33'));
insert into account (name, password) values ('john',SHA1('john44'));

