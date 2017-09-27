## Just a test for using [FFmpeg Android Java]
The link is [ffmpeg-android-java](https://github.com/WritingMinds/ffmpeg-android-java), and the gradle dependency is on [writingminds.github.io/ffmpeg-android-java] (http://writingminds.github.io/ffmpeg-android-java/)

## test日志完成情况
1. 选择视频返回视频路径到剪切板
2. 权限判断,参考了PermissionDispatcher库。

## Caption
The test will not updated at all,the use of ffmpeg can be found in [ffmpeg-android-java](https://github.com/WritingMinds/ffmpeg-android-java).
The key is as follows:

1. According to the cpu arch ,copy the executable file which is compiled ffmpeg from  assert to app-file which called F.
2. Enter param and Add the F's absolutely path to string[] param,the api is :
```java
String[] ffmpegBinary = new String[] { FileUtils.getFFmpeg(context, environvenmentVars) };
String[] command = concatenate(ffmpegBinary, cmd);
```
3. Finally, run 
`Process process = Runtime.getRuntime().exec(commandString)`