<?php

	require 'connect.php';

	if($conn){
		$sql = "select * from employee_data";
		$res = mysqli_query($conn,$sql);

		if(mysqli_num_rows($res) > 0){
			while($row = mysqli_fetch_assoc($res)){
				$data[] = $row;
			}
			print(json_encode($data));
		}
		if(mysqli_num_rows($res)==0){
				echo json_encode(array("success"));
		}
		else{ echo json_encode(array("failed to connect"));	}
	}
	else{
		echo json_encode("failed to connect");
	}

	$conn-> close();
?>
