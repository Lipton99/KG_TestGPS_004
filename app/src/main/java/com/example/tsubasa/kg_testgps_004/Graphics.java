package com.example.tsubasa.kg_testgps_004;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 *  画面出力に関するクラス
 *
 *  @version 1.0
 *  @author Tsubasa
 */
public class Graphics {
    private SurfaceHolder holder;
    private Paint paint;
    private Canvas canvas;
    private int originX;
    private int originY;

    /**
     * コンストラクタ
     *
     * @param holder
     * @return 無し
     */
    public Graphics(SurfaceHolder holder){
        this.holder = holder;
        paint = new Paint();
        paint.setAntiAlias(true);
    }
    /**
     * ロック
     *
     * @param
     * @return 無し
     */
    public void lock(){
        canvas = holder.lockCanvas();
        if(canvas == null){
            return;
        }
        canvas.translate(originX, originY);
    }
    /**
     * アンロック
     *
     * @param
     * @return 無し
     */
    public void unlock(){
        if(canvas == null){
            return;
        }
        holder.unlockCanvasAndPost(canvas);
    }
    /**
     * 原点の指定
     *
     * @param
     * @return 無し
     */
    public void  setOrigin(int x, int y){
        originX = x;
        originY = y;
    }
    /**
     * 原点Xの取得
     *
     * @param
     * @return int
     */
    public int getOriginX(){
        return originX;
    }
    /**
     * 原点Yの取得
     *
     * @param
     * @return int
     */
    public int getOriginY(){
        return originY;
    }
    /**
     * 色の指定
     *
     * @param color
     * @return 無し
     */
    public void setColor(int color){
        paint.setColor(color);
    }
    /**
     * フォントサイズの指定
     *
     * @param fontSize
     * @return 無し
     */
    public void setTextSize(int fontSize) {
        paint.setTextSize(fontSize);
    }
    /**
     * フォントメトリックスの取得
     *
     * @param
     * @return FontMetrics
     */
    public FontMetrics getFontMetrics(){
        return paint.getFontMetrics();
    }
    /**
     * 文字幅の取得
     *
     * @param string
     * @return int
     */
    public int measureText(String string){
        return (int)paint.measureText(string);
    }
    /**
     * 矩形の描画
     *
     * @param x 幅開始位置
     * @param y 高さ開始位置
     * @param w 幅
     * @param h 高さ
     * @return 無し
     */
    public void fillRect(int x, int y, int w, int h) {
        try {
            if (canvas == null) {
                return;
            }
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(new Rect(x, y, x + w, y + h), paint);
        } catch (Exception e) {
            //エラー処理
            e.printStackTrace();
        }
    }
    /**
     * ビットマップの描画
     *
     * @param bitmap
     * @param x
     * @param y
     * @return 無し
     */
    public  void drawBitmap(Bitmap bitmap, int x, int y){
        try {
            if(canvas == null){
                return;
            }
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Rect src = new Rect(0, 0, w, h);
            Rect dst = new Rect(x, y, x + w, y + h);
            canvas.drawBitmap(bitmap, src, dst, null);
        }catch (Exception e) {
            //エラー処理
            e.printStackTrace();
    }
}
    /**
     * 文字列の描画
     *
     * @param string
     * @param x
     * @param y
     * @return 無し
     */
    public void drawText(String string, int x, int y){
        if(canvas == null){
            return;
        }
        canvas.drawText(string, x, y, paint);
    }
}
