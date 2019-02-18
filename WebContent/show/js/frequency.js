//设置基本参数 
	var map;
	var zoomMarker;
	var markers=[],pointOfInterestMarkers=[];
	var trajectory=[];
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
	var zoomLevel = 14;
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

	var testData={taxiId:null,comparedTaxiIds:null,index:0,clusterId:null}; 
	var selectedComparedTaxiIds = [];
	var similarityNode=[],similarityEdge=[];
	
	
	function initialize() {
	    var mapOptions = {
	        zoom: zoomLevel,
	        center: new google.maps.LatLng(31.230416  , 121.473701),
	        mapTypeId: google.maps.MapTypeId.ROADMAP
	    };
	    map = new google.maps.Map(document.getElementById('map-canvas'),mapOptions);
		geocoder = new google.maps.Geocoder;
		
//		 google.maps.event.addListener(map, 'click', function(event) {
//			    alert(event.latLng.lat()+"/"+event.latLng.lng());
//			  });
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
	
	function clearPathAndMarker()
	{
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
	d3.select("#force").remove();
	d3.select("#chord").remove();
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
		var height = 700;
		$("#statistics_graph").show();
		var svg = d3.select("#statistics_graph")
					.append("svg")
					.attr("width",width)
					.attr("height", height);
		
		//画布周边的空白
		var padding = {left:40, right:10, top:20, bottom:60};
		
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
					 //	$(".demo--radio").attr("checked",false);
					}
			});
	 };
	//修改:增加所有轨迹在感兴趣区域
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
				 //	$(".demo--radio").attr("checked",false);
				}
		});
		 
}
	);
	//以上：修改增加所有轨迹
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
	 $("hierarchical-cluster").click(function(){
		 
	 });
	//show single path
	$("#show_single_data").click(function(){
	//
	  //  if (document.getElementById("frequency").style.display = 'block')
			 //  if (document.getElementById('fuzzy-search-id').value.)
		        //    document.getElementById('frequency').style.display = 'none';
		      //  else
		            document.getElementById('frequency').style.display = 'block'
		//
		var taxiId = $("#fuzzy-search-id").val();
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
		});}
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
	
	$("#show_pick_drop_points").click(function(){
		
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

