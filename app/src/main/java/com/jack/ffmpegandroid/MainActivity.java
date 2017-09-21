package com.jack.ffmpegandroid;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.jack.ffmpegandroid.utils.PermissionUtils;
import com.jack.ffmpegandroid.utils.UriUtils;
import com.jack.test.logger.Log;
import com.jack.test.logger.LogCatWrapper;
import com.jack.test.logger.LogFragment;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivity";

    private FFmpeg mFFmpeg;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.edit_cmd);

        mFFmpeg = FFmpeg.getInstance(this);

        final android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final LogFragment fragment = new LogFragment();
        transaction.replace(R.id.framelog, fragment);

        LogCatWrapper logcat = new LogCatWrapper();
        logcat.setNext(fragment.getLogView());
        Log.setLogNode(logcat);
    }

    public void selectVideo(View view) {
        if (PermissionUtils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openVideo();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (PermissionUtils.getTargetSdkVersion(this) < 23 && !PermissionUtils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    target.showDenyForStore();
                    return;
                }

                if (PermissionUtils.verifyPermissions(grantResults)) {
                    openVideo();
                } else {
                    Toast.makeText(this, "为获取到sd卡权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void openVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void executeCmd(View view) {
        execute(mEditText.getText().toString().trim());
    }

    private void execute(final String... cmd) {
        try {
            mFFmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d(TAG, "onSuccess:" + message);
                }

                @Override
                public void onProgress(String message) {
                    Log.d(TAG, "onProgress:" + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onFailure:" + message);
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "onStart cmd:" + cmd[0]);
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "onFinish");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.d(TAG, "error:" + "FFmpegCommandAlreadyRunningException");
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String videoPath = UriUtils.getPath(this, uri);

            android.util.Log.d(TAG, videoPath);

            //剪切板
            ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text_label", videoPath);
            cmb.setPrimaryClip(clip);
            Toast.makeText(this, "路径已经复制到剪切板", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
