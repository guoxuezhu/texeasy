package com.texeasy.base.adapterbinding;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.common.binding.command.BindingCommand;
import com.google.android.material.tabs.TabLayout;
import com.texeasy.base.R;

import java.util.List;

/**
 * @author caozhihuang
 */
public class TabLayoutBindAdapter {
    @BindingAdapter(value = {"tabLayoutIndicatorColor", "tabLayoutSelectedTextColor", "tabLayoutTextColor"}, requireAll = false)
    public static void setTabLayout(TabLayout tabLayout, int tabLayoutIndicatorColor, int tabLayoutSelectedTextColor, int tabLayoutTextColor) {
        if (tabLayout != null) {
            tabLayout.setSelectedTabIndicatorColor(tabLayoutIndicatorColor);
            tabLayout.setTabTextColors(tabLayoutTextColor, tabLayoutSelectedTextColor);
        }
    }

    @BindingAdapter(value = {"onTabSelectedCommand"})
    public static void setTabLayout(TabLayout tabLayout, BindingCommand onTabSelectedCommand) {
        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    onTabSelectedCommand.execute(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    @BindingAdapter(value = {"bindViewPager", "tabsTitleRes", "onTabSelected"}, requireAll = false)
    public static void setTabs(TabLayout tabLayout, int viewPagerId, List<Integer> tabsTitleRes, BindingCommand onTabSelectedCommand) {
        if (tabLayout != null && tabsTitleRes != null) {
            tabLayout.post(() -> {
                Context context = tabLayout.getContext();
                ViewPager viewPager = ((Activity) context).findViewById(viewPagerId);
                for (int i = 0; i < tabsTitleRes.size(); i++) {
                    tabLayout.addTab(tabLayout.newTab());
                }
                if (viewPager != null) {
                    tabLayout.setupWithViewPager(viewPager);
                }
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null && tabsTitleRes.size() <= tabLayout.getTabCount()) {
                        tab.setCustomView(R.layout.base_tab_text_item);
                        if (i == 0) {
                            TextView textView = (TextView) tab.getCustomView();
                            if (textView != null) {
                                textView.setTypeface(Typeface.DEFAULT_BOLD);
                                textView.setTextColor(context.getResources().getColor(R.color.base_default_text));
                                textView.setScaleX(1.1f);
                                textView.setScaleY(1.1f);
                            }
                        }
                        tab.setText(tabsTitleRes.get(i));
                    }
                }
                tabLayout.setTabTextColors(ContextCompat.getColor(context, R.color.base_default_text_hint),
                        ContextCompat.getColor(context, R.color.base_default_text));
                tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.colorAccent));
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        TextView textView = (TextView) tab.getCustomView();
                        if (textView != null) {
                            textView.setTypeface(Typeface.DEFAULT_BOLD);
                            textView.setTextColor(context.getResources().getColor(R.color.base_default_text));
                            textView.setScaleX(1.1f);
                            textView.setScaleY(1.1f);
                        }
                        if (onTabSelectedCommand != null) {
                            onTabSelectedCommand.execute(tab.getPosition());
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        TextView textView = (TextView) tab.getCustomView();
                        if (textView != null) {
                            textView.setTypeface(Typeface.DEFAULT);
                            textView.setTextColor(context.getResources().getColor(R.color.base_default_text_hint));
                            textView.setScaleX(1f);
                            textView.setScaleY(1f);
                        }
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            });

        }
    }
}
