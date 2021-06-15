<?php

  include "./dbconfig.php";


  $id = $_POST["id"];
  $password = $_POST["password"];
  $nickname = $_POST["nickname"];
  $birth = $_POST["birth"];
  $gender = $_POST["gender"];

  // $id = "aaaa";
  // $password = "bbbb";
  // $nickname = "cccc";
  // $birth = "19990101";
  // $gender = "남";

  $salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
  $cryptedPw = crypt($password, $salt);
  if($gener == "남자"){
    $gener = "male";
  }else  if($gener == "여자"){
    $gener = "female";
  }else  if($gener == "기타"){
    $gener = "etc";
  }


  //아이디 중복체크
  $sql = 'select count(*) as cnt from memberInfo where id="'.$id.'"';
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();

  if($row['cnt'] == 0){ //아이디 중복이 아닌 경우 정보 저장
    $sql = "insert into memberInfo (id, password, nickname, birth, gender) values";
    $sql = $sql."('".$id."', '".$cryptedPw."', '".$nickname."', '".$birth."', '".$gender."')";

    if ($conn->query($sql) === TRUE) {
      // 가입 성공시
        //echo "Success: register";

        $arr = array ('task'=>'signup','result'=>'success');

        echo json_encode($arr);

    } else {
      //echo "Error: register";

      $arr = array ('task'=>'signup','result'=>'error without duplication');

      echo json_encode($arr);
    }

  }else{
    //echo "Error: idDuplication";

    $arr = array ('task'=>'signup','result'=>'error with idDuplication');

    echo json_encode($arr);
  }

  $conn->close();


?>
