<?php
  include "./dbconfig.php";


  $requestCode = $_POST["requestCode"];
  $nickname = $_POST["nickname"];
  $birth = $_POST["birth"];
  $gender = $_POST["gender"];
  $userId = $_POST["userId"];

  // $id = "aaaa";
  // $password = "bbbb";
  // $nickname = "cccc";
  // $birth = "19990101";
  // $gender = "남";

  //아이디 중복체크
  $sql = "select count(*) as cnt from memberInfo where id='".$userId."'";
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();

  if($row['cnt'] == 1){ //아이디 중복이 아닌 경우 정보 저장
    // $sql = "update memberInfo set(nickname, birth, gender) = ('".$nickname."', '".$birth."', '".$gender."')";
    // $sql .= " where id= '".$userId."'";

    $sql = "UPDATE memberInfo SET nickname='".$nickname."', birth='".$birth."', gender='".$gender."' WHERE id='".$userId."' ";

    if ($conn->query($sql) === TRUE) {
      // 가입 성공시
        //echo "Success: register";

        $arr = array ('task'=>'editInfo','result'=>'success');

        echo json_encode($arr);

    } else {
      //echo "Error: register";

      $arr = array ('task'=>'editInfo','result'=>'fail', 'sql' => $sql);

      echo json_encode($arr);
    }

  }else{
    //echo "Error: idDuplication";

    $arr = array ('task'=>'editInfo','result'=>'fail', 'sql' => $sql);

    echo json_encode($arr);
  }

  $conn->close();



 ?>
