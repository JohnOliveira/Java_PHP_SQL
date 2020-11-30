<?php
	//$login=$_GET['login'];
	//$password=$_GET['password'];

	$login=$_POST['login'];
	$password=$_POST['password'];

	$conection = mysql_connect('localhost','rio_carioca_app', 'beerapp');//Connect to server
	mysql_select_db('rio_carioca', $conection );//Find data base
	$sql = "select * from users where login='$login' and password='$password'";//Call user and password

	$answer = mysql_query($sql) or die("Error .:" . mysql_error());

	if (mysql_num_rows($answer) > 0)
		echo "1";
	else
		echo 0;
?>