//设置基本参数 
	var map;
	var zoomMarker;
	var markers=[],pointOfInterestMarkers=[];
	var repMarker=[];
	var ClusterMarkers=[];
	var Cluster_trajectory=[];
	var repTrajectory=[];
	var clickedRep = [];
	var pointOfRawData=[];
	var trajectory=[];
	var repPoly;
	var previousMarker, currentMarker;
	var onLoadTaxi={
			url: 'icon/on_load_taxi.png'
	};
	var noLoadTaxi={
			url: 'icon/no_load_taxi.png'
	};
	var POI = {
			url: 'icon/YFRrQn.png'
	};
	var nextLayerPOI = {
			url: 'icon/nextlayer.png'
	};
	var newPoint = {
			url :'icon/point.png'
	};
	var allTaxiIds = [];
	//修改：展示所有轨迹
	var getAllIds = [];
	//修改：以上用于存储所有TAXIid
	var internal;
	var singlePathIndex = 0, comparedPathIndex=0;
	var color = d3.scale.category20();
	var EARTH_RADIUS = 6378.137;    //单位KM
    var PI = Math.PI;
	var maxSpeed=0,minSpeed=200,averageSpeed=0;
	var longestDistance=0, shortestDistance=9999;
	var turningPoints=[];
	var selectedSingleTaxiId, selectedComparedTaxiId=[];
	var nodes = [];
	var poly;
	var geocoder;
	var zoomLevel = 11;
	//0---id, 1---speed, 2---load, 3---points size,  4---total distance
	
	var edges=[
	           {"source":0,"target":1},
	           {"source":0,"target":2},
	           {"source":0,"target":3},
	           {"source":0,"target":4},
	           {"source":1,"target":5},
	           {"source":1,"target":6},
	           {"source":1,"target":7},
	           {"source":2,"target":8},
	           {"source":2,"target":9},
	           {"source":2,"target":10},
	           {"source":4,"target":11},
	           {"source":4,"target":12}
	         ];
	var edgesNew = [
	 	           {"source":0,"target":1},
		           {"source":0,"target":2},
		           {"source":0,"target":3},
		           {"source":0,"target":4},
		           {"source":0,"target":5},
		           {"source":0,"target":6} 
		          
		         ];
    var SGAdata = {minAngle:null,minStopDuration:null,minDistance:null,maxDistance:null};
	var testData={method:null,user:null,taxiId:null,comparedTaxiIds:null,index:0,clusterId:null,k:null,iterator:null,partition_type:null,cluster_type:null}; 
	var ClusterData={k:null,iterator:null,traclus_eps:null,minlns:null,rep_order:null,taxi_order:null,eps:null,minpts:null,cluster_type:null,partition_type:null,n:null,m:null,nx:null,ny:null};
	var DbscanData={traclus_eps:null,minlns:null,rep_order:null,taxi_order:null,eps:null,minpts:null,cluster_type:null,partition_type:null,n:null,m:null,nx:null,ny:null};
	var kMeansData={taxiId:null,comparedTaxiIds:null,index:0,eps:null,minpts:null,clusterId:null,k:null,iterator:null,partition_type:null,n:null}; 
	var TraclusData={eps:null,minpts:null,traclus_eps:null,traclus_minlns:null,partition_type:null,cluster_type:null,rep_order:null};
	var ReData={eps:null,minpts:null,partition_type:null,cluster_type:null};
	var AgnesData={n:null,partition_type:null};
	var MopsiData={method:null,user:null,routes:null};
	var selectedComparedTaxiIds = [];
	var similarityNode=[],similarityEdge=[];
	
	  
	function initialize() {
	    var mapOptions = {
	        zoom: zoomLevel,
	       // center: new google.maps.LatLng(31.230416  , 121.673701),
	        center: new google.maps.LatLng(62.6  , 29.7),
	        mapTypeId: google.maps.MapTypeId.ROADMAP
	    
	    };
	    map = new google.maps.Map(document.getElementById('map-canvas'),mapOptions);
		geocoder = new google.maps.Geocoder;
		
	}

	//添加监听函数加载地图
	google.maps.event.addDomListener(window, 'load', initialize);
	
	//绘制路径
	function animateArrow(poly){
		var count=0;
		internal=window.setInterval(function() {
		      count = (count + 1) % 200;
		      var icons = poly.get('icons');
		      icons[0].offset = (count / 2) + '%';
		      poly.set('icons', icons);
		  }, 200);
	}
	function clearTheRed(){
		for (var i = 0; i < repMarker.length; i++) {
			repMarker[i].setMap(null);
		}
		repMarkers = [];
		for (var i = 0; i < repTrajectory.length; i++) {
			repTrajectory[i].setMap(null);
		}
		repTrajectory = [];
	}
	function clearRedPart(){
	/*	for (var i = 0; i < clickedRep.length; i++) {
			clickedRep[i].setMap(null);
		}
		clickedRep = [];
		if (repPoly != null)
		repPoly.setMap(null);
		*/
	for (var i = 0; i < repMarker.length; i++) {
		repMarker[i].setMap(null);
	}
	repMarkers = [];
	for (var i = 0; i < repTrajectory.length; i++) {
		repTrajectory[i].setMap(null);
	}
	repTrajectory = [];
	}
	function clearCluster(){
		for (var i = 0; i < ClusterMarkers.length; i++) {
			ClusterMarkers[i].setMap(null);
		}
		ClusterMarkers = [];
		for (var i = 0; i < Cluster_trajectory.length; i++) {
			Cluster_trajectory[i].setMap(null);
		}
		Cluster_trajectory = [];
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
 
	for (var i = 0; i < trajectory.length; i++) {
		trajectory[i].setMap(null);
	}
	trajectory = [];
	for (var i = 0; i < pointOfInterestMarkers.length; i++) {
		pointOfInterestMarkers[i].setMap(null);
	}
	pointOfInterestMarkers = [];
	d3.select("#force").remove();
	d3.select("#chord").remove();
	}

	//对选中的线段进行加粗
	function showSpecified(singleTrajectory){
	 
					clearPathAndMarker();
		 
					
					var arrowSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};
			 
						var lineSymbol = {
							    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
							  };
						var getRandomColor = randomColor();
						var poly = new google.maps.Polyline({
							    id:a,
							    strokeColor: "#FF0000",
							    strokeOpacity: 2.0,
							    strokeWeight: 4,
							    map:map,
							    icons: [{
							       // icon: lineSymbol,
							    	icon:arrowSymbol ,
							        offset: '100%'
							      }]
							  });
						
					 //	poly.addListener('click',showOriginal );
						for(var b in singleTrajectory){
							var myLatlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude);
							if(a==0) map.setCenter(myLatlng);
							var path = poly.getPath();
							path.push(myLatlng);
							//if(b==0){
								currentMarker = new google.maps.Marker({
			            		    position: myLatlng,
			            		    title:"line"+a,
			            		  //  animation: google.maps.Animation.DROP,
			            		    icon:newPoint,
			            		    map:map
			            		});
								//}
							//open a info window
							//	currentMarker.addListener('click',showSpecified());
			            		markers.push(currentMarker);
						}
						
						var path = poly.getPath();
	            		path.push(myLatlng);
	            		animateArrow(poly);
				//	}
					selectedComparedTaxiIds = [];
					//$(".demo--radio").attr("checked",false);
				};
				// 绘制力导向图
				function showForceGraphNew()
				{
					var forceDiv = d3.select("#main")
					.append("div")
					.attr("id","force")
					
					forceDiv.style("width","1500px")
					.style("height","800px")
					.style("left","400px")
					.style("top","20px")
					.style("position","absolute")
					.style("background","#FFFFFF")
					.style("opacity",0.8)
					
					var closeButton = forceDiv.append("button")
									.text("Close");
					
					closeButton.on("click",function(){
						d3.select("#force").selectAll("*").remove();
						d3.select("#force").remove();
						//$("#force").remove();
						})
						
					var forceInnerDivWidth = 1500, forceInnerDivHeight=800;
					var forceInnerDiv = forceDiv.append("div")
										.attr("id","forceInnerDiv")
										.style("width","1500px")
										.style("height","800px")
										.style("margin","0 auto")
										.style("z-index",9999)
						
					//draw d3
					
					var force = d3.layout.force()
					.nodes(nodes)
					.links(edgesNew)
					.gravity(.05)
					.linkDistance(300)
					.charge(-800)
					.size([forceInnerDivWidth, forceInnerDivHeight]);
					
					var svg = d3.select("#forceInnerDiv")
					.append("svg")
				    .style("height", "800px")
				    .style("width","1500px")
				    .style("margin","0 auto")
					.style("z-index",9999)

					
					force.start();
					
					var svg_link = svg.selectAll("line")
				      .data(edgesNew)
				      .enter()
				      .append("line")
				      .style("stroke","#ccc")
				      .style("stroke-width",3);
					
					var svg_node = svg.selectAll("circle")
				      .data(nodes)
				      .enter()
				      .append("circle")
				      .attr("r", 40)
				      .style("fill", function(d,i) { return color(i); })
				      .call(force.drag);
					
					var svg_texts = svg.selectAll("text")
						.data(nodes)
						.enter()
						.append("text")
						.style("fill","black")
						.style("font-weight","bold")
						.attr("dx", -10)
						.attr("dy", 8)
						.text(function(d) { 
						if(d.name!="speed") return d.name+":"+d.value; 
						return d.name; 
						});
					
					force.on("tick",function(){
						svg_link.attr("x1", function(d) { return d.source.x;})
								.attr("y1", function(d) { return d.source.y;})
								.attr("x2", function(d) { return d.target.x; })
								.attr("y2", function(d) { return d.target.y; });
						
						svg_node.attr("cx", function(d) { return d.x; })
				        		.attr("cy", function(d) { return d.y; });
						
						svg_texts.attr("x",function(d){return d.x;})
				        		.attr("y",function(d){return d.y;});
					})
					.on("start",function(){console.log("start drag");})
					.on("end",function(){console.log("end drag");});
					
					var drag = force.drag()
									.on("dragstart",function(d){d.fixed = true;})
									.on("dragend",function(d,i){d3.select(this).style("fill",color(i));})
					
				}
				//关于地图和D3合并展现Voronoi
			function showD3Leaflet(data){
				var forceDiv = d3.select("#main")
				.append("div")
				.attr("id","maps")
				
				forceDiv.style("width","1100px")
				.style("height","650px")
				.style("left","400px")
				.style("top","20px")
				.style("position","absolute")
				.style("background","#FFFFFF")
				.style("opacity",1.0)
				
				var closeButton = forceDiv.append("button")
								.text("Close");
				
				closeButton.on("click",function(){
					d3.select("#maps").selectAll("*").remove();
					d3.select("#maps").remove();
					//$("#force").remove();
					})
					
				var forceInnerDivWidth = 1500, forceInnerDivHeight=800;
			 	var forceInnerDiv = forceDiv.append("div")
									.attr("id","forceInnerDiv")
									.style("width","1100px")
									.style("height","650px")
									.style("margin","0 auto")
									.style("z-index",9999) 
				var select = forceInnerDiv.append("div")
				                          .attr("id","selections")
				                          .attr("class","selections")
				  select.append("div")
				 .attr("id","toggles")
				//draw d3
				L.mapbox.accessToken = 'pk.eyJ1IjoiemV0dGVyIiwiYSI6ImVvQ3FGVlEifQ.jGp_PWb6xineYqezpSd7wA';
			    mapVoronoi = L.mapbox.map("forceInnerDiv", 'zetter.i73ka9hn')
			     .fitBounds([[62.602876 , 29.755762], [62.60296 , 29.755935]]);
			   //  .fitBounds([[30.40 , 120.80], [32 , 122.20]]);
			   //   .fitBounds([[59.355596 , -9.052734], [49.894634 , 3.515625]]);

			    url = '/TaxiCluster/show/test1.json';
			   // initialSelection = d3.set(['Tesco', 'Sainsburys']);
			   initialSelection = d3.set(['Voronoi']);
			  
			    voronoiMap(mapVoronoi,url, initialSelection,data);
			}
	// 绘制力导向图
	function showForceGraph()
	{
		var forceDiv = d3.select("#main")
		.append("div")
		.attr("id","force")
		
		forceDiv.style("width","1500px")
		.style("height","800px")
		.style("left","400px")
		.style("top","20px")
		.style("position","absolute")
		.style("background","#FFFFFF")
		.style("opacity",0.8)
		
		var closeButton = forceDiv.append("button")
						.text("Close");
		
		closeButton.on("click",function(){
			d3.select("#force").selectAll("*").remove();
			d3.select("#force").remove();
			//$("#force").remove();
			})
			
		var forceInnerDivWidth = 1500, forceInnerDivHeight=800;
		var forceInnerDiv = forceDiv.append("div")
							.attr("id","forceInnerDiv")
							.style("width","1500px")
							.style("height","800px")
							.style("margin","0 auto")
							.style("z-index",9999)
			
		//draw d3
		
		var force = d3.layout.force()
		.nodes(nodes)
		.links(edges)
		.gravity(.05)
		.linkDistance(300)
		.charge(-800)
		.size([forceInnerDivWidth, forceInnerDivHeight]);
		
		var svg = d3.select("#forceInnerDiv")
		.append("svg")
	    .style("height", "800px")
	    .style("width","1500px")
	    .style("margin","0 auto")
		.style("z-index",9999)

		
		force.start();
		
		var svg_link = svg.selectAll("line")
	      .data(edges)
	      .enter()
	      .append("line")
	      .style("stroke","#ccc")
	      .style("stroke-width",3);
		
		var svg_node = svg.selectAll("circle")
	      .data(nodes)
	      .enter()
	      .append("circle")
	      .attr("r", 40)
	      .style("fill", function(d,i) { return color(i); })
	      .call(force.drag);
		
		var svg_texts = svg.selectAll("text")
			.data(nodes)
			.enter()
			.append("text")
			.style("fill","black")
			.style("font-weight","bold")
			.attr("dx", -10)
			.attr("dy", 8)
			.text(function(d) { 
			if(d.name!="speed") return d.name+":"+d.value; 
			return d.name; 
			});
		
		force.on("tick",function(){
			svg_link.attr("x1", function(d) { return d.source.x;})
					.attr("y1", function(d) { return d.source.y;})
					.attr("x2", function(d) { return d.target.x; })
					.attr("y2", function(d) { return d.target.y; });
			
			svg_node.attr("cx", function(d) { return d.x; })
	        		.attr("cy", function(d) { return d.y; });
			
			svg_texts.attr("x",function(d){return d.x;})
	        		.attr("y",function(d){return d.y;});
		})
		.on("start",function(){console.log("start drag");})
		.on("end",function(){console.log("end drag");});
		
		var drag = force.drag()
						.on("dragstart",function(d){d.fixed = true;})
						.on("dragend",function(d,i){d3.select(this).style("fill",color(i));})
		
		
	}
	function testSegment(){
        clearPathAndMarker();
    	clearCluster();
    	clearRedPart();
	}
    function showSegment(content,dataOfRe,color,eps,minlns,cluster_type ){
    	clearPathAndMarker();
    	
    	clearCluster();
    	clearRedPart();
    	console.log("in the begining of function,content is :"+content);
    	//点击代表轨迹展示对应聚类的线段
    //	console.log("dataRe"+dataOfRe);
    	var click_content = content;
    	TraclusData.rep_order = content;
        TraclusData.traclus_eps=eps;
	    TraclusData.minlns= minlns;
	    TraclusData.cluster_type=cluster_type;
	    TraclusData.partition_type = window.cp;
		$.ajax({ 
		      type:"POST", 
	            url:"taxi/traclus_rep_raw", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8", 
				data:JSON.stringify(TraclusData),
	            success:function(data){
				//	clearPathAndMarker();
	            //	clearRedPart();
	             	clearTheRed();
	            	map.setZoom(11);
					var allTrajectory = data.cluster ;
					var repreTrack = data.representative;
					console.log(data);
					console.log("allTracks in representative:"+allTrajectory.length);
					
					var arrowSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};
					//画cluster部分
					for(var a in allTrajectory){
						var singleTrajectory = allTrajectory[a];
						var lineSymbol = {
							    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
							  };
						var poly = new google.maps.Polyline({
							    title:a,
							    strokeColor: "#000000",
							    strokeOpacity: 2.0,
							    strokeWeight: 1,
							    map:map,
							    icons: [{
							       // icon: lineSymbol,
							    	icon:arrowSymbol ,
							        offset: '100%'
							      }]
							  });
						 poly.addListener('click',function(){//点击之后触发的事件
							// alert(this.title);
							// testD3(this.title,DbscanData);
						 });
						//	for(var a in allTrajectory){
						
					//	repPoly.push(poly);	 
				//	for(var b =0;b<2;b++){
						 //start点
							var myLatlng = new google.maps.LatLng(singleTrajectory.s.x,singleTrajectory.s.y);
							if(a==0) map.setCenter(myLatlng);
							var path = poly.getPath();
							path.push(myLatlng);
								currentMarker = new google.maps.Marker({
			            		    position: myLatlng,
			            		    title:a,
			            		    icon:newPoint,
			            		    map:map,
			            		    track:singleTrajectory
			            		});
			            	//	markers.push(currentMarker);
								
								ClusterMarkers.push(currentMarker);
			            		//end点
			            		 myLatlng = new google.maps.LatLng(singleTrajectory.e.x,singleTrajectory.e.y);
									if(a==0) map.setCenter(myLatlng);
									var path = poly.getPath();
									path.push(myLatlng);
									Cluster_trajectory.push(poly);
					            	//	markers.push(currentMarker);
						//}
						//trajectory.push(poly);
						
						var path = poly.getPath();
	            		//path.push(myLatlng);
	            		animateArrow(poly);
					}Cluster_trajectory.push(poly);
				//画出代表轨迹，这里画出被选中的轨迹
				
					var countRe = 0;
					for(var m in dataOfRe){
						var singleTrajectoryRe = dataOfRe[m];
						var lineSymbol = {
							    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
							  };
						var reColor = '#000000';
						if(countRe==click_content){
							reColor = '#FF0000';
						}
						console.log("content is"+ click_content +"and countRe is :"+countRe);
						console.log(reColor);
						countRe=countRe+1;
						var poly = new google.maps.Polyline({
							    title:m,
							    Colorchanged:0,//0代表未被改变
							  //  strokeColor: getRandomColor,
							    strokeColor: reColor,
							    strokeOpacity: 1.0,
							    strokeWeight: 3,
							    map:map,
							    icons: [{
							        icon: lineSymbol,
							        offset: '100%'
							      }]
							  });
					//	console.log(poly.strokeColor);
					//	repTrajectory.push(poly);
						//console.log(singleTrajectory);
						for(var b in singleTrajectoryRe.points){
							var myLatlng = new google.maps.LatLng(singleTrajectoryRe.points[b].x,singleTrajectoryRe.points[b].y);
							
						//	console.log(singleTrajectory.points[b].x+":"+singleTrajectory.points[b].y);
							if(a==0) map.setCenter(myLatlng);
							var path = poly.getPath();
							path.push(myLatlng);
							if(b==0){
								var content = a;
								//similarityNode.push(content);
						 	marker = new google.maps.Marker({
			            		    position: myLatlng,
			            		    title:m,
			            		    map:map,
			            		   // icon:newPoint
			            		});
						 	 repMarker.push(marker);
							  marker.addListener('click',function(){//点击之后触发的事件
									// alert(this.title);
								     //点击之后将对应的Poly放入数组并清除
								    // clickedRep.push(poly);
								    // poly.Colorchanged=1;//标记为该线段被ga
								     var color_poly = poly.strokeColor;//点击线段的原本颜色
								     poly.strokeColor = '#FF0000';
								  //   console.log(poly.strokeColor +";"+color_poly);
								     console.log("when i click , the title is :"+this.title);
									 showSegment(this.title,dataOfRe,color_poly,eps,minlns,cluster_type);
								    // testSegment();
								//	 console.log(trajectory);
								 });
							 
							//	var latlng = new google.maps.LatLng(singleTrajectory.points[b].x,singleTrajectory.points[b].y+0.02);
							}
						}
						//trajectory.push(poly);
						repTrajectory.push(poly);
					} repTrajectory.push(poly);
				}
	});
    }
	//绘制弦图
	function showChord(){
		var data = similarityEdge;
		for(var a in data){
			for(var b in data)
			{
				if(data[a][b]==0) continue;
				data[a][b] = parseFloat("1")/parseFloat(data[a][b]);
			}
		}
		
		
		var chord = d3.layout.chord()
			.padding(0.03)
			.sortSubgroups(d3.descending)
			.matrix(data);
			
		
		var groups = chord.groups();
		var chords = chord.chords();
			
		var width = 550;
		var height = 550;
		var innerRadius = width/2 * 0.7;
		var outerRadius = innerRadius * 1.1;
		var color20 = d3.scale.category20();
		
		var forceDiv = d3.select("#main")
		.append("div")
		.attr("id","chord");
		
		forceDiv.style("width","600px")
		.style("height","600px")
		.style("top","20px")
		.style("right","20px")
		.style("position","absolute")
		.style("background","#FFFFFF")
		.style("opacity",0.8);
		 
		var closeButton = forceDiv.append("button")
						.style("position","absolute")
						.text("close");
		
		closeButton.on("click",function(){
			d3.select("#chord").selectAll("*").remove();
			d3.select("#chord").remove();
			})
			
		var svg = d3.select("#chord").append("svg")
					.attr("width", width)
					.attr("height", height)
					.style("top","40px")
					.style("padding-left","40px")
					.style("padding-top","10px")
					.append("g")
					.attr("transform", "translate(" + width/2 + "," + height/2 + ")");
		
		var textForSimilarity = d3.select("#chord").append("text").attr("id","textForSimilarity").style("padding-left","240px");
		
		var outer_arc = d3.svg.arc().innerRadius(innerRadius).outerRadius(outerRadius);
		
		var g_outer = svg.append("g");
		
		g_outer.selectAll("path")
			.data(groups)
			.enter()
			.append("path")
			.style("fill", function(d) { return color20(d.index); })
			.style("stroke", function(d) { return color20(d.index); })
			.attr("d", outer_arc );
			
		g_outer.selectAll("text")
			.data(groups)
			.enter()
			.append("text")
			.each(function(d,i){
				d.angle = (d.startAngle + d.endAngle) / 2; 
				d.name = similarityNode[i];
			})
			.attr("dy",".35em")
			.attr("transform", function(d){
					return "rotate(" + ( d.angle * 180 / Math.PI ) + ")" +
							"translate(0,"+ -1.0*(outerRadius+10) +")" +
							( ( d.angle > Math.PI*3/4 && d.angle < Math.PI*5/4 ) ? "rotate(180)" : "");
			})
			.text(function(d){return d.name;});
		
		//画弦
		var inner_chord = d3.svg.chord().radius(innerRadius);
		
		svg.append("g")
			.selectAll("path")
			.data(chords)
			.enter()
			.append("path")
			.attr("d",inner_chord)
			.style("fill", function(d) { return color20(d.source.index); })
			.style("opacity", 1)
			.on("mouseover",function(d,i){
					d3.select(this).style("fill","yellow");
					if(data[d.source.index][d.target.index]!=0)
						d3.select("#textForSimilarity").text("similarity："+new Number(1/data[d.source.index][d.target.index]).toFixed(4)+"(Hausdorff measure)");
					else d3.select("#textForSimilarity").text("similarity："+new Number(data[d.source.index][d.target.index]).toFixed(4)+"(Hausdorff measure)");
			})
			.on("mouseout",function(d,i) { 
					d3.select(this).transition().duration(1000).style("fill",color20(d.source.index));
			});
		
	}
	
	function showThePanel(){
		var cluster =  document.getElementById("menu"); 
		   cluster.style.display="block";
	} 
	
	//绘制柱状图
	function showStatistical(path){
		var district = ["Huangpu","Jingan","Xuhui", "Pudong", "Hongkou","Yangpu", "Baoshan", "Putuo", "Changning", "Minhang"];
		var agnesDataSet = [4, 4, 7,44, 5, 7, 8, 7, 5, 9];
		var dbscanDataSet = [3, 3, 2, 1, 2, 3, 0, 0, 1, 0];
		var kmeansDataSet = [10, 12, 10, 18, 8, 8, 6, 14, 10, 4];
		var dataset = [];
		if(path=="cluster/show/kmeans") dataset = kmeansDataSet;
		else if(path=="cluster/show/dbscan") dataset = dbscanDataSet;
		else if(path=="cluster/show/agnes") dataset = agnesDataSet;
		
		var width = 356;
		var height = 680;
		$("#statistics_graph").show();
		var svg = d3.select("#statistics_graph")
					.append("svg")
					.attr("width",width)
					.attr("height", height);
		
		//画布周边的空白
		var padding = {left:40, right:10, top:20, bottom:120};
		
		//x轴的比例尺
		var xScale = d3.scale.ordinal()
					   //.domain(district)
					   .domain(d3.range(dataset.length))
					   .rangeRoundBands([0, width - padding.left - padding.right]);
 
		//y轴的比例尺
		var yScale = d3.scale.linear()
					   .domain([0,d3.max(dataset)])
                       .range([height - padding.top - padding.bottom, 0]);
		
				
		var rectPadding =4;
		
		//添加矩形
		var rects = svg.selectAll("rect")
					   .data(dataset)
					   .enter()
					   .append("rect")
					   .attr("fill","steelblue")
					   .attr("transform","translate(" + padding.left + "," + padding.top + ")")
					   .attr("x",function(d,i){
						   return xScale(i) +rectPadding/2;
					   })
					   .attr("y",function(d){
						   var min = yScale.domain()[0];
						   return yScale(min);
					   })
					   .attr("width", xScale.rangeBand() - rectPadding )
					   .attr("height", function(d){
							return 0;
					   })
					   .transition()
					   .delay(function(d,i){
							return i * 200;
					   })
		               .duration(2000)
			           .ease("bounce")
		               .attr("y",function(d){
			                return yScale(d);
					   })
		               .attr("height", function(d){
							return height - padding.top - padding.bottom - yScale(d);
					   });
		
		//矩形上文字
		var texts = svg.selectAll("text")
					   .data(dataset)
					   .enter()
					   .append("text")
					   .attr("fill","white")
					   .attr("font-size","10px")
					   .attr("text-anchor","middle")
					   .attr("transform","translate(" + padding.left + "," + padding.top + ")")
					   .attr("x",function(d,i){
							return xScale(i) + rectPadding/2;
						} )
						.attr("y",function(d){
							var min = yScale.domain()[0];
							return yScale(min);
						})
						.attr("dx",function(){
							return (xScale.rangeBand() - rectPadding)/2;
						})
						.attr("dy",function(d){
							return 15;
						})
						.text(function(d){
							return d;
						})
						.transition()
						.delay(function(d,i){
							return i * 200;
						})
						.duration(2000)
						.ease("bounce")
						.attr("y",function(d){
							return yScale(d);
						});

						
		//定义x轴
		var xAxis = d3.svg.axis()
					  .scale(xScale)
					  .orient("bottom")
					  .tickFormat(function(d) { return district[d]; })
        
		//定义y轴
		var yAxis = d3.svg.axis()
					.scale(yScale)
					.orient("left");
		
		//添加x轴		
		svg.append("g")
		   .attr("class","axis")
           .attr("transform","translate(" + padding.left + "," + (height - padding.bottom) + ")")
           .call(xAxis)
           .selectAll("text")	
           .style("text-anchor", "end")
           .attr("dx", "-.8em")
           .attr("dy", ".15em")
           .attr("transform", function(d) {
               return "rotate(-65)" 
               });

		   
		//添加y轴
		svg.append("g")
		   .attr("class","axis")
           .attr("transform","translate(" + padding.left + "," + padding.top + ")")
           .call(yAxis);
	}
	
	function Node(name, value)
	{
	
		this.name = name;
		if(value!=null)
		this.value = value;
		else this.value=" ";
	}
	
	function getRad(d){
        return d*PI/180.0;
    }
	
	function getFlatternDistance(lat1,lng1,lat2,lng2){ 
	var f = getRad((lat1 + lat2)/2); 
	var g = getRad((lat1 - lat2)/2); 
	var l = getRad((lng1 - lng2)/2); 

	var sg = Math.sin(g); 
	var sl = Math.sin(l); 
	var sf = Math.sin(f); 

	var s,c,w,r,d,h1,h2; 
	var a = EARTH_RADIUS; 
	var fl = 1/298.257; 

	sg = sg*sg; 
	sl = sl*sl; 
	sf = sf*sf; 

	s = sg*(1-sl) + (1-sf)*sl; 
	c = (1-sg)*(1-sl) + sf*sl; 

	w = Math.atan(Math.sqrt(s/c)); 
	r = Math.sqrt(s*c)/w; 
	d = 2*w*a; 
	h1 = (3*r -1)/2/c; 
	h2 = (3*r +1)/2/s; 
	return d*(1 + fl*(h1*sf*(1-sg) - h2*(1-sf)*sg)); 
	} 
	
	function timeCompare(str1,str2){
		time1=new Date(str1);
		time2=new Date(str2);
		return (time1.getTime()-time2.getTime())/60000;
	}
	
	function randomRange(min, max) {
	    return Math.floor(Math.random() * (max - min + 1) + min);
	};

	function randomColor() {
	    var color = randomRange(0, 0xFFFFFF);
	    return '#' + ('000000' + color.toString(16)).slice(-6);
	};
	
$(function(){
	
	//title tab
	$(".tab-content").hide();
	$("#tab-title ul:first li:first").addClass("select").show();
	$(".tab-content:first").show();
	$("#statistics_graph").hide();
	
	$("#tab-title ul:first li").click(function() {
		 $(this).addClass("select").siblings().removeClass();
		 $("#tab-content > div").hide().eq($('#tab-title ul li').index(this)).show(); 
	});
	
	
	
	
	//get taxiid by page
	$.ajax({
		 type:"POST", 
         url:"taxi/taxiid", 
         dataType:"json",      
         contentType:"application/json; charset=utf-8",
         data:JSON.stringify(testData),
         success:function(data){
        	 $(".tab-content-span").each(function(i,item){
        		 $(item).text(data[i]);
        	 });
			 $(".tab-compared-span").each(function(i,item){
        		 $(item).text(data[i]);
        	 });
         }
	});
	
	//test
//	$.ajax({
//		type:"POST", 
//        url:"cluster/test/get", 
//        dataType:"json",      
//        contentType:"application/json; charset=utf-8",
//        success:function(data){
//        	for(var i in data){
//        		var myLatlng = new google.maps.LatLng(data[i].centerLatitude,data[i].centerLongtitude);
//        		 var marker = new MarkerWithLabel({
//					    position: myLatlng,
//					    map: map,
//					    raiseOnDrag: true,
//					    labelContent: data[i].size,
//					    labelAnchor: new google.maps.Point(10,35),
//					    labelClass: "labels", // the CSS class for the label
//					    labelInBackground: false,
//					    icon: POI
//					  });
//        	}
//        }
//	});
	
	//get all taxi_id to support fuzzy search
	$.ajax({
		type:"POST", 
        url:"taxi/alltaxiid", 
        dataType:"json",      
        contentType:"application/json; charset=utf-8",
        success:function(data){
        	for(var i in data){
        		allTaxiIds.push(data[i]);
        	}
        }
	});
	
	var $searchBox = $('#fuzzy-search-id');
	$searchBox.autocomplete({
		source: allTaxiIds,
		minLength: 3,
		max:3,
		width:100
		
		});
	
	$(".ui-helper-hidden-accessible").hide();
	//勾选了展示所有路径的复选框之后定义一个函数用于展示所有轨迹
	 function showAllTracks(){
		 similarityNode = [];
			similarityEdge = [];
			$.ajax({ 
		            type:"POST", 
		            url:"taxi/showAll", 
		            dataType:"json",      
		            contentType:"application/json; charset=utf-8", 
					data:JSON.stringify(testData),
		            success:function(data){
						clearPathAndMarker();
						var allTrajectory = data.fragment;
						similarityEdge = data.similarity;
						countCluster = 0;
						for(var a in allTrajectory){
						//	countCluster = countCluster+1;
							var singleTrajectory = allTrajectory[a];
							var lineSymbol = {
								    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
								  };
							var getRandomColor = randomColor();
							//if(countCluster<13){
							var poly = new google.maps.Polyline({
								    strokeColor: '#FF0000',
								    strokeOpacity: 1.0,
								    strokeWeight: 3,
								    map:map,
								    icons: [{
								        icon: newPoint,
								        offset: '100%'
								      }]
								  });
							//} 
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
				            		  //  title:content,
				            		    map:map
				            		});
								//	marker.addListener('click',showChord);
									markers.push(marker);
								//	var latlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude+0.02);
								//	var infowindow = new google.maps.InfoWindow({
									//    content: content
									//  });
									//infowindow.open(map,marker);
								}
							}
							trajectory.push(poly);
						}
						selectedComparedTaxiIds = [];
					 //	$(".demo--radio").attr("checked",false);
					}
			});
	 };
     function initMapCluster(triangleCoords,color) {
         map = new google.maps.Map(document.getElementById('map-canvas'), {
           zoom: 11,
           center: {lat: 31.230416, lng: 121.473701}, 
           mapTypeId: 'terrain'
         });

         // Define the LatLng coordinates for the polygon.
         var triangleCoords1 = [
             {lat: 31.774, lng: 121.190},
             {lat: 31.466, lng: 121.118},
             {lat: 31.321, lng: 121.757}
         ];

         // Construct the polygon.
         var bermudaTriangle = new google.maps.Polygon({
           paths: triangleCoords,
           strokeColor: color,
           strokeOpacity: 0.8,
           strokeWeight: 3,
           fillColor: color,
           fillOpacity: 0.35
         });
         bermudaTriangle.setMap(map);

         // Add a listener for the click event.
         bermudaTriangle.addListener('click', showArrays);

         infoWindow = new google.maps.InfoWindow;
       };
       function showArrays(event) {
           // Since this polygon has only one path, we can call getPath() to return the
           // MVCArray of LatLngs.
           var vertices = this.getPath();

           var contentString = '<b>Clustering result polygon</b><br>' +
               'Clicked location: <br>' + event.latLng.lat() + ',' + event.latLng.lng() +
               '<br>';

           // Iterate over the vertices.
           for (var i =0; i < vertices.getLength(); i++) {
             var xy = vertices.getAt(i);
             contentString += '<br>' + 'Coordinate ' + i + ':<br>' + xy.lat() + ',' +
                 xy.lng();
           }

           // Replace the info window's content and position.
           infoWindow.setContent(contentString);
           infoWindow.setPosition(event.latLng);

           infoWindow.open(map);
         };
         $("#mopsi_jump").click(function(){
 		    alert("clicked");
 		 	$.ajax({ 
 		 		  // type:"POST", 
 	             // url:"mopsi/mopsi_jump", 
 	             //  dataType:"json",      
 	             //  contentType:"application/json; charset=utf-8", 
 	            //   data:JSON.stringify(testData),
 		          success:function( ){
 		        	 window.location.href ="/WEB-INF/jsp/mopsi_jump.jsp";
 		          }
 			}) 
 		});
     	$("#mopsi_all").click(function(){ 
  			$.ajax({ 
  		            type:"POST", 
  		            url:"taxi/showAllMopsi", 
  		            dataType:"json",      
  		            contentType:"application/json; charset=utf-8", 
  					data:JSON.stringify(testData),
  		            success:function(data){
  		            	console.log(data);
  						clearPathAndMarker();
  						var allTrajectory = data.fragmentMopsi;
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
  								    map:map ,
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
  				            		    map:map 
  				            		});
  									marker.addListener('click',showChord);
  									markers.push(marker);
  									var latlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude+0.02);
  									var infowindow = new google.maps.InfoWindow({
  									    content: content
  									  });
  									infowindow.open(map ,marker);
  								}
  							}
  							trajectory.push(poly);
  						}
  					}
  			});
  	});
     	
     	$("#mopsi_seg").click(function(){
     		cluster_path="taxi/mopsi_seg";
    		$.ajax({ 
                type:"POST", 
                url:cluster_path, 
                dataType:"json",      
                contentType:"application/json; charset=utf-8",
                data:JSON.stringify(testData),
                success:function(data){
                //	alert(data );
               	console.log(data);
                }});
     	});
    	 
     	$("#mopsi_rep").click(function(){
     		cluster_path="taxi/mopsi_avg";
    		$.ajax({ 
                type:"POST", 
                url:cluster_path, 
                dataType:"json",      
                contentType:"application/json; charset=utf-8",
                data:JSON.stringify(testData),
                success:function(data){
                //	alert(data );
               	console.log(data);
            	clearPathAndMarker();
            	clearRedPart();
            	map.setZoom(10);
            	//console.log(data.length);
				//similarityEdge = data.similarity;
				var countIt = 0;
				for(var a in data){//对于每条代表轨迹
					var singleTrajectory = data[a];
					var lineSymbol = {
						    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
						  };
					var getRandomColor = randomColor();
					var poly = new google.maps.Polyline({
						    title:a,
						    Colorchanged:0,//0代表未被改变
						  //  strokeColor: getRandomColor,
						    strokeColor: '#000000',
						    strokeOpacity: 1.0,
						    strokeWeight: 3,
						    map:map,
						    icons: [{
						        icon: lineSymbol,
						        offset: '100%'
						      }]
						  });
					//console.log(singleTrajectory);
					for(var b in singleTrajectory.points){//对于每条代表轨迹的每个点
						var myLatlng = new google.maps.LatLng(singleTrajectory.points[b].x,singleTrajectory.points[b].y);
						
					//	console.log(singleTrajectory.points[b].x+":"+singleTrajectory.points[b].y);
						if(a==0) map.setCenter(myLatlng);
						var path = poly.getPath();
						path.push(myLatlng);
						 if(b==0){
							 
							var content = a;
							//similarityNode.push(content);
					 	marker = new google.maps.Marker({
		            		    position: myLatlng,
		            		    title:a,
		            		    map:map,
		            		  //  icon:newPoint
		            		});
					 	 
						  marker.addListener('click',function(){//点击之后触发的事件
								// alert(this.title);
							     //点击之后将对应的Poly放入数组并清除
							    // clickedRep.push(poly);
							    console.log("clicked segment is:"+this.title);
							     poly.Colorchanged=1;//标记为该线段被
							     var color_poly = poly.strokeColor;//点击线段的原本颜色
							     poly.strokeColor = '#FF0000';
							     console.log(poly.strokeColor +";"+color_poly);
								 showSegment(this.title,data,color_poly,eps,minlns,algor);
								 console.log(trajectory);
							 });
							markers.push(marker);
							repMarker.push(marker);
						//	var latlng = new google.maps.LatLng(singleTrajectory.points[b].x,singleTrajectory.points[b].y+0.02);
						/*var infowindow = new google.maps.InfoWindow({
						    content: content
							  });
							infowindow.open(map,marker);
							*/
					 	}
					}
					trajectory.push(poly);
					repTrajectory.push(poly);
				} 
                }});
     	});
	//修改:增加所有轨迹在感兴趣区域
		//将聚类效果用D3和leaflet的合并版画出来
		$("#voronoi").click(function(){
			//window.open('/TaxiCluster/show/leaf.html');
			 
			//现在需要把html的内容转化到SVG上面
	       $.ajax({ 
	    	    type:"POST", 
	            url:"taxi/raw_Cluster", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8", 
				data:JSON.stringify(testData),
	            success:function(data){
	            	 var clusterData = data.clusterResult;
	            	 var rawData = data.rawPoints;
	        
	        var array = new Array();
	        //this part is showing the clustering result point
	        for(var i=0;i<clusterData.length;i++){
	    	   console.log("data:"+clusterData[i].centerLatitude);
	    	   var lat =  clusterData[i].centerLatitude ;
	    	   var lon =  clusterData[i].centerLongtitude ;
	    	   console.log("lat:"+lat);
	    	   console.log("lon:"+lon);
	    	   array[i]= {id:i,latitude:lat,longitude:lon,name:'Voronoi',type:'Voronoi',color:'fc8233'};
	       }  
	       for(var j=clusterData.length;j<rawData.length;j++){
	      //  console.log("length"+rawData.length);
	       // for(var j=0;j<rawData.length;j++){
	        //	console.log(rawData[j]);
	    	   var lat_raw = rawData[j].latitude;
	    	   var lon_raw = rawData[j].longtitude;
	    	   console.log(lat_raw);
	    	   array[j] = {id:j,latitude:lat_raw,longitude:lon_raw,name:'Voronoi',type:'Voronoi',color:'00529d'};
	       }
	            	showD3Leaflet(array);
	            }});  
		 
		});
		//voronoi mopsi performance
		$("#voronoi_Mopsi").click(function(){
			//window.open('/TaxiCluster/show/leaf.html');
			
			testData.user  = window.user; 
			//现在需要把html的内容转化到SVG上面
	       $.ajax({ 
	    	    type:"POST", 
	            url:"taxi/vor_Mopsi", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8", 
				data:JSON.stringify(testData),
	            success:function(data){
	            	 var clusterData = data.clusterResult;
	            	 var rawData = data.rawPoints;
	        
	        var array = new Array();
	        //this part is showing the clustering result point
	        for(var i=0;i<clusterData.length;i++){
	    	   console.log("data:"+clusterData[i].centerLatitude);
	    	   var lat =  clusterData[i].centerLatitude ;
	    	   var lon =  clusterData[i].centerLongtitude ;
	    	   console.log("lat:"+lat);
	    	   console.log("lon:"+lon);
	    	   array[i]= {id:i,latitude:lat,longitude:lon,name:'Voronoi',type:'Voronoi',color:'fc8233'};
	       }  
	       for(var j=clusterData.length;j<rawData.length;j++){
	      //  console.log("length"+rawData.length);
	       // for(var j=0;j<rawData.length;j++){
	        //	console.log(rawData[j]);
	    	   var lat_raw = rawData[j].latitude;
	    	   var lon_raw = rawData[j].longtitude;
	    	   console.log(lat_raw);
	    	   array[j] = {id:j,latitude:lat_raw,longitude:lon_raw,name:'Voronoi',type:'Voronoi',color:'00529d'};
	       }
	            	showD3Leaflet(array);
	            }});  
		 
		});
		
		$("#traclus-rep").click(function(){
		 
			ClusterData.partition_type =window.cp;
			var type = window.select_algor;
	    	ClusterData.cluster_type =window.select_algor;
		 	console.log("cp is"+window.cp+"and cluster is:"+window.select_algor);
			//根据不同的cluster类型来传入不同参数
			if(type=="kmeans"){
				var k_value = $("#k").val();
				var k_iterator = $("#k-iterator").val();
				ClusterData.k=k_value;
			    ClusterData.iterator = k_iterator;
			}
			else if(type=="dbscan"){
				var eps=$("#eps").val();
				var minpts = $("#minpts").val();
			    ClusterData.eps = eps;
			    ClusterData.minpts = minpts;
			}else if(type = "hierarchical"){
				var n=$("#n").val(); 
				ClusterData.n = n;
			}
			
			/*var eps;
			var minpts;
			var algor = window.select_algor;
			if( algor!=("dbscan")){
				eps=-1;
				minpts=-1;
			}
			else{
				  eps=$("#eps").val();
				  minpts = $("#minpts").val();
			}
			console.log("minlns is:"+minpts);
			console.log("algor is:"+window.select_algor);
			
     		  ReData.eps=eps;
			  ReData.minpts= minpts;
			  ReData.partition_type = window.cp;
			  ReData.cluster_type =algor;*/
			clearCluster();
			clearRedPart();
           //展示traclus得到的代表轨迹
			$.ajax({ 
	            type:"POST", 
	            url:"taxi/traclus_rep", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8", 
				data:JSON.stringify(ClusterData),
	            success:function(data){
	            	clearPathAndMarker();
	            	clearRedPart();
	            	map.setZoom(10);
	            	//console.log(data.length);
					similarityEdge = data.similarity;
					var countIt = 0;
					for(var a in data){
						var singleTrajectory = data[a];
						var lineSymbol = {
							    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
							  };
						var getRandomColor = randomColor();
						var poly = new google.maps.Polyline({
							    title:a,
							    Colorchanged:0,//0代表未被改变
							  //  strokeColor: getRandomColor,
							    strokeColor: '#000000',
							    strokeOpacity: 1.0,
							    strokeWeight: 3,
							    map:map,
							    icons: [{
							        icon: lineSymbol,
							        offset: '100%'
							      }]
							  });
						//console.log(singleTrajectory);
						for(var b in singleTrajectory.points){
							var myLatlng = new google.maps.LatLng(singleTrajectory.points[b].x,singleTrajectory.points[b].y);
							
						//	console.log(singleTrajectory.points[b].x+":"+singleTrajectory.points[b].y);
							if(a==0) map.setCenter(myLatlng);
							var path = poly.getPath();
							path.push(myLatlng);
							 if(b==0){
								 
								var content = a;
								//similarityNode.push(content);
						 	marker = new google.maps.Marker({
			            		    position: myLatlng,
			            		    title:a,
			            		    map:map,
			            		  //  icon:newPoint
			            		});
						 	 
							  marker.addListener('click',function(){//点击之后触发的事件
									// alert(this.title);
								     //点击之后将对应的Poly放入数组并清除
								    // clickedRep.push(poly);
								    console.log("clicked segment is:"+this.title);
								     poly.Colorchanged=1;//标记为该线段被
								     var color_poly = poly.strokeColor;//点击线段的原本颜色
								     poly.strokeColor = '#FF0000';
								     console.log(poly.strokeColor +";"+color_poly);
									 showSegment(this.title,data,color_poly,eps,minlns,algor);
									 console.log(trajectory);
								 });
								markers.push(marker);
								repMarker.push(marker);
							//	var latlng = new google.maps.LatLng(singleTrajectory.points[b].x,singleTrajectory.points[b].y+0.02);
							/*var infowindow = new google.maps.InfoWindow({
							    content: content
								  });
								infowindow.open(map,marker);
								*/
						 	}
						}
						trajectory.push(poly);
						repTrajectory.push(poly);
					} 
				}
		}); 
		});
//	$("#all_click").click(function(){
		$("#showAllTrack").click(function(){
		//get selected ids		
		//selectedComparedTaxiIds=54230;
	 	//testData.taxiId =  54230;
		similarityNode = [];
		similarityEdge = [];
		$.ajax({ 
	            type:"POST", 
	            url:"taxi/showAll", 
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
							    map:map,
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
			            		    map:map
			            		});
								marker.addListener('click',showChord);
								markers.push(marker);
								var latlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude+0.02);
								var infowindow = new google.maps.InfoWindow({
								    content: content
								  });
								infowindow.open(map,marker);
							}
						}
						trajectory.push(poly);
					}
					selectedComparedTaxiIds = [];
				  	$(".demo--radio").attr("checked",false);
				}
		});
		 
}
	);
	//以上：修改增加所有轨迹
		//获取对应出租车ID的频率
		function getFrequency(id){
			 $.ajax({ 
		            type:"POST", 
		            url:"taxi/frequency", 
		            dataType:"json",      
		            contentType:"application/json; charset=utf-8", 
		            data:JSON.stringify(testData),
		            success:function(data){ 
		            	if(data.length==0){
		            		alert("无此id");
		            		return -1;
		            	}
		            	return 10;
		            } 
		         }); 
		};
	//添加判断是否修改了输入框
	 $("#fuzzy-search-id").click(function(){
			//频率
	 var flag = false;
	 document.getElementById('fuzzy-search-id').onfocus=function(){flag=true;};
		 //  if(!IsModified())
		
		//以上为频率
	})	;  
	//以上判断
	 //层次聚类后展示所有轨迹
	 
	//show single path
	$("#show_single_data").click(function(){
	//
		
		var taxiId = $("#fuzzy-search-id").val();
	 
		//展示出租车频率
		document.getElementById('frequency').style.display = 'block';
		         var tableFrequency =  document.getElementById('frequency');
	 
		
		if(taxiId=="")
			taxiId = $("input[name='demo-radio']:checked").next().next().text();
		testData.taxiId = taxiId;
		 $.ajax({ 
			
	            type:"POST", 
	            url:"taxi/single", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8", 
	            data:JSON.stringify(testData),
	            success:function(data){ 
	            	if(data.length==0){
	            		alert("无此id");
	            		return;
	            	}
					//添加频率信息
	            	var tab = document.getElementById("frequency_table");
	            	if(tab.rows.length==1)
	            	 $("#tr1").after('<tr><td>'+taxiId+'</td><td>'+data.length+'</td></tr>'); 
	            	else{
	            		//删除最后一行再加入新的一行
	            		tab.deleteRow(1);
	            		 $("#tr1").after('<tr><td>'+taxiId+'</td><td>'+data.length+'</td></tr>'); 
	            	}
	            	//以上添加频率信息
					clearPathAndMarker(); 
					
					var arrowSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};
	    
					poly = new google.maps.Polyline({
								strokeColor: '#000000',
								strokeOpacity: 1.0,
								strokeWeight: 3,
								icons:[
									{
									icon:arrowSymbol,
									offset: '100%'
									}
								],
								map:map
								});
	            	
	            	
	            	var totalSpeed = 0;
	            	var pathCount = 1,noLoadCount,onLoadCount;
	            	var totalDistance = 0;
	            	var count = 0;
	            	var pathDistance=0;
	            	maxSpeed=0,minSpeed=200,averageSpeed=0;
	            	
	            	//begins to mark on map
	            	for (var o in data	){
	            		count++;
	            		var myLatlng = new google.maps.LatLng(data[o].latitude,data[o].longtitude);
						if(o==0) map.setCenter(myLatlng);
	            		var currentMarker;
	            		if(data[o].state=='1'){
	            			currentMarker = new google.maps.Marker({
		            		    position: myLatlng,
		            		    title:data[o].time,
		            		  //  animation: google.maps.Animation.DROP,
		            		    icon:onLoadTaxi,
		            		    map:map
		            		});
	            		}
	            		else currentMarker = new google.maps.Marker({
	            		    position: myLatlng,
	            		    title:data[o].time,
	            		    animation: google.maps.Animation.DROP,
	            		    icon:noLoadTaxi,
	            		    map:map
	            		});
	            		currentMarker.addListener('click',showForceGraph);
	            		markers.push(currentMarker);
	            		
	            		var path = poly.getPath();
	            		path.push(myLatlng);
	            		animateArrow(poly);
	            		
	            		//begins to calculate
	            		var speed=parseInt(data[o].speed);
	            		
	            		if(speed>maxSpeed) maxSpeed = speed;
	            		if(speed<minSpeed&&speed!=0) minSpeed = speed;
	            		totalSpeed = totalSpeed+speed;
	            		
	            		if(count==1) turningPoints.push(data[o]);
	            		
	            		if(count!=1)
	            			{
	            			if(currentMarker.position.lat()==previousMarker.position.lat()&&currentMarker.position.lng()==previousMarker.position.lng()) continue;
							var fragDistance=getFlatternDistance(
									currentMarker.position.lat(),
									currentMarker.position.lng(),
									previousMarker.position.lat(),
									previousMarker.position.lng());
	            			totalDistance = totalDistance+ fragDistance;
	            			pathDistance=parseFloat(pathDistance)+parseFloat(fragDistance);
	            			if(data[o-1].state!=data[o].state) {
	            				if(pathDistance>longestDistance)longestDistance=pathDistance;
	            				if(pathDistance<shortestDistance)shortestDistance=pathDistance;
	            				pathCount++;
	            				if(turningPoints[turningPoints.length-1].time!=data[o-1].time)turningPoints.push(data[o-1]);
	            				turningPoints.push(data[o]);
	            				pathDistance=0;
	            				}
	            			}
	            		previousMarker = currentMarker;	
	            	}
	            	
	            	//push to nodes
	            	if(pathCount%2==1){
	            		if(data[0].state==0){
	            			onLoadCount=(pathCount-1)/2;
	            			noLoadCount=onLoadCount+1;
	            		}
	            		else{
	            			noLoadCount=(pathCount-1)/2;
	            			onLoadCount=noLoadCount+1;
	            		}
	            	}
	            	else noLoadCount=onLoadCount=pathCount/2;
	            	nodes=[];
					
            		nodes.push(new Node("id",data[0].taxi_Id));
            		nodes.push(new Node("speed",null));
            		nodes.push(new Node("total trips",pathCount+" times"));
            		nodes.push(new Node("total geo-points",data.length));
            		nodes.push(new Node("total distance",new Number(totalDistance).toFixed(2)+"km"));
            		
            		nodes.push(new Node("max speed",maxSpeed+"km/h"));
            		nodes.push(new Node("min speed",minSpeed+"km/h"));
            		nodes.push(new Node("avg speed",parseInt(totalSpeed/data.length)+"km/h"));
            		nodes.push(new Node("no load",noLoadCount+" times"));
            		nodes.push(new Node("on load",onLoadCount+" times"));
            		nodes.push(new Node("total time",new Number(timeCompare(data[data.length-1].time,data[0].time)).toFixed(2)+" min"));
            		nodes.push(new Node("max trip",new Number(longestDistance).toFixed(2)+"km"));
            		nodes.push(new Node("min trip",new Number(shortestDistance).toFixed(2)+"km"));
            		
            		
	            } 
	         }); 
		 $("#fuzzy-search-id").val(null);
	});
	
	
	$("#show_single_data_next_page").click(function(){
		$(".demo--radio").attr("checked",false);
		singlePathIndex++;
		testData.index = singlePathIndex;
		$.ajax({
		 type:"POST", 
         url:"taxi/taxiid", 
         dataType:"json",      
         contentType:"application/json; charset=utf-8",
         data:JSON.stringify(testData),
         success:function(data){
        	 $(".tab-content-span").each(function(i,item){
        		 $(item).text(data[i]);
        	 });
         }
	});
		
	});
	
	$("#show_single_data_previous_page").click(function(){
		if(singlePathIndex==0) {alert("first page");return;}
		$(".demo--radio").attr("checked",false);
		singlePathIndex--;
		testData.index = singlePathIndex;
		$.ajax({
		 type:"POST", 
         url:"taxi/taxiid", 
         dataType:"json",      
         contentType:"application/json; charset=utf-8",
         data:JSON.stringify(testData),
         success:function(data){
        	 $(".tab-content-span").each(function(i,item){
        		 $(item).text(data[i]);
        	 });
         }
	});
	});
	
	$("#show_compared_data_next_page").click(function(){
		
		//set checked false
		$(".demo--radio").attr("checked",false);
		
		comparedPathIndex++;
		testData.index = comparedPathIndex;
		//load next page
		$.ajax({
		 type:"POST", 
         url:"taxi/taxiid", 
         dataType:"json",      
         contentType:"application/json; charset=utf-8",
         data:JSON.stringify(testData),
         success:function(data){
        	 $(".tab-compared-span").each(function(i,item){
        		 $(item).text(data[i]);
        	 });
         }
	});	
	});
	
	$("#show_compared_data_previous_page").click(function(){
		if(comparedPathIndex==0) {alert("first page");return;}
		$(".demo--radio").attr("checked",false);
		comparedPathIndex--;
		testData.index = comparedPathIndex;
		$.ajax({
		 type:"POST", 
         url:"taxi/taxiid", 
         dataType:"json",      
         contentType:"application/json; charset=utf-8",
         data:JSON.stringify(testData),
         success:function(data){
        	 $(".tab-compared-span").each(function(i,item){
        		 $(item).text(data[i]);
				 for(var o in selectedComparedTaxiIds){
					 if(selectedComparedTaxiIds[o]==data[i]) {
						 $(item).prevUntil("input").attr("checked",true);
					 }
				 }
        	 });
         }
	});
	});
    function showD3Info(DbscanData){
    	$.ajax({ 
		      type:"POST", 
	            url:"taxi/rawData", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8", 
				data:JSON.stringify(DbscanData),
	            success:function(data){
	            	clearPathAndMarker();
	        		var arrowSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};
	        		var allTrajectory = data.rawFragment;
					console.log("allTracks:"+allTrajectory.length);
					similarityEdge = data.similarity;
					
					var arrowSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};
					for(var a in allTrajectory){
						var singleTrajectory = allTrajectory[a];
					poly = new google.maps.Polyline({
								strokeColor: '#000000',
								strokeOpacity: 1.0,
								strokeWeight: 3,
								icons:[
									{
									icon:arrowSymbol,
									offset: '100%'
									}
								],
								map:map
								});
	            	
	             	var totalSpeed = 0;
	            	var pathCount = 1,noLoadCount,onLoadCount;
	            	var totalDistance = 0;
	            	var count = 0;
	            	var pathDistance=0;
	            	maxSpeed=0,minSpeed=200,averageSpeed=0;
	            	 
	            	//begins to mark on map
	            	for (var o in singleTrajectory){
	            		count++;
	            		var myLatlng = new google.maps.LatLng(data[o].latitude,data[o].longtitude);
						if(o==0) map.setCenter(myLatlng);
	            		var currentMarker;
	            		if(data[o].state=='1'){
	            			currentMarker = new google.maps.Marker({
		            		    position: myLatlng,
		            		    title:data[o].time,
		            		  //  animation: google.maps.Animation.DROP,
		            		    icon:onLoadTaxi,
		            		    map:map
		            		});
	            		}
	            		else currentMarker = new google.maps.Marker({
	            		    position: myLatlng,
	            		    title:data[o].time,
	            		    animation: google.maps.Animation.DROP,
	            		    icon:noLoadTaxi,
	            		    map:map
	            		});
	            		currentMarker.addListener('click',showForceGraph);
	            		markers.push(currentMarker);
	            		
	            		var path = poly.getPath();
	            		path.push(myLatlng);
	            		animateArrow(poly);
	            		
	            		//begins to calculate
	            		var speed=parseInt(data[o].speed);
	            		
	            		if(speed>maxSpeed) maxSpeed = speed;
	            		if(speed<minSpeed&&speed!=0) minSpeed = speed;
	            		totalSpeed = totalSpeed+speed;
	            		
	            		if(count==1) turningPoints.push(data[o]);
	            		
	            		if(count!=1)
	            			{
	            			if(currentMarker.position.lat()==previousMarker.position.lat()&&currentMarker.position.lng()==previousMarker.position.lng()) continue;
							var fragDistance=getFlatternDistance(
									currentMarker.position.lat(),
									currentMarker.position.lng(),
									previousMarker.position.lat(),
									previousMarker.position.lng());
	            			totalDistance = totalDistance+ fragDistance;
	            			pathDistance=parseFloat(pathDistance)+parseFloat(fragDistance);
	            			if(data[o-1].state!=data[o].state) {
	            				if(pathDistance>longestDistance)longestDistance=pathDistance;
	            				if(pathDistance<shortestDistance)shortestDistance=pathDistance;
	            				pathCount++;
	            				if(turningPoints[turningPoints.length-1].time!=data[o-1].time)turningPoints.push(data[o-1]);
	            				turningPoints.push(data[o]);
	            				pathDistance=0;
	            				}
	            			}
	            		previousMarker = currentMarker;	
	            	}
	            	
	            	//push to nodes
	            	if(pathCount%2==1){
	            		if(data[0].state==0){
	            			onLoadCount=(pathCount-1)/2;
	            			noLoadCount=onLoadCount+1;
	            		}
	            		else{
	            			noLoadCount=(pathCount-1)/2;
	            			onLoadCount=noLoadCount+1;
	            		}
	            	}
	            	else noLoadCount=onLoadCount=pathCount/2;
	            	nodes=[];
					
            		nodes.push(new Node("id",data[0].taxi_Id));
            		nodes.push(new Node("speed",null));
            		nodes.push(new Node("total trips",pathCount+" times"));
            		nodes.push(new Node("total geo-points",data.length));
            		nodes.push(new Node("total distance", totalDistance +"km"));
            		
            		nodes.push(new Node("max speed",maxSpeed+"km/h"));
            		nodes.push(new Node("min speed",minSpeed+"km/h"));
            		var avgSpeed = totalSpeed/data.length;
            		nodes.push(new Node("avg speed",Math.round(avgSpeed*100)/100+"km/h"));
            		nodes.push(new Node("no load",noLoadCount+" times"));
            		nodes.push(new Node("on load",onLoadCount+" times"));
            		nodes.push(new Node("total time",new Number(avgSpeed).toFixed(2)+" min"));
            		nodes.push(new Node("max trip",new Number(longestDistance).toFixed(2)+"km"));
            		nodes.push(new Node("min trip",new Number(shortestDistance).toFixed(2)+"km"));
					}
					}});
    };
    function testD3(title,DbscanData){
    	//根据title获取该线段的所有信息
    	DbscanData.taxi_order = title;
    	$.ajax({ 
		      type:"POST", 
	            url:"taxi/D3_info", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8", 
				data:JSON.stringify(DbscanData),
	            success:function(data){
    	//title即为点击线段的序列号
	    //push to nodes
    	nodes=[];
		var id = data.taxiId;
		var cPointNum = data.CpointNum;
		var maxSpeed = data.maxSpeed;
		var minSpeed = data.minSpeed;
		var avgSpeed = Math.round(data.avgSpeed * 100) / 100; 
		
		var totalDis = Math.round(data.totalDis * 100) / 100; 
		var totalTime = Math.round((data.totalTime / 60000)*100) / 100;
		nodes.push(new Node("id",id));
	 //	nodes.push(new Node("speed",null));
	// 	nodes.push(new Node("total trips",1+" times"));
		nodes.push(new Node("total characteristic-points",cPointNum));
		nodes.push(new Node("total distance",totalDis+"km"));
		
		nodes.push(new Node("max speed",maxSpeed+"km/h"));
		nodes.push(new Node("min speed",minSpeed+"km/h"));
		nodes.push(new Node("avg speed",avgSpeed+"km/h"));
	//	 nodes.push(new Node("no load",1+" times"));
	//	 nodes.push(new Node("on load",1+" times"));
		nodes.push(new Node("total time",totalTime+" min"));
	//	 nodes.push(new Node("max trip",1+"km"));
	//	 nodes.push(new Node("min trip",1+"km"));
		showForceGraphNew();
	            }});
    }
    function ExpMarkerRawData_seg_cluster(cp,type){//展示聚类效果
    	var voronoi_submit =  document.getElementById("voronoi"); 
		 if(voronoi_submit.style.display=='block'||voronoi_submit.style.display==''){
			 voronoi_submit.style.display="none";
		  }
		 var reCluster = document.getElementById("representative_info");
		  	if(reCluster.style.display='none'){
				reCluster.style.display="block";
		  	}
    	 var representative = document.getElementById("traclus-rep");
		 representative.style.display="block";
	//	DbscanData.partition_type = cp;
		//DbscanData.cluster_type = type;
    	ClusterData.partition_type = cp;
    	ClusterData.cluster_type = type;
		console.log("cp is"+cp+"and cluster is:"+type);
		//根据不同的cluster类型来传入不同参数
		if(type=="kmeans"){
			var k_value = $("#k").val();
			var k_iterator = $("#k-iterator").val();
			ClusterData.k=k_value;
		    ClusterData.iterator = k_iterator;
		}
		else if(type=="dbscan"){
			var eps=$("#eps").val();
			var minpts = $("#minpts").val();
		    ClusterData.eps = eps;
		    ClusterData.minpts = minpts;
		}else if(type = "hierarchical"){
			var n=$("#n").val(); 
			ClusterData.n = n;
		}
		else{
			var m=$("#m").val();
			var nx = $("#nx").val();
			var ny = $("#ny").val();
			ClusterData.m = m;
			ClusterData.nx = nx;
			ClusterData.ny = ny;
		}
		 var mse = document.getElementById("mse_div");
		 mse.style.display = "block";
		$.ajax({ 
		      type:"POST", 
	            url:"taxi/exper", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8", 
				data:JSON.stringify(ClusterData),
	            success:function(data){
	            	 GetMSE("agnes",window.cp);
					clearPathAndMarker();
					 clearCluster();
					 clearTheRed();
					 clearRedPart();
					var allTrajectory = data.lineList;
					var clusterNumber = data.ClusterNumber;
					console.log("allTracks:"+allTrajectory.length);
					//similarityEdge = data.similarity;
		            var color_Array = new Array();
		            for(var c =0;c<clusterNumber+10;c++){
		           	    color_Array[c] = randomColor();
		            	//   color_Array[c] ="#000000"
		            }
		            console.log("clusterNumber:"+clusterNumber);
					var count = 0;
					var arrowSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};
					for(var a in allTrajectory){
						var singleTrajectory = allTrajectory[a];
						var lineSymbol = {
							    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
							  };
						var color_line;
					/*	if(singleTrajectory.clusterId==1){
							color_line = "#FF0000";
						}
						else if(singleTrajectory.clusterId==2){
							color_line = "#00FF00";
						}
						else if(singleTrajectory.clusterId==3){
							color_line = "#0000FF";
						}else{
							color_line = "##33FF33";
						}
						*/
						color_line = color_Array[singleTrajectory.clusterId+1];
						//console.log("the clusterid is:"+singleTrajectory.clusterId);
						var poly = new google.maps.Polyline({
							    title:a,
							    //strokeColor: "#000000",
							    strokeColor: color_line,
							    strokeOpacity: 2.0,
							    strokeWeight: 3,
							    map:map,
							    icons: [{
							       // icon: lineSymbol,
							    	icon:arrowSymbol ,
							        offset: '100%'
							      }]
							  }); 
						 //start点
							var myLatlng = new google.maps.LatLng(singleTrajectory.s.x,singleTrajectory.s.y);
							if(a==0) map.setCenter(myLatlng);
							var path = poly.getPath();
							path.push(myLatlng);
								currentMarker = new google.maps.Marker({
			            		    position: myLatlng,
			            		    title:a,
			            		    icon:newPoint,
			            		    map:map,
			            		    track:singleTrajectory
			            		});
								ClusterMarkers.push(currentMarker);
			            		//end点
			            		 myLatlng = new google.maps.LatLng(singleTrajectory.e.x,singleTrajectory.e.y);
									if(a==0) map.setCenter(myLatlng);
									var path = poly.getPath();
									path.push(myLatlng);
									Cluster_trajectory.push(poly);
					            	//	markers.push(currentMarker);
						//}
						//trajectory.push(poly);
						
						var path = poly.getPath();
	            		//path.push(myLatlng);
	            		animateArrow(poly);
					}
					selectedComparedTaxiIds = [];
					//$(".demo--radio").attr("checked",false);
				}
	});
    };
    function ExpMarkerRawData_seg(cp,type){
		path="taxi/raw_data_seg";
		DbscanData.partition_type = cp;
		DbscanData.cluster_type = type;
		console.log("cp is"+cp+"and cluster is:"+type);
		$.ajax({ 
		      type:"POST", 
	            url:"taxi/raw_data_seg", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8", 
				data:JSON.stringify(DbscanData),
	            success:function(data){
					clearPathAndMarker();
					var allTrajectory = data.lineList;
					console.log("allTracks:"+allTrajectory.length);
					similarityEdge = data.similarity;
					var count = 0;
					var arrowSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};
					for(var a in allTrajectory){
						var singleTrajectory = allTrajectory[a];
						var lineSymbol = {
							    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
							  };
						var getRandomColor = randomColor();
						var poly = new google.maps.Polyline({
							    title:a,
							    strokeColor: "#000000",
							    strokeOpacity: 2.0,
							    strokeWeight: 3,
							    map:map,
							    icons: [{
							       // icon: lineSymbol,
							    	icon:arrowSymbol ,
							        offset: '100%'
							      }]
							  });
						 poly.addListener('click',function(){//点击之后触发的事件
							// alert(this.title);
							 testD3(this.title,DbscanData);
						 });
						//	for(var a in allTrajectory){
							 
					 	for(var b in singleTrajectory){
							 //start点
							var myLatlng = new google.maps.LatLng(b.Latitude,b.Longtitude);
							if(a==0) map.setCenter(myLatlng);
							var path = poly.getPath();
							path.push(myLatlng);
								currentMarker = new google.maps.Marker({
			            		    position: myLatlng,
			            		    title:a,
			            		    map:map,
			            		    track:singleTrajectory
			            		});
								ClusterMarkers.push(currentMarker);
			            		 
							
						 }
						trajectory.push(poly);
						var path = poly.getPath();
	            		path.push(myLatlng);
	            		animateArrow(poly);
	            		count = count+1;//对轨迹数量进行计算
					}
					console.log("there are "+count+" segments")
					selectedComparedTaxiIds = [];
					//$(".demo--radio").attr("checked",false);
				}
	});
    };
	function MarkerRawData(cp,type){//将用于聚类的原始点标出来，需要连线
		path="taxi/rawData";
		DbscanData.partition_type = cp;
		DbscanData.cluster_type = type;
		console.log("cp is"+cp+"and cluster is:"+type);
		$.ajax({ 
		      type:"POST", 
	            url:"taxi/rawData", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8", 
				data:JSON.stringify(DbscanData),
	            success:function(data){
					clearPathAndMarker();
					var allTrajectory = data.rawFragment;
					console.log("allTracks:"+allTrajectory.length);
					similarityEdge = data.similarity;
					var count = 0;
					var arrowSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};
					for(var a in allTrajectory){
						var singleTrajectory = allTrajectory[a];
						var lineSymbol = {
							    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
							  };
						var getRandomColor = randomColor();
						var poly = new google.maps.Polyline({
							    title:a,
							    strokeColor: "#000000",
							    strokeOpacity: 2.0,
							    strokeWeight: 1,
							    map:map,
							    icons: [{
							       // icon: lineSymbol,
							    	icon:arrowSymbol ,
							        offset: '100%'
							      }]
							  });
						 poly.addListener('click',function(){//点击之后触发的事件
							// alert(this.title);
							 testD3(this.title,DbscanData);
						 });
						//	for(var a in allTrajectory){
							 
						for(var b in singleTrajectory){
							var myLatlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude);
							if(a==0) map.setCenter(myLatlng);
							var path = poly.getPath();
							path.push(myLatlng);
							 if(b==0||b==(singleTrajectory.length-1)){
								currentMarker = new google.maps.Marker({
			            		    position: myLatlng,
			            		    title:a,
			            		    icon:newPoint,
			            		    map:map,
			            		    track:singleTrajectory
			            		});
								 }
							//open a info window
							 	currentMarker.addListener('click',function(){//点击线段后加粗变色
							 		var obj = $(this)[0];
							 		var key = "title";
							 		var value = obj[key]; //value就是第几条轨迹
							 	//	clearPathAndMarker();
							 		$.ajax({ 
									      type:"POST", 
								            url:"taxi/rawData", 
								            dataType:"json",      
								            contentType:"application/json; charset=utf-8", 
											data:JSON.stringify(DbscanData),
								            success:function(data){
											//	clearPathAndMarker();
								            //	clearRedPart();
								            	//这里应该是清除之前红色的部分，需要重写函数类似clearPathAndMarker()
												var allTrajectory = data.rawFragment;
								             	console.log("all in insert"+allTrajectory.length); 
								             	for(var a in allTrajectory){
													var singleTrajectory = allTrajectory[a];
								    var  singleTrajectory = allTrajectory[value];
									var arrowSymbol = {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW};//修改
							 
										var lineSymbol = {
											    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
											  };
										var getRandomColor = randomColor();
										if(a==value){
										var poly = new google.maps.Polyline({
											    id:a,
											    strokeColor: "#FF0000",
											    strokeOpacity: 2.0,
											    strokeWeight: 6,
											    map:map,
											    icons: [{
											       // icon: lineSymbol,
											    	icon:arrowSymbol ,
											        offset: '100%'
											      }]
											  });
										trajectory.push(poly);
									//	redPoly.push(poly);
										}else{
											var poly = new google.maps.Polyline({
											    id:a,
											    strokeColor: "#000000",
											    strokeOpacity: 1.0,
											    strokeWeight: 1,
											    map:map,
											    icons: [{
											       // icon: lineSymbol,
											    	icon:arrowSymbol ,
											        offset: '100%'
											      }]
											  });
											trajectory.push(poly);
										}
										for(var b in singleTrajectory){
											var myLatlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude);
											if(a==0) map.setCenter(myLatlng);
											var path = poly.getPath();
											path.push(myLatlng);
									 
												currentMarker = new google.maps.Marker({
							            		    position: myLatlng,
							            		    title:"line"+a,
							            		    icon:newPoint,
							            		    map:map
							            		});
										 
							            		markers.push(currentMarker);
										}
										
										var path = poly.getPath();
					            		path.push(myLatlng);
					            		animateArrow(poly);
								            }
								    trajectory.push(poly);
									selectedComparedTaxiIds = [];
									 
							 	//	console.log($(this)[0]);
							 	//	console.log(value);
								            }
								            });
							 	});
			            		markers.push(currentMarker);
						}
						trajectory.push(poly);
						var path = poly.getPath();
	            		path.push(myLatlng);
	            		animateArrow(poly);
	            		count = count+1;//对轨迹数量进行计算
					}
					console.log("there are "+count+" segments")
					selectedComparedTaxiIds = [];
					//$(".demo--radio").attr("checked",false);
				}
	});
	};
	 window.cp="";
	 $("#btn-cp").click(function(){ 
		 
		 var sga_cp =  document.getElementById("sga_panel"); 
		 if(sga_cp.style.display=='block'){
		  sga_cp.style.display="none";
		  }
		 var voronoi_submit =  document.getElementById("voronoi"); 
		 if(voronoi_submit.style.display=='block'){
			 voronoi_submit.style.display="none";
		  }
		// var cluster_cp = document.getElementById("divCluster");
		// cluster_cp.style.display = "none";
	 });
	 $("#cp li").click(function(){
		// var cluster = document.getElementById("divCluster");
	   //  cluster.style.display = "block";
		 var obj_select = document.getElementById("cp");
		 if($(this).index()==0){
			//选择的是traclus的partition这里没有参数输入
			 window.cp = "traclus";
			 var sga_divCluster =  document.getElementById("divCluster"); 
			  sga_divCluster.style.display="block";
			
			 //显示信息栏并显示选的是哪种partition
			 var par_type = document.getElementById("choosed_partition_table");
			 par_type.style.display = "block";
			 showPartition("traclus");
		 }else{
			 window.cp="SGA";
			 //选择的数据类型是SGA
			 var cluster = document.getElementById("divCluster");
				  //并且选择了SGA之后不能直接跳出Cluster的按钮因此需要设置为none;
			 if(cluster.style.display==("block")){
				 cluster.style.display = "none";
			 }
			 ChooseSGA();//调用函数展示SGA的参数面板
			// var sga =  document.getElementById("sga_panel"); 
			//  sga.style.display="block";
			 //显示信息栏，并且显示选的是哪种partition
			 var par_type = document.getElementById("choosed_partition_table");
			 par_type.style.display = "block";
			 showPartition("SGA");
		 }
	});
 
	 $("#user li").click(function(){
		 $("#progress_bar").css("width","0");
		 $("#progress_value").html("0%")
		   // alert("hello")
			 var obj_select = document.getElementById("user");
			 if($(this).index()==0){
				 $("#user_menu").html("Kuurna ▽");
				window.user = "Kurrna";
			 }else{
				 $("#user_menu").text("Pasi ▽");
				 window.user = "Pasi";
			 }
		});
	 $("#routes li").click(function(){
		 $("#progress_bar").css("width","0");
		 $("#progress_value").html("0%")
		   // alert("hello")
			 var obj_select = document.getElementById("routes");
			 if($(this).index()==0){
				 $("#routes_menu").html("2 ▽");
				window.routes = 2;
			 }else if($(this).index()==1){
				 $("#routes_menu").html("3 ▽");
					window.routes = 3;
			 }else if($(this).index()==2){
				 $("#routes_menu").html("4 ▽");
					window.routes = 4;
			 }else{
				 $("#routes_menu").html("5 ▽");
					window.routes = 5;
			 }
			 //两个下拉菜单都选好之后开始使得隐藏的提交按钮显示
			/* if(window.user=="Kurrna"|window.user=="Pasi"){ 
			  var routeButton =  document.getElementById("route_submit"); 
			  routeButton.style.display="block";
			 }
			 */
 		});
	 $("#avg_method li").click(function(){
		 $("#progress_bar").css("width","0");
		 $("#progress_value").html("0%")
			 var obj_select = document.getElementById("avg_method");
			 if($(this).index()==0){
				 $("#method_menu").html("Traclus ▽");
				window.method = 1;
			 }else if($(this).index()==1){
				 $("#method_menu").html("DBA ▽");
					window.method = 2;
			 } 
			 //两个下拉菜单都选好之后开始使得隐藏的提交按钮显示
			 if(window.user=="Kurrna"|window.user=="Pasi"){ 
				//  if(window.route==2|window.route==3|window.route==4|window.route==5){ 
			  var routeButton =  document.getElementById("route_submit"); 
			  routeButton.style.display="block";
				//  }
			 }
		});
	 $("#mopsi-cluster").click(function(){
		 var panel =  document.getElementById("kmeansMopsi_panel"); 
		 panel.style.display="block";
	 });
	 $("#mopsi_DBA").click(function(){
		// var legend =  document.getElementById("legend_table"); 
		// legend.style.display="block";
		// var cluster =  document.getElementById("mopsi-cluster"); 
		// cluster.style.display="block";
		 MopsiData.user = window.user;
		 MopsiData.routes = window.routes;
		 $.ajax({ 
	            type:"POST", 
	            url:"taxi/mopsi_DBA", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8",
	            data:JSON.stringify(MopsiData),
	            success:function(data){
	               
	            	clearPathAndMarker();
	            	clearRedPart();
	            	map.setZoom(10);
	            	//console.log(data.length);
					//similarityEdge = data.similarity;
					var countIt = 0;
					var Rep=data.reRoutes;
					console.log(Rep);
					for(var a in Rep){//对于每条代表轨迹
						
						var singleTrajectory = Rep[a];
						var lineSymbol = {
							    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
							  };
						var getRandomColor = randomColor();
						var poly = new google.maps.Polyline({
							    title:a,
							    Colorchanged:0,//0代表未被改变
							  //  strokeColor: getRandomColor,
							    strokeColor: '#000000',
							    strokeOpacity: 1.0,
							    strokeWeight: 3,
							    map:map,
							    icons: [{
							        icon: lineSymbol,
							        offset: '100%'
							      }]
							  });
						//console.log(singleTrajectory);
						for(var b in singleTrajectory.points){//对于每条代表轨迹的每个点
							var myLatlng = new google.maps.LatLng(singleTrajectory.points[b].x,singleTrajectory.points[b].y);
							
						//	console.log(singleTrajectory.points[b].x+":"+singleTrajectory.points[b].y);
							if(a==0) map.setCenter(myLatlng);
							var path = poly.getPath();
							path.push(myLatlng);
							 if(b==0){
								 
								var content = "averaging route";
								//similarityNode.push(content);
						 	marker = new google.maps.Marker({
			            		    position: myLatlng,
			            		    title:content,
			            		    map:map,
			            		  //  icon:newPoint
			            		});
						 	 
							  marker.addListener('click',function(){//点击之后触发的事件
									// alert(this.title);
								     //点击之后将对应的Poly放入数组并清除
								    // clickedRep.push(poly);
								    console.log("clicked segment is:"+this.title);
								     poly.Colorchanged=1;//标记为该线段被
								     var color_poly = poly.strokeColor;//点击线段的原本颜色
								     poly.strokeColor = '#FF0000';
								     console.log(poly.strokeColor +";"+color_poly);
									 showSegment(this.title,data,color_poly,eps,minlns,algor);
									 console.log(trajectory);
								 });
								markers.push(marker);
								repMarker.push(marker);
							 
						 	}
						}
						trajectory.push(poly);
						repTrajectory.push(poly);
					} 
					//接下来画原始轨迹
						var allRawTrajectory = data.fragmentMopsi;
  					//	similarityEdge = data.similarity;
  						for(var a in allRawTrajectory){
  							var singleRawTrajectory = allRawTrajectory[a];
  							var lineSymbol = {
  								    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
  								  };
  							var getRandomColor = randomColor();
  							var poly = new google.maps.Polyline({
  								    strokeColor: '#FF0000',
  								    strokeOpacity: 1.0,
  								    strokeWeight: 3,
  								    map:map ,
  								    icons: [{
  								        icon: lineSymbol,
  								        offset: '100%'
  								      }]
  								  });
  							for(var b in singleRawTrajectory){
  								var myLatlng = new google.maps.LatLng(singleRawTrajectory[b].latitude,singleRawTrajectory[b].longtitude);
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
  				            		    map:map 
  				            		});
  									marker.addListener('click',showChord);
  									markers.push(marker);
  									var latlng = new google.maps.LatLng(singleRawTrajectory[b].latitude,singleRawTrajectory[b].longtitude);
  									var infowindow = new google.maps.InfoWindow({
  									    content: content
  									  });
  									infowindow.open(map ,marker);
  								}
  							}
  							trajectory.push(poly);
  						}
					}
		});
	 });
	 function showProgress(){
		   var loader = new Loader('#loader', {
		        onComplete: function () {
		            console.log('loading effect finish!');
		        }
		    });

		    var Task = function (index, duration) {
		        setTimeout(function () {
		            //异步任务完成进度等于 index / 4
		            loader.setValue(index / 4);
		            console.log('第' + index + '个异步任务执行完毕');
		        }, duration);
		    };
		    //模拟四个同时发起的异步任务
		   
		    switch (window.user){
		       case 'Kurrna':{
		     	if(window.method==1){//Traclus
		    		var task1 = new Task(1, 10  );
		 		    var task2 = new Task(2, 10  );
		 		    var task3 = new Task(3, 10 );
		 		    var task4 = new Task(4, 50 );
		     	}else{
		    		//DBA
		     		var task1 = new Task(1, 3000  );
		 		    var task2 = new Task(2, 4500  );
		 		    var task3 = new Task(3, 5500 );
		 		    var task4 = new Task(4, 6500 );
		     	}
		    	break;
		     }
		     }
	 }
	 
	 
	 $("#route_submit").click(function(){
		
		 var legend =  document.getElementById("legend_table"); 
		 legend.style.display="block";
		 var cluster =  document.getElementById("mopsi-cluster"); 
		 cluster.style.display="block";
		 MopsiData.user = window.user;
		 MopsiData.routes = window.routes;
		 MopsiData.method = window.method;//the method chosed
		 //progress
	   //  showProgress();
		 $.ajax({ 
	            type:"POST", 
	            url:"taxi/mopsi_avg", 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8",
	            data:JSON.stringify(MopsiData),
	            
	            success:function(data){
	            	$("#progress_bar").css("width","100%");
	            	$("#progress_value").html("100%");
	            	 $("#progress_bar").unbind();
	            	var myDate1 = new Date();
	            //	console.log(myDate1); 
	            //	console.log(myDate1-myDate); 
	            	clearPathAndMarker();
	            	clearRedPart();
	            	map.setZoom(10);
	            	//console.log(data.length);
					//similarityEdge = data.similarity;
					var countIt = 0;
					var Rep=data.reRoutes;
					console.log(data);
					for(var a in Rep){//对于每条代表轨迹
						
						var singleTrajectory = Rep[a];
						var lineSymbol = {
							    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
							  };
						var getRandomColor = randomColor();
						var poly = new google.maps.Polyline({
							    title:a,
							    Colorchanged:0,//0代表未被改变
							  //  strokeColor: getRandomColor,
							    strokeColor: '#FF0000',
							    strokeOpacity: 1.0,
							    strokeWeight: 3,
							    map:map,
							    icons: [{
							        icon: lineSymbol,
							        offset: '100%'
							      }]
							  });
						//console.log(singleTrajectory);
						for(var b in singleTrajectory.points){//对于每条代表轨迹的每个点
							var myLatlng = new google.maps.LatLng(singleTrajectory.points[b].x,singleTrajectory.points[b].y);
							
						//	console.log(singleTrajectory.points[b].x+":"+singleTrajectory.points[b].y);
							if(a==0) map.setCenter(myLatlng);
							var path = poly.getPath();
							path.push(myLatlng);
							 if(b==0){
								 
								var content = "averaging route";
								//similarityNode.push(content);
						 	marker = new google.maps.Marker({
			            		    position: myLatlng,
			            		    title:content,
			            		    map:map,
			            		  //  icon:newPoint
			            		});
						 	 
							 /* marker.addListener('click',function(){//点击之后触发的事件
								    console.log("clicked segment is:"+this.title);
								     poly.Colorchanged=1;//标记为该线段被
								     var color_poly = poly.strokeColor;//点击线段的原本颜色
								     poly.strokeColor = '#FF0000';
								     console.log(poly.strokeColor +";"+color_poly);
									 showSegment(this.title,data,color_poly,eps,minlns,algor);
									 console.log(trajectory);
								 });
								 */
								markers.push(marker);
								repMarker.push(marker);
							 
						 	}
						}
						trajectory.push(poly);
						repTrajectory.push(poly);
					} 
					//接下来画原始轨迹
						var allRawTrajectory = data.fragmentMopsi;
  					//	similarityEdge = data.similarity;
  						for(var a in allRawTrajectory){
  							var singleRawTrajectory = allRawTrajectory[a];
  							var lineSymbol = {
  								    path: google.maps.SymbolPath.FORWARD_OPEN_ARROW,
  								  };
  							var poly = new google.maps.Polyline({
  								    strokeColor: '#000000',
  								    strokeOpacity: 1.0,
  								    strokeWeight: 3,
  								    map:map ,
  								    icons: [{
  								        icon: lineSymbol,
  								        offset: '100%'
  								      }]
  								  });
  							for(var b in singleRawTrajectory){
  								var myLatlng = new google.maps.LatLng(singleRawTrajectory[b].latitude,singleRawTrajectory[b].longtitude);
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
  				            		    map:map 
  				            		});
  									//marker.addListener('click',showChord);
  									markers.push(marker);
  									//var latlng = new google.maps.LatLng(singleRawTrajectory[b].latitude,singleRawTrajectory[b].longtitude);
  								//	var infowindow = new google.maps.InfoWindow({
  								//	    content: content
  								//	  });
  								//	infowindow.open(map ,marker);
  								}
  							}
  							trajectory.push(poly);
  						}
					}
		});
	 });
	 function showPartition(type){
	/*	 var divshow = $("#show_partition");
         divshow.text("");// 清空数据
         divshow.append("the partition type is: "+type); // 添加Html内容，
		*/
		 var type1 = type;
		 if(type=="traclus"){
		 type1 = type+"_mdl";}
			//添加表格信息
     	var tab = document.getElementById("partition_table");
     	if(tab.rows.length==1)
     	 $("#partition_tr").after('<tr><td>'+"Partition"+'</td><td>'+type1+'</td></tr>'); 
     	else{
     		//删除最后一行再加入新的一行
     		tab.deleteRow(1);
     		 $("#partition_tr").after('<tr><td>'+"Partition"+'</td><td>'+type1+'</td></tr>'); 
     	}
	 };
	function ChooseSGA(){
		// console.log("choose SGA");
		// var testit = document.getElementById("checkBlock");
		// testit.style.display="none";
		  var sga_choose =  document.getElementById("sga_panel"); 
		  sga_choose.style.display="block";
		 // console.log(sga_choose.style.display);
		
	}
	 $("#repre_track").click(function(){
		 //document.getElementById("repre_track").style.display="none";
		  //这里对聚类生成的结果进行聚类会得到代表点，traclus算法中，后面的聚类
	 });
	function GetMSE(cluster_type,cp_type){
		//需要计算各个点到中心的距离，因此只需要传入参数：cluster_type
		var path = "taxi/MSE"
		var result ;
		//DbscanData.cluster_type = cluster_type;
		//DbscanData.partition_type = cp_type;
		$.ajax({ 
            type:"POST", 
            url:path, 
            dataType:"json",      
            contentType:"application/json; charset=utf-8",
            data:JSON.stringify(DbscanData),
            success:function(data){
				 
            	// var divshow = $("#showInfo");
               //  divshow.text("");// 清空数据
                // divshow.append("the MSE is: "+data); // 添加Html内容，
        		//添加表格信息
             	var tab = document.getElementById("mse_table");
             	if(tab.rows.length==1)
             	 $("#mse_tr").after('<tr><td>'+"clusters :"+'</td><td>'+data+'</td></tr>'); 
             	else{
             		//删除最后一行再加入新的一行
             		tab.deleteRow(1);
             		 $("#mse_tr").after('<tr><td>'+"clusters :"+'</td><td>'+data+'</td></tr>'); 
             	}
				}
	});
	}
	$("#kmeansMopsi_submit").click(function(){
		var buttonVor =  document.getElementById("voronoi_Mopsi"); 
		buttonVor.style.display="block";
	 	var k_value = $("#k_mopsi").val();
	 	var k_iterator = $("#k-iterator_mopsi").val();
		var user = window.user;
		console.log(user);
	 
	//	 console.log(k_value+":"+k_iterator+";"+window.user);
	 	cluster_path="taxi/kmeansMopsi_cluster";
		//MarkerRawData(window.cp,"kmeans");//这里要改，是重点
		 testData.k=k_value;
		 testData.iterator = k_iterator;
		 testData.user = user;
  
		$.ajax({ 
           type:"POST", 
           url:cluster_path, 
           dataType:"json",      
           contentType:"application/json; charset=utf-8",
           data:JSON.stringify(testData),
           success:function(data){
           //	 GetMSE("kmeans",window.cp);
				// clearCluster();
				// clearTheRed();
				// clearRedPart();
        	     clearTheRed();
				 clearPathAndMarker();
                console.log(data);
                
				  for(var i in data){
					  var myLatlng = new google.maps.LatLng(data[i].centerLatitude,data[i].centerLongtitude);
					  var marker = new MarkerWithLabel({
						    position: myLatlng,
						    map: map,
						    raiseOnDrag: true,
						    labelContent: "C",
						    labelAnchor: new google.maps.Point(10,35),
						    labelClass: "labels", // the CSS class for the label
						    labelInBackground: false,
						    icon: POI
						  });
					  pointOfInterestMarkers.push(marker);
					  //open next layer
						  var cluster_id = data[i].id;
						  var index = i;
						  var size = data[i].size;
					/*	  google.maps.event.addListener(marker,"click",(function(cluster_id, index, size){return function(){
							  if(size>=100){
								  testData.clusterId = cluster_id;
								  $.ajax({ 
							            type:"POST", 
							            url:"cluster/nextlayer", 
							            dataType:"json",      
							            contentType:"application/json; charset=utf-8",
							            data:JSON.stringify(testData),
							            success:function(data){
							            	//remove parent cluster
							            	pointOfInterestMarkers[index].setMap(null);
							            	//map.setCenter(31.2312501945,121.4505406163);
							            	console.log(data);
							            	for(var a in data){
							            		if(data[a].size==0)continue;
							            		var Latlng = new google.maps.LatLng(data[a].centerLatitude,data[a].centerLongtitude);
							            		var marker = new MarkerWithLabel({
												    position: Latlng,
												    map: map,
												    raiseOnDrag: true,
												    labelContent: data[a].size,
												    labelAnchor: new google.maps.Point(10,35),
												    labelClass: "labels", // the CSS class for the label
												    labelInBackground: false,
												    icon: nextLayerPOI
												  });
							            		pointOfInterestMarkers.push(marker);
							            	}
							            }
								  });
							  }
						  }
						  }(cluster_id, index,size)));
						  
						  */
				  }
				  map.setCenter(new google.maps.LatLng(62.6  , 29.75));
				}
	});
		 
	});
	$("#kmeans_submit").click(function(){
		
		 var representative = document.getElementById("traclus-rep");
		 representative.style.display="none";
		
		//var SegCluster = document.getElementById("exp_seg");
		//SegCluster.style.display="block";
		// var voronoi_submit =  document.getElementById("voronoi"); 
		var voronoi_submit =  document.getElementById("voronoi"); 
		 if(voronoi_submit.style.display=='none'){
			 voronoi_submit.style.display="";
		  }
		 var mse = document.getElementById("mse_div");
		 mse.style.display = "block";
		
		var k_value = $("#k").val();
		var k_iterator = $("#k-iterator").val();
		
		 console.log(k_value+":"+k_iterator+";"+window.cp);
		//画出用于聚类的特征点的，以taxi作为主键的轨迹
		// testData.partition_type = cp;
	/*	 $.ajax({ 
            type:"POST", 
            url:"taxi/raw_data", 
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
					var poly = new google.maps.Polyline({
						    strokeColor: '#FF0000',
						    strokeOpacity: 1.0,
						    strokeWeight: 3,
						    map:map,
						    icons: [{
						        icon: lineSymbol,
						        offset: '100%'
						      }]
						  });
					for(var b in singleTrajectory){
						var myLatlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude);
						if(b==0){
						currentMarker = new google.maps.Marker({
	            		    position: myLatlng,
	            		    title:"line"+a,
	            		  //  animation: google.maps.Animation.DROP,
	            		    icon:onLoadTaxi,
	            		    map:map
	            		});}
						if(a==0) map.setCenter(myLatlng);
						var path = poly.getPath();
						path.push(myLatlng);
				 
					}
					trajectory.push(poly);
					
				}
				selectedComparedTaxiIds = [];
			}
	}); */
		//画出聚类结果
		cluster_path="taxi/kmeans_cluster";
		   MarkerRawData(window.cp,"kmeans");
		  testData.k=k_value;
		 testData.iterator = k_iterator;
		 testData.partition_type = window.cp;
		// testData.partition_type = cp; 
		$.ajax({ 
            type:"POST", 
            url:cluster_path, 
            dataType:"json",      
            contentType:"application/json; charset=utf-8",
            data:JSON.stringify(testData),
            success:function(data){
            	 GetMSE("kmeans",window.cp);
			//	clearPathAndMarker();
				 clearCluster();
				 clearTheRed();
				 clearRedPart();
                 console.log("kemans:"+data.length);
                 
				  for(var i in data){
					  var myLatlng = new google.maps.LatLng(data[i].centerLatitude,data[i].centerLongtitude);
					  var marker = new MarkerWithLabel({
						    position: myLatlng,
						    map: map,
						    raiseOnDrag: true,
						    labelContent: "C",
						    labelAnchor: new google.maps.Point(10,35),
						    labelClass: "labels", // the CSS class for the label
						    labelInBackground: false,
						    icon: POI
						  });
					  pointOfInterestMarkers.push(marker);
					  
					  //open next layer
						  var cluster_id = data[i].id;
						  var index = i;
						  var size = data[i].size;
						  google.maps.event.addListener(marker,"click",(function(cluster_id, index, size){return function(){
							  if(size>=100){
								  testData.clusterId = cluster_id;
								  $.ajax({ 
							            type:"POST", 
							            url:"cluster/nextlayer", 
							            dataType:"json",      
							            contentType:"application/json; charset=utf-8",
							            data:JSON.stringify(testData),
							            success:function(data){
							            	//remove parent cluster
							            	pointOfInterestMarkers[index].setMap(null);
							            //	map.setZoom(10);
							            //	map.setCenter(pointOfInterestMarkers[index].getPosition());
							            	//31.2312501945,121.4505406163
							            	map.setCenter(31.2312501945,121.4505406163);
							            	console.log(data);
							            	for(var a in data){
							            		if(data[a].size==0)continue;
							            		var Latlng = new google.maps.LatLng(data[a].centerLatitude,data[a].centerLongtitude);
							            		var marker = new MarkerWithLabel({
												    position: Latlng,
												    map: map,
												    raiseOnDrag: true,
												    labelContent: data[a].size,
												    labelAnchor: new google.maps.Point(10,35),
												    labelClass: "labels", // the CSS class for the label
												    labelInBackground: false,
												    icon: nextLayerPOI
												  });
							            		pointOfInterestMarkers.push(marker);
							            	}
							            }
								  });
							  }
						  }}(cluster_id, index,size)));
				  }

				  //draw graph
				//  d3.select("#statistics_graph").selectAll("*").remove();
				  //d3.select("#chord").remove();
				//  showStatistical(path);
				 // map.setZoom(6);
				  map.setCenter(new google.maps.LatLng(31.230416  , 121.473701));
				}
	});
	});
	 
	$("#sga_submit").click(function(){
		 var sga_cp_submit =  document.getElementById("sga_panel"); 
		 if(sga_cp_submit.style.display=='block'){
		  sga_cp_submit.style.display="none";
		  }
		 var next_cluster =  document.getElementById("divCluster"); 
		   next_cluster.style.display="block";
		
		//showThePanel();
		var minAngle=$("#minAngle").val();
		var minStopDuration = $("#minStopDuration").val();
		var minDis = $("#minDistance").val();
		var maxDis = $("#maxDistance").val();
		SGAdata.minAngle = minAngle;
		SGAdata.minStopDuration = minStopDuration;
		SGAdata.minDistance = minDis;
		SGAdata.maxDistance = maxDis;
		cluster_path="taxi/sga";
		$.ajax({ 
            type:"POST", 
            url:cluster_path, 
            dataType:"json",      
            contentType:"application/json; charset=utf-8",
            data:JSON.stringify(SGAdata),
            success:function(data){
            //	alert(data );
           	
            }});
		 
	});
	$("#traclus_dbscan_submit").click(function(){
		//var representative = document.getElementById("traclus-rep");
		//representative.style.display="block";
		//使用traclus的方法对特征点聚类并获得结果
		var voronoi_submit =  document.getElementById("voronoi"); 
		 if(voronoi_submit.style.display=='none'){
			 voronoi_submit.style.display="";
		  }
		var eps=$("#traclus_eps").val();
		var minlns_val = $("#traclus_minlns").val();
	 
		 var mse = document.getElementById("mse_div");
		 mse.style.display = "block";
	 	
		 console.log(eps+":"+minpts+";"+window.cp);
		//画出用于聚类的特征点的，以taxi作为主键的轨迹
 
		//画出聚类结果
		cluster_path="taxi/traclus_dbscan_cluster";
	 	MarkerRawData(window.cp,"traclus_dbscan");
		  TraclusData.traclus_eps=eps;
		  TraclusData.traclus_minlns= minlns_val;
		  TraclusData.partition_type = window.cp;
		// testData.partition_type = cp; 
		$.ajax({ 
            type:"POST", 
            url:cluster_path, 
            dataType:"json",      
            contentType:"application/json; charset=utf-8",
            data:JSON.stringify(TraclusData),
            success:function(data){
            	// clearPathAndMarker();
				 clearCluster();
				 clearTheRed();
				 clearRedPart();
            	 GetMSE("traclus_dbscan",window.cp);
				//clearPathAndMarker();
                 console.log("db:"+data.length);
				  for(var i in data){
					  var myLatlng = new google.maps.LatLng(data[i].centerLatitude,data[i].centerLongtitude);
					  var marker = new MarkerWithLabel({
						    position: myLatlng,
						    map: map,
						    raiseOnDrag: true,
						    labelContent: "C",
						    labelAnchor: new google.maps.Point(10,35),
						    labelClass: "labels", // the CSS class for the label
						    labelInBackground: false,
						    icon: POI
						  });
					  pointOfInterestMarkers.push(marker);
					  
					  //open next layer
						  var cluster_id = data[i].id;
						  var index = i;
						  var size = data[i].size;
						  google.maps.event.addListener(marker,"click",(function(cluster_id, index, size){return function(){
							  if(size>=100){
								  testData.clusterId = cluster_id;
								  $.ajax({ 
							            type:"POST", 
							            url:"cluster/nextlayer", 
							            dataType:"json",      
							            contentType:"application/json; charset=utf-8",
							            data:JSON.stringify(testData),
							            success:function(data){
							            	//remove parent cluster
							            	pointOfInterestMarkers[index].setMap(null);
							            	map.setZoom(14);
							            	map.setCenter(pointOfInterestMarkers[index].getPosition());
							            	
							            	console.log(data);
							            	for(var a in data){
							            		if(data[a].size==0)continue;
							            		var Latlng = new google.maps.LatLng(data[a].centerLatitude,data[a].centerLongtitude);
							            		var marker = new MarkerWithLabel({
												    position: Latlng,
												    map: map,
												    raiseOnDrag: true,
												    labelContent: data[a].size,
												    labelAnchor: new google.maps.Point(10,35),
												    labelClass: "labels", // the CSS class for the label
												    labelInBackground: false,
												    icon: nextLayerPOI
												  });
							            		pointOfInterestMarkers.push(marker);
							            	}
							            }
								  });
							  }
						  }}(cluster_id, index,size)));
				  }

				  map.setZoom(11);
				  map.setCenter(new google.maps.LatLng(31.230416  , 121.473701));
				}
	});
		 
			
	});
	$("#dbscan_submit").click(function(){
	 	var representative = document.getElementById("traclus-rep");
	 	representative.style.display="none";
	//	var SegCluster = document.getElementById("exp_seg");
	//	SegCluster.style.display="block";
		var voronoi_submit =  document.getElementById("voronoi"); 
		 if(voronoi_submit.style.display=='none'){
			 voronoi_submit.style.display="";
		  }
		var eps=$("#eps").val();
		var minpts = $("#minpts").val();
	 
		 var mse = document.getElementById("mse_div");
		 mse.style.display = "block";
		// GetMSE("dbscan",window.cp);
		 console.log(eps+":"+minpts+";"+window.cp);
		//画出用于聚类的特征点的，以taxi作为主键的轨迹
 
		//画出聚类结果
		cluster_path="taxi/dbscan_cluster";
		   MarkerRawData(window.cp,"dbscan");
		  DbscanData.eps=eps;
		  DbscanData.minpts = minpts;
		  DbscanData.partition_type = window.cp;
		// testData.partition_type = cp; 
		$.ajax({ 
            type:"POST", 
            url:cluster_path, 
            dataType:"json",      
            contentType:"application/json; charset=utf-8",
            data:JSON.stringify(DbscanData),
            success:function(data){
				// clearPathAndMarker();
            	 GetMSE("dbscan",window.cp);
				 clearCluster();
				 clearTheRed();
				 clearRedPart();
                 console.log("db:"+data.length);
				  for(var i in data){
					  var myLatlng = new google.maps.LatLng(data[i].centerLatitude,data[i].centerLongtitude);
					  var marker = new MarkerWithLabel({
						    position: myLatlng,
						    map: map,
						    raiseOnDrag: true,
						    labelContent: "C",
						    labelAnchor: new google.maps.Point(10,35),
						    labelClass: "labels", // the CSS class for the label
						    labelInBackground: false,
						    icon: POI
						  });
					  pointOfInterestMarkers.push(marker);
					  
					  //open next layer
						  var cluster_id = data[i].id;
						  var index = i;
						  var size = data[i].size;
						  google.maps.event.addListener(marker,"click",(function(cluster_id, index, size){return function(){
							  if(size>=100){
								  testData.clusterId = cluster_id;
								  $.ajax({ 
							            type:"POST", 
							            url:"cluster/nextlayer", 
							            dataType:"json",      
							            contentType:"application/json; charset=utf-8",
							            data:JSON.stringify(testData),
							            success:function(data){
							            	//remove parent cluster
							            	pointOfInterestMarkers[index].setMap(null);
							            	map.setZoom(14);
							            	map.setCenter(pointOfInterestMarkers[index].getPosition());
							            	
							            	console.log(data);
							            	for(var a in data){
							            		if(data[a].size==0)continue;
							            		var Latlng = new google.maps.LatLng(data[a].centerLatitude,data[a].centerLongtitude);
							            		var marker = new MarkerWithLabel({
												    position: Latlng,
												    map: map,
												    raiseOnDrag: true,
												    labelContent: data[a].size,
												    labelAnchor: new google.maps.Point(10,35),
												    labelClass: "labels", // the CSS class for the label
												    labelInBackground: false,
												    icon: nextLayerPOI
												  });
							            		pointOfInterestMarkers.push(marker);
							            	}
							            }
								  });
							  }
						  }}(cluster_id, index,size)));
				  }

				  //draw graph
				//  d3.select("#statistics_graph").selectAll("*").remove();
				  //d3.select("#chord").remove();
				//  showStatistical(path);
				  map.setZoom(12);
				  map.setCenter(new google.maps.LatLng(31.230416  , 121.473701));
				}
	});
		 
			
	});
	$("#hierarchical_submit").click(function(){
		 var representative = document.getElementById("traclus-rep");
	  	representative.style.display="none";
	//	var SegCluster = document.getElementById("exp_seg");
		//SegCluster.style.display="block";
		var voronoi_submit =  document.getElementById("voronoi"); 
		 if(voronoi_submit.style.display=='none'){
			 voronoi_submit.style.display="";
		  }
		
		var n=$("#n").val(); 
		 var mse = document.getElementById("mse_div");
		 mse.style.display = "block";
		
		 console.log(n+";"+window.cp);
		//画出用于聚类的特征点的，以taxi作为主键的轨迹
 
		//画出聚类结果
		cluster_path="taxi/agnes_cluster";
		   MarkerRawData(window.cp,"agnes");
          DbscanData.n=n;
		  DbscanData.partition_type = window.cp;
		// testData.partition_type = cp; 
		$.ajax({ 
            type:"POST", 
            url:cluster_path, 
            dataType:"json",      
            contentType:"application/json; charset=utf-8",
            data:JSON.stringify(DbscanData),
            success:function(data){
            	 GetMSE("agnes",window.cp);
				//clearPathAndMarker();
				 clearCluster();
				 clearTheRed();
				 clearRedPart();
                 console.log("agnes:"+data.length);
				  for(var i in data){
					  var myLatlng = new google.maps.LatLng(data[i].centerLatitude,data[i].centerLongtitude);
					  var marker = new MarkerWithLabel({
						    position: myLatlng,
						    map: map,
						    raiseOnDrag: true,
						    labelContent: "C",
						    labelAnchor: new google.maps.Point(10,35),
						    labelClass: "labels", // the CSS class for the label
						    labelInBackground: false,
						    icon: POI
						  });
					  pointOfInterestMarkers.push(marker);
					  
					  //open next layer
						  var cluster_id = data[i].id;
						  var index = i;
						  var size = data[i].size;
						  google.maps.event.addListener(marker,"click",(function(cluster_id, index, size){return function(){
							  if(size>=100){
								  testData.clusterId = cluster_id;
								  $.ajax({ 
							            type:"POST", 
							            url:"cluster/nextlayer", 
							            dataType:"json",      
							            contentType:"application/json; charset=utf-8",
							            data:JSON.stringify(testData),
							            success:function(data){
							            	//remove parent cluster
							            	pointOfInterestMarkers[index].setMap(null);
							            	map.setZoom(14);
							            	map.setCenter(pointOfInterestMarkers[index].getPosition());
							            	
							            	console.log(data);
							            	for(var a in data){
							            		if(data[a].size==0)continue;
							            		var Latlng = new google.maps.LatLng(data[a].centerLatitude,data[a].centerLongtitude);
							            		var marker = new MarkerWithLabel({
												    position: Latlng,
												    map: map,
												    raiseOnDrag: true,
												    labelContent: data[a].size,
												    labelAnchor: new google.maps.Point(10,35),
												    labelClass: "labels", // the CSS class for the label
												    labelInBackground: false,
												    icon: nextLayerPOI
												  });
							            		pointOfInterestMarkers.push(marker);
							            	}
							            }
								  });
							  }
						  }}(cluster_id, index,size)));
				  }

				  //draw graph
				//  d3.select("#statistics_graph").selectAll("*").remove();
				  //d3.select("#chord").remove();
				//  showStatistical(path);
				  map.setZoom(12);
				  map.setCenter(new google.maps.LatLng(31.230416  , 121.473701));
				}
	});
		 
	});
	function voronoi(){
		// forked from _shimizu's "d3.js voronoi" http://jsdo.it/_shimizu/sKcL
		var width = 960,
		    height = 500;

		var vertices = d3.range(100).map(function(d) {
		  return [Math.random() * width, Math.random() * height];
		});

		var data = d3.geom.voronoi(vertices)
		        .map(function(d) {
		            return "M" + d.join("L") + "Z"; 
		        });

		var svg = d3.select("svg")
		    .attr("width", width)
		    .attr("height", height)

		var path = svg.append("g").selectAll("path");
		path = path
		    .data(data);

		var colorScale = d3.scale.category20c();


		path
		.enter()
		.append("path")
		.attr({
		    "fill":function(d, i) { return colorScale(i) },
		    "stroke": "black",
		    "d": function(d){ console.log(d); return d }
		});
		path.order();
	};
	 $("#exp").click(function(){
		 showAllTracks();
		   // ExpMarkerRawData_seg(window.cp,window.select_algor); 
	}); 
	$("#exp_seg_kmeans").click(function(){
		  ExpMarkerRawData_seg_cluster(window.cp,window.select_algor);
	});
	$("#exp_seg_dbscan").click(function(){
		  ExpMarkerRawData_seg_cluster(window.cp,window.select_algor);
	});
	$("#exp_seg_agnes").click(function(){
		  ExpMarkerRawData_seg_cluster(window.cp,window.select_algor);
	});
	$("#grid_submit").click(function(){
		 var representative = document.getElementById("traclus-rep");
		 representative.style.display="none";
		
		var voronoi_submit =  document.getElementById("voronoi"); 
		 if(voronoi_submit.style.display=='none'){
			 voronoi_submit.style.display="";
		  } 
		
		var m=$("#m").val();
		var nx = $("#nx").val();
		var ny = $("#ny").val();
		 var mse = document.getElementById("mse_div");
		 mse.style.display = "block";
		
		 console.log(m+";"+nx+";"+ny+":"+window.cp);
		//画出用于聚类的特征点的，以taxi作为主键的轨迹

		//画出聚类结果
		cluster_path="taxi/grid_cluster";
		   MarkerRawData(window.cp,"grid");
         DbscanData.m=m;
         DbscanData.nx=nx;
         DbscanData.ny = ny;
		  DbscanData.partition_type = window.cp;
		// testData.partition_type = cp; 
		$.ajax({ 
           type:"POST", 
           url:cluster_path, 
           dataType:"json",      
           contentType:"application/json; charset=utf-8",
           data:JSON.stringify(DbscanData),
           success:function(data){
        	   GetMSE("grid",window.cp);
				//clearPathAndMarker();
				 clearCluster();
				 clearTheRed();
				 clearRedPart();
                console.log("grid:"+data.length);
				  for(var i in data){
					  var myLatlng = new google.maps.LatLng(data[i].centerLatitude,data[i].centerLongtitude);
					  var marker = new MarkerWithLabel({
						    position: myLatlng,
						    map: map,
						    raiseOnDrag: true,
						    labelContent: "C",
						    labelAnchor: new google.maps.Point(10,35),
						    labelClass: "labels", // the CSS class for the label
						    labelInBackground: false,
						    icon: POI
						  });
					  pointOfInterestMarkers.push(marker);
					  
					  //open next layer
						  var cluster_id = data[i].id;
						  var index = i;
						  var size = data[i].size;
						  google.maps.event.addListener(marker,"click",(function(cluster_id, index, size){return function(){
							  if(size>=100){
								  testData.clusterId = cluster_id;
								  $.ajax({ 
							            type:"POST", 
							            url:"cluster/nextlayer", 
							            dataType:"json",      
							            contentType:"application/json; charset=utf-8",
							            data:JSON.stringify(testData),
							            success:function(data){
							            	//remove parent cluster
							            	pointOfInterestMarkers[index].setMap(null);
							            	map.setZoom(14);
							            	map.setCenter(pointOfInterestMarkers[index].getPosition());
							            	
							            	console.log(data);
							            	for(var a in data){
							            		if(data[a].size==0)continue;
							            		var Latlng = new google.maps.LatLng(data[a].centerLatitude,data[a].centerLongtitude);
							            		var marker = new MarkerWithLabel({
												    position: Latlng,
												    map: map,
												    raiseOnDrag: true,
												    labelContent: data[a].size,
												    labelAnchor: new google.maps.Point(10,35),
												    labelClass: "labels", // the CSS class for the label
												    labelInBackground: false,
												    icon: nextLayerPOI
												  });
							            		pointOfInterestMarkers.push(marker);
							            	}
							            }
								  });
							  }
						  }}(cluster_id, index,size)));
				  }

				  //draw graph
				//  d3.select("#statistics_graph").selectAll("*").remove();
				  //d3.select("#chord").remove();
				//  showStatistical(path);
				  map.setZoom(12);
				  map.setCenter(new google.maps.LatLng(31.230416  , 121.473701));
				}
	});
	});
	 $("#btn-cluster").click(function(){
		 window.select_algor="";
		 //点击之后voronoi按钮消失
		 var voronoi_submit =  document.getElementById("voronoi"); 
		 if(voronoi_submit.style.display=='block'){
			 voronoi_submit.style.display="none";
		  }
			var reCluster = document.getElementById("representative_info");
		  	if(reCluster.style.display='block'){
				reCluster.style.display="none";
		  	}
	  	var SegCluster = document.getElementById("exp_seg");
	  	if(SegCluster.style.display='block'){
			SegCluster.style.display="none";
	  	}
	  	var repLine = document.getElementById("traclus-rep");
	  	if(repLine.style.display="block"){
	  		repLine.style.display="none";
	  	}
	  	
		  //点击关于聚类方法的下拉菜单之后，先将面板隐藏再根据选项显示面板
		 // var obj_select = document.getElementById("select_algor");
		 	 var kmeans =  document.getElementById("kmeans_panel"); 
			 kmeans.style.display = "none";
			 var dbscan =  document.getElementById("dbscan_panel");
			 dbscan.style.display = "none";
			 var hierarchical =  document.getElementById("hierarchical_panel");
			 hierarchical.style.display = "none";
	         var grid =  document.getElementById("grid_panel");
	         grid.style.display = "none";
	         var mse = document.getElementById("mse_div");
			 mse.style.display = "none";
			 var voronoi_display = document.getElementById("voronoi");
			 voronoi_display.display = "none";
	 });
	 window.select_algor="";
	$("#select_algor li").click(function(){//点击之后显示特征点
		//	var check=  $("#allTracks").prop("checked");
			//get selected ids		
		  //  alert($(this).index());
		 var obj_select = document.getElementById("select_algor");
		 var kmeans =  document.getElementById("kmeans_panel"); 
		 var dbscan =  document.getElementById("dbscan_panel");
		 var hierarchical =  document.getElementById("hierarchical_panel");
         var grid =  document.getElementById("grid_panel");
       //  var traclus_dbscan = document.getElementById("traclus_dbscan_panel");
		 if($(this).index()==0){
		   kmeans.style.display="block";
		   dbscan.style.display="none";
		   hierarchical.style.display="none";
		   grid.style.display="none";
		//   traclus_dbscan.style.display="none";
		   window.select_algor ="kmeans";
			var SegCluster1 = document.getElementById("exp_seg_kmeans");
		  	if(SegCluster1.style.display='none'){
				SegCluster1.style.display="block";
		  	}
		 }
		 else if($(this).index()==1){
			 kmeans.style.display="none";
			   dbscan.style.display="block";
			   hierarchical.style.display="none";
			   grid.style.display="none";
			 //  traclus_dbscan.style.display="none";
			   window.select_algor ="dbscan";
			   var SegCluster2 = document.getElementById("exp_seg_dbscan");
			  	if(SegCluster2.style.display='none'){
					SegCluster2.style.display="block";
			  	}
		 }
		 else if($(this).index()==2){
			 kmeans.style.display="none";
			   dbscan.style.display="none";
			   hierarchical.style.display="block";
			   grid.style.display="none";
			//   traclus_dbscan.style.display="none";
			   window.select_algor ="hierarchical";
			   var SegCluster3 = document.getElementById("exp_seg_agnes");
			  	if(SegCluster3.style.display='none'){
					SegCluster3.style.display="block";
			  	}
		 }else {
			 kmeans.style.display="none";
			   dbscan.style.display="none";
			   hierarchical.style.display="none";
			   grid.style.display="block";
			//   traclus_dbscan.style.display="none";
			   window.select_algor ="grid";
		 }
		 /*else{
			 kmeans.style.display="none";
			   dbscan.style.display="none";
			   hierarchical.style.display="none";
			   grid.style.display="none";
			//   traclus_dbscan.style.display="block";
			//   window.select_algor ="traclus_dbscan";
		 }*/
		 
		});
	$("#characteristics").click(function(){//测试使用，点击之后显示特征点
		//	var check=  $("#allTracks").prop("checked");
			//get selected ids		
		alert("hhe");
		path="taxi/traclus_character";
		$.ajax({ 
	            type:"POST", 
	            url:path, 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8",
	            data:JSON.stringify(testData),
	            success:function(data){
					clearPathAndMarker();

					  for(var i in data){
						  var myLatlng = new google.maps.LatLng(data[i].latitude,data[i].longtitude);
						  if(i==0) map.setCenter(myLatlng);
							var currentMarker;
		            			currentMarker = new google.maps.Marker({
			            		    position: myLatlng,
			            		    title:data[i].time,
			            		  //  animation: google.maps.Animation.DROP,
			            		    icon:POI,
			            		    map:map
			            		});
		            		 
		            			markers.push(currentMarker);
		            			var path = poly.getPath();
			            		path.push(myLatlng);
					
					  }
					  //draw graph
					 // d3.select("#statistics_graph").selectAll("*").remove();
					  //d3.select("#chord").remove();
					 // showStatistical(path);
					  map.setZoom(12);
					  map.setCenter(new google.maps.LatLng(31.230416  , 121.473701));
					}
		});
		 
		});
	$("#traclus").click(function(){
	//	var check=  $("#allTracks").prop("checked");
		//get selected ids		
		
		testData.comparedTaxiIds = selectedComparedTaxiIds;
		similarityNode = [];
		similarityEdge = [];
		$.ajax({ 
	            type:"POST", 
	            url:"taxi/traclus", 
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
							    strokeColor: getRandomColor,
							    strokeOpacity: 1.0,
							    strokeWeight: 3,
							    map:map,
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
			            		    map:map
			            		});
								marker.addListener('click',showChord);
								markers.push(marker);
								var latlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude+0.02);
								var infowindow = new google.maps.InfoWindow({
								    content: content
								  });
								infowindow.open(map,marker);
							}
						}
						trajectory.push(poly);
						
					}
				//	selectedComparedTaxiIds = [];
				//	$(".demo--radio").attr("checked",false);
				}
		});
	});
	$("#show_compared_data").click(function(){
		var check=  $("#allTracks").prop("checked");
		if(check){
			//alert("showTracks");
			 showAllTracks();
			  $("#allTracks").attr("checked",false);
		}
	else{
		//get selected ids		
		testData.comparedTaxiIds = selectedComparedTaxiIds;
		similarityNode = [];
		similarityEdge = [];
		$.ajax({ 
	            type:"POST", 
	            url:"taxi/compare", 
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
							    strokeColor: getRandomColor,
							    strokeOpacity: 1.0,
							    strokeWeight: 3,
							    map:map,
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
			            		    map:map
			            		});
								marker.addListener('click',showChord);
								markers.push(marker);
								var latlng = new google.maps.LatLng(singleTrajectory[b].latitude,singleTrajectory[b].longtitude+0.02);
								var infowindow = new google.maps.InfoWindow({
								    content: content
								  });
								infowindow.open(map,marker);
							}
						}
						trajectory.push(poly);
						
					}
					selectedComparedTaxiIds = [];
					$(".demo--radio").attr("checked",false);
				}
		});
		}
	});
	
	$("input[type=checkbox]").click(function(){
		var checked = $(this).prop("checked");
		if(checked == true) {
			selectedComparedTaxiIds.push($(this).next().next().text());
		}
		else {
			var array = [];
			for(var i in selectedComparedTaxiIds){
				if(selectedComparedTaxiIds[i]!=$(this).next().next().text()) array.push(selectedComparedTaxiIds[i]);
			}
			selectedComparedTaxiIds = array;
		}
	});
	$("#show_Characteristics").click(function(){//show traclus和SGA算法得到的特征点，待完善
	//	if(directURL=="traclus") path="taxi/traclus_character";
		path="taxi/traclus_character";
		$.ajax({ 
	            type:"POST", 
	            url:path, 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8",
	            success:function(data){
					clearPathAndMarker();

					  for(var i in data){
						  var myLatlng = new google.maps.LatLng(data[i].centerLatitude,data[i].centerLongtitude);
						  var marker = new MarkerWithLabel({
							    position: myLatlng,
							    map: map,
							    raiseOnDrag: true,
							    labelContent: data[i].size,
							    labelAnchor: new google.maps.Point(10,35),
							    labelClass: "labels", // the CSS class for the label
							    labelInBackground: false,
							    icon: POI
							  });
						  pointOfInterestMarkers.push(marker);
						  
						  //open next layer
							  var cluster_id = data[i].id;
							  var index = i;
							  var size = data[i].size;
							  google.maps.event.addListener(marker,"click",(function(cluster_id, index, size){return function(){
								  if(size>=100){
									  testData.clusterId = cluster_id;
									  $.ajax({ 
								            type:"POST", 
								            url:"cluster/nextlayer", 
								            dataType:"json",      
								            contentType:"application/json; charset=utf-8",
								            data:JSON.stringify(testData),
								            success:function(data){
								            	//remove parent cluster
								            	pointOfInterestMarkers[index].setMap(null);
								            	map.setZoom(14);
								            	map.setCenter(pointOfInterestMarkers[index].getPosition());
								            	
								            	console.log(data);
								            	for(var a in data){
								            		if(data[a].size==0)continue;
								            		var Latlng = new google.maps.LatLng(data[a].centerLatitude,data[a].centerLongtitude);
								            		var marker = new MarkerWithLabel({
													    position: Latlng,
													    map: map,
													    raiseOnDrag: true,
													    labelContent: data[a].size,
													    labelAnchor: new google.maps.Point(10,35),
													    labelClass: "labels", // the CSS class for the label
													    labelInBackground: false,
													    icon: nextLayerPOI
													  });
								            		pointOfInterestMarkers.push(marker);
								            	}
								            }
									  });
								  }
							  }}(cluster_id, index,size)));
					  }

					  //draw graph
					  d3.select("#statistics_graph").selectAll("*").remove();
					  //d3.select("#chord").remove();
					  showStatistical(path);
					  map.setZoom(12);
					  map.setCenter(new google.maps.LatLng(31.230416  , 121.473701));
					}
		});
		
	});
	$("#show_pick_drop_points").click(function(){//show 图标
		
		var path = null;
		var directURL = $("#selector").val();
		if(directURL=="kmeans") path="cluster/show/kmeans";
		else if(directURL=="dbscan") path="cluster/show/dbscan";
		else if(directURL=="agnes") path="cluster/show/agnes";
		
		$.ajax({ 
	            type:"POST", 
	            url:path, 
	            dataType:"json",      
	            contentType:"application/json; charset=utf-8",
	            success:function(data){
					clearPathAndMarker();

					  for(var i in data){
						  var myLatlng = new google.maps.LatLng(data[i].centerLatitude,data[i].centerLongtitude);
						  var marker = new MarkerWithLabel({
							    position: myLatlng,
							    map: map,
							    raiseOnDrag: true,
							    labelContent: data[i].size,
							    labelAnchor: new google.maps.Point(10,35),
							    labelClass: "labels", // the CSS class for the label
							    labelInBackground: false,
							    icon: POI
							  });
						  pointOfInterestMarkers.push(marker);
						  
						  //open next layer
							  var cluster_id = data[i].id;
							  var index = i;
							  var size = data[i].size;
							  google.maps.event.addListener(marker,"click",(function(cluster_id, index, size){return function(){
								  if(size>=100){
									  testData.clusterId = cluster_id;
									  $.ajax({ 
								            type:"POST", 
								            url:"cluster/nextlayer", 
								            dataType:"json",      
								            contentType:"application/json; charset=utf-8",
								            data:JSON.stringify(testData),
								            success:function(data){
								            	//remove parent cluster
								            	pointOfInterestMarkers[index].setMap(null);
								            	map.setZoom(14);
								            	map.setCenter(pointOfInterestMarkers[index].getPosition());
								            	
								            	console.log(data);
								            	for(var a in data){
								            		if(data[a].size==0)continue;
								            		var Latlng = new google.maps.LatLng(data[a].centerLatitude,data[a].centerLongtitude);
								            		var marker = new MarkerWithLabel({
													    position: Latlng,
													    map: map,
													    raiseOnDrag: true,
													    labelContent: data[a].size,
													    labelAnchor: new google.maps.Point(10,35),
													    labelClass: "labels", // the CSS class for the label
													    labelInBackground: false,
													    icon: nextLayerPOI
													  });
								            		pointOfInterestMarkers.push(marker);
								            	}
								            }
									  });
								  }
							  }}(cluster_id, index,size)));
					  }

					  //draw graph
					  d3.select("#statistics_graph").selectAll("*").remove();
					  //d3.select("#chord").remove();
					  showStatistical(path);
					  map.setZoom(12);
					  map.setCenter(new google.maps.LatLng(31.230416  , 121.473701));
					}
		});
	});
});

