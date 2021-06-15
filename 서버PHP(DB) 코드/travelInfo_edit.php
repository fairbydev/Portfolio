<?php

  include "./dbconfig.php";

  $seq = $_POST["seq"];
  $id = $_POST["userId"];
  $title = $_POST["title"];
  $startDate = $_POST["startDate"];
  $endDate = $_POST["endDate"];
  $maxPerson = $_POST["maxPerson"];
  $place = $_POST["place"];
  $cost = $_POST["cost"];
  $gender = $_POST["gender"];
  $fromAge = $_POST["fromAge"];
  $toAge = $_POST["toAge"];
  $briefing = $_POST["briefing"];

  $enterTime = $_POST["enterTime"];
  $createdTime = $_POST["createdTime"];

  $picture01 = $_POST["picture01"];
  $picture02 = $_POST["picture02"];
  $picture03 = $_POST["picture03"];
  $picture04 = $_POST["picture04"];
  //아이디 중복체크


// $sql = "update travel_Info set (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04)";
// $sql .= " = ('$id', '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
// $sql .= "where seq = '$seq'";


$sql = "update travel_Info set title = '$title', startDate = '$startDate', endDate = '$endDate', maxPerson = '$maxPerson',";
$sql .= " place = '$place', cost = '$cost', gender = '$gender', fromAge = '$fromAge', toAge = '$toAge', briefing = '$briefing',";
$sql .= " enterTime = '$enterTime', createdTime = '$createdTime', picture01 = '$picture01', picture02 = '$picture02',";
$sql .= " picture03 = '$picture03', picture04 = '$picture04'";
$sql .= " where seq = '$seq'";


    if ($conn->query($sql) === TRUE) {
      // 성공시
        //echo "Success: edit";

        $arr = array ('task'=>'travelInfo_edit','result'=>'success');

        echo json_encode($arr);
        echo json_encode($sql);


    } else {
      //echo "Error: edit";

      $arr = array ('task'=>'travelInfo_edit','result'=>$conn->error);

      echo json_encode($arr);
      echo json_encode($sql);
    }


  $conn->close();


?>
