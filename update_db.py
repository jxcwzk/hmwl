#!/usr/bin/env python3
"""
修改数据库字段长度 - 使用mysql.connector
"""

try:
    import mysql.connector
except:
    print("mysql.connector不可用")

# 尝试使用pymysql
try:
    import pymysql as mysqldb
    print("使用pymysql")
except ImportError:
    try:
        import MySQLdb as mysqldb
        print("使用MySQLdb")
    except:
        print("没有可用的MySQL驱动")
        exit(1)

# 连接数据库
connection = mysqldb.connect(
    host='localhost',
    user='root',
    password='Jxcwzk7226456!',
    database='hmwl',
    charset='utf8mb4'
)

try:
    with connection.cursor() as cursor:
        # 修改image_url字段长度
        sql = "ALTER TABLE order_image MODIFY COLUMN image_url varchar(1000) DEFAULT NULL"
        cursor.execute(sql)
        print("字段修改成功!")

    connection.commit()
    print("数据库更新完成!")

except Exception as e:
    print(f"错误: {e}")

finally:
    connection.close()
