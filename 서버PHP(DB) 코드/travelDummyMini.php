<?php

include "./dbconfig.php";


$id;
$title;
$startDate;
$endDate;
$maxPerson;
$place;
$cost;
$gender;
$fromAge;
$toAge;
$briefing;
$enterTime;
$createdTime;
$picture01;
$picture02;
$picture03;
$picture04;





  //2.작성자 밑 제목
  for ($j=0; $j < 10; $j++){

    $i = $j+1;
    $id = (String)$i.(String)$i.(String)$i.'@gmail.com';
    //$id = '111@gmail.com';
    $titleList = array('서울','도쿄','북경','파리','뉴욕','블라디보스토크','시드니','토론토','런던','싱가포르');
    $title = $titleList[$j].' 같이 갈 동행 구합니다';

    //3.시작일 + 종료일
    for ($k=6; $k <7 ; $k++) {
      if ($k < 11) {
        $startDate = '20190'.$k.'10';
        $endDate = '20190'.$k.'20';
      }else{
        $startDate = '2019'.$k.'10';
        $endDate = '2019'.$k.'20';
      }

      // 4.인원
      for ($l=3; $l <4 ; $l++) {
        $personList = array('2','3to5','6to10','from11');
        $maxPerson = $personList[$l];

        // 5. 장소(제목과 통일)
          $placeList = array('서울','도쿄','북경','파리','뉴욕','블라디보스토크','시드니','토론토','런던','싱가포르');
          $place = $placeList[$j];

        // 6. 비용
        for ($n=6; $n < 7; $n++) {
          $cost = $n*100000 + 400000;

          // 7. 성별
          for ($o=0; $o < 1 ; $o++) {
            $genderList = array('male','female','all');
            $gender = $genderList[$o];

            // 8. 연령구간
            for ($p=2; $p < 3; $p++) {
              $fromAge = $p*10;
              $toAge = $p*10+10;

            // 9. 여행 설명
            $briefing = '혼자 여행왔는데 동행할 친구를 찾습니다.';

            // 10. 생성시각,진입시각
            $enterTime = '20180101_114817';
            $createdTime = '20180101_11494';

              // 11. 사진
                $placeList = array('seoul','tokyo','china','paris','newyork','russia','sydney','toronto','london','singapore');
                $picture01 = $placeList[$j].'.jpg';
                $picture02 = 'noPicture';
                $picture03 = 'noPicture';
                $picture04 = 'noPicture';

                $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
                $sql .= "('$id', '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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

          }

        }

      }

    }

  }












$conn->close();


?>
