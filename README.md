# **Recovery**
A crash recovery framework!

----

[ ![Download](https://api.bintray.com/packages/sunzxyong/maven/Recovery/images/download.svg) ](https://bintray.com/sunzxyong/maven/Recovery/_latestVersion) ![build](https://img.shields.io/badge/build-passing-blue.svg) [![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/Sunzxyong/Recovery/blob/master/LICENSE)

[中文文档](https://github.com/Sunzxyong/Recovery/blob/master/README-Chinese.md)

# **Introduction**

[Blog entry with introduction](http://zhengxiaoyong.me/2016/09/05/Android%E8%BF%90%E8%A1%8C%E6%97%B6Crash%E8%87%AA%E5%8A%A8%E6%81%A2%E5%A4%8D%E6%A1%86%E6%9E%B6-Recovery)

“Recovery” can help you to automatically handle application crash in runtime. It provides you with following functionality:

* Automatic recovery activity with stack and data;
* Ability to recover to the top activity;
* A way to view and save crash info;
* Ability to restart and clear the cache;
* Allows you to do a restart instead of recovering if failed twice in one minute.

# **Art**
![recovery](http://7xswxf.com2.z0.glb.qiniucdn.com/blog/Recovery_main.png)

# **Usage**
## **Installation**
**Using Gradle**

```gradle
compile 'com.zxy.android:recovery:0.0.8'
```

**Using Maven**

```xml
<dependency>
  	<groupId>com.zxy.android</groupId>
  	<artifactId>recovery</artifactId>
  	<version>0.0.8</version>
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
        .callback(new MyCrashCallback())
        .init(this);
```
and grant permission:

```
android.permission.GET_TASKS
```

If you don't want to show the RecoveryActivity when the application crash in runtime,you can use silence recover to restore your application.

You can use this code sample to initialize Recovery in your application:

```java
Recovery.getInstance()
        .debug(true)
        .recoverInBackground(false)
        .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
        .init(this);
```

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
}
```

## **Custom Theme**

You can customize UI by setting these properties in your styles file:

```xml
<color name="recoveryColorPrimary">#F44336</color>
<color name="recoveryColorPrimaryDark">#D32F2F</color>
<color name="recoveryColorAccent">#BDBDBD</color>
<color name="recoveryTextColor">#FFFFFF</color>
```

## **Crash File Path**
> {SDCard Dir}/Android/data/{packageName}/files/recovery_crash/

----
## **Update history**
`VERSION-0.0.5`——**Support silent recovery**
`VERSION-0.0.6`——**Strengthen the protection of silent restore mode**
`VERSION-0.0.7`——**Add confusion configuration**
`VERSION-0.0.8`——**Add the skip Activity features,method:skip()**

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

