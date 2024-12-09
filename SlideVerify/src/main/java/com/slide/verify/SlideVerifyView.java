package com.slide.verify;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.slideverify.R;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SlideVerifyView extends LinearLayout {

    //控件成员
    private PictureVerifyView verifyView;         //拼图块
    private TextSeekbar seekbar;                    //滑动条块
    private View accessSuccess, accessFailed;       //验证成功/失败显示的视图
    private TextView accessText, accessFailedText;  //验证成功/失败显示的文字
    //控件属性
    private int drawableId = -1;          //验证图片资源id
    private int progressDrawableId;  //滑动条背景id
    private int thumbDrawableId;     //滑动条滑块id
    private int mMode;               //控件验证模式(有滑动条/无滑动条)
    private int maxFailedCount;      //最大失败次数
    private int failCount;           //已失败次数
    private int blockSize;           //拼图缺块大小

    //处理滑动条逻辑
    private boolean isResponse;
    private boolean isDown;

    private CaptchaListener mListener;

    /**
     * 带滑动条验证模式
     */
    public static final int MODE_BAR = 1;
    /**
     * 不带滑动条验证，手触模式
     */
    public static final int MODE_NO_BAR = 2;

    @IntDef(value = {MODE_BAR, MODE_NO_BAR})
    public @interface Mode {
    }


    public interface CaptchaListener {

        /**
         * Called when captcha access.
         *
         * @param time cost of access time
         * @return text to show,show default when return null
         */
        String onAccess(long time);

        /**
         * Called when captcha failed.
         *
         * @param failCount fail count
         * @return text to show,show default when return null
         */
        String onFailed(int failCount);

        /**
         * Called when captcha failed
         *
         * @return text to show,show default when return null
         */
        String onMaxFailed();

    }


    public SlideVerifyView(@NonNull Context context) {
        super(context);
    }

    public SlideVerifyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideVerifyView(@NonNull final Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Verify);
        drawableId = typedArray.getResourceId(R.styleable.Verify_src, R.drawable.default_background);
        progressDrawableId = typedArray.getResourceId(R.styleable.Verify_progressDrawable, R.drawable.po_seekbar);
        thumbDrawableId = typedArray.getResourceId(R.styleable.Verify_thumbDrawable, R.drawable.thumb);
        mMode = typedArray.getInteger(R.styleable.Verify_mode, MODE_BAR);
        maxFailedCount = typedArray.getInteger(R.styleable.Verify_max_fail_count, 3);
        blockSize = typedArray.getDimensionPixelSize(R.styleable.Verify_blockSize, Utils.dp2px(getContext(), 50));
        typedArray.recycle();
        init();
    }


    private void init() {
        View parentView = LayoutInflater.from(getContext()).inflate(R.layout.container, this, true);
        verifyView = (PictureVerifyView) parentView.findViewById(R.id.verifyView);
        seekbar = (TextSeekbar) parentView.findViewById(R.id.seekbar);
        accessSuccess = parentView.findViewById(R.id.accessRight);
        accessFailed = parentView.findViewById(R.id.accessFailed);
        accessText = (TextView) parentView.findViewById(R.id.accessText);
        accessFailedText = (TextView) parentView.findViewById(R.id.accessFailedText);
        //刷新按钮
        ImageView refreshView = (ImageView) parentView.findViewById(R.id.refresh);
        setMode(mMode);
        if(drawableId!=-1){
            verifyView.setImageResource(drawableId);
        }
        setBlockSize(blockSize);
        verifyView.callback(new PictureVerifyView.Callback() {
            @Override
            public void onSuccess(long time) {
                if (mListener != null) {
                    String s = mListener.onAccess(time);
                    if (s != null) {
                        accessText.setText(s);
                    } else {//默认文案
                        accessText.setText(String.format(getResources().getString(R.string.verify_access), time));
                    }
                }
                accessSuccess.setVisibility(VISIBLE);
                accessFailed.setVisibility(GONE);
            }

            @Override
            public void onFailed() {
                seekbar.setEnabled(false);
                verifyView.setTouchEnable(false);
                failCount = failCount > maxFailedCount ? maxFailedCount : failCount + 1;
                accessFailed.setVisibility(VISIBLE);
                accessSuccess.setVisibility(GONE);
                if (mListener != null) {
                    if (failCount == maxFailedCount) {
                        String s = mListener.onMaxFailed();
                        if (s != null) {
                            accessFailedText.setText(s);
                        } else {//默认文案
                            accessFailedText.setText(String.format(getResources().getString(R.string.verify_failed), maxFailedCount - failCount));
                        }
                    } else {
                        String s = mListener.onFailed(failCount);
                        if (s != null) {
                            accessFailedText.setText(s);
                        } else {//默认文案
                            accessFailedText.setText(String.format(getResources().getString(R.string.verify_failed), maxFailedCount - failCount));
                        }
                    }
                }
            }

        });
        setSeekBarStyle(progressDrawableId, thumbDrawableId);
        //用于处理滑动条渐滑逻辑
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isDown) {  //手指按下
                    isDown = false;
                    if (progress > 10) { //按下位置不正确
                        isResponse = false;
                    } else {
                        isResponse = true;
                        accessFailed.setVisibility(GONE);
                        verifyView.down();
                    }
                }
                if (isResponse) {
                    verifyView.move(progress);
                } else {
                    seekBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDown = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isResponse) {
                    verifyView.loose();
                }
            }
        });
        refreshView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startRefresh(v);
            }
        });
    }

    private void startRefresh(View v) {
        //点击刷新按钮，启动动画
        v.animate().rotationBy(360).setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        reset(false);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }


    public void setVerifyListener(CaptchaListener listener) {
        this.mListener = listener;
    }

    public void setStrategy(Strategy strategy) {
        if (strategy != null) {
            verifyView.setCaptchaStrategy(strategy);
        }
    }

    public void setSeekBarStyle(@DrawableRes int progressDrawable, @DrawableRes int thumbDrawable) {
        seekbar.setProgressDrawable(getResources().getDrawable(progressDrawable));
        seekbar.setThumb(getResources().getDrawable(thumbDrawable));
        seekbar.setThumbOffset(0);
    }

    /**
     * 设置滑块图片大小，单位px
     */
    public void setBlockSize(int blockSize) {
        verifyView.setBlockSize(blockSize);
    }

    /**
     * 设置滑块验证模式
     */
    public void setMode(@Mode int mode) {
        this.mMode = mode;
        verifyView.setMode(mode);
        if (mMode == MODE_NO_BAR) {
            seekbar.setVisibility(GONE);
            verifyView.setTouchEnable(true);
        } else {
            seekbar.setVisibility(VISIBLE);
            seekbar.setEnabled(true);
        }
        hideText();
    }

    public int getMode() {
        return this.mMode;
    }

    public void setMaxFailedCount(int count) {
        this.maxFailedCount = count;
    }

    public int getMaxFailedCount() {
        return this.maxFailedCount;
    }


    public void setBitmap(int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableId);
        setBitmap(bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        verifyView.setImageBitmap(bitmap);
        reset(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * 复位
     * @param clearFailed 是否清除失败次数
     */
    public void reset(boolean clearFailed) {
        hideText();
        verifyView.reset();
        if (clearFailed) {
            failCount = 0;
        }
        if (mMode == MODE_BAR) {
            seekbar.setEnabled(true);
            seekbar.setProgress(0);
        } else {
            verifyView.setTouchEnable(true);
        }
    }
    
    /**
     * 隐藏成功失败文字显示
     * */
    public void hideText() {
        accessFailed.setVisibility(GONE);
        accessSuccess.setVisibility(GONE);
    }


}
