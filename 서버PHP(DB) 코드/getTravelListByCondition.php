<?php

  include "./dbconfig.php";

  $requestCode;
  if (isset($_POST["requestCode"])) {
    $requestCode = $_POST["requestCode"];
  }

  $filterStartDate;
  if (isset($_POST["filterStartDate"])) {
    $filterStartDate = $_POST["filterStartDate"];
  }else {
    $filterStartDate = "noCondition";
  }

  $filterEndDate;
  if (isset($_POST["filterEndDate"])) {
    $filterEndDate = $_POST["filterEndDate"];
  }else {
    $filterEndDate = "noCondition";
  }

  $filterPlace;
  if (isset($_POST["filterPlace"])) {
    $filterPlace = $_POST["filterPlace"];
  }else {
    $filterPlace = "noCondition";
  }

  $filterMaxPerson;
  if (isset($_POST["filterMaxPerson"])) {
    $filterMaxPerson = $_POST["filterMaxPerson"];
  }else{
    $filterMaxPerson = "noCondition";
  }

  $filterGender;
  if (isset($_POST["filterGender"])) {
    $filterGender = $_POST["filterGender"];
  }else {
    $filterGender = "noCondition";
  } // all이 male과 femle을 포함하지 않으므로 별도의 처리를 해줘야함

  $filterCost;
  if (isset($_POST["filterCost"])) {
    $filterCost = $_POST["filterCost"];
  }else {
    $filterCost = "noCondition";
  }

  $filterHighAge;
  if (isset($_POST["filterHighAge"])) {
    $filterHighAge = $_POST["filterHighAge"];
  }else{
    $filterHighAge = "noCondition";
  }

  $filterLowAge;
  if (isset($_POST["filterLowAge"])) {
    $filterLowAge = $_POST["filterLowAge"];
  }else {
    $filterLowAge = "noCondition";
  }

  $filterWord;
  if (isset($_POST["filterWord"])) {
    $filterWord = $_POST["filterWord"];
  }else {
    $filterWord = "noCondition";
  }




  if ($requestCode == "getTravelLisByCondition") {

    $sql = 'select * from travel_Info ';
    $sql .= 'where (startDate <= '.$filterEndDate.')';
    $sql .= ' and (endDate >= '.$filterStartDate.')';
    $sql .= ' and (endDate >= '.$filterStartDate.')';


    if ($filterWord != 'noCondition') {
      $sql .= ' and ((title like \'%'.$filterWord.'%\')';
      $sql .= ' or (place like \'%'.$filterWord.'%\'))';
    }

    if ($filterPlace != "noCondition") {
      $sql .= ' and (place like \'%'.$filterPlace.'%\')';
    }

    if ($filterMaxPerson != "noCondition") {
      switch ($filterMaxPerson) {
        case '2':
          $sql .= ' and (maxPerson = 2)';
          break;
        case '3to5':
          $sql .= ' and (maxPerson = 3to5)';
        break;
        case '6to10':
          $sql .= ' and (maxPerson = 6to10)';
        break;
        case 'from11':
          $sql .= ' and (maxPerson = from11)';
        break;
      }
    }


    if ($filterGender != "noCondition") {
      switch ($filterMaxPerson) {
        case 'female':
          $sql .= ' and (gender = female)';
          break;
        case 'male':
          $sql .= ' and (gender = male)';
        break;
        case 'all':
          $sql .= ' and (gender = all)';
        break;
      }
    } // all이 male과 femle을 포함하지 않으므로 별도의 처리를 해줘야함


    if ($filterCost != 'noCondition') {
      $sql .= ' and (cost <= '.$filterCost.')';
    }

    if ($filterHighAge != 'noCondition') {
      $sql .= ' and (toAge <= '.$filterHighAge.')';
    }

    if ($filterLowAge != 'noCondition') {
      $sql .= ' and (fromAge <= '.$filterLowAge.')';
    }






    // 쿼리문 실행 후 res에 저장
    $res = $conn->query ( $sql );

    // 배열 생성
    $result = array ();

    // Java와 비슷하게 쿼리의 내용을 한 줄 단위로 검색
    while ( $row = mysqli_fetch_array ( $res ) ) {
        // 이전에 생성한 result Array에 push $row[0] -> 1번째 col
        array_push ( $result, array (
          'task'=>'loadingTravelData',
          'result'=>'loadingSuccess',
          'seq'=>$row['seq'],
          'id'=>$row['id'],
          'title'=>$row['title'],
          'startDate'=>$row['startDate'],
          'endDate'=>$row['endDate'],
          'maxPerson'=>$row['max'],
          'place'=>$row['place'],
          'cost'=>$row['cost'],
          'gender'=>$row['gender'],
          'fromAge'=>$row['fromAge'],
          'toAge'=>$row['toAge'],
          'briefing'=>$row['briefing'],
          'createdTime'=>$row['createdTime'],
          'enterTime'=>$row['enterTime'],
          'hit'=>$row['hit'],
          'picture01'=>$row['picture01'],
          'picture02'=>$row['picture02'],
          'picture03'=>$row['picture03'],
          'picture04'=>$row['picture04']
        ) );
    }

    // json 형태로 변환시켜 저장 사실상 jsonArray 형식으로 반환 키는 "result" value는 jsonArray
    $output = json_encode ( array (
            "result" => $result
    ) );

    // json 형태로 화면에 출력
    // urldecode 함수는 한글이 유니코드로 출력되는것을 해결하기 위함
    echo urldecode ( $output );
    echo urldecode ( $sql );

  }else{
    $arr = array ('task'=>'getTravelLisByCondition','result'=>'requestCodeDoesNotMatch');
    echo json_encode($arr);
  }




    $conn->close();


?>
