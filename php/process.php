<?php
	$connection = mysql_connect('localhost','rio_carioca_app', 'beerapp');
	mysql_select_db('rio_carioca', $connection);
	$sql = "select * from places";

	$answer = mysql_query($sql) or die("Error .:" . mysql_error());

        echo "Dados atuais<br><br>";

	while ($linha = mysql_fetch_object($answer))
	{	
                echo $linha->lat." / ";
		echo $linha->lng." / ";
                echo $linha->names." / ";
		echo $linha->address." / ";
                echo $linha->number." / ";
		echo $linha->available."<br><br>";
	}
?>

<!DOCTYPE html>
<html>
<head>
	<title>Rio Carioca</title>
	<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>

	<form action="save.php" method="POST">
		.:: Adicionar - Insira as coordenadas, bar, endereço, número e cervejas ::.
		<div id="save">
			<input type="text" name="lat" placeholder="Latitude">
			<input type="text" name="lng" placeholder="Longitude">
			<input type="text" name="names" placeholder="Bar">
			<input type="text" name="address" placeholder="Endereço">
			<input type="text" name="number" placeholder="Número">
			<input type="text" name="available" placeholder="Cervejas">
			<input type="submit" value="Salvar!">
		</div>
	</form>

	<form action="change.php" method="POST">
		.:: Alterar - Insira novas coordenadas, bar, endereço, numero, cervejas e o antigo endereço ::.
		<div id="change">
			<input type="text" name="lat" placeholder="Nova latitude">
			<input type="text" name="lng" placeholder="Nova longitude">
			<input type="text" name="names" placeholder="Novo bar">
			<input type="text" name="address" placeholder="Novo endereço">
			<input type="text" name="number" placeholder="Novo número">
			<input type="text" name="available" placeholder="Nova cerveja">
			<input type="text" name="old" placeholder="Endereço antigo">
			<input type="submit" value="Alterar!">
		</div>
	</form>
	<form action="erase.php" method="POST">
		.:: Apagar - Insira apenas o endereço ::.
		<div id="erase">
			<input type="text" name="address" placeholder="Endereço">
			<input type="submit" value="Apagar!">
		</div>
	</form>
</body>
</html>

<!DOCTYPE html>
<html>
<head>
	<title>Rio Carioca</title>
	<link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
	<form action="process.php" method="GET">
        .:: Insira o endereço todo em minúsculo, seguido do bairro ou cidade ::.
        <div>
	        <input type="text" name="address" placeholder="Endereço">
	        <input type="submit" name="off" value="Mostrar">
        </div>
    </form>
</body>
</html>

<?php 
	
	if(isset($_GET['off']))
	{
		function getCoordinates($address)
		{

			$address = str_replace(" ", "+", $address); // replace all the white space with "+" sign to match with google search pattern

			$url = "http://maps.google.com/maps/api/geocode/json?sensor=false&address=$address";

			$response = file_get_contents($url);

			$json = json_decode($response,TRUE); //generate array object from the response from the web

			return ("LATITUDE: ".$json['results'][0]['geometry']['location']['lat']." , LONGITUDE: ".$json['results'][0]['geometry']['location']['lng']);
		}
		echo getCoordinates($_GET['address']);
    }
?>