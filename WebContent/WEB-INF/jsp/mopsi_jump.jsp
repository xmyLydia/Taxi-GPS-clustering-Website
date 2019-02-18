<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Mopsi</title>
  <style>
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      #map {
        height: 100%;
      }
      /* Optional: Makes the sample page fill the window. */
      html, body {
        height: 100%;
        margin: 0;
        padding: 0;
      }
    </style>
 <link rel="stylesheet" href="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/css/bootstrap.min.css">
	<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
	<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<script src="js/jquery-1.12.3.js"> </script>
<script src="js/jquery-ui.js"></script>
<script type="text/javascript" src="js/mopsiJS.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAY-8D0bHCyjkP-ZIOwWltkd1FfsXF-Bw4&callback=initMap" async defer></script>
</head>
<body>
 <button id="mopsi_all"  >show trajectories</button>
    <div id="map"></div>
 
</body>
</html>