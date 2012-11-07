package com.example.uninstall;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_query_app_info = (Button)findViewById(R.id.btn_query_app_info);
        btn_query_app_info.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				queryAppInfo();
			}
        });

        Button btn_delete_pkg = (Button)findViewById(R.id.btn_delete_pkg);
        btn_delete_pkg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		    	EditText et_pkg_name = (EditText)findViewById(R.id.et_pkg_name);
		    	String pkgName = et_pkg_name.getText().toString();
				deletePackage(pkgName);
				if (false == findPkgInfo(pkgName)) {
					displayMsg("Succeeded to delete package " + pkgName);
				} else {
					String sourceDir = getPkgSourceDir(pkgName);
					if ((null != sourceDir) && sourceDir.startsWith("/system/app")) {
						displayMsg("Succeeded to delete package update of "  + pkgName + 
								" but its system package " + sourceDir + " remains.");
					} else {
						displayMsg("Failed to delete package " + pkgName);
					}
				}
			}
		});
        
        Button btn_su_rm = (Button)findViewById(R.id.btn_su_rm);
        btn_su_rm.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
		    	EditText et_pkg_name = (EditText)findViewById(R.id.et_pkg_name);
		    	String pkgName = et_pkg_name.getText().toString();
				uninstallSystemPkg(pkgName);
				if (false == findPkgInfo(pkgName)) {
					displayMsg("Succeeded to remove " + pkgName + " along with its data");
				} else {
					displayMsg("Failed to su remove " + pkgName);
				}
			}

        });
    }

    private void displayMsg(String msg) {
    	TextView tv_exist = (TextView)findViewById(R.id.tv_exist);
    	tv_exist.setText(msg);
    }
    
    private void queryAppInfo() {
    	EditText et_pkg_name = (EditText)findViewById(R.id.et_pkg_name);
    	String pkgName = et_pkg_name.getText().toString();
    	boolean found = findPkgInfo(pkgName);
    	String sourceDir = getPkgSourceDir(pkgName);
    	if (found) {
    		displayMsg("source dir: " + sourceDir);
    	} else {
    		displayMsg("not exist");
    	}
    }
	
    private String getPkgSourceDir(String pkgName) {
		String sourceDir = null;
		try {
		    PackageManager pkgManager = MainActivity.this.getPackageManager();
			ApplicationInfo appInfo = pkgManager.getApplicationInfo(
					pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
			sourceDir = appInfo.sourceDir;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return sourceDir;
	}

    private boolean findPkgInfo(String pkgName) {
		try {
		    PackageManager pkgManager = MainActivity.this.getPackageManager();
			pkgManager.getApplicationInfo(pkgName, 0);
			return true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

    /**
     * delete the package with the android internal api of PackageManager
     * Note: 
     *     to make this call taking effect, the application's apk shall be put under ROM's /system/app
     * @param packageName
     */
    private void deletePackage(String packageName) {
        try {
            Class<?> packageManagerClass = Class.forName("android.content.pm.PackageManager");
            Class<?> observerClass = Class.forName("android.content.pm.IPackageDeleteObserver");
            Method method = packageManagerClass.getDeclaredMethod("deletePackage", String.class, observerClass, Integer.TYPE);
            method.invoke(getPackageManager(), packageName, null, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uninstallSystemPkg(String pkgName) {
    	String sourceDir = getPkgSourceDir(pkgName);
    	if ((sourceDir == null) || ("".equals(sourceDir))) {
    		displayMsg("Error: The source dir to be uninstalled is empty");
    		return;
    	}
    	
        try {
            Process process = Runtime.getRuntime().exec("su");
            
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            // make the file system writable
            os.writeBytes("mount -o remount,rw -t rfs /dev/stl5 /system; \n");
            os.flush();
            
            // remove the apk file
            os.writeBytes("rm -r " + sourceDir + "; \n");
            os.flush();
            
            // remove the data directory
            os.writeBytes("rm -r " + "/data/data/" + pkgName + "; \n");
            os.flush();
            
            // restore the file system to read-only
            os.writeBytes("mount -o remount,ro -t rfs /dev/stl5 /system; \n");
            os.flush();
            
            // terminate the shell
            os.writeBytes("exit\n");
            os.flush();
            
            os.close();
            
            int errorCode = process.waitFor();
            displayMsg("Succeeded to execute commands to remove " + sourceDir + ", exit code: " + errorCode);
        } catch (IOException e) {
        	displayMsg("Failed to execute commands to uninstall " + sourceDir);
            e.printStackTrace();
        } catch (InterruptedException e) {
        	displayMsg("Interrupted while exiting root shell");
			e.printStackTrace();
		}
    }
}
