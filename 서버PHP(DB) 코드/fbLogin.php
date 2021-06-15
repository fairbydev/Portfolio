<?php

  include "./dbconfig.php";

  //null 값이 존재하지 않는 자료들
  $id = $_POST["id"];
  $nickname = $_POST["name"];
  $password = $_POST["id"];

  //null 값이 들어올 가능성이 있는 자료들
  $birth;
  if(isset($_POST["birthday"])){
    $rawBirth = $_POST["birthday"];
    $birthYear = substr($rawBirth, 6, 4);
    $birthMonth = substr($rawBirth, 0, 2);
    $birthDay = substr($rawBirth, 3, 2);
    $birth = $birthYear.$birthMonth.$birthDay;
          // $birth = $_POST["birthday"];
  }else{
      $birth = "noData";
  }

  $gender;
  if(isset($_POST["gender"])){
    $gender = $_POST["gender"];
  }else{
    $gender = "noData";
  }

  $salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
  $cryptedPw = crypt($password, $salt);

  //아이디 중복체크
  $sql = 'select count(*) as cnt from memberInfo where id="'.$id.'"';
  $result = $conn->query($sql);
  $row = $result->fetch_assoc();

  if($row['cnt'] == 0){ //처음 로그인 되어 가입하는 경우 경우 정보 저장
    $sql = "insert into memberInfo (id, password, nickname, birth, gender) values";
    $sql = $sql."('".$id."', '".$cryptedPw."', '".$nickname."', '".$birth."', '".$gender."')";

    if ($conn->query($sql) === TRUE) {
      // 가입 성공시
        //echo "Success: register";

        $arr = array ('task'=>'fbLogin','result'=>'registerSuccess', 'nickname'=> $nickname, 'birth'=> $birth );

        echo json_encode($arr);

    } else {
      //echo "Error: register";

      $arr = array ('task'=>'fbLogin','result'=>'regisgerError');

      echo json_encode($arr);
    }

  }else{  // 가입기록이 있는 페이스북 로그인
    //echo "Error: idDuplication";

    $arr = array ('task'=>'fbLogin','result'=>'memberLogin', 'nickname'=> $nickname, 'birth'=> $birth) ;

    echo json_encode($arr);
  }

  $conn->close();


?>
