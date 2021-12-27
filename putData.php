<?php
	require 'connect.php';

	if($conn){
	  $id = $_POST["id"];
	  $password = $_POST["password"];
	  $name = $_POST["name"];
    $image = $_POST["image"];
		$number = $_POST["number"];
		$filename = "http://192.168.1.49/Employee/images/".$name.".jpg";
		// $filename = "http://192.168.1.7/Employee/images/".$name.".jpg";

	  file_put_contents("images/".$name.".jpg",base64_decode($image));

		$sql1 = "select * from employee_data where id='$id' ";
	  $res1 = mysqli_query($conn,$sql1);

	  if(mysqli_num_rows($res1) > 0){
			while($row = mysqli_fetch_assoc($res1)){
				echo "user already exists";
			}
	  }
	  else{
		$sql = "insert into employee_data(id,password,name,number,image)
		         VALUES('$id','$password','$name','$number','$filename') ";
	  	$res = mysqli_query($conn,$sql);

          	if($res){
			echo "Data inserted Successfully";
	  	}else{
			echo "Failed to insert data";
          	}
	}
       }
	else{
          echo "Failed";
    	}

	$conn -> close();
?>
