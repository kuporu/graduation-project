#!/usr/bin/env python
# coding=utf-8
from selenium import webdriver
import time as sysTime
import pymysql
#新建数据表
db = pymysql.connect("localhost","root","hgc123","graduation")
cursor = db.cursor()

try:
    cursor.execute("DROP TABLE IF EXISTS CITYCOUNTBYTIME")
except:
    pass
sql = """CREATE TABLE CITYCOUNTBYTIME (
  City VARCHAR(40) NOT NULL,
  Date VARCHAR(40) )"""
cursor.execute(sql)
#获取网页中的数据
browser = webdriver.Chrome("D:\softWare\Chrome\driver\chromedriver_74.exe")
browser.get('http://comment5.news.sina.com.cn/comment/skin/default.html?channel=gn&newsid=comos-hvhiqax2164874&group=0')
#browser.get('http://comment5.news.sina.com.cn/comment/skin/default.html?channel=gn&newsid=comos-hvhiqax8954701&group=0')
def xh():
    t=True
    while t:
        browser.execute_script('''document.documentElement.scrollTop=100000''')
        sysTime.sleep(4)
        try:
            if "已经到底啦~"==browser.find_element_by_class_name('msg').text:
                break
        except:
            xh()
xh()

place=browser.find_elements_by_class_name('area')
father=browser.find_elements_by_css_selector(".item.clearfix")
father_exc=browser.find_elements_by_class_name('dialog-list-wrap')

time_all=[]
time_unnecessary=[]

for son_exc in father_exc:
    time_exc=son_exc.find_elements_by_class_name('time')
    for time_son_exc in time_exc:
        time_unnecessary.append(time_son_exc.text)
for son in father:
    time=son.find_element_by_class_name('time')
    if time.text not in time_unnecessary:
        if(len(time.text)<9):
            time_all.append(str(sysTime.localtime(sysTime.time()).tm_mon)+"月"+str(sysTime.localtime(sysTime.time()).tm_mday)+"日")
        else:
            time_all.append(time.text[:-5])

place_text=[]
for i in range(0,len(place)):
    place_text.append(place[i].text)

sql_add = "insert into CITYCOUNTBYTIME (City,Date) values ('%s','%s')"
for city, time in zip(place_text,time_all):
    cursor.execute(sql_add %(city,time))

browser.close()
db.commit()

try:
    sql_find_date="select c.date from citycountbytime c"
    cursor.execute(sql_find_date)
    results=cursor.fetchall()
    date=[]
    
    for row in results:
        date.append(row[0])
        
    date_set=set(date)
except:
    print("Error:unable to fetch data")

#DDL语句不加""
sql_create_table="""CREATE TABLE %s (
  City VARCHAR(40) NOT NULL,
  Count TINYINT )"""

sql_trop_table="DROP TABLE IF EXISTS %s"

#创建时间表
def createTable(tableName):
    cursor.execute(sql_trop_table %(tableName))
    cursor.execute(sql_create_table %(tableName))
    
sql_find_city="select c.city from citycountbytime c where c.date='%s'"
sql_insert_date_table="insert into %s (city,count) values ('%s',%d)"

for item in date_set:
    
        city=[]
        createTable(item)
        
        
        cursor.execute(sql_find_city %(item))
        results=cursor.fetchall()
       
        for row in results:
            city.append(row[0])
        city_set=set(city)
        
        for city_set_item in city_set:
            cursor.execute(sql_insert_date_table %(item,city_set_item,city.count(city_set_item)))
# commit提交没写会执行前一次的，后一次不会执行
            db.commit()
db.close


