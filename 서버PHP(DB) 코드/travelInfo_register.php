<?php

  include "./dbconfig.php";


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

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "('$id', '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
  if ($conn->query($sql) === TRUE)
  {
    // 여행등록 성공시
      $sql2 = "select * from travel_Info where title = '$title' and startDate = '$startDate' and endDate = '$endDate' and maxPerson = '$maxPerson' and";
      $sql2 .= " place = '$place' and cost = '$cost' and gender = '$gender' and fromAge = '$fromAge' and toAge = '$toAge' and briefing = '$briefing' and";
      $sql2 .= " enterTime = '$enterTime' and createdTime = '$createdTime'";

        //echo "Success: getTravelInfo 2nd phase";
        $result = $conn->query($sql2);
        $row = $result->fetch_assoc();
        $seq = $row['seq'];

        if($seq === null || seq === "")//문제가 생겨 seq가 없울 경우
        {

          $sql3 = "delete * from travel_Info where title = '$title' and startDate = '$startDate' and endDate = '$endDate' and maxPerson = '$maxPerson' and";
          $sql3 .= " place = '$place' and cost = '$cost' and gender = '$gender' and fromAge = '$fromAge' and toAge = '$toAge' and briefing = '$briefing' and";
          $sql3 .= " enterTime = '$enterTime' and createdTime = '$createdTime'";

          $arr = array ('task'=>'travelInfo_register step2-2','result'=>'fail - no seq');
          echo json_encode($arr);
          echo json_encode($sql2);

        }
        else
        { // 정상적으로 쿼리 실행되었을 경우 
          $arr = array ('task'=>'travelInfo_register step2-1','result'=>'success', 'seq' => $seq);
          echo json_encode($arr);
          echo json_encode($sql2);
        }


  }
  else
  {
    //echo "Error: register";
    $arr = array ('task'=>'travelInfo_register','result'=>$conn->error);
    echo json_encode($arr);
    echo json_encode($sql);
  }

  $conn->close();


?>
