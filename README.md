# BehaviorDroid
Monitoring framework for Android.


## How to use

1. Download and copy the folder [*behaviordroid*](https://github.com/alexissilva/behaviordroid/tree/master/app/src/main/java/behaviordroid) in your project.
2. Declare the class [DroidService](https://github.com/alexissilva/behaviordroid/blob/master/app/src/main/java/behaviordroid/DroidService.java) as service in the *manifest* and add permissions to read and write in external memory and to read logs.
3. Call the method *adjustIfNeeded* of the class [AdjustReadLogsPermission](https://github.com/alexissilva/behaviordroid/blob/master/app/src/main/java/behaviordroid/util/AdjustReadLogsPermission.java) in the *onCreate* of the main activity of your app.
4. Configure the framework...  **more details soon**.
5. Call the method *startService* in a event (like to touch a button) to start framework. To stop it, call to *stopService*.
6. Copy configuration files and strace on your device. You can download example files [here](https://github.com/alexissilva/behaviordroid/tree/master/example_files).

This is a testing project, use it as example.

