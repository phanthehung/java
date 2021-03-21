CREATE TABLE `voucher` (
  `id_voucher` int(11) NOT NULL AUTO_INCREMENT,
  `transaction` varchar(100) NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  `voucher_code` varchar(20) DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `retry` int(2) NOT NULL DEFAULT '0',
  `status` enum('processing','failed','success') DEFAULT 'processing',
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id_voucher`),
  UNIQUE KEY `transaction` (`transaction`),
  KEY `phone_number` (`phone_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;