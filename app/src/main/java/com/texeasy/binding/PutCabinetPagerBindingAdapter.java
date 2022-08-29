package com.texeasy.binding;

import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;

import com.texeasy.databinding.ItemPutCabinetAdminLoginViewpagerBinding;
import com.texeasy.view.fragment.putcabinet.PutCabinetPageViewModel;

import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter;

/**
 * Created by goldze on 2018/6/21.
 */

public class PutCabinetPagerBindingAdapter extends BindingViewPagerAdapter<PutCabinetPageViewModel> {

    @Override
    public void onBindBinding(final ViewDataBinding binding, int variableId, int layoutRes, final int position, PutCabinetPageViewModel item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);
        //这里可以强转成ViewPagerItemViewModel对应的ViewDataBinding，
        if (position == 0) {
            ItemPutCabinetAdminLoginViewpagerBinding _binding = (ItemPutCabinetAdminLoginViewpagerBinding) binding;
        } else {
            ItemPutCabinetAdminLoginViewpagerBinding _binding = (ItemPutCabinetAdminLoginViewpagerBinding) binding;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
