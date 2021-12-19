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
		}else{
			echo "ok, failed to fetch data";
		}

	}else{
		echo json_encode("failed to connect");
	}

	$conn-> close();
?>
