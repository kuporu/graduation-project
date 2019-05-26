import requests
import json
import pymysql

#从txt文件中获取相关城市并重新生成一个列表
city_list=[]
with open('city.txt','r',encoding='utf-8') as f:
    for eachline in f:
        if eachline:
            city=eachline.split('\n')[0]
            city_list.append(city)
    f.close()
#定义函数用来访问API得到json数据
def getjson(city):
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/18.17763'
    }
    url='http://api.map.baidu.com/geocoder'
    params={
        'address':city,
        'output':'json',
    }
    response=requests.get(url=url,params=params,headers=headers)
    html=response.text
#python解析json  https://www.cnblogs.com/wangyayun/p/6699184.html?utm_source=tuicool&utm_medium=referral
#将json字符串转换为Python对象
    decodejson=json.loads(html)
    return decodejson
#连接数据库，将数据插入数据库表中
db = pymysql.connect("localhost","root","hgc123","graduation" )
cursor = db.cursor()
for city in city_list:
    if getjson(city).get("result"):
        lat=getjson(city).get("result").get("location").get("lat")
        lng=getjson(city).get("result").get("location").get("lng")
        sql = "INSERT INTO city(name,lat,lng) VALUES ('"+city+"',"+str(lat)+","+str(lng)+")"
    else:
        sql = "INSERT INTO city(name,lat,lng) VALUES ('"+city+"',0,0)"
    try:
        cursor.execute(sql)
        db.commit()
    except:
        db.rollback()
        db.close()

