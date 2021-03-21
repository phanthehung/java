CREATE TABLE `purchase_history` (
  `id_purchase_history` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(10) DEFAULT NULL,
  `time` datetime(6) DEFAULT NULL,
  `transaction` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_purchase_history`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
