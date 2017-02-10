package com.guo.surfaceclient;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private boolean prepared;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceView.setKeepScreenOn(true);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.setFixedSize(300, 400);
        holder.addCallback(this);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                prepared = true;
            }
        });

        mediaController = new MediaController(this);
        mediaController.setMediaPlayer(new MediaPlayControlImp(mediaPlayer));
        mediaController.setAnchorView(surfaceView);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prepared) {

                    mediaPlayer.start();
                } else {
                    Log.d(TAG, "has not prepare,please wait");
                }
            }
        });
        findViewById(R.id.pick).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                mediaPlayer.setDataSource(this,data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mediaController.setVisibility(View.VISIBLE);
        mediaController.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mediaController.isShowing()){
            mediaController.hide();
        }else {
            mediaController.show();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://com.guo.surfaceclient/raw/welcome"));
            mediaPlayer.prepare();
            Log.d(TAG, "media prepare");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        mediaPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}

class MediaPlayControlImp implements MediaController.MediaPlayerControl {

    private WeakReference<MediaPlayer> mediaPlayerWeakReference ;

    public MediaPlayControlImp(MediaPlayer mediaPlayer) {
        this.mediaPlayerWeakReference = new WeakReference<MediaPlayer>(mediaPlayer);
    }

    private MediaPlayer getMediaPlayer() {
        return mediaPlayerWeakReference.get();
    }

    @Override
    public void start() {
        getMediaPlayer().start();
    }

    @Override
    public void pause() {
        getMediaPlayer().pause();
    }

    @Override
    public int getDuration() {
        return getMediaPlayer().getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return getMediaPlayer().getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        getMediaPlayer().seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return getMediaPlayer().isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return getMediaPlayer().getAudioSessionId();
    }
}
