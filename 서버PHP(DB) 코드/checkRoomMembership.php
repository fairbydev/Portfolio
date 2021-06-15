<?php


  include "./dbconfig.php";

  $roomID = $_POST["roomID"];
  $memberIdx = $_POST["memberIdx"];


  //유저정보 불러오기
  $sql = "select count(*) as cnt from BelongedRoom where room_idx= '".$roomID."' and user_idx= '".$memberIdx."'";
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();

   if($row['cnt'] == 1)
   {  // 해당 방에 이미 입장 처리가 되어 있음
      $arr = array ('task'=>'membershipCheck', 'result'=>'hasMembership');
      echo json_encode($arr);
    }
    else if($row['cnt'] == 0)
    {
      $arr = array ('task'=>'membershipCheck', 'result'=>'hasNoMembership');
      echo json_encode($arr);
    }
    else 
    {
        $arr = array ('task'=>'membershipCheck','result'=>'SqlError');
        echo json_encode($arr);
    }

    $conn->close();


 ?>
