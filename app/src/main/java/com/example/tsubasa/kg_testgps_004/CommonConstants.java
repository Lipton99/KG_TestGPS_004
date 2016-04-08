package com.example.tsubasa.kg_testgps_004;

/**
 * Created by Tsubasa on 2016/02/22.
 */
public class CommonConstants {
    //シーン定数
    public static final int S_START = 0;
    public static final int S_MAP = 1;
    public static final int S_APPEAR = 2;
    public static final int S_COMMAND = 3;
    public static final int S_ATTACK = 4;
    public static final int S_DEFENCE = 5;
    public static final int S_ESCAPE = 6;

    //画面サイズ定数
    public static final int D_WAIDTH = 360;
    public static final int D_HEIGTH = 280;

    //フィールドビットマップサイズ
    public static final int BMP_SIZE = 120;

    //キー定数
    public static final int KEY_NONE = -1;
    public static final int KEY_LEFT = 0;
    public static final int KEY_RIGHT = 1;
    public static final int KEY_UP = 2;
    public static final int KEY_DOWN = 3;
    public static final int KEY_1 = 4;
    public static final int KEY_2 = 5;
    public static final int KEY_SELECT = 6;

    //BMP定数
    public static final int BMP_FIELD = 0;
    public static final int BMP_PLAYER = 1;
    public static final int BMP_ENEMY = 2;

    //BGM定数
    public static final int BGM_FIELD = 0;
    public static final int BGM_BATTLE_NORMAL = 1;
    public static final int BGM_BATTLE_GIL = 2;
    public static final int BGM_BATTLE_BOSS = 3;
    public static final int BGM_VICTORY = 4;
    public static final int BGM_REQUIEM = 5;

    //SE定数
    public static final int SE_PLAYER_ATTACK = 0;
    public static final int SE_ENEMY_ATTACK = 1;

    //マップ定数
    public static final int[][] MAP = {
            {3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
            {3, 0, 0, 0, 0, 3, 0, 0, 0, 3},
            {3, 0, 0, 0, 0, 3, 1, 3, 0, 3},
            {3, 0, 0, 0, 0, 0, 0, 0, 0, 3},
            {3, 0, 0, 0, 3, 0, 0, 0, 0, 3},
            {3, 0, 0, 3, 3, 3, 0, 0, 0, 3},
            {3, 0, 0, 0, 3, 0, 0, 0, 0, 3},
            {3, 0, 0, 0, 0, 0, 0, 3, 0, 3},
            {3, 0, 0, 0, 0, 0, 0, 3, 2, 3},
            {3, 3, 3, 3, 3, 3, 3, 3, 3, 3},};

    //プレイヤー定位数
    public static final int YU_MAXHP[] = {0, 30, 50, 70};
    public static final int YU_ATTACK[] = {0, 5, 10, 30};
    public static final int YU_DEFENCE[] = {0, 5, 10, 15};
    public static final int YU_EXP[] = {0, 3, 6, 9};

    //敵定数
    public  static final String EN_NAME[] = {"ザコ", "ボス"};
    public static final int EN_MAXHP[] = {20, 50};
    public static final int EN_ATTACK[] = {5, 50};
    public static final int EN_DEFENCE[] = {0, 50};
    public static final int EN_EXP[] = {10, 50};
}
