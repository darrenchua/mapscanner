<?php

include 'dbFunctions.php';

$placeName = $_GET['placeName'];
$placeDetails = $_GET['placeDetails'];
$placeCoord = $_GET['placeCoord'];
$placeType = $_GET['placeType'];

mysql_query("INSERT INTO `mapscanner`.`poitable` (`id`, `placeName`, `placeCoord`, `placeTitle`, `placeSnippet`, `placeDetails`, `misc`) VALUES (NULL, '$placeName', '$placeCoord', '$placeName', '$placeDetails', '$placeDetails', '$placeType')");

mysql_close();

?>