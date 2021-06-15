<?php

include "./dbconfig.php";

for ($i=3; $i < 11 ; $i++) {
  $id = (String)$i.(String)$i.(String)$i.'@gmail.com';

  $password='qwer1234!';
  $salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
  $cryptedPw = crypt($password, $salt);


  $sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
  $sql .= "('$id', '$cryptedPw', 'neco', '19880226', 'male', 'profile_1323210117716137.jpg')";

  if ($conn->query($sql) === TRUE) {
    // 성공시
      //echo "Success: register";

      $arr = array ('task'=>'travelInfo_register','result'=>'success');

      echo json_encode($arr);

  } else {
    //echo "Error: register";

    $arr = array ('task'=>'travelInfo_register','result'=>$conn->error);

    echo json_encode($arr);
  }

}





$conn->close();


?>
