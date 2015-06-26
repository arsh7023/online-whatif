<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title>UAZ zip file upload</title>
<style>
	body {font-family: "Trebuchet MS";}
	h1 {font-size: 1.5em;}
</style>


</head>
<body>
<h1>Show UAZ zip file upload</h1>


<form:form method="post" action="saveFile.html" 
		modelAttribute="uploadForm" enctype="multipart/form-data">

	<p>Select files to upload.</p>

	<table id="fileTable">
		<tr>
			<td><input name="file" type="file" /></td>
		</tr>
	</table>
	<br/><input type="submit" value="Upload" />
</form:form>
</body>
</html>
