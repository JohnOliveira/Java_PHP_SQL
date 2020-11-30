<?php
	$lat=$_POST['lat'];
        $lng=$_POST['lng'];
        $names=$_POST['names'];
        $address=$_POST['address'];
        $number=$_POST['number'];
        $available=$_POST['available'];

	//$usuario=$_GET['usuario'];
	//$senha=$_GET['senha'];

	$conection = mysql_connect('localhost','rio_carioca_app', 'beerapp');//Connect to server
	mysql_select_db('rio_carioca', $conection);//Find data base
	$sql = "insert into places (lat, lng, names, address, number, available) values ('$lat', '$lng', '$names', '$address', '$number', '$available')";

	$answer = mysql_query($sql) or die("Error .:" . mysql_error());

	if ($answer)
		echo "SAVED!!!";
	else
		echo "HAS ERROR!!!";
?>

<!DOCTYPE html>
<html>
<head>
	<title>Rio Carioca</title>
</head>
<body>
	<form action="process.php">
		<input type="submit" value="Voltar">
	</form>
</body>
</html>