package com.example.tsubasa.kg_testgps_004;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

import static com.example.tsubasa.kg_testgps_004.CommonConstants.*;


/**
 *  メイン処理クラス
 *
 *  @version 1.0
 *  @author Tsubasa
 */
public class RPGView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //システム
    private SurfaceHolder holder;
    private Graphics g;
    private Thread thread;
    private int init = S_START;
    private int scene;
    private int key;
    private Bitmap[][] bmp = new Bitmap[3][10];

    //乱数の取得
    private static Random rand = new Random();

    //勇者パラメーター
    private int yuX = 1;
    private int yuY = 2;
    private int yuDirection = 3;
    private int yuLV = 1;
    private int yuHP = 30;
    private int yuEXP = 0;

    //敵パラメーター
    private int enType;
    private int enHP;

    //BGMプレーヤ
    private int bgmPlayingNumber;//再生中BGM

    //RPGSoundクラス
    private RPGSound rpgSound;
    /**
     * コンストラクタ
     *
     * @param activity
     * @param surfaceView
     */
    public RPGView(Activity activity, SurfaceView surfaceView) {
        super(activity);
        try {
            //フィールド系　ビットマップ読み込み
            bmp[BMP_FIELD][0] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.png_field1);
            bmp[BMP_FIELD][1] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.png_field2);
            bmp[BMP_FIELD][2] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.png_field3);
            bmp[BMP_FIELD][3] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.png_field4);

            //プレイヤー系　ビットマップ読み込み
            bmp[BMP_PLAYER][KEY_LEFT] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.png_player1);
            bmp[BMP_PLAYER][KEY_RIGHT] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.png_player2);
            bmp[BMP_PLAYER][KEY_UP] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.png_player3);
            bmp[BMP_PLAYER][KEY_DOWN] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.png_player4);

            //エネミー系　ビットマップ読み込み
            bmp[BMP_ENEMY][0] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.rpg5);
            bmp[BMP_ENEMY][1] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.rpg6);

            //BGM設定
            rpgSound = new RPGSound(activity);

            //BGM開始
            bgmPlayingNumber = BGM_FIELD;
            bgmPlayingNumber = rpgSound.startBGM(BGM_FIELD, bgmPlayingNumber);

            //サーフェイスホルダーの生成
            holder = surfaceView.getHolder();
            holder.setFormat(PixelFormat.RGBA_8888);
            holder.addCallback(this);

            //画面サイズ指定
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point p = new Point();
            display.getSize(p);
            int dw = D_HEIGTH * p.x / p.y;
            getHolder().setFixedSize(dw, D_HEIGTH);

            //グラフィック生成
            g = new Graphics(holder);
            g.setOrigin((dw - D_WAIDTH) / 2, 0);

        } catch (Exception e) {
            //エラー処理
            e.printStackTrace();
        }
    }

    /**
     * サーフェイスの生成
     * @param holder
     * @return 無し
     */
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * サーフェイスの終了
     * @param holder
     * @return 無し
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }

    /**
     * サーフェイスの変更
     * @param holder
     * @param format
     * @param w
     * @param h
     * @return 無し
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    /**
     * スレッドの開始
     * @param
     * @return 無し
     */
    public void run() {
        try {
            while (thread != null) {
                //シーンの初期化
                if (init >= 0) {
                    scene = init;
                    //スタート
                    if (scene == S_START) {
                        scene = S_MAP;
                        yuX = 1;
                        yuY = 2;
                        yuLV = 1;
                        yuHP = 30;
                        yuEXP = 0;
                        fieldStart();
                    }
                    init = -1;
                    key = KEY_NONE;
                }
                //マップ
                if (scene == S_MAP) {
                    boolean flag = movePlayer();
                    enemyAppear(flag);
                    drawMap();
                }
                //モンスター出現の処理
                else if (scene == S_APPEAR) {
                    encountEnemy();
                }
                //バトルメニューコマンド
                else if (scene == S_COMMAND) {menuCommand();
                }
                //プレイヤー攻撃の処理
                else if (scene == S_ATTACK) {
                    //メッセージ
                    playerAttack();
                    //勝利
                    if (enHP == 0) {
                        battleWin();
                    }
                }
                //モンスター攻撃
                else if (scene == S_DEFENCE) {
                    enemyAttack();
                    //敗北
                    if (yuHP == 0) {
                        battleLose();
                    }
                }
                //逃げる
                else if (scene == S_ESCAPE) {battleEscape();
                }
                //スリープ
                key = KEY_NONE;
                sleep(200);
            }
        } catch (Exception e) {
            //エラー処理
            e.printStackTrace();
        }
    }

    /**
     * 戦闘の「逃げる」こコマンド実行時
     * @param
     * @return 無し
     */
    private void battleEscape() throws InterruptedException {
        //メッセージ
        drawBattle("勇者は逃げた。勇者なのに。");
        waitSelect();

        //逃げる計算
        if (enType == 1 || rand(100) <= 10) {
            drawBattle(EN_NAME[enType] + "は回りこんだ");
            waitSelect();
            init = S_DEFENCE;
        }
        else{
            fieldStart();
        }
    }

    /**
     * フィールド移動状態に戻す
     *
     * @param
     * @return 無し
     */
    private void fieldStart() {
        bgmPlayingNumber = rpgSound.startBGM(BGM_FIELD, bgmPlayingNumber);
        init = S_MAP;
    }

    /**
     * 戦闘プレイヤー敗北時
     *
     * @param
     * @return 無し
     */
    private void battleLose() throws InterruptedException {
        bgmPlayingNumber = rpgSound.startBGM(BGM_REQUIEM, bgmPlayingNumber);
        drawBattle("勇者は力尽きた");
        waitSelect();
        init = S_START;
    }

    /**
     * 戦闘モンスター攻撃時
     * @param　
     * @return 無し
     */
    private void enemyAttack() throws InterruptedException {
        //メッセージ
        drawBattle(EN_NAME[enType] + "の攻撃");
        waitSelect();
        rpgSound.startSE(SE_ENEMY_ATTACK);
        //フラッシュ
        for (int i = 0; i < 10; i++) {
            if (1 % 2 == 0) {
                g.lock();
                g.setColor(Color.rgb(255, 255, 255));
                g.fillRect(0, 0, D_WAIDTH, D_HEIGTH);
                g.unlock();
            } else {
                drawBattle(EN_NAME[enType] + "の攻撃");
            }
            sleep(100);
        }

        //防御の計算
        int damage = EN_ATTACK[enType] - YU_DEFENCE[yuLV] + rand(10);
        if (damage <= 1) {
            damage = 1;
        }
        if (damage >= 99) {
            damage = 99;
        }

        //メッセージ
        drawBattle(damage + "ダメージ受けた！");
        waitSelect();

        //体力の計算
        yuHP = yuHP - damage;

        if (yuHP <= 0) {
            yuHP = 0;
        }
        init = S_COMMAND;
    }

    /**
     * 戦闘プレイヤー勝利時
     * @param　
     * @return 無し
     */
    private void battleWin() throws InterruptedException {
        bgmPlayingNumber = rpgSound.startBGM(BGM_VICTORY, bgmPlayingNumber);
        //メッセージ
        drawBattle(EN_NAME[enType] + "を倒した");
        waitSelect();

        //経験値計算
        yuEXP = yuEXP + EN_EXP[enType];
        if (yuLV < 3 && YU_EXP[yuLV + 1] <= yuEXP) {
            yuLV++;
            drawBattle("レベルアップした");
            waitSelect();
        }

        //エンディング
        if (enType == 1) {
            g.lock();
            g.setColor(Color.rgb(0, 0, 0));
            g.fillRect(0, 0, D_WAIDTH, D_HEIGTH);
            g.setColor(Color.rgb(255, 255, 255));
            g.setTextSize(32);
            String str = "Fin...";
            g.drawText(str, (D_WAIDTH - g.measureText(str)) / 2, 180 - (int) g.getFontMetrics().top);
            g.unlock();
            waitSelect();
            init = S_START;
        }
        fieldStart();
    }

    /**
     * 戦闘プレイヤー攻撃時
     * @param　
     * @return 無し
     */
    private void playerAttack() throws InterruptedException {
        drawBattle("勇者の攻撃");
        waitSelect();
        rpgSound.startSE(SE_PLAYER_ATTACK);
        //フラッシュ
        for (int i = 0; i < 10; i++) {
            drawBattle("勇者の攻撃", i % 2 == 0);
            sleep(100);
        }
        //攻撃の計算
        int damage = YU_ATTACK[yuLV] - EN_DEFENCE[enType] + rand(10);
        if (damage <= 1) {
            damage = 1;
        }
        if (damage >= 99) {
            damage = 99;
        }
        //メッセージ
        drawBattle(damage + "ダメージ与えた！");
        waitSelect();

        //体力の計算
        enHP = enHP - damage;
        if (enHP <= 0) {
            enHP = 0;
        }
        init = S_DEFENCE;
    }

    /**
     * 戦闘コマンド選択時
     * @param　
     * @return 無し
     */
    private void menuCommand() throws InterruptedException {
        drawBattle("    1.攻撃    2.逃げる");
        key = KEY_NONE;
        while (init == -1) {
            if (key == KEY_1) init = S_ATTACK;
            if (key == KEY_2) init = S_ESCAPE;
            sleep(100);
        }
    }

    /**
     * フィールドでモンスターエンカウント時
     * @param　
     * @return 無し
     */
    private void encountEnemy() throws InterruptedException {
        //初期化
        enHP = EN_MAXHP[enType];
        //フラッシュ
        sleep(300);
        for (int i = 0; i < 6; i++) {
            g.lock();
            if (i % 2 == 0) {
                g.setColor(Color.rgb(0, 0, 0));
            } else {
                g.setColor(Color.rgb(255, 255, 255));
            }
            g.fillRect(0, 0, D_WAIDTH, D_HEIGTH);
            g.unlock();
            sleep(100);
        }
        //メッセージ
        drawBattle(EN_NAME[enType] + "あらわれた");
        //BGM切り替え
        if (enType == 0){
            bgmPlayingNumber = rpgSound.startBGM(BGM_BATTLE_GIL, bgmPlayingNumber);
        }
        else if(enType == 1){
            bgmPlayingNumber = rpgSound.startBGM(BGM_BATTLE_BOSS, bgmPlayingNumber);
        }
        waitSelect();
        init = S_COMMAND;
    }

    /**
     * マップ描画処理
     *
     * @param　
     * @return 無し
     */
    private void drawMap() {
        g.lock();
        for (int j = -3; j <= 3; j++) {
            for (int i = -5; i <= 5; i++) {
                int idx = 3;
                if (0 <= yuX + i && yuX + i < MAP[0].length && 0 <= yuY + j && yuY + j < MAP.length) {
                    idx = MAP[yuY + j][yuX + i];
                }
                g.drawBitmap(bmp[BMP_FIELD][idx], D_WAIDTH / 2 - (BMP_SIZE / 2)  + BMP_SIZE * i, D_HEIGTH / 2 - (BMP_SIZE / 2) + BMP_SIZE * j);
            }
        }
        g.drawBitmap(bmp[BMP_PLAYER][yuDirection], D_WAIDTH / 2 - BMP_SIZE / 2, D_HEIGTH / 2 - BMP_SIZE / 2 );

        drawStatus();
        g.unlock();
    }


    /**
     * モンスターエンカウント計算
     * @param flag
     * @return 無し
     */
    private void enemyAppear(boolean flag) {
        if (flag) {
            if (MAP[yuY][yuX] == 0 && rand(10) == 0) {
                enType = 0;
                init = S_APPEAR;
            }
            if (MAP[yuY][yuX] == 1) {
                yuHP = YU_MAXHP[yuLV];
            }
            if (MAP[yuY][yuX] == 2) {
                enType = 1;
                init = S_APPEAR;
            }
        }
    }

    /**
     * フィールドのプレイヤー移動処理
     *
     * @param　
     * @return 無し
     */
    private boolean movePlayer() {
        boolean flag = false;
        if (key == KEY_UP) {
            yuDirection = key;
            if (MAP[yuY - 1][yuX] <= 2) {
                yuY--;
                flag = true;
            }
        } else if (key == KEY_DOWN) {
            yuDirection = key;
            if (MAP[yuY + 1][yuX] <= 2) {
                yuY++;
                flag = true;
            }
        } else if (key == KEY_LEFT) {
            yuDirection = key;
            if (MAP[yuY][yuX - 1] <= 2) {
                yuX--;
                flag = true;
            }
        } else if (key == KEY_RIGHT) {
            yuDirection = key;
            if (MAP[yuY][yuX + 1] <= 2) {
                yuX++;
                flag = true;
            }
        }
        return flag;
    }
    /**
     * 戦闘画面の描画(HP判定)
     *
     * @param message
     * @return 無し
     */
    private void drawBattle(String message) {
        drawBattle(message, enHP >= 0);
    }

    /**
     * 戦闘画面の描画
     *
     * @param message
     * @param visible
     * @return 無し
     */
    private void drawBattle(String message, boolean visible) {
        try {
            int color;
            if (yuHP == 0) {
                color = Color.rgb(255, 0, 0);
            } else {
                color = Color.rgb(0, 0, 0);
            }
            g.lock();
            g.setColor(color);
            g.fillRect(0, 0, D_WAIDTH, D_HEIGTH);
            drawStatus();
            if (visible == true) {
                g.drawBitmap(bmp[BMP_ENEMY][enType], (D_WAIDTH - bmp[BMP_ENEMY][enType].getWidth()) / 2, D_HEIGTH - 100 - bmp[BMP_ENEMY][enType].getHeight());
            }
            g.setColor(Color.rgb(255, 255, 255));
            g.fillRect((D_WAIDTH - 504) / 2, D_HEIGTH - 122, 504, 104);
            g.setColor(color);
            g.fillRect((D_WAIDTH - 500) / 2, D_HEIGTH - 120, 500, 100);
            g.setColor(Color.rgb(255, 255, 255));
            g.setTextSize(32);
            g.drawText(message, (D_WAIDTH - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top);
            g.unlock();
        } catch (Exception e) {
            //エラー処理
            e.printStackTrace();
        }
    }

    /**
     * ステータスの描画
     *
     * @param　
     * @return 無し
     */
    private void drawStatus() {
        try {
            int color;
            if (yuHP == 0) {
                color = Color.rgb(255, 0, 0);
            } else {
                color = Color.rgb(0, 0, 0);
            }
            g.setColor(Color.rgb(255, 255, 255));
            g.fillRect((D_WAIDTH - 504) / 2, 8, 504, 54);
            g.setColor(color);
            g.fillRect((D_WAIDTH - 500) / 2, 10, 500, 50);
            g.setColor(Color.rgb(255, 255, 255));
            g.setTextSize(32);
            g.drawText("勇者 LV" + yuLV + " HP" + yuHP + " /" + YU_MAXHP[yuLV], (D_WAIDTH - 500) / 2 + 80, 15 - (int) g.getFontMetrics().top);
        } catch (Exception e) {
            //エラー処理
            e.printStackTrace();
        }
    }

    /**
     * 画面タッチ時の処理
     *
     * @param　
     * @return 無し
     */
/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            int touchX = (int) (event.getX() * D_WAIDTH / getWidth());
            int touchY = (int) (event.getY() * D_HEIGTH / getHeight());
            int touchAction = event.getAction();
            if (touchAction == MotionEvent.ACTION_DOWN) {

                if (scene == S_MAP) {
                    if (Math.abs(touchX - D_WAIDTH / 2) > Math.abs(touchY - D_HEIGTH / 2)) {
                        key = (touchX - D_WAIDTH / 2 < 0) ? KEY_LEFT : KEY_RIGHT;
                    } else {
                        key = (touchY - D_HEIGTH / 2 < 0) ? KEY_UP : KEY_DOWN;
                    }
                } else if (scene == S_APPEAR || scene == S_ATTACK || scene == S_DEFENCE || scene == S_ESCAPE) {
                    key = KEY_SELECT;
                } else if (scene == S_COMMAND) {
                    if (D_WAIDTH / 2 - 250 < touchX && touchX < D_WAIDTH / 2 && D_HEIGTH - 190 < touchY && touchY < D_HEIGTH) {
                        key = KEY_1;
                    } else if (D_WAIDTH / 2 < touchX && touchX < D_WAIDTH / 2 + 250 && D_HEIGTH - 190 < touchY && touchY < D_HEIGTH) {
                        key = KEY_2;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            //エラー処理
            e.printStackTrace();
            return false;
        }
    }
*/


    /**
     * 決定キー待ち
     *
     * @param　
     * @return 無し
     */
    private void waitSelect() throws InterruptedException {
        key = KEY_NONE;
        while (key != KEY_SELECT) {
            sleep(100);
        }
    }

    /**
     * スリープ
     *
     * @param time
     * @return 無し
     */
    private void sleep(int time) throws InterruptedException {
        Thread.sleep(time);
    }

    /**
     * 乱数取得
     *
     * @param num
     * @return int
     */
    public static int rand(int num) {
        return (rand.nextInt() >>> 1) % num;
    }

    /**
     * 十字ボタンタッチ時の処理
     *
     * @param　
     * @return 無し
     */
    public void onTouchButtonEvent(String Button) {
        if (scene == S_MAP) {
            if (Button.equals("up")) {
                key = KEY_UP;
            }
            else if(Button.equals("down")){
                key = KEY_DOWN;
            }
            else if(Button.equals("left")){
                key = KEY_LEFT;
            }
            else if(Button.equals("right")){
                key = KEY_RIGHT;
            }
        } else if (scene == S_APPEAR || scene == S_ATTACK || scene == S_DEFENCE || scene == S_ESCAPE) {
            key = KEY_SELECT;
        } else if (scene == S_COMMAND) {
            if (Button.equals("up")) {
                key = KEY_1;
            }
            else if(Button.equals("down")){
                key = KEY_2;
            }
        }
    }
}


