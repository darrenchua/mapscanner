<?php

include 'dbFunctions.php';

date_default_timezone_set("Asia/Singapore");

$busArray = array("A1","A1E","A2","A2E","B1","B2","C1","C2","D1A","D1B","D2A","D2B","BTC1","BTC2","P1A","P1B","P2A","P2B");

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
		if($db_field['misc'] == "transport"){
			$busstop = $db_field['placeDetails'];
			$placeDetails = "";
			for($i = 0 ; $i < 15 ; $i++){
				$busName = $busArray[$i];
				$currentTime = date("Y-m-d%20G:i:s");
				$endTime = date("Y-m-d%20G:i:s",time()+600); // next 10 minutes
				$requestString = "http://www.nuslivinglab.nus.edu.sg/api_dev/nus_bus_log_api/bus?request=basic&bus=".intval($i+1)."&bus_station=".$db_field['placeDetails']."&start_time=".$currentTime."&end_time=".$endTime."&output=json";
				//echo $requestString."<br>";
				$resp = file_get_contents($requestString);
				$resp = json_decode($resp);
				if(count($resp) > 0){
					//var_dump($resp[0]);
					$shuttlePlate = $resp[0]->shuttle_plate;
					$estimatedTime = $resp[0]->estimated_eta;
					//echo "<br><br>plate: ".$shuttlePlate." eta: ".$estimatedTime."<br>";
					$placeDetails .= "Bus: ".$busName."\nShuttle Plate: ".$shuttlePlate."\nEstimated Arrival: ".$estimatedTime."\n\n\n";
				}
			}
			array_push($returnArray, array("placeName"=>$placeName,"placeCoord"=>$placeCoord,"placeTitle"=>$placeTitle,"placeSnippet"=>$placeSnippet,"placeDetails"=>$placeDetails));
		}
		else{
			array_push($returnArray, array("placeName"=>$placeName,"placeCoord"=>$placeCoord,"placeTitle"=>$placeTitle,"placeSnippet"=>$placeSnippet,"placeDetails"=>$placeDetails));
		}
	}
}

echo json_encode($returnArray);

?>