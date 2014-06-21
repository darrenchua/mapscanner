<?php

include 'dbFunctions.php';

$locationCoords = $_GET['locationCoords'];
$coordsArray = explode(",", $locationCoords);
$lat = $coordsArray[0];
$lon = $coordsArray[1];

$result = mysql_query("SELECT * FROM poitable");

$returnArray = array();

while($db_field = mysql_fetch_assoc($result)){
	$placeName = $db_field['placeName'];
	$placeCoord = $db_field['placeCoord'];
	$placeTitle = $db_field['placeTitle'];
	$placeSnippet = $db_field['placeSnippet'];
	$placeDetails = $db_field['placeDetails'];

	$placeArray = explode(",", $placeCoord);
	$placeLat = $placeArray[0];
	$placeLon = $placeArray[1];
	$distance = (floatval($lat) - floatval($placeLat))*(floatval($lat) - floatval($placeLat)) + (floatval($lon) - floatval($placeLon))*(floatval($lon) - floatval($placeLon));
	$distance = sqrt($distance)/1000; // distance in kilometers

	if($distance < 2 ){ // 2km radius
		array_push($returnArray, array("placeName"=>$placeName,"placeCoord"=>$placeCoord,"placeTitle"=>$placeTitle,"placeSnippet"=>$placeSnippet,"placeDetails"=>$placeDetails));
	}
}

echo json_encode($returnArray);

?>