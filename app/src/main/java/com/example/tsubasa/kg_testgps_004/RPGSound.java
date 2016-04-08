package com.example.tsubasa.kg_testgps_004;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import static com.example.tsubasa.kg_testgps_004.CommonConstants.*;


/**
 * Created by Tsubasa on 2016/02/22.
 */
public class RPGSound {

    //BGMプレーヤ
    private MediaPlayer[] mediaPlayer = new MediaPlayer[10];

    //SEプレーヤ
    private SoundPool soundPool;
    private int[] soundId = new int[10];

    /**
     * コンストラクタ
     *
     * @param activity
     */
    public RPGSound(Activity activity){
        //BGM読み込み
        mediaPlayer[BGM_FIELD] = MediaPlayer.create(activity, R.raw.bgm_field);
        mediaPlayer[BGM_BATTLE_NORMAL] = MediaPlayer.create(activity, R.raw.bgm_battle1);
        mediaPlayer[BGM_BATTLE_GIL] = MediaPlayer.create(activity, R.raw.bgm_battle2);
        mediaPlayer[BGM_BATTLE_BOSS] = MediaPlayer.create(activity, R.raw.bgm_battle3);
        mediaPlayer[BGM_VICTORY] = MediaPlayer.create(activity, R.raw.bgm_victory);
        mediaPlayer[BGM_REQUIEM] = MediaPlayer.create(activity, R.raw.bgm_requiem);

        //SE読み込み
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        soundId[SE_PLAYER_ATTACK] = soundPool.load(activity, R.raw.se_player_attack, 1);
        soundId[SE_ENEMY_ATTACK] = soundPool.load(activity, R.raw.se_enemy_attack, 1);
    }

    /** BGM開始
     *
     * @param bgmNumber//再生するBGM番号
     * @param bgmPlayingNumber//再生中のBGM番号
     * @return int
     *
     */
    public int startBGM(int bgmNumber , int bgmPlayingNumber) {
        try{
            //再生中のBGM停止
            stopBGM(bgmPlayingNumber);

            mediaPlayer[bgmNumber].setLooping(true);
            mediaPlayer[bgmNumber].seekTo(0);
            mediaPlayer[bgmNumber].start();
            bgmPlayingNumber = bgmNumber;

        }catch (Exception e){
            e.printStackTrace();
        }
        return bgmPlayingNumber;
    }

    /** BGM停止
     *
     * @param bgmNumber //停止するBGM番号
     * @return int
     *
     */
    public void stopBGM(int bgmNumber) {
        try{
            mediaPlayer[bgmNumber].stop();
            mediaPlayer[bgmNumber].prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** SE再生
     *
     * @param seNumber //再生するSE
     * @return int
     *
     */
    public void startSE(int seNumber) {
        try{
            //サウンドプールのSE再生
            soundPool.play(soundId[seNumber], 100, 100, 1, 0, 1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
