<?php

include "./getThumb.php";

$data = $_POST["data1"];  //data1이라는 키 값으로 들어오는 밸류선언: 생성할 폴더의 이름

$file_path = "/var/www/html/TMphp/".$data."/";   // 사진경로, 위에 들어온 폴더이름 아래에 저장하기 위함

if (is_dir($data)) {
  echo "폴더존재 O"; // pass
  @chmod($data, 777);
} else {
  echo "폴더존재 X";
  @mkdir($data, 0777);
  @chmod($data, 0777);
  if (is_dir($data)) {
      echo " ** 폴더 생성성공";
  }else{
      echo " ** 폴더 생성성공";
  }

};

echo " **  fileSize".$_FILES['upload_file']['size'];
echo " **  fileType".$_FILES['upload_file']['type'];
echo " **  tmp_name".$_FILES['upload_file']['tmp_name'];


// basename : 디렉토리명이 있다면, 그 부분을 제외하고 파일명만 출력, 즉 abc/def/ghi.jpg면 ghi.jpg만 가져올수 있음

$file_name = basename($_FILES['upload_file']['name']);
$n_path = $file_path.'thumbnail_'.$file_name;  //썸네일 파일 경로
$file_path = $file_path.$file_name; //원본파일 경로

echo " ** file_name : ".$file_name;
echo " ** file_path : ".$file_path;
echo " ** n_path : ".$n_path;

//$_FILES['upload_file'] 과 $_FILES['uploaded_file']은 다른 파일이다

if (move_uploaded_file($_FILES['upload_file']['tmp_name'], $file_path)) {
  echo " ** file upload 성공";

  //썸네일 파일 작성
  $param1 = array('o_path' => $file_path, 'n_path' => $n_path, 'mode' => 'ratio', 'width' => 2000, 'height' => 1000);
  $thumb1 = getThumb($param1);
  $output = json_encode ( array (
          "thumbResult" => $thumb1
  ) );
  echo urldecode ( $output );


} else {
  echo " ** file upload 실패";
  echo " ** errorCode : ".$_FILES['upload_file']['error'];
}






 ?>
