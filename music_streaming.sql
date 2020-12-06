-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.5.6-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- music_streaming 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `music_streaming` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `music_streaming`;

-- 테이블 music_streaming.contain 구조 내보내기
CREATE TABLE IF NOT EXISTS `contain` (
  `playlist_user` int(11) NOT NULL,
  `playlist_number` int(11) NOT NULL,
  `music_number` int(11) NOT NULL,
  PRIMARY KEY (`playlist_user`,`music_number`,`playlist_number`) USING BTREE,
  KEY `comufk` (`music_number`),
  KEY `coplfk` (`playlist_user`,`playlist_number`),
  CONSTRAINT `comufk` FOREIGN KEY (`music_number`) REFERENCES `music` (`number`) ON UPDATE CASCADE,
  CONSTRAINT `coplfk` FOREIGN KEY (`playlist_user`, `playlist_number`) REFERENCES `playlist` (`user_number`, `number`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music_streaming.contain:~4 rows (대략적) 내보내기
DELETE FROM `contain`;
/*!40000 ALTER TABLE `contain` DISABLE KEYS */;
INSERT INTO `contain` (`playlist_user`, `playlist_number`, `music_number`) VALUES
	(1, 4, 2),
	(1, 1, 5),
	(1, 3, 5),
	(1, 4, 6),
	(1, 1, 7),
	(1, 4, 8),
	(1, 1, 12),
	(1, 1, 13);
/*!40000 ALTER TABLE `contain` ENABLE KEYS */;

-- 테이블 music_streaming.license 구조 내보내기
CREATE TABLE IF NOT EXISTS `license` (
  `number` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `streamable_songs` int(11) NOT NULL,
  `fee` int(11) NOT NULL,
  PRIMARY KEY (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music_streaming.license:~4 rows (대략적) 내보내기
DELETE FROM `license`;
/*!40000 ALTER TABLE `license` DISABLE KEYS */;
INSERT INTO `license` (`number`, `name`, `streamable_songs`, `fee`) VALUES
	(1, 'small', 100, 10000),
	(2, 'midium', 200, 20000),
	(3, 'big', 300, 30000);
/*!40000 ALTER TABLE `license` ENABLE KEYS */;

-- 테이블 music_streaming.manager 구조 내보내기
CREATE TABLE IF NOT EXISTS `manager` (
  `number` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `id` varchar(50) NOT NULL DEFAULT '',
  `pw` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`number`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music_streaming.manager:~6 rows (대략적) 내보내기
DELETE FROM `manager`;
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` (`number`, `name`, `id`, `pw`) VALUES
	(0, 'President', 'president', '0000'),
	(1, 'Jeff', 'a', '0000'),
	(2, 'Bill', 'b', '0000'),
	(3, 'Steve', 'c', '0000');
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;

-- 테이블 music_streaming.music 구조 내보내기
CREATE TABLE IF NOT EXISTS `music` (
  `number` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `singer` varchar(50) DEFAULT NULL,
  `genre` varchar(20) DEFAULT NULL,
  `played_number` int(11) NOT NULL,
  PRIMARY KEY (`number`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music_streaming.music:~11 rows (대략적) 내보내기
DELETE FROM `music`;
/*!40000 ALTER TABLE `music` DISABLE KEYS */;
INSERT INTO `music` (`number`, `name`, `singer`, `genre`, `played_number`) VALUES
	(1, 'a', 'garlic', 'pop', 434),
	(2, 'b', 'apple', 'classic', 32),
	(3, 'b', 'banana', 'jazz', 232),
	(4, 'test', 'tester', 'pop', 134),
	(5, 'drank', 'lim', 'ballad', 977),
	(6, 'moon', 'man', 'ballad', 344),
	(7, 'grass', 'back', 'ballad', 342),
	(8, 'a', 'cheeze', 'pop', 768),
	(9, 'a', 'goose', 'classic', 48),
	(10, 'go', 'smith', 'rock', 941),
	(11, 'baam', 'bam', 'hiphop', 453),
	(12, 'dynamite', 'bts', 'pop', 4543),
	(13, 'sleep', 'jang', 'ballad', 746),
	(14, 'girl', 'pink', 'pop', 2422),
	(15, 'dinga', 'mama', 'pop', 1566);
/*!40000 ALTER TABLE `music` ENABLE KEYS */;

-- 테이블 music_streaming.playlist 구조 내보내기
CREATE TABLE IF NOT EXISTS `playlist` (
  `user_number` int(11) NOT NULL,
  `number` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`user_number`,`number`) USING BTREE,
  UNIQUE KEY `name` (`name`),
  CONSTRAINT `plusfk` FOREIGN KEY (`user_number`) REFERENCES `user` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music_streaming.playlist:~4 rows (대략적) 내보내기
DELETE FROM `playlist`;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` (`user_number`, `number`, `name`) VALUES
	(2, 6, 'aabb'),
	(1, 5, 'new'),
	(1, 1, 'qwer'),
	(1, 4, 'you'),
	(1, 3, 'zxcv');
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;

-- 테이블 music_streaming.streaming 구조 내보내기
CREATE TABLE IF NOT EXISTS `streaming` (
  `user_number` int(11) NOT NULL,
  `music_number` int(11) NOT NULL,
  PRIMARY KEY (`user_number`,`music_number`),
  UNIQUE KEY `user_number` (`user_number`),
  KEY `stmufk` (`music_number`),
  CONSTRAINT `stmufk` FOREIGN KEY (`music_number`) REFERENCES `music` (`number`),
  CONSTRAINT `stusfk` FOREIGN KEY (`user_number`) REFERENCES `user` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music_streaming.streaming:~3 rows (대략적) 내보내기
DELETE FROM `streaming`;
/*!40000 ALTER TABLE `streaming` DISABLE KEYS */;
INSERT INTO `streaming` (`user_number`, `music_number`) VALUES
	(1, 9),
	(2, 3),
	(3, 4),
	(4, 11);
/*!40000 ALTER TABLE `streaming` ENABLE KEYS */;

-- 테이블 music_streaming.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `number` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `streamed_num` int(11) NOT NULL,
  `license` int(11) NOT NULL,
  `id` varchar(50) NOT NULL DEFAULT '',
  `pw` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`number`),
  UNIQUE KEY `id` (`id`),
  KEY `uslifk` (`license`),
  CONSTRAINT `uslifk` FOREIGN KEY (`license`) REFERENCES `license` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music_streaming.user:~15 rows (대략적) 내보내기
DELETE FROM `user`;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`number`, `name`, `streamed_num`, `license`, `id`, `pw`) VALUES
	(1, 'Joan', 33, 2, 'a', '0000'),
	(2, 'Hatty', 134, 3, 'b', '0000'),
	(3, 'Roy', 255, 3, 'c', '0000'),
	(4, 'Elon', 655, 2, 'd', '0000'),
	(5, 'Robert', 113, 2, 'e', '0000'),
	(6, 'Jerry', 535, 1, 'f', '0000'),
	(7, 'Sue', 52, 2, 'g', '0000'),
	(8, 'Kang', 74, 3, 'h', '0000'),
	(9, 'Hyeon', 255, 3, 'i', '0000'),
	(10, 'New', 775, 1, 'j', '0000');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
