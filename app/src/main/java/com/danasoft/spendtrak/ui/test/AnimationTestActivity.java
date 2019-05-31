package com.danasoft.spendtrak.ui.test;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.danasoft.spendtrak.R;

import java.util.ArrayList;

public class AnimationTestActivity extends AppCompatActivity {
    ImageView iv_add_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_test);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        iv_add_anim = findViewById(R.id.iv_add_anim);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runAnimation();
            }
        });
    }

    private void runAnimation() {
        animateTransactionAdd();
    }

    private void animateTransactionAdd() {
        AnimatorSet forwardSet = new AnimatorSet();
        AnimatorSet reverseSet = new AnimatorSet();
        forwardSet.setDuration(400);
        forwardSet.setInterpolator(new AccelerateDecelerateInterpolator());
        reverseSet.setDuration(400);
        reverseSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> forwardList=new ArrayList<Animator>();
        ArrayList<Animator> reverseList=new ArrayList<Animator>();
        ObjectAnimator scaleXForward = ObjectAnimator.ofFloat(iv_add_anim, "ScaleX", 0f, 1.2f, 1f);
        forwardList.add(scaleXForward);
        ObjectAnimator scaleYForward = ObjectAnimator.ofFloat(iv_add_anim, "ScaleY", 0f, 1.2f, 1f);
        forwardList.add(scaleYForward);
        ObjectAnimator scaleXReverse = ObjectAnimator.ofFloat(iv_add_anim, "ScaleX", 1f, 1.2f, 0f);
        reverseList.add(scaleXReverse);
        ObjectAnimator scaleYReverse = ObjectAnimator.ofFloat(iv_add_anim, "ScaleY", 1f, 1.2f, 0f);
        reverseList.add(scaleYReverse);
        ObjectAnimator translateYReverse = ObjectAnimator.ofFloat(iv_add_anim, "translationY", 0f, 700f);
        ObjectAnimator undoTranslateYReverse = ObjectAnimator.ofFloat(iv_add_anim, "translationY", 700f, 0f);
        reverseList.add(translateYReverse);
        forwardSet.playTogether(forwardList);
        reverseSet.playTogether(reverseList);
        forwardSet.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationEnd(Animator animation) { reverseSet.start(); }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        reverseSet.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationEnd(Animator animation) {
                undoTranslateYReverse.start();
            }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        iv_add_anim.setVisibility(View.VISIBLE);
        forwardSet.start();
    }

    private void animateAddShrinkDrop() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(500);
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator translateYAnimator = ObjectAnimator.ofFloat(iv_add_anim, "translationY", 500);
        animatorList.add(translateYAnimator);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(iv_add_anim, "ScaleX", 1f, 1.2f, 0f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(iv_add_anim, "ScaleY", 1f, 1.2f, 0f);
        animatorList.add(scaleYAnimator);
        animatorSet.playSequentially(animatorList);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                iv_add_anim.setVisibility(View.GONE);
            }
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        animatorSet.start();
    }
}
