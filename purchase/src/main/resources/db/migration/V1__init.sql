CREATE TABLE IF NOT EXISTS `purchase` (
  `id_purchase` int(11) NOT NULL AUTO_INCREMENT,
  `transaction` varchar(100) NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  `credit_card_number` varchar(16) NOT NULL,
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` enum('pending','confirmed','processing','failed','success', 'cancel') DEFAULT 'pending',
  PRIMARY KEY (`id_purchase`),
  unique(transaction)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
