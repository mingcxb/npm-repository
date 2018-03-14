# npm-repository
npm repository proxy

用于npm本地代理或者内网仓库，仅支持npm install命令。

**环境：**
  jdk 8
  maven 3

配置文件: application.properties

  #代理服务器主机
  server.hostname=localhost
  #端口
  server.port=3000
  
**编译：**

  mvn clean package
  
**启动：**

  java -jar npm-repository-0.0.1-SNAPSHOT.jar --server.hostname=localhost

**使用：**

  1.访问：http://localhost:3000/，确实是否正常运行；
  
  2.配置npm仓库地址：npm config set registry http://localhost:3000/


**内网使用：**
  1、将npm-repository-0.0.1-SNAPSHOT.jar和同级目录生成的repository仓库文件一同拷贝到内网环境中，部署运行
  2、以后需要增加js依赖，在外网先下载一次，再将repository仓库拷贝到内网
 
 
 **安装node-sass错误解决：**
 
  以window 7、node v9.2.0、npm 5.6.0环境为列，安装node-sass需要从github下载文件，
下载地址为：https://github.com/sass/node-sass/releases/download/v4.7.2/win32-x64-59_binding.node
  手动下载该文件，并将该文件放到repository/my-mirrors/node-sass/v4.7.2/win32-x64-59_binding.node
  最后设置node-sass binary代理地址：
  npm config set sass_binary_site http://localhost:3000/my-mirrors/node-sass
  这样就可以在内网环境中安装node-sass了。
  需要注意的是不同环境使用的binding.node文件，并不相同，所以需要根据具体情况下载对应的文件

