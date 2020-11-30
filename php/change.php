<?php
        $old=$_POST['old'];

	$lat=$_POST['lat'];
        $lng=$_POST['lng'];
        $names=$_POST['names'];
        $address=$_POST['address'];
        $number=$_POST['number'];
        $available=$_POST['available'];
        
        //$senha=$_POST['senha'];
	//$usuario=$_POST['usuario'];
	//$senha=$_POST['senha'];

	$conection = mysql_connect('localhost','rio_carioca_app', 'beerapp');//Connect to server
	mysql_select_db('rio_carioca', $conection);//Find data base
	$sql = "update places set lat='$lat', lng='$lng', names='$names', address='$address', number='$number', available='$available' where address='$old'";//Call user and password

	$answer = mysql_query($sql) or die("Error .:" . mysql_error());

	if ($answer)
		echo "Alterado!!!";
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