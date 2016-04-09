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

    //画面サイズ
    public int vWidth;
    public int vHeight;

    //勇者パラメーター
    private int yuX = 1;
    private int yuY = 1;
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
            /*
            Display display = activity.getWindowManager().getDefaultDisplay();
            vWidth = display.getWidth();
            vHeight = display.getHeight();
            */

            //グラフィック生成
            g = new Graphics(holder);
            g.setOrigin(0, 0);

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
                        yuY = 1;
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
     * ファイルロード
     * @param
     * @return 無し
     */
    private void gameDataLoad() {
        //TODO:ファイル出力、ファイル読み込み
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
                g.fillRect(0, 0, vWidth, vHeight);
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
            g.fillRect(0, 0, vWidth, vHeight);
            g.setColor(Color.rgb(255, 255, 255));
            g.setTextSize(32);
            String str = "Fin...";
            g.drawText(str, (vWidth - g.measureText(str)) / 2, vHeight / 2 - (int) g.getFontMetrics().top);
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
        drawBattle("↑.攻撃 \n ↓.逃げる");
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
            g.fillRect(0, 0, vWidth, vHeight);
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
        for (int j = 0; j <= MAP.length + PLAYER_SPACE_Y; j++) {
            for (int i = 0; i <= MAP[0].length + PLAYER_SPACE_X; i++) {
                int idx = 3;//MAP範囲外のBitMap(山)
                if (0 <= yuX - PLAYER_SPACE_X + i && yuX - PLAYER_SPACE_X + i < MAP[0].length && 0 <= yuY - PLAYER_SPACE_Y + j && yuY - PLAYER_SPACE_Y + j < MAP.length) {
                    idx = MAP[yuY - PLAYER_SPACE_Y + j][yuX - PLAYER_SPACE_X + i];
                }
                g.drawBitmap(bmp[BMP_FIELD][idx], BMP_SIZE * i,BMP_SIZE * j);
            }
        }
        g.drawBitmap(bmp[BMP_PLAYER][yuDirection], BMP_SIZE * PLAYER_SPACE_X , BMP_SIZE * PLAYER_SPACE_Y );
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
            g.fillRect(0, 0, vWidth, vHeight);
            drawStatus();
            if (visible == true) {
                g.drawBitmap(bmp[BMP_ENEMY][enType], (vWidth - bmp[BMP_ENEMY][enType].getWidth()) / 2, (vHeight - bmp[BMP_ENEMY][enType].getHeight()) / 4);
            }
            g.setColor(Color.rgb(255, 255, 255));
            g.fillRect(vWidth / 4 - 2, (vHeight * 3 / 4) - 2, vWidth / 2 + 4, 204);//白
            g.setColor(color);
            g.fillRect(vWidth / 4, (vHeight * 3 / 4), vWidth / 2, 200);//黒
            g.setColor(Color.rgb(255, 255, 255));
            g.setTextSize(32);
            g.drawText(message, vWidth / 4 + 10, vHeight * 3 / 4 - (int) g.getFontMetrics().top);
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
            //(右上左下 )
            g.fillRect(8, 8, vWidth - 18, 54);
            g.setColor(color);
            g.fillRect(10, 10, vWidth -22 , 50);
            g.setColor(Color.rgb(255, 255, 255));
            g.setTextSize(32);
            g.drawText("勇者 LV" + yuLV + " HP" + yuHP + " /" + YU_MAXHP[yuLV], 20 , 15 - (int) g.getFontMetrics().top);
        } catch (Exception e) {
            //エラー処理
            e.printStackTrace();
        }
    }

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


