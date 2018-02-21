<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<font color="red"> ${errorMessage}</font>
	<div>Current Topic: ${topic }</div>
	<form method="post">
		Topic:   <select name="topic">
						<option value="topicExchange_Quote">Quote</option>
						<option value="topicExchange_Order">Order</option>
  				</select><br/>
		Routing Key:  <select name="route">
						<option value="stock">stock</option>
						<option value="option">option</option>
						<option value="cbbc">CBBC (not ready)</option>
  				</select><br/>
		Number of Msg: <input type="number" step="1" name="numOfMsg"/><br/>
		<input type="submit"/>
	</form>
</body>
</html>