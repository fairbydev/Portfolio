<?php

  include "./dbconfig.php";

  $id = $_POST["id"];
  $password = $_POST["password"];

  $salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
  $cryptedPw = crypt($password, $salt);

  //비번 해시 불러오기
  $sql = 'select count(*) as cnt from memberInfo where id="'.$id.'" and password="'.$cryptedPw.'"';
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();

   if($row['cnt'] == 1){  // 아이디가 존재
      //아이디가 존재하면 비번 해쉬를 불러옴;
      $sqlForHash = 'select * from memberInfo where id="'.$id.'"';
      $resultForHash = $conn->query($sqlForHash);
      $rowForHash = $resultForHash->fetch_assoc();

      $arr = array ('task'=>'login','result'=>'loginSuccess', 'nickname'=>$rowForHash['nickname'], 'id'=>$rowForHash['id']);
      echo json_encode($arr);
    } else {
      $arr = array ('task'=>'login','result'=>'loginFail');
      echo json_encode($arr);
    }

    $conn->close();


?>
