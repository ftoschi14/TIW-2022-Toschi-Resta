CREATE DATABASE  IF NOT EXISTS `bank_ria` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bank_ria`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Table USER
DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
	`ID` integer AUTO_INCREMENT,
	`Username` varchar(50) not null,
	`Password` varchar(50) not null,
	`Name` varchar(20) not null,
	`Surname` varchar(30) not null,
	primary key(`ID`),
	unique(`Username`)
	)engine=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `bank_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_account`(
	`ID` integer AUTO_INCREMENT primary key,
	`UserID` integer not null,
	`Name` varchar(40) not null,
	`Balance` decimal(10,2) not null,
	constraint `UserID` foreign key(`UserID`) references `User`(`ID`) on update cascade on delete cascade
	)engine=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `Transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Transfer`(
	`ID` integer AUTO_INCREMENT,
	`Amount` decimal(8,2) not null,
	`Timestamp` timestamp not null,
	`Reason` varchar(255) not null,
	`SenderID` integer not null,
	`RecipientID` integer not null,
	primary key(`ID`,`SenderID`,`RecipientID`),
	constraint `OAccount`foreign key(`SenderID`) references `bank_account`(`ID`) on update cascade on delete cascade,
	constraint `DAccount` foreign key(`RecipientID`) references `bank_account`(`ID`) on update cascade on delete cascade,
	constraint `PositiveAMount` check (`Amount`>=0))engine=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `Contacts`;
CREATE TABLE `Contacts`(
	`OwnerID` integer not null,
	`ContactID` integer not null,
	 constraint `OwnerID`foreign key(`OwnerID`) references `User`(`ID`) on update cascade on delete cascade,
	 constraint `ContactID`foreign key(`ContactID`) references `User`(`ID`) on update cascade on delete cascade,
	 primary key(`OwnerID`,`ContactID`)
)engine=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
