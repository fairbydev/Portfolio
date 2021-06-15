<?php

  include "./dbconfig.php";


//1-1-1. 남자 2인 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = '2';
  $place = '도쿄';
  $gender = 'male';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  $picture01 = '132321011771613720170729_06133320170729_161136.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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


//1-1-2.남자 3-5인 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = '3to5';
  $place = '도쿄';
  $gender = 'male';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  // $picture01 = '132321011771613720170723_11481720170723_114933.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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

//1-1-3.남자 6-10인 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = '6to10';
  $place = '도쿄';
  $gender = 'male';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  // $picture01 = '132321011771613720170723_11481720170723_114933.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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



//1-1-4.남자 11인이상 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = 'from11';
  $place = '도쿄';
  $gender = 'male';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  // $picture01 = '132321011771613720170729_06133320170729_161136.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 1-2. 여자 도쿄



//1-2-1.여자 2인 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = '2';
  $place = '도쿄';
  $gender = 'female';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  // $picture01 = '132321011771613720170723_11481720170723_114933.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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


//1-2-2.여자 3-5인 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = '3to5';
  $place = '도쿄';
  $gender = 'female';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  // $picture01 = '132321011771613720170723_11481720170723_114933.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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

//1-1-3.여자 6-10인 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = '6to10';
  $place = '도쿄';
  $gender = 'female';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  // $picture01 = '132321011771613720170723_11481720170723_114933.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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



//1-2-4.여자 11인이상 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = 'from11';
  $place = '도쿄';
  $gender = 'female';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  // $picture01 = '132321011771613720170723_11481720170723_114933.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 1-3. 성별무관 도쿄



//1-3-1.성별무관 2인 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = '2';
  $place = '도쿄';
  $gender = 'all';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  // $picture01 = '132321011771613720170723_11481720170723_114933.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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


//1-3-2.성별무관 3-5인 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = '3to5';
  $place = '도쿄';
  $gender = 'all';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  // $picture01 = '132321011771613720170723_11481720170723_114933.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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

//1-3-3.성별무관 6-10인 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = '6to10';
  $place = '도쿄';
  $gender = 'all';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  // $picture01 = '132321011771613720170723_11481720170723_114933.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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







//1-3-4.성별무관 11인이상 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = 'from11';
  $place = '서울';
  $gender = 'all';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  $picture01 = '132321011771613720170729_05201020170729_052051.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04, hit) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04', '10')";
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
















//1-3-4.성별무관 11인이상 도쿄
for ($i=1; $i < 10; $i++) {

  $maxPerson = 'from11';
  $place = '도쿄';
  $gender = 'all';
  $cost = $i*10000;
  $fromAge = $i*10+10;
  if ($fromAge >= 80) {
    $fromAge = 80;
  }
    $toAge = $i*10+20;
  if ($toAge >= 80) {
    $toAge = 90;
  }

  $startDate = '20170'.$i.'01';
  $endDate = '20170'.$i.'30';
  $title= $i.'월 '.$place.' '.$maxPerson.'인 '.$gender.' ('.$fromAge.'-'.$toAge.')';
  $briefing = $title;


  $enterTime = '20170723_114817';
  $createdTime = '20170723_11494';

  $picture01 = '132321011771613720170729_06133320170729_161136.jpg';
  $picture02 = 'noPicture';
  $picture03 = 'noPicture';
  $picture04 = 'noPicture';

  $sql = "insert into travel_Info (id, title, startDate, endDate, maxPerson, place, cost, gender, fromAge, toAge, briefing, enterTime, createdTime, picture01, picture02, picture03, picture04) values";
  $sql .= "(1323210117716137, '$title', '$startDate', '$endDate', '$maxPerson', '$place', '$cost', '$gender', '$fromAge', '$toAge', '$briefing', '$enterTime', '$createdTime', '$picture01', '$picture02', '$picture03', '$picture04')";
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



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////









  $conn->close();


?>
