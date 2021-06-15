<?php


  include "./dbconfig.php";

  $hostId = $_POST["hostId"];

  //유저정보 불러오기
  $sql = "select count(*) as cnt from memberInfo where id= '".$hostId."'";
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();

   if($row['cnt'] == 1){  // 아이디가 존재
      //아이디가 존재하면 비번 해쉬를 불러옴;
      $sqlForHash = "select * from memberInfo where id= '".$hostId."'";
      $resultForHash = $conn->query($sqlForHash);
      $rowForHash = $resultForHash->fetch_assoc();

      $arr = array ('task'=>'getHostNickname', 'result'=>'Success', 'hostNickname'=>$rowForHash['nickname']);

      echo json_encode($arr);
    } else {
        $arr = array ('task'=>'getHostNickname','result'=>'Fail : memberDoesNotExist');
        echo json_encode($arr);
    }

    $conn->close();


 ?>
