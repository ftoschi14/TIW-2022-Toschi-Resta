CREATE DATABASE  IF NOT EXISTS `bank_ria` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bank_ria`;
-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bank_ria
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
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
  `name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `balance` decimal(10,2) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `UID` (`userID`),
  CONSTRAINT `UID` FOREIGN KEY (`userID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_account`
--

LOCK TABLES `bank_account` WRITE;
/*!40000 ALTER TABLE `bank_account` DISABLE KEYS */;
INSERT INTO `bank_account` VALUES (1,1,'Default account',9030.00),(2,2,'Default account',11070.00),(3,3,'Default account',10020.00),(4,4,'Default account',9910.00),(5,5,'Default account',9990.00),(6,6,'Default account',8990.00),(7,7,'Default account',10080.00),(8,8,'Default account',9910.00),(9,9,'Default account',10000.00),(10,10,'Default account',10070.00),(11,11,'Default account',10050.00),(12,12,'Default account',10000.00),(13,1,'Personal account',4650.00),(14,2,'Personal account',4330.00),(15,1,'account2',3200.00),(16,2,'account2',2700.00),(17,1,'account3',1000.00),(18,2,'account3',1000.00);
/*!40000 ALTER TABLE `bank_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contacts`
--

DROP TABLE IF EXISTS `contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contacts` (
  `ownerID` int NOT NULL,
  `accountID` int NOT NULL,
  PRIMARY KEY (`ownerID`,`accountID`),
  KEY `contactID` (`accountID`),
  CONSTRAINT `accountID` FOREIGN KEY (`accountID`) REFERENCES `bank_account` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ownerID` FOREIGN KEY (`ownerID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contacts`
--

LOCK TABLES `contacts` WRITE;
/*!40000 ALTER TABLE `contacts` DISABLE KEYS */;
INSERT INTO `contacts` VALUES (2,1),(4,1),(5,1),(8,1),(1,2),(3,2),(4,2),(5,2),(6,2),(8,2),(1,3),(2,3),(1,4),(2,4),(3,5),(6,5),(2,6),(3,7),(1,10),(2,10),(1,11),(2,11);
/*!40000 ALTER TABLE `contacts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfer`
--

DROP TABLE IF EXISTS `transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transfer` (
  `senderID` int NOT NULL,
  `recipientID` int NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `amount` decimal(8,2) NOT NULL,
  PRIMARY KEY (`timestamp`,`senderID`,`recipientID`),
  KEY `OAccount` (`senderID`),
  KEY `DAccount` (`recipientID`),
  CONSTRAINT `DAccount` FOREIGN KEY (`recipientID`) REFERENCES `bank_account` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `OAccount` FOREIGN KEY (`senderID`) REFERENCES `bank_account` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `PositiveAMount` CHECK ((`amount` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfer`
--

LOCK TABLES `transfer` WRITE;
/*!40000 ALTER TABLE `transfer` DISABLE KEYS */;
INSERT INTO `transfer` VALUES (1,2,'2022-06-23 15:16:41','bill',100.00),(1,10,'2022-06-23 15:17:01','books',20.00),(13,1,'2022-06-27 04:24:36','money transfer',100.00),(13,15,'2022-06-27 04:26:16','money transfer',200.00),(13,11,'2022-06-27 04:26:52','train ticket',50.00),(2,6,'2022-06-27 04:34:22','book shopping',20.00),(14,2,'2022-06-27 04:36:16','money transfer',300.00),(14,16,'2022-06-27 04:38:33','money transfer',500.00),(14,3,'2022-06-27 04:38:58','bills',70.00),(2,7,'2022-06-27 04:46:25','food shopping',80.00),(2,4,'2022-06-27 04:46:48','bakery shop',10.00),(2,16,'2022-06-27 04:47:51','money transfer',200.00),(1,10,'2022-06-27 04:56:46','train ticket',50.00),(1,4,'2022-06-27 04:57:28','laura\'s gift',20.00),(1,5,'2022-06-27 04:59:20','cinema tickets',20.00),(3,2,'2022-06-27 05:12:34','food shopping',50.00),(4,2,'2022-06-27 05:13:15','bill',70.00),(4,1,'2022-06-27 05:13:38','train ticket',50.00),(5,1,'2022-06-27 05:14:49','new mouse',30.00),(5,2,'2022-06-27 05:15:37','books',30.00),(6,5,'2022-06-27 05:16:47','food',30.00),(6,2,'2022-06-27 05:17:05','new Ipad',1000.00),(8,1,'2022-06-27 05:18:24','train ticket',60.00),(8,2,'2022-06-27 05:18:39','food shopping',30.00),(1,15,'2022-06-27 05:20:36','money transfer',1000.00);
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
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `surname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'user1@mail.polimi.it','password001','User','1'),(2,'uner2@mail.polimi.it','password002','User','2'),(3,'user3@mail.polimi.it','password003','User','3'),(4,'user4@mail.polimi.it','password004','User','4'),(5,'user5@mail.polimi.it','password005','User','5'),(6,'user6@mail.polimi.it','password006','User','6'),(7,'user7@mail.polimi.it','password007','User','7'),(8,'user8i@mail.polimi.it','password008','User','8'),(9,'user9@mail.polimi.it','password009','User','9'),(10,'user10@mail.polimi.it','password010','User','10'),(11,'user11@mail.polimi.it','password011','User','11'),(12,'user12@mail.polimi.it','password012','User','12');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-06-27  7:23:22
