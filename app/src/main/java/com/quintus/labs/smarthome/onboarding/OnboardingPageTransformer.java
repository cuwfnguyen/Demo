package com.quintus.labs.smarthome.onboarding;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.quintus.labs.smarthome.R;

public class OnboardingPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View page, float position) {

        int pagePosition = (int) page.getTag();
        int pageWidth = page.getWidth();
        float pageWidthTimesPosition = pageWidth * position;
        float absPosition = Math.abs(position);

        if (position <= -1.0f || position >= 1.0f) {


        } else if (position == 0.0f) {


        } else {

            View title = page.findViewById(R.id.textView7);
            title.setAlpha(1.0f - absPosition);

            View description = page.findViewById(R.id.textView8);
            description.setTranslationY(-pageWidthTimesPosition / 2f);
            description.setAlpha(1.0f - absPosition);


            View computer = page.findViewById(R.id.imageView3);

            if (computer != null) {
                computer.setAlpha(1.0f - absPosition);
                computer.setTranslationX(-pageWidthTimesPosition * 1.5f);
//                computer.setTranslationY(-pageWidthTimesPosition / 2f);
            }



            if (position < 0) {
                // Create your out animation here
            } else {
                // Create your in animation here
            }
        }
    }

}
