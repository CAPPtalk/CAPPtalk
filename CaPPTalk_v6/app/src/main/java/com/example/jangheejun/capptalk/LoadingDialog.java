package com.example.jangheejun.capptalk;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;

public class LoadingDialog extends ProgressDialog {

    private Context c;
    private ImageView imgLogo;
    public LoadingDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        c=context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dailog);

        AnimatedCircleLoadingView animatedCircleLoadingView;
        animatedCircleLoadingView = findViewById(R.id.circle_loading_view);
        animatedCircleLoadingView.startDeterminate();
        animatedCircleLoadingView.setPercent(100);
        animatedCircleLoadingView.stopOk();

    }
    @Override
    public void show() {
        super.show();
    }
    @Override
    public void dismiss() {
        super.dismiss();
    }
}
