UninstallExampleApp
====================

This project demonstrates how to uninstall an application SILENTLY under Android, even a system application (root required) along with the data, and how to remove its data as well.


���������ʾ����android����Ρ���Ĭ����ж�ص�һ��Ӧ�ã��豸����root��������ж�ص�һ��ϵͳ�Դ���Ӧ�ã��豸��Ҫroot������Ӧ�����ݣ��Լ�����ж��Ƿ�ж�سɹ���


�ο�������������Դ����лԭ���ߣ�
    http://stackoverflow.com/questions/10900928/uninstall-app-silently-with-system-priveleges
    http://stackoverflow.com/questions/9047930/how-to-uninstall-own-app-from-system-app


    
������������������²��ܹ�����
1. Android 2.2�����ϣ�
2. ��װΪϵͳӦ�ã�����װ��ROM��/system/appĿ¼�£�������/data/app��



��Ҫ��

1. ����һ���Ӧ�ã���װ��/data/appĿ¼�µ�Ӧ�ã���ֻ��Ҫͨ��������Ƶ���android.content.pm.PackageManager��������deletePackage�������ɣ�

2. ����ϵͳӦ�ã���װ��/system/appĿ¼�µ�Ӧ�ã�����ҪrootȨ�ޣ�����shell����rm��������룩��

3. ���һ��ϵͳӦ�ô��ڸ��£�����°���װ��/data/appĿ¼�£���Ҫ�ȵ���deletePackage����ж�ظ��£��ٵ���root rm����ɾ��/data/appĿ¼�µ�apk�����򽫳���

4. ��֤Ӧ���Ƿ���ڣ����Ƿ񱻳ɹ�ɾ��������PackageManager.getApplicationInfo���ڶ�������Ϊ0��ѯ���ɣ�ApplicationInfo.sourceDir���������˸�Ӧ�õ�apk��װ·���������/data/app����һ��Ӧ�ã�/system/app����ϵͳӦ�ã�/mnt/asec�����ǰ�װ��SD���ϵ�һ��Ӧ�ã����һ��Ӧ�ñ���װ��SD���ϣ�����SD����ж�أ���PackageManager��ѯ������Ӧ�ã�ע��Ҫ��Ӧ�ñ�ж�ص����������

5. Ӧ�õ������ļ�������/data/data/<PACKAGE>Ŀ¼�£�Ҫɾ�����Ŀ¼������֮һ����shell�������������pm clear PACKAGE



zx.zhangxiong@gmail.com

2012.11.07