<?php

require 'connect.php';

if ($conn) {
	$id = $_POST["id"];
	$password = $_POST["password"];

  $query = "select * from employee_data where id='$id' AND password='$password'";

	$result = mysqli_query($conn,$query);
	if(mysqli_num_rows($result)>0)
	{
			 while($row=mysqli_fetch_array($result)){
						$id=$row['id'];
						$name=$row['name'];
						$password=$row['password'];
						$image = $row['image'];
						$number = $row['number'];
			}
 }

 if(mysqli_num_rows($result)==0)
 {
		 $response["success"] = "0";
		 $response["message"]="user is not Registered, Please Register";
		 echo json_encode($response);
 }
 else
		{
			$response["success"]="1";
			$response["message"]="Logged in successful";
			$response["id"]=$id;
			$response["name"]=$name;
			$response["password"]=$password;
			$response["image"]= $image;
			$response["number"]=$number;
			echo json_encode($response);
}
}else {
	echo "Failed to connect";
}

$conn -> close();

?>
