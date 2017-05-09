package com.duowei.dw_pos.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;


/**
 * Created by Administrator on 2017-01-13.
 */

public class AnimUtils {
    private static final String ACTION_NAME = "animStore";
    private int PX;
    //动画时间
    private int AnimationDuration = 500;
    //正在执行的动画数量
    private int number = 0;
    private Boolean isClean=false;

    private static Context mContext;

    private static AnimUtils anim=null;
    private AnimUtils(){};
    public static synchronized AnimUtils getInstance(Context context){
        mContext=context;
        if(anim==null){
            anim=new AnimUtils();
        }
        return anim;
    }

    public void setPX(int PX) {
        this.PX = PX;
    }

    public void doAnim(final FrameLayout animation_viewGroup, ImageView image, Drawable drawable, int[] start_location){
        if(!isClean){
            setAnim(animation_viewGroup,image,drawable,start_location);
        }else{
            try{
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        animation_viewGroup.removeAllViews();
                    }
                });
                isClean = false;
                setAnim(animation_viewGroup,image,drawable,start_location);
            }catch(Exception e){
                e.printStackTrace();
            }
            finally{
                isClean = true;
            }
        }
    }
    /**
     * 动画效果设置
     * @param drawable
     *       将要加入购物车的商品
     * @param start_location
     *        起始位置
     */
    private void setAnim(final FrameLayout animation_viewGroup, ImageView image, Drawable drawable, int[] start_location){
        //缩放动画效果
        Animation mScaleAnimation = new ScaleAnimation(1.5f,0.5f,1.5f,0.5f,Animation.RELATIVE_TO_SELF,0.1f,Animation.RELATIVE_TO_SELF,0.1f);
        mScaleAnimation.setDuration(AnimationDuration);
        mScaleAnimation.setFillAfter(true);
        final ImageView iview = new ImageView(mContext);
        iview.setImageDrawable(drawable);
        final View view = addViewToAnimLayout(animation_viewGroup,iview,start_location);
        view.setAlpha(1f);//透明度
        //曲线运动
        int[] end_location = new int[2];
        image.getLocationInWindow(end_location);
        int endX = end_location[0]-start_location[0];
        int endY = end_location[1]-start_location[1];
        Animation mTranslateAnimation = new TranslateAnimation(0,endX,0,endY);
        //旋转动画效果
        Animation mRotateAnimation = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(AnimationDuration);
        mTranslateAnimation.setDuration(AnimationDuration);
        AnimationSet mAnimationSet = new AnimationSet(true);
        mAnimationSet.setFillAfter(true);
        mAnimationSet.addAnimation(mRotateAnimation);
        mAnimationSet.addAnimation(mScaleAnimation);
        mAnimationSet.addAnimation(mTranslateAnimation);
        mAnimationSet.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
                number++;
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                number--;
                if(number==0){
                    isClean = true;
                    animation_viewGroup.removeAllViews();
                    Intent mIntent = new Intent(ACTION_NAME);
                    //发送广播
                    mContext.sendBroadcast(mIntent);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(mAnimationSet);
    }
    /**
     * @deprecated 将要执行动画的view 添加到动画层
     * @param vg
     *        动画运行的层 这里是frameLayout
     * @param view
     *        要运行动画的View
     * @param location
     *        动画的起始位置
     * @return
     */
    private View addViewToAnimLayout(ViewGroup vg, View view, int[] location){
        int x = location[0];
        int y = location[1];
        vg.addView(view);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                dip2px(mContext,PX),dip2px(mContext,PX));
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setPadding(5, 5, 5, 5);
        view.setLayoutParams(lp);
        return view;
    }
    /**
     * dip，dp转化成px 用来处理不同分辨路的屏幕
     * @param context
     * @param dpValue
     * @return
     */
    private int dip2px(Context context, float dpValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale +0.5f);
    }
}
