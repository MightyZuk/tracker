<?php

	require 'connect.php';

	if($conn){
		$id = $_POST["employee_id"];
		$name = $_POST["employee_name"];
		$sql = "select * from client_data where employee_name='$name' AND employee_id='$id' ";
		$res = mysqli_query($conn,$sql);

		if(mysqli_num_rows($res) > 0){
			while($row = mysqli_fetch_assoc($res)){
				$data[] = $row;
			}
			echo json_encode($data);
		}
		if(mysqli_num_rows($res)==0)
		{
				echo json_encode(array("success"));
		}
	}else{
		echo json_encode("failed to connect");
	}

	$conn-> close();
?>
