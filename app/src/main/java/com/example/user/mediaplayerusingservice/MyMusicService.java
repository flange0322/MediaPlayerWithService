package com.example.user.mediaplayerusingservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import java.io.IOException;

public class MyMusicService extends Service {

    private MyBinder mBinder = new MyBinder();
    private int i = 0;
    private String[] musicPath = new String[]{
            Environment.getExternalStorageDirectory() + "/Music/BeautifulSpider.mp3",
            Environment.getExternalStorageDirectory() + "/Music/bebiaibuyu.mp3",
            Environment.getExternalStorageDirectory() + "/Music/LucidDream.mp3",
            Environment.getExternalStorageDirectory() + "/Music/Toseethefuture.mp3"
    };

    public MediaPlayer mMediaPlayer = new MediaPlayer();

    public MyMusicService(){
        iniMediaPlayerFile(i);
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder{

        public MediaPlayer getMediaPlayer(){
            return mMediaPlayer;
        }

        public void play_pauseMusic(){
            if(!mMediaPlayer.isPlaying()){
                mMediaPlayer.start();
            }
            else if(mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }
        }

        public void resetMusic(){
            mMediaPlayer.reset();
            iniMediaPlayerFile(i);
        }

        public void close(){
            if(mMediaPlayer != null){
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }

        public void nextMusic(){
            if(mMediaPlayer != null){
                mMediaPlayer.reset();
                i = i + 1;
                if(i == musicPath.length){
                    i = 0;
                }
                iniMediaPlayerFile(i);
            }
        }

        public void previousMusic(){
            if(mMediaPlayer != null){
                mMediaPlayer.reset();
                i = i - 1;
                if(i == -1){
                    i = musicPath.length - 1;
                }
                iniMediaPlayerFile(i);
            }
        }

        public int getProgress(){
            return mMediaPlayer.getDuration();
        }

        public int getPlayPosition(){
            return mMediaPlayer.getCurrentPosition();
        }

        public void seekToPosition(int msec){
            mMediaPlayer.seekTo(msec);
        }

        public void OnCompletion(){
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    nextMusic();
                    play_pauseMusic();
                }
            });
        }
    }

    private void iniMediaPlayerFile(int dex){

        try{
            mMediaPlayer.setDataSource(musicPath[dex]);
            mMediaPlayer.prepare();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
