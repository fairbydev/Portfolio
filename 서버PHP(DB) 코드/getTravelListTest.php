<?php

  include "./dbconfig.php";

  $requestCode = 'ALL';



  $sql = 'select * from travel_Info order by seq desc';
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



    $conn->close();


?>
