# 定时Finereport报表邮件发送

## 1. Download or Git clone project ##
```
    git@gitlab.esf.fangdd.net:esf_datawarehouse/bi-email.git
```
## 2. Config Info ##
* Mysql connection
```
    jdbc:mysql://10.50.23.208/fangdd_esf_data
```
* Finereport CPT
    * 参考bi-system设置
* Email Host
```
    private static String mailHost = "mail.fangdd.com";
    private static String mailFrom = "bigdata_notice@fangdd.com";
    private static String mailUserName = "bigdata_notice@fangdd.com";
    private static String mailPwd = "data@FDD2016";
    private static int port = 587;
``` 


## 3. Time Schedule ##
* step1 打包成可运行.Jar文件，上传到线上HDFS

* step2 workflow参数设置
    * 邮件类型："周报"、"月报"、"全部""
    * 发送类型："预发"、"正式"
    * 发送日期："yyyy-MM-dd"

* step3 cordinator参数设置
    * 预发时间：07:30
    * 正式时间：09:00
    * dt = ${coord:formatTime(coord:dateOffset(coord:nominalTime(),-1,'DAY'), 'yyyyMMdd')}