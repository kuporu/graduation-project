import requests
import json
import pymysql

#从txt文件中获取相关城市并重新生成一个列表
province_list=[]
with open('province.txt','r',encoding='utf-8') as f:
    for eachline in f:
        if eachline:
            province=eachline.split('\n')[0]
            province_list.append(province)
    f.close()
#连接数据库，将数据插入数据库表中
db = pymysql.connect("localhost","root","hgc123","graduation" )
cursor = db.cursor()

try:
    cursor.execute("DROP TABLE IF EXISTS PROVINCECOUNT")
except:
    pass
sql = """CREATE TABLE PROVINCECOUNT (
  Province VARCHAR(40) NOT NULL)"""

cursor.execute(sql)


for province in province_list:
    sql = "INSERT INTO PROVINCECOUNT (Province) VALUES ('%s')"
    cursor.execute(sql %(province))
db.commit()
db.close()
