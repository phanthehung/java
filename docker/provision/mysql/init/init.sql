# create databases
CREATE DATABASE IF NOT EXISTS `purchase`;
CREATE DATABASE IF NOT EXISTS `voucher`;

# create root user and grant rights
CREATE USER 'root'@'localhost' IDENTIFIED BY 'local';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';
