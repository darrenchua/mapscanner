-- phpMyAdmin SQL Dump
-- version 4.1.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 21, 2014 at 03:03 PM
-- Server version: 5.6.15
-- PHP Version: 5.4.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `mapscanner`
--

-- --------------------------------------------------------

--
-- Table structure for table `poitable`
--

CREATE TABLE IF NOT EXISTS `poitable` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `placeName` text NOT NULL,
  `placeCoord` text NOT NULL,
  `placeTitle` text NOT NULL,
  `placeSnippet` text NOT NULL,
  `placeDetails` text NOT NULL,
  `misc` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `poitable`
--

INSERT INTO `poitable` (`id`, `placeName`, `placeCoord`, `placeTitle`, `placeSnippet`, `placeDetails`, `misc`) VALUES
(1, 'My house', '1.341906,103.704439', 'My house', 'This is my house', 'it is awesome!', ''),
(2, 'Jurong Point', '1.340233,103.705855', 'Jurong Point', 'There are many shops here', 'There are many many many shops here', ''),
(3, 'Medical Center', '1.343531,103.705152', 'Medical Center', 'Medical Center has many doctors', 'Medical Center has many doctors. They can heal people', ''),
(4, 'Shell', '1.344207,103.707861', 'Shell', 'Petrol Station', 'Can pump petrol', '');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
