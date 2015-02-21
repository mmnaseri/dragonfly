CREATE USER 'dragonfly'@'localhost' IDENTIFIED BY 'dragonfly12345678';
GRANT ALL PRIVILEGES ON `test`.* TO 'dragonfly'@'localhost' WITH GRANT OPTION;
CREATE USER 'dragonfly'@'%' IDENTIFIED BY 'dragonfly12345678';
GRANT ALL PRIVILEGES ON `test`.* TO 'dragonfly'@'%' WITH GRANT OPTION;