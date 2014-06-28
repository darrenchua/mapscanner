<?php

$user_name = "root";
$password = "a";
$database = "mapscanner";
$server = "localhost";

$db_handle = mysql_connect($server,$user_name,$password);
$db_found = mysql_select_db($database,$db_handle);

?>