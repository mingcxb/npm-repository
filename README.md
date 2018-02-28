# npm-repository
npm repository proxy

用于npm本地代理或者内网仓库，仅支持npm install命令。

环境：
  jdk 8
  maven 3

配置文件: application.properties

  #代理服务器主机
  server.hostname=localhost
  #端口
  server.port=3000
  
编译：

  mvn clean package
  
启动：

  java -jar npm-repository-0.0.1-SNAPSHOT.jar --server.hostname=localhost

使用：

  1.访问：http://localhost:3000/，确实是否正常运行；
  
  2.配置npm仓库地址：npm config set registry http://localhost:3000/

