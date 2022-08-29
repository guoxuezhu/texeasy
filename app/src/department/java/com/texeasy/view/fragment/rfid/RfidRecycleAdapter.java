package com.texeasy.view.fragment.rfid;

import android.content.Context;

import com.texeasy.base.constant.HardwareConfig;
import com.texeasy.base.entity.RfidInfo;

import java.util.List;

class RfidRecycleAdapter extends BaseRecycleAdapter {

    public RfidRecycleAdapter(Context context, List<RfidInfo> list) {
        super(context, list);
    }

    /**
     * 添加数据
     */
    @Override
    public void addData() {
//   在list中添加数据，并通知条目加入一条
        list.add(new RfidInfo(HardwareConfig.ALL_DOORS, "", ""));
        //添加动画
        notifyDataSetChanged();
    }
}
