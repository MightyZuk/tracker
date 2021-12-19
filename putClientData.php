<?php
	require 'connect.php';

	if ($conn) {
		$employee_id = $_POST["employee_id"];
		$employee_name = $_POST["employee_name"];
		$client_name = $_POST["client_name"];
		$image = $_POST["image"];
		$purpose = $_POST["purpose"];
		$initial_location = $_POST["initial_location"];
		$final_location = $_POST["final_location"];
		$amount = $_POST["amount"];
		$number = $_POST["number"];
		$filename = "http://192.168.1.7/Employee/images/".$client_name.".jpg";
	  file_put_contents("images/".$client_name.".jpg",base64_decode($image));

		$sql = "INSERT INTO client_data(employee_id,employee_name,client_name,image,purpose,amount,initial_location,final_location,number)
		        VALUES('$employee_id','$employee_name','$client_name','$filename','$purpose','$amount','$initial_location','$final_location','$number')";

		$res = mysqli_query($conn,$sql);
		if ($res) {
			echo "Data inserted Successfully";
		}else {
			echo "failed to insert data";
		}
	}else {
		echo "Failed to connect to server";
	}
	$conn -> close();

?>
