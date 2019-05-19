<%@page import="com.hgc.entity.ProviceCount"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	import="java.util.List" import="com.hgc.entity.CityCount"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>city-count</title>

<!-- Bootstrap -->
<link href="${pageContext.request.contextPath}/CSS/bootstrap.min.css"
	rel="stylesheet">
<script src="${pageContext.request.contextPath}/JS/echarts.min.js"></script>
<script src="${pageContext.request.contextPath}/JS/china.js"></script>
<script src="${pageContext.request.contextPath}/JS/jQuery.js"></script>
<script src="${pageContext.request.contextPath}/JS/bootstrap.min.js"></script>
<style>
.mapDiv {
	width: 100%;
	height: 780px;
}

.loading {
	width: 100%;
	height: 100%;
	position: fixed;
	top: 0;
	left: 0;
	z-index: 100;
	background-color: #fff;
}

.loading .pic {
	width: 160px;
	height: 22px;
	background: url(${pageContext.request.contextPath}/PICTURE/load.gif);
	position: absolute;
	top: 0;
	left: 0;
	bottom: 0;
	right: 0;
	margin: auto;
}
</style>
</head>

<% String table=(String)request.getAttribute("table"); //表名，用于显示时间%>
<% List<CityCount> lCityCounts= (List<CityCount>)request.getAttribute("cityCount");//表字段值集合，用于下拉数据的显示和echarts数据的加载%>
<% List<ProviceCount> proviceCounts= (List<ProviceCount>)request.getAttribute("provinceCount");%>

<body>

	<!-- loading animation -->
	<div class="loading">
		<div class="pic"></div>
	</div>
	<!-- page按钮，ul为其展开列表，使用a标签请求服务器，服务器返回再重新加载success-copy页面 -->
	<button type="button" class="btn btn-info"
		style="position: absolute; top: 30%; z-index: 1; width: 7%"
		data-toggle="dropdown">page</button>
	<ul class="dropdown-menu"
		style="position: absolute; top: 35%; z-index: 1; width: 5%">
		<li><a href="${pageContext.request.contextPath}/login/down">
				previous page</a></li>
		<li><a href="${pageContext.request.contextPath}/login/up">
				next page</a></li>
	</ul>

	<!-- City按钮，显示城市数据 -->
	<button type="button" class="btn btn-danger" data-toggle="modal"
		data-target=".city"
		style="position: absolute; top: 20%; z-index: 1; width: 7%">City</button>
	<!-- Province按钮，显示省数据 -->
	<button type="button" class="btn btn-primary" data-toggle="modal"
		data-target=".province"
		style="position: absolute; top: 15%; z-index: 1; width: 7%">Province</button>
		
	<button type="button" class="btn btn-success" data-toggle="modal"
		data-target=".news"
		style="position: absolute; top: 10%; z-index: 1; width: 7%">News</button>

	<!-- 显示时间，对应数据库中的表名 -->
	<button type="button" class="btn btn-warning"
		style="position: absolute; top: 25%; z-index: 1; width: 7%"><%= table %></button>

	<!-- City下拉显示 -->
	<div class="modal fade city" tabindex="-1" role="dialog"
		aria-labelledby="myLargeModalLabel">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<!-- city，count统计总数 -->
					<h4 class="modal-title" id="gridSystemModalLabel">
						City:<%=lCityCounts.size() %>
						Count:<% int count=0;for(int i=0;i<lCityCounts.size();i++){count+=lCityCounts.get(i).getCount();} out.print(count); %>
					</h4>
				</div>

				<div class="modal-body" style="text-align: center">
					<!-- 循环显示数据 -->
					<div class="row">
						<% for(int i=0;i<lCityCounts.size();i++){out.print("<h4>City:"+lCityCounts.get(i).getCity()+"----Count:"+lCityCounts.get(i).getCount()+"</h4><br/>");} %>
					</div>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>

			</div>
		</div>
	</div>
	
	<!-- News下拉显示 -->
	<div class="modal fade news" tabindex="-1" role="dialog"
		aria-labelledby="myLargeModalLabel">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4>add the url of news</h4>
				</div>

				<form action="${pageContext.request.contextPath}/login/usePython" method="post">
				  <div class="form-group">
				    <label for="news">News</label>
				    <input type="text" class="form-control" id="news" placeholder="news" name="url">
				  </div>
				  <button type="submit" class="btn btn-info btn-block">Submit</button>
				</form>

			</div>
		</div>
	</div>

	<!-- Pro
	province下拉显示 -->
	<div class="modal fade province" tabindex="-1" role="dialog"
		aria-labelledby="myLargeModalLabel">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<!-- city，count统计总数 -->
					<h4 class="modal-title" id="gridSystemModalLabel">
						Province:<%= proviceCounts.size() %>
						Count:<% int provinceCount=0;for(int i=0;i<proviceCounts.size();i++){provinceCount+=proviceCounts.get(i).getCount();} out.print(provinceCount); %>
					</h4>
				</div>

				<div class="modal-body" style="text-align: center">
					<!-- 循环显示数据 -->
					<div class="row">
						<% for(int i=0;i<proviceCounts.size();i++){out.print("<h4>Province:"+proviceCounts.get(i).getProvice()+"----Count:"+proviceCounts.get(i).getCount()+"</h4><br/>");} %>
					</div>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>

			</div>
		</div>
	</div>

	<div class="mapDiv" id="map"></div>

	<script>
	//只适用于文档加载，请求服务器无效
		document.onreadystatechange = function () {//即在加载的过程中执行下面的代码(进度)
			if(document.readyState=="complete"){//complete加载完成
				$(".loading").fadeOut();
			}
		}
		
		var mycharts = echarts.init(document.getElementById('map'))
		//城市名称和对应的经纬度
		var geoCoordMap = {
			<% for(int i=0;i<lCityCounts.size();i++){out.print("\""+lCityCounts.get(i).getCity()+"\":["+lCityCounts.get(i).getLng()+","+lCityCounts.get(i).getLat()+"],");} %>
			};
		//æ¼æ¥å¯¹è±¡æ°ç»
		var convertData = function (data) {
		    var res = [];
		    for (var i = 0; i < data.length; i++) {
		        var geoCoord = geoCoordMap[data[i].name];//è·ååæ 
		        if (geoCoord) {//å¦ææåæ 
		        	res.push({//åå»ºå¯¹è±¡æ°ç»ï¼
	                    name: data[i].name,  //åå¸åç§°
	                    value: geoCoord.concat(data[i].value)//
	                });  
		            //res.push(geoCoord.concat(data[i].value));
		            //res.push(geoCoord.concat(data[i].name));
		        }
		    }
		    return res;
		};
		option = {
		    backgroundColor: '#FFFFFF',//æ´ä¸ªç»å¸èæ¯
		    //æ é¢ç»ä»¶
		    title: {
		        text: '基于Python地理空间大数据的网络舆情监测研究',
		        left: 'center',
		        textStyle: {
		            color: '#696969'
		        }
		    }, 
		    //鼠标显示
		    tooltip: {
		        trigger: 'item',
		        "confine": true,
	            "formatter": (p)=>{//èªå®ä¹æç¤ºä¿¡æ¯
	              	let dataCon = p.data;
	                txtCon =dataCon.name+'<br>评论数量:'+dataCon.value[2];
	                return txtCon;
	            }
		    },
		    //图例
		    visualMap: {
		        min: 0,//æå°
		        max: 10,//æå¤§
		        splitNumber: 5,//å±å5å±
		        color: ['#ff6300','#eac736','#50a3ba'],//é¢è²ä»é«å°ä½ä¾æ¬¡æ¸å
		        textStyle: {
		            color: '#696969'
		        }
		    },
		    //å°å¾åºåæ ·å¼
		    geo: {
		        map: 'china',
		        label: {
		            emphasis: {
		                show: false
		            }
		        },
		        itemStyle: {
		        	//未激活样式
		            normal: {
		                areaColor: '#FFFFFF',
		                borderColor: '#111'
		            },
		            //激活样式
		            emphasis: {
		                areaColor: '#CCCCCC'
		            }
		        }
		    },
		    //æ°æ®
		    series: [
		        {
		            name: '舆论',
		            type: 'scatter',
		            coordinateSystem: 'geo',
		            data: convertData([
		            	<% for(int i=0;i<lCityCounts.size();i++){out.print("{name: '"+lCityCounts.get(i).getCity()+"', value: "+lCityCounts.get(i).getCount()+"},");} %>
			            ]),
		            symbolSize: 24,
		            //ç´æ¥å¨ç¹ä¸æ¾ç¤ºæ ç­¾
		            label: {
		            	show:false,
	//	                normal: {
	//	                    show: true
	//	                },
	//	                emphasis: {
	//	                    show: true
	//	                },
		                formatter:'{b}',
		                offset:[15,-15],//æå­çç¸å¯¹åç§»
		            },
		            //æ ç­¾çæ ·å¼
		            itemStyle: {
		                emphasis: {
		                    borderColor: '#fff',
		                    borderWidth: 1
		                }
		            }
		        }
		    ]
		}
		mycharts.setOption(option);
	</script>
</body>
</html>