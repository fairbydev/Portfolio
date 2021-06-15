<?php

  include "./dbconfig.php";

  $id = $_POST["currentUserId"];
  $seq = $_POST["travelSeq"];
  $hitTime = $_POST["hitTime"];


  //클릭한 유저와 여행 시퀀스를 이용하여 데이터베이스 조회
  $sql = 'select count(*) as cnt from hitHistory where id="'.$id.'" and travelSeq="'.$seq.'"';
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();

   if($row['cnt'] == 1){  // 아이디와 여행 시퀀스가 동시가 같은 데이터가 있는 경우 이전에 조회한 적이 있음

      $arr = array ('task'=>'hitCheck','result'=>'Success : already hit');
      echo json_encode($arr);
      echo urldecode ( $sql );
    }else if($row['cnt'] == 0){ //처음 조회하는 경우 => 해당 여행의 hit을 +1하고 조회정보를 hitHistory에 저장

      //hitHistory에 아이디와 여행 시퀀스 저장
      $sql = "insert into hitHistory (id, travelSeq, hitTime) values";
      $sql = $sql."('".$id."', '".$seq."', '".$hitTime."')";

      if ($conn->query($sql) === TRUE){  // 데이터 입력 성공시 여행 데이터의 hit에 +1
        $sql = "update travel_Info set hit = hit + 1 where seq = '".$seq."'";

        if ($conn->query($sql) === TRUE){  //hit에 +1 성공시
          $arr = array ('task'=>'hitCheck','result'=>'Success : FirstHit Updated');
          echo json_encode($arr);
          echo urldecode ( $sql );
        }else{   //hit 업데이트 실패 시
          $arr = array ('task'=>'hitCheck','result'=>'fail : Could Not Update hitHistory');
          echo json_encode($arr);
          echo urldecode ( $sql );
        }


      }else{   // hitHistory Insert 실패 시
        $arr = array ('task'=>'hitCheck','result'=>'fail : Could Not Insert Data');
        echo json_encode($arr);
        echo urldecode ( $sql );
      }

    }else{  //기타 에러
      $arr = array ('task'=>'hitCheck','result'=>'fail : Could Not Check Count');
      echo json_encode($arr);
      echo urldecode ( $sql );
    }

    $conn->close();


?>
