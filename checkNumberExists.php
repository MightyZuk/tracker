<?php
	require 'connect.php';

	if($conn){
          $number = $_POST["number"];
          $sql = "select * from employee_data where number='$number' ";
	  $res = mysqli_query($conn,$sql);

	  if(mysqli_num_rows($res) > 0){
			while($row = mysqli_fetch_assoc($res)){
				echo "user already exists";
			}
	  }
	  else{ echo "Go to otp verification"; }
  }
	else{
      echo "Failed";
  }

	$conn -> close();
?>
