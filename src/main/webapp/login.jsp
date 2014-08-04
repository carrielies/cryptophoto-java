<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="loginFailed" type="java.lang.Boolean"--%>

<!DOCTYPE html>

<html>
<head>
  <meta charset="utf-8" />

  <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1" />
  <meta name="viewport"
        content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,minimal-ui" />
  <meta name="robots" content="index,follow" />

  <meta name="description" content="CryptoPhoto Demo v. 1.0, Java (EE) version" />
  <meta name="author" content="CryptoPhoto (http://cryptophoto.com/)" />
  <meta name="keywords" content="cryptophoto java java-ee" />

  <title>CryptoPhoto Demo v. 1.0, Java (EE) version</title>

  <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Lato:400,900" />
  <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/normalize/3.0.1/normalize.min.css" />
  <link rel="stylesheet" href="css/main.css" />

  <script src="http://cdnjs.cloudflare.com/ajax/libs/modernizr/2.8.2/modernizr.min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.6.0/underscore-min.js"></script>
</head>

<body>

<main role="main">
  <h1>Log in with CryptoPhoto</h1>

  <c:if test="${loginFailed}">
    <p class="error">The username or password you entered is not correct. Please try again.</p>
  </c:if>

  <form method="post" action="login">
    <label for="userId">Username:</label>
    <input id="userId" name="userId" placeholder="Enter the user id" autofocus="true" />
    <label for="passWd">Password:</label>
    <input id="passWd" name="passWd" type="password" />
    <input type="submit" value="Go" />
  </form>
</main>

<footer role="contentinfo"><p>Copyright (C) 2014 <a href="http://cryptophoto.com/">Cryptophoto.com</a>. All Rights
                              Reserved.</p></footer>

</body>

</html>
