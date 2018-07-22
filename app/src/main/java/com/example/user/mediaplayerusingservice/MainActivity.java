package com.example.user.mediaplayerusingservice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler mHandler = new Handler();
    private SimpleDateFormat time = new SimpleDateFormat("m:ss");
    private MyMusicService.MyBinder mMyBinder;
    private Button playBtn;
    private Button nextBtn;
    private Button previousBtn;
    private SeekBar mSeekBar;
    private TextView mTextView;
    private TextView mTextView_final;
    private int checkingMaxSeekBar;
    Intent MediaService;
    Bundle dataKeeper_Main2toMain;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniView();

        dataKeeper_Main2toMain = this.getIntent().getExtras();
        int number = dataKeeper_Main2toMain.getInt("Song");

        MediaService = new Intent(this,MyMusicService.class);
        MediaService.putExtra("Song",number);

        startService(MediaService);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                  Manifest.permission.WRITE_EXTERNAL_STORAGE
          }, 1);
        }
        else{
          bindService(MediaService, mServiceConnection, BIND_AUTO_CREATE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    bindService(MediaService,mServiceConnection,BIND_AUTO_CREATE);
                }
                else{
                    Toast.makeText(this,"權限不足，即將退出",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMyBinder = (MyMusicService.MyBinder) iBinder;

            changeIcon();
            mMyBinder.OnCompletion();

            checkingMaxSeekBar = mMyBinder.getProgress();
            mSeekBar.setMax(checkingMaxSeekBar);
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if(b){
                        mMyBinder.seekToPosition(seekBar.getProgress());
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mHandler.post(mRunnable);
        }

        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private void iniView(){
        playBtn = findViewById(R.id.button_Play);
        nextBtn = findViewById(R.id.button_Next);
        previousBtn = findViewById(R.id.button_Previous);
        mSeekBar = findViewById(R.id.seekbar);
        mTextView = findViewById(R.id.textView_Time);
        mTextView_final = findViewById(R.id.textView_TimeFinal);
        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
    }

    public void changeIcon(){
        if(!mMyBinder.getMediaPlayer().isPlaying()){
            playBtn.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
        }
        else{
            playBtn.setBackgroundResource(R.drawable.ic_pause_black_24dp);
        }
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.button_Play:
                mMyBinder.play_pauseMusic();
                changeIcon();
                break;
            case R.id.button_Next:
                if(mMyBinder.getMediaPlayer().isPlaying()) {
                    mMyBinder.nextMusic();
                    mMyBinder.play_pauseMusic();
                }
                else{
                    mMyBinder.nextMusic();
                }
                changeIcon();
                break;
            case R.id.button_Previous:
                if(mMyBinder.getMediaPlayer().isPlaying()) {
                    mMyBinder.previousMusic();
                    mMyBinder.play_pauseMusic();
                }
                else{
                    mMyBinder.previousMusic();
                }
                changeIcon();
                break;
        }
    }

    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        unbindService(mServiceConnection);
        super.onDestroy();
    }


    private Runnable mRunnable = new Runnable() {
        public void run() {
            if(checkingMaxSeekBar!=mMyBinder.getProgress()) {
                checkingMaxSeekBar = mMyBinder.getProgress();
                mSeekBar.setMax(checkingMaxSeekBar);
            }
            mSeekBar.setProgress(mMyBinder.getPlayPosition());
            mTextView.setText(time.format(mMyBinder.getPlayPosition()));
            mTextView_final.setText("-"+time.format(mMyBinder.getProgress()-mMyBinder.getPlayPosition()));
            mHandler.postDelayed(mRunnable,1000);
        }
    };
}
