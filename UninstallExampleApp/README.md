UninstallExampleApp
====================

This project demonstrates how to uninstall an application SILENTLY under Android, even a system application (root required) along with the data, and how to remove its data as well.


这个程序演示了在android下如何“静默”地卸载掉一个应用（设备无须root），甚至卸载掉一个系统自带的应用（设备需要root）和相应的数据，以及如何判断是否卸载成功。


参考了以下网络资源，感谢原作者！
    http://stackoverflow.com/questions/10900928/uninstall-app-silently-with-system-priveleges
    http://stackoverflow.com/questions/9047930/how-to-uninstall-own-app-from-system-app


    
这个例子在以下条件下才能工作：
1. Android 2.2及以上；
2. 安装为系统应用（即安装到ROM的/system/app目录下，而不是/data/app）



概要：

1. 对于一般的应用（安装到/data/app目录下的应用），只需要通过反射机制调用android.content.pm.PackageManager不公开的deletePackage方法即可；

2. 对于系统应用（安装到/system/app目录下的应用），需要root权限，启动shell运行rm命令（见代码）；

3. 如果一个系统应用存在更新，则更新包安装在/data/app目录下，需要先调用deletePackage方法卸载更新，再调用root rm命令删除/data/app目录下的apk；否则将出错；

4. 验证应用是否存在（即是否被成功删除），用PackageManager.getApplicationInfo，第二个参数为0查询即可；ApplicationInfo.sourceDir变量保存了该应用的apk安装路径，如果是/data/app下是一般应用，/system/app下是系统应用，/mnt/asec下则是安装到SD卡上的一般应用；如果一个应用被安装到SD卡上，并且SD卡被卸载，则PackageManager查询不到该应用，注意要和应用被卸载的情况相区别；

5. 应用的数据文件保存在/data/data/<PACKAGE>目录下，要删除这个目录，方案之一是在shell中运行如下命令：pm clear PACKAGE



zx.zhangxiong@gmail.com

2012.11.07