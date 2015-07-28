package org.tbadg.memory;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

public class Music {

    private final String TAG = Music.class.getSimpleName();

    public Music(Context context) {
        mContext = context;
    }

    public void play(int resourceId) {
        Uri uri = new Uri.Builder()
                .scheme("android.resource")
                .authority(mContext.getPackageName())
                .path(String.valueOf(resourceId))
                .build();
        play(uri);
    }

    public void play(Uri musicUri) {
        mMusicUri = musicUri;
        mSeekToPosition = 0;
        play();
    }

    public void play() {
        reset();

        // The member variable isn't set until mOnPreparedListener is called
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mediaPlayer.setOnErrorListener(mOnErrorListener);
            mediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(mContext, mMusicUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
        } catch (IOException | IllegalArgumentException | SecurityException e) {
            Log.e(TAG, "Failed to open the darn music uri", e);
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
    }

    public void resume() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
                setVolumeFromPreferences();
                return;
            }
            if (mMusicUri != null) {
                play();
            }
        } catch (Exception ignored) {
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mSeekToPosition = mMediaPlayer.getCurrentPosition();
        }
        reset();
    }

    private void setVolumeFromPreferences() {
        if (mMediaPlayer == null)
            return;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int volume = sharedPreferences.getInt("prefMusicVolume", 15);
        float volumeLevel = currentVolumeLevel(mContext) * volume / 100.0f;
        mMediaPlayer.setVolume(volumeLevel, volumeLevel);
    }

    public static boolean isResourceLoadingFinished() {
        return resourceLoadingFinished;
    }

    /*
     * Implementation below
     */

    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private Uri mMusicUri;
    private int mSeekToPosition;
    private static boolean resourceLoadingFinished = true;


    private void reset() {
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        mMediaPlayer = null;
    }

    private float currentVolumeLevel(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        return maxVolume <= 0f ? 0f : actualVolume / maxVolume;
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mSeekToPosition != 0) {
                mp.seekTo(mSeekToPosition);
                mSeekToPosition = 0;
            }

            mMediaPlayer = mp;
            setVolumeFromPreferences();
            mp.start();
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e("Music", "onError(): " + what + "  " + extra);
            // return true to avoid a pop-up
            return true;
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // as long as we loop the same media source, this should never be called
            Log.i("Music", "onCompletion()");
        }
    };
}
