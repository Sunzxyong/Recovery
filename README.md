# **Recovery**
A crash recovery framework!

----

[ ![Download](https://api.bintray.com/packages/sunzxyong/maven/Recovery/images/download.svg) ](https://bintray.com/sunzxyong/maven/Recovery/_latestVersion) ![build](https://img.shields.io/badge/build-passing-blue.svg) [![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/Sunzxyong/Recovery/blob/master/LICENSE)

[中文文档](https://github.com/Sunzxyong/Recovery/blob/master/README-Chinese.md)

# **Introduction**

[Blog entry with introduction](http://zhengxiaoyong.com/2016/09/05/Android%E8%BF%90%E8%A1%8C%E6%97%B6Crash%E8%87%AA%E5%8A%A8%E6%81%A2%E5%A4%8D%E6%A1%86%E6%9E%B6-Recovery)

“Recovery” can help you to automatically handle application crash in runtime. It provides you with following functionality:

* Automatic recovery activity with stack and data;
* Ability to recover to the top activity;
* A way to view and save crash info;
* Ability to restart and clear the cache;
* Allows you to do a restart instead of recovering if failed twice in one minute.

# **Art**
![recovery](http://7xswxf.com2.z0.glb.qiniucdn.com//blog/recovery.jpg)

# **Usage**
## **Installation**
**Using Gradle**

```gradle
    implementation 'com.zxy.android:recovery:1.0.0'
```

or

```gradle
    debugImplementation 'com.zxy.android:recovery:1.0.0'
    releaseImplementation 'com.zxy.android:recovery-no-op:1.0.0'
```


**Using Maven**

```xml
<dependency>
  	<groupId>com.zxy.android</groupId>
  	<artifactId>recovery</artifactId>
  	<version>1.0.0</version>
  	<type>pom</type>
</dependency>
```

## **Initialization**
You can use this code sample to initialize Recovery in your application:

```java
        Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity.class)
                .recoverEnabled(true)
                .callback(new MyCrashCallback())
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                .skip(TestActivity.class)
                .init(this);
```

If you don't want to show the RecoveryActivity when the application crash in runtime,you can use silence recover to restore your application.

You can use this code sample to initialize Recovery in your application:

```java
        Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity.class)
                .recoverEnabled(true)
                .callback(new MyCrashCallback())
                .silent(true, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                .skip(TestActivity.class)
                .init(this);
```

If you only need to display 'RecoveryActivity' page in development to obtain the debug data, and in the online version does not display, you can set up `recoverEnabled(false);`

## **Arguments**

| Argument | Type | Function |
| :-: | :-: | :-: |
| debug | boolean | Whether to open the debug mode |
| recoverInBackgroud | boolean | When the App in the background, whether to restore the stack  |
| recoverStack | boolean | Whether to restore the activity stack, or to restore the top activity |
| mainPage | Class<? extends Activity> | Initial page activity |
| callback | RecoveryCallback | Crash info callback |
| silent | boolean,SilentMode | Whether to use silence recover，if true it will not display RecoveryActivity and restore the activity stack automatically |

**SilentMode**
> 1. RESTART - Restart App
> 2. RECOVER_ACTIVITY_STACK - Restore the activity stack
> 3. RECOVER_TOP_ACTIVITY - Restore the top activity
> 4. RESTART_AND_CLEAR - Restart App and clear data

## **Callback**

```java
public interface RecoveryCallback {

    void stackTrace(String stackTrace);

    void cause(String cause);

    void exception(
    	String throwExceptionType,
    	String throwClassName,
    	String throwMethodName,
    	int throwLineNumber
    );
    
    void throwable(Throwable throwable);
}
```

## **Custom Theme**

You can customize UI by setting these properties in your styles file:

```xml
    <color name="recovery_colorPrimary">#2E2E36</color>
    <color name="recovery_colorPrimaryDark">#2E2E36</color>
    <color name="recovery_colorAccent">#BDBDBD</color>
    <color name="recovery_background">#3C4350</color>
    <color name="recovery_textColor">#FFFFFF</color>
    <color name="recovery_textColor_sub">#C6C6C6</color>
```

## **Crash File Path**
> {SDCard Dir}/Android/data/{packageName}/files/recovery_crash/

----
## **Update history**
* `VERSION-0.0.5`——**Support silent recovery**
* `VERSION-0.0.6`——**Strengthen the protection of silent restore mode**
* `VERSION-0.0.7`——**Add confusion configuration**
* `VERSION-0.0.8`——**Add the skip Activity features,method:skip()**
* `VERSION-0.0.9`——**Update the UI and solve some problems**
* `VERSION-0.1.0`——**Optimization of crash exception delivery, initial Recovery framework can be in any position, release the official version-0.1.0**
* `VERSION-0.1.3`——**Add 'no-op' support**
* `VERSION-0.1.4`——**update default theme**
* `VERSION-0.1.5`——**fix 8.0+ hook bug**
* `VERSION-0.1.6`——**update**
* `VERSION-1.0.0`——**Fix 8.0 compatibility issue**

## **About**
* **Blog**：[https://zhengxiaoyong.com](https://zhengxiaoyong.com)
* **Wechat**：

![](https://raw.githubusercontent.com/Sunzxyong/ImageRepository/master/qrcode.jpg)
# **LICENSE**

```
   Copyright 2016 zhengxiaoyong

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

