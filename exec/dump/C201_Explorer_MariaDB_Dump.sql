CREATE DATABASE  IF NOT EXISTS `s10p31c201` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin */;
USE `s10p31c201`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: stg-yswa-kr-practice-db-master.mariadb.database.azure.com    Database: s10p31c201
-- ------------------------------------------------------
-- Server version	5.6.47.0

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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_avatar` int(11) DEFAULT NULL,
  `user_login_id` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `user_nickname` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `user_password` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `user_status` enum('RUN','EXIST') COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_hy85e8mct2r1rmhbjt9i6bl` (`user_login_id`),
  UNIQUE KEY `UK_cr59axqya8utby3j37qi341rm` (`user_nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (11,0,'SSAFY123','SSAFY','{bcrypt}$2a$10$LLMYdR.EbN0k1lL.egE6ourJKoWjYlCwHbAftjVpibCilCd/Srys.',NULL),(12,0,'test01','엄마는외계인','{bcrypt}$2a$10$yzFjlgriPWDyFMXD95mrdOBv/wCNXjInnWaJezH.Ix5xLdz5Ykzl2',NULL),(13,0,'test02','아몬드봉봉','{bcrypt}$2a$10$MWiD8DgZfKT6/5eKYWacTuabMTLkkpb1imDV52oRCM4r8tc5m3qFK',NULL),(14,0,'test03','초콜릿무스','{bcrypt}$2a$10$p1x2EpzUXTvABy5/6j1Hiev.IDY5kZUoAOLfgM1nJiiqpkPkNMHG2',NULL),(15,0,'test04','레인보우샤베트','{bcrypt}$2a$10$yRAmnR8zjndsROofNecYJuJJd5n77xXjonROLDS9b5XJgxUbvVJIy',NULL),(16,0,'test05','초코나무숲','{bcrypt}$2a$10$QJbYLj74Ncw8djORv0KMC.9YM5ai7u8yyNIXXeZ2ASAR/HyM0bBKO',NULL),(17,0,'test06','슈팅스타','{bcrypt}$2a$10$Pfe0kGIDnmcwoHO5MTPA3.rctTDIJfm2MwWAGuWyO1HbCrtDsUccq',NULL);
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

-- Dump completed on 2024-05-20  5:10:12
