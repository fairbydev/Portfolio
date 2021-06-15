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

  //여행 중복체크
  $sql = "select count(*) from travel_Info where title = '$title', startDate = '$startDate', endDate = '$endDate', maxPerson = '$maxPerson',";
  $sql .= " place = '$place', cost = '$cost', gender = '$gender', fromAge = '$fromAge', toAge = '$toAge', briefing = '$briefing',";
  $sql .= " enterTime = '$enterTime', createdTime = '$createdTime', picture01 = '$picture01', picture02 = '$picture02',";
  $sql .= " picture03 = '$picture03', picture04 = '$picture04'";

  $result = $conn->query($sql);
  $row = $result->fetch_assoc();


  if($row['cnt'] == 1){ //찾고자하는 유일한 여행일 경우 이 아닌 경우

    if ($conn->query($sql) === TRUE) {
        //echo "Success: getTravelInfo";
        $sql = "select * from travel_Info where title = '$title', startDate = '$startDate', endDate = '$endDate', maxPerson = '$maxPerson',";
        $sql .= " place = '$place', cost = '$cost', gender = '$gender', fromAge = '$fromAge', toAge = '$toAge', briefing = '$briefing',";
        $sql .= " enterTime = '$enterTime', createdTime = '$createdTime', picture01 = '$picture01', picture02 = '$picture02',";
        $sql .= " picture03 = '$picture03', picture04 = '$picture04'";

            if ($conn->query($sql) === TRUE) {
              // 성공시
                //echo "Success: getTravelInfo 2nd phase";
                $result = $conn->query($sql);
                $row = $result->fetch_assoc();
                $seq = $row['seq'];

                $arr = array ('task'=>'getTravelIndex','result'=>'success', 'seq' => $seq);
                echo json_encode($arr);
                echo json_encode($sql);

            } else {
              //echo "Error: edit";

              $arr = array ('task'=>'getTravelIndex','result'=>$conn->error);

              echo json_encode($arr);
              echo json_encode($sql);
            }

    } else {
      //echo "Error: getTravelInfo";

      $arr = array ('task'=>'getTravelIndex','result'=>'error without duplication');

      echo json_encode($arr);
    }

  }else{  // 원하는 여행에 중복되는 자료가 있는 경우
    //echo "Error: idDuplication";

    $arr = array ('task'=>'getTravelIndex','result'=>'error with duplication of travelInfo');

    echo json_encode($arr);
  }

  $conn->close();


?>
