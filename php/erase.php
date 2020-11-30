<?php
	$address=$_POST['address'];

	$conection = mysql_connect('localhost','rio_carioca_app', 'beerapp');
	mysql_select_db('rio_carioca', $conection);
	$sql = "delete from places where address like '$address'";

	$answer= mysql_query($sql) or die("Error .:" . mysql_error());

	if ($answer)
		echo "APAGADO";
	else
		echo "ERROR BY ERASE";
?>

<!DOCTYPE html>
<html>
<head>
	<title>Rio Carioca</title>
	<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
	<form action="process.php">
		<input type="submit" value="Voltar">
	</form>
</body>
</html>