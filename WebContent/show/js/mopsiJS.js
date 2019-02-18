   
  var testData={taxiId:null,comparedTaxiIds:null,index:0,clusterId:null,k:null,iterator:null,partition_type:null,cluster_type:null}; 
  var map_mopsi;
  function initMap() {
      map_mopsi = new google.maps.Map(document.getElementById('map'), {
      center: {lat: 62.604896, lng: 29.760147},
      zoom: 12
    });
  }
    function clearPathAndMarker()
	{
		/*if(rePoly!=null){
			rePoly.setMap(null);
		}*/
		if (poly != null)
		poly.setMap(null);
	for (var i = 0; i < markers.length; i++) {
		markers[i].setMap(null);
	}
	markers = [];
	for (var i = 0; i < trajectory.length; i++) {
		trajectory[i].setMap(null);
	}
	trajectory = [];
	for (var i = 0; i < pointOfInterestMarkers.length; i++) {
		pointOfInterestMarkers[i].setMap(null);
	}
	pointOfInterestMarkers = [];
	//d3.select("#force").remove();
	//d3.select("#chord").remove();
	}
      //jquery function
      $(function(){
    	 
  		$("#mopsi_all").click(function(){ 
  			$.ajax({ 
  		            type:"POST", 
  		            url:"taxi/showAllMopsi", 
  		            dataType:"json",      
  		            contentType:"application/json; charset=utf-8", 
  					data:JSON.stringify(testData),
  		            success:function(data){
  						clearPathAndMarker();
  						var allTrajectory = data.fragment;
  						similarityEdge = data.similarity;
  						for(var a in allTrajectory){
  							var singleTrajectory = allTrajectory[a];
  							var lineSymbol = {
  								    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
  								  };
  							var getRandomColor = randomColor();
  							var poly = new google.maps.Polyline({
  								    strokeColor: '#FF0000',
  								    strokeOpacity: 1.0,
  								    strokeWeight: 3,
  								    map:map_mopsi,
  								    icons: [{
  								        icon: lineSymbol,
  								        offset: '100%'
  								      }]
  								  });
  							for(var b in singleTrajectory){
  								var myLatlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude);
  								if(a==0) map.setCenter(myLatlng);
  								var path = poly.getPath();
  								path.push(myLatlng);
  								//open a info window
  								if(b==0){
  									index = parseInt(a)+parseInt("1");
  								 	var content = "track"+index;
  								 	similarityNode.push(content);
  									marker = new google.maps.Marker({
  				            		    position: myLatlng,
  				            		    title:content,
  				            		    map:map_mopsi
  				            		});
  									marker.addListener('click',showChord);
  									markers.push(marker);
  									var latlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude+0.02);
  									var infowindow = new google.maps.InfoWindow({
  									    content: content
  									  });
  									infowindow.open(map_mopsi,marker);
  								}
  							}
  							trajectory.push(poly);
  						}
  					 
  					}
  			});
  			 
  	}
  		);
      
      });