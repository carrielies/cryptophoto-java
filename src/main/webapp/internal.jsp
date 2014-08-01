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
  <!--link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.css" /-->

  <style type="text/css">
    *, *:before, *:after { -moz-box-sizing: border-box; -webkit-box-sizing: border-box; box-sizing: border-box; }

    html { height: 100%; overflow: auto; background: #ffffff; color: #505050; }

    body {
      position: relative;
      margin: 0 auto;
      max-width: 800px;
      min-height: 100%;
      outline: 1px solid #dadada;
      overflow: auto;
      padding: 0 25px;
      font: 62.5% Lato, "Gill Sans", "Trebuchet MS", Calibri, Tahoma, Verdana, sans-serif;
      text-shadow: 0 0 1px rgba(16, 16, 16, 0.1);
      -webkit-font-smoothing: subpixel-antialiased;
      -webkit-text-stroke: 1px rgba(16, 16, 16, 0.1);
      zoom: 1;
    }

    a { outline: none; color: #002d36; /* 0, 45, 54 */ text-decoration: none; }

    a:hover { color: #888888; }

    .error { color: pink; }

    [role=main] {
      font-size: 1rem;
    }

    [role=main] h1 { font-weight: normal; }

    [role=main] input {
      margin: 5px 5px 0;
    }

    [role=contentinfo] {
      position: fixed;
      bottom: 0;
      left: 0;
      width: 100%;
      color: #888888;
      font-size: 1rem;
      font-variant: small-caps;
      letter-spacing: 0.1rem;
      text-align: center;
    }

    [role=contentinfo] a:hover { color: #111111; }
  </style>

  <script src="http://cdnjs.cloudflare.com/ajax/libs/modernizr/2.8.2/modernizr.min.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.6.0/underscore-min.js"></script>
</head>

<body>

<main role="main">
  <h1>Log in with CryptoPhoto</h1>

  <p>Hello ${userId}, and welcome!</p>
</main>

<footer role="contentinfo"><p>Copyright (C) 2014 <a href="http://cryptophoto.com/">Cryptophoto.com</a>. All Rights
                              Reserved.</p></footer>

</body>

</html>
