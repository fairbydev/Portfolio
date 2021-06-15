5<?php

include "./dbconfig.php";

$id = '111@gmail.com';

$password='qwer1234!';
$salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
$cryptedPw = crypt($password, $salt);


$sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
$sql .= "('$id', '$cryptedPw', 'Autumn', '19880226', 'female', 'profile_111@gmail.com.jpg')";

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


$id = '222@gmail.com';

$password='qwer1234!';
$salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
$cryptedPw = crypt($password, $salt);


$sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
$sql .= "('$id', '$cryptedPw', '곧미남', '19880226', 'male', 'profile_222@gmail.com.jpg')";

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


$id = '333@gmail.com';

$password='qwer1234!';
$salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
$cryptedPw = crypt($password, $salt);


$sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
$sql .= "('$id', '$cryptedPw', 'ChoSickNam', '19880226', 'male', 'profile_333@gmail.com.jpg')";

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


$id = '444@gmail.com';

$password='qwer1234!';
$salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
$cryptedPw = crypt($password, $salt);


$sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
$sql .= "('$id', '$cryptedPw', '나돌아갈래', '19880226', 'female', 'profile_444@gmail.com.jpg')";

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


$id = '555@gmail.com';

$password='qwer1234!';
$salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
$cryptedPw = crypt($password, $salt);


$sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
$sql .= "('$id', '$cryptedPw', '스치듯수지', '19880226', 'female', 'profile_555@gmail.com.jpg')";

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





$id = '666@gmail.com';

$password='qwer1234!';
$salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
$cryptedPw = crypt($password, $salt);


$sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
$sql .= "('$id', '$cryptedPw', '페페', '19880226', 'noData', 'profile_666@gmail.com.jpg')";

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



$id = '777@gmail.com';

$password='qwer1234!';
$salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
$cryptedPw = crypt($password, $salt);


$sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
$sql .= "('$id', '$cryptedPw', '곧미녀', '19880226', 'female', 'profile_777@gmail.com.jpg')";

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




$id = '888@gmail.com';

$password='qwer1234!';
$salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
$cryptedPw = crypt($password, $salt);


$sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
$sql .= "('$id', '$cryptedPw', '방구석여포', '19880226', 'noData', 'profile_888@gmail.com.jpg')";

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


$id = '999@gmail.com';

$password='qwer1234!';
$salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
$cryptedPw = crypt($password, $salt);


$sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
$sql .= "('$id', '$cryptedPw', '겐도', '19880226', 'male', 'profile_999@gmail.com.jpg')";

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



$id = '101010@gmail.com';

$password='qwer1234!';
$salt = '$2a$07$R.gJb2U2N.FmZ4hPp1y2CN$';
$cryptedPw = crypt($password, $salt);


$sql = "insert into memberInfo (id, password, nickname, birth, gender, profilePicture) values";
$sql .= "('$id', '$cryptedPw', '파이탄', '19880226', 'female', 'profile_101010@gmail.com.jpg')";

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

$conn->close();


?>
