# sqlextractor
简单实现对SQL文件中`insert`语句、`update`语句、`delete`语句的提取操作

## 启动示例：
```shell
# 编译项目
mvn clean package -DskipTest=true
# 进入编译结果目录
# 运行项目
java -jar custom-sqlextractor-1.0-SNAPSHOT-jar-with-dependencies.jar --dir=解析SQL文件目录 --targetDir=SQL语句结果目录

```

