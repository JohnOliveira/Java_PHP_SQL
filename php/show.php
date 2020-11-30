<?php
	$connection = mysql_connect('localhost','rio_carioca_app', 'beerapp');//Connect to server
	mysql_select_db('rio_carioca', $connection);//Find data base
	$sql = "select * from places";//Call user and password

	$answer = mysql_query($sql) or die("Error .:" . mysql_error());

	while ($linha = mysql_fetch_object($answer))
	{
		echo $linha->lat."#";
		echo $linha->lng."#";
                echo $linha->names."#";
		echo $linha->address."#";
                echo $linha->number."#";
		echo $linha->available."#";
                echo $linha->available."!";

	}
	echo "@";
?>