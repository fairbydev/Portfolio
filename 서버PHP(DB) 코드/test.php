<?php

  include "./dbconfig.php";

  $id = $_POST["id"];
  $password = $_POST["password"];

  $salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
  $cryptedPw = crypt($password, $salt);



    $sql = 'select password from memberInfo where id="'.$id.'"';
    $result = $conn->query($sql);
    $rowForHash = $result->fetch_assoc();


        if (password_verify($password, $rowForHash['password'])) {  //비번과 해쉬가 일치
          $arr = array ('task'=>'login','result'=>'loginSuccess');
          echo json_encode($arr);
        } else {  //비번과 해쉬가 불일치
          $arr = array ('task'=>'login','result'=>'loginFail');
          echo json_encode($arr);
        }



    $conn->close();


?>
