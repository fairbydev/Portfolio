<?php
$servername = "localhost";
$username = "travelMaster";
$mysqlpassword = "870831";
$dbname = "travelMate";


// Create connection
$conn = new mysqli($servername, $username, $mysqlpassword, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

 $conn->set_charset('utf8');
?>
