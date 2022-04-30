CREATE DATABASE  IF NOT EXISTS `bank` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bank`;
-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bank
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET nameS utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bank_account`
--

DROP TABLE IF EXISTS `bank_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bank_account` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL,
  `name` varchar(40) COLLATE utf8mb4_general_ci NOT NULL,
  `balance` decimal(10,2) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `UID` (`userID`),
  CONSTRAINT `UID` FOREIGN KEY (`userID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  constraint `NoNegativebalance` check(`balance`>=0)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_account`
--

LOCK TABLES `bank_account` WRITE;
/*!40000 ALTER TABLE `bank_account` DISABLE KEYS */;
INSERT INTO `bank_account` VALUES (1,1,'myAccount',15000.00),(2,2,'myAccount',15000.00),(3,3,'myAccount',20000.00),(4,4,'default',20000.00),(5,5,'default',25000.00),(6,6,'default',25000.00),(7,1,'accountA',5000.00),(8,1,'AccountB',2000.00),(9,2,'Account1',5000.00),(10,3,'NewAccount',1000.00);
/*!40000 ALTER TABLE `bank_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfer`
--

DROP TABLE IF EXISTS `transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transfer` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(8,2) NOT NULL,
  `Timestamp` timestamp NOT NULL default current_timestamp,
  `reason` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `senderID` int NOT NULL,
  `recipientID` int NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `OAccount` (`senderID`),
  KEY `DAccount` (`recipientID`),
  CONSTRAINT `DAccount` FOREIGN KEY (`recipientID`) REFERENCES `bank_account` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `OAccount` FOREIGN KEY (`senderID`) REFERENCES `bank_account` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Positiveamount` CHECK ((`amount` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfer`
--

LOCK TABLES `transfer` WRITE;
/*!40000 ALTER TABLE `transfer` DISABLE KEYS */;
INSERT INTO `transfer` VALUES (1,40.00,'2020-12-31 23:00:01','bill',1,2),(2,20.00,'2021-01-01 23:00:01','shopping',2,3),(3,500.00,'2021-01-01 23:03:01','new tablet',3,4),(4,1000.00,'2021-03-01 23:05:34','new pc',4,5),(5,10.00,'2021-04-01 22:05:34','grocery',6,1),(6,50.00,'2021-03-01 23:05:34','bill',6,1),(7,30.00,'2021-12-31 23:00:01','shopping',7,1),(8,50.00,'2022-01-01 23:00:01','shopping',8,5),(9,5.00,'2022-01-01 23:03:01','breakfast',10,5);
/*!40000 ALTER TABLE `transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `Password` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `surname` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'sara.resta@mail.polimi.it','pw001','Sara','Resta'),(2,'federico.toschi@mail.polimi.it','pw002','Federico','Toschi'),(3,'alessia.rossi@gmail.com','pw003','Alessia','Rossi'),(4,'giulio.verdi@gmail.com','pw004','Giulio','Verdi'),(5,'gianfilippo.bianchi@libero.it','pw005','Gianfilippo','Bianchi'),(6,'giuseppina.rosa@libero.it','pw006','Giuseppina','Rosa');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'bank'
--

--
-- Dumping routines for database 'bank'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-04-12 13:02:09
