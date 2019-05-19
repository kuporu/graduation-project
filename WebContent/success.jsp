<%@ page language="java" contentType="text/html; charset=utf-8"
	import="java.util.List" import="com.hgc.entity.CityCount"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/CSS/ol.css" type="text/css">
<style>
.chart {
	width: 600px;
	heigth: 400px
}

.mapDiv {
	width: 90%;
	height: 700px;
	float: left;
}

.inputTextTitle {
	font-size: 30px;
	width: 100%;
	background: rgba(0, 0, 0, 0);
	text-align: center;
	color: #696969;
}

.inputDiv {
	float: left;
	width: 10%;
	height: 95%;
}

.inputText {
	width: 100%;
}

.a {
	text-decoration: none
}

.pageDiv {
	float: left;
	width: 50%;
	text-align: center;
}

.cityCountText {
	width: 100%;
	height: 680px;
	border-width: 0;
}

.textArea {
	border-width: 0
}

.textAreaDiv {
	margin-top: 20%
}
</style>
<script src="${pageContext.request.contextPath}/JS/jQuery.js"></script>
<script src="${pageContext.request.contextPath}/JS/echarts.min.js"></script>
<script src="${pageContext.request.contextPath}/JS/china.js"></script>
<title>Insert title here</title>
</head>

<% String table=(String)request.getAttribute("table"); //声明tableList集合,使用session是应为a标签为跳转，没有携带request请求域%>
<% List<CityCount> lCityCounts= (List<CityCount>)request.getAttribute("cityCount");%>
<body>
	<div id="text">
		<input type="text" class="inputTextTitle" value="<%= table %>"
			id="inputTextTitle">
	</div>

	<div id="map" class="mapDiv"></div>

	<div class="inputDiv">
		<div class="pageDiv">
			<a class="a" href="${pageContext.request.contextPath}/login/down">下一页</a>
		</div>
		<div class="pageDiv">
			<a class="a" href="${pageContext.request.contextPath}/login/up">上一页</a>
		</div>
		<div class="textAreaDiv">
			<textarea class="textArea" id="textarea" rows="44" cols="18">
				<% for(int i=0;i<lCityCounts.size();i++){out.print("CITY:"+lCityCounts.get(i).getCity()+"&#10COUNT:"+lCityCounts.get(i).getCount()+"&#10");} %>
			</textarea>
		</div>
	</div>

	<script type="text/javascript">
	var mycharts = echarts.init(document.getElementById('map'))
	//自定义城市坐标菜单
	var geoCoordMap = {
			<% for(int i=0;i<lCityCounts.size();i++){out.print("\""+lCityCounts.get(i).getCity()+"\":["+lCityCounts.get(i).getLng()+","+lCityCounts.get(i).getLat()+"],");} %>
	   
	};
	//拼接对象数组
	var convertData = function (data) {
	    var res = [];
	    for (var i = 0; i < data.length; i++) {
	        var geoCoord = geoCoordMap[data[i].name];//获取坐标
	        if (geoCoord) {//如果有坐标
	        	res.push({//创建对象数组，
                    name: data[i].name,  //城市名称
                    value: geoCoord.concat(data[i].value)  //用连接数组的形式将value值加入，城市坐标，城市数量
                });  
	            //res.push(geoCoord.concat(data[i].value));
	            //res.push(geoCoord.concat(data[i].name));
	        }
	        else{//如果没有坐标
	        	geoCoord=[120.33,36.07];
	        	res.push({//创建对象数组，
                    name: data[i].name,  //城市名称
                    value: geoCoord.concat(data[i].value)  //用连接数组的形式将value值加入，城市坐标，城市数量
                }); 
	        }
	    }
	    return res;
	};
	option = {
	    backgroundColor: '#FFFFFF',//整个画布背景
	    //标题组件
	    /* title: {
	        text: '全国主要城市空气质量',
	        subtext: 'data from PM25.in',
	        sublink: 'http://www.pm25.in',
	        left: 'center',
	        textStyle: {
	            color: '#696969'
	        }
	    }, */
	    //提示框组件
	    tooltip: {
	        trigger: 'item',
	        "confine": true,
            "formatter": (p)=>{//自定义提示信息
              	let dataCon = p.data;
                txtCon =dataCon.name+'<br>评论数量:'+dataCon.value[2];
                return txtCon;
            }
	    },
	    //左下lengend
	    visualMap: {
	        min: 0,//最小
	        max: 10,//最大
	        splitNumber: 5,//共分5层
	        color: ['#ff6300','#eac736','#50a3ba'],//颜色从高到低依次渐变
	        textStyle: {
	            color: '#696969'
	        }
	    },
	    //地图区域样式
	    geo: {
	        map: 'china',
	        label: {
	            emphasis: {
	                show: false
	            }
	        },
	        itemStyle: {
	        	//未激活样式
	            normal: {
	                areaColor: '#FFFFFF',
	                borderColor: '#111'
	            },
	            //激活样式
	            emphasis: {
	                areaColor: '#CCCCCC'
	            }
	        }
	    },
	    //数据
	    series: [
	        {
	            name: 'pm2.5',
	            type: 'scatter',
	            coordinateSystem: 'geo',
	            data: convertData([
	            	<% //for(int i=0;i<lCityCounts.size();i++){out.print("{name: \"新疆阿克苏地区\", value: 1},");} %>
	            	<% for(int i=0;i<lCityCounts.size();i++){out.print("{name: '"+lCityCounts.get(i).getCity()+"', value: "+lCityCounts.get(i).getCount()+"},");} %>
	            ]),
	            symbolSize: 24,
	            //直接在点上显示标签
	            label: {
	            	show:false,
//	                normal: {
//	                    show: true
//	                },
//	                emphasis: {
//	                    show: true
//	                },
	                formatter:'{b}',
	                offset:[15,-15],//文字的相对偏移
	            },
	            //标签的样式
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