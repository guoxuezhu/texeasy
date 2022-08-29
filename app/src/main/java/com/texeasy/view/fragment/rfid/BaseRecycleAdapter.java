package com.texeasy.view.fragment.rfid;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.texeasy.R;
import com.texeasy.base.entity.RfidInfo;
import com.texeasy.base.widget.TwoButtonDialog;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by qzs on 2017/9/04.
 */
class BaseRecycleAdapter extends RecyclerView.Adapter<BaseRecycleAdapter.MyViewHolder> {
    private Context context;
    protected List<RfidInfo> list;

    public BaseRecycleAdapter(Context context, List<RfidInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_rfid_view, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, final int position) {
        RfidInfo rfidInfo = list.get(position);
        if (rfidInfo != null) {
            holder.etDoor.setText(rfidInfo.getDoor());
            holder.etAntenna.setText(rfidInfo.getAntenna());
            holder.etPower.setText(rfidInfo.getPower());
        }
        TextWatcher doorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                rfidInfo.setDoor(s.toString());
            }
        };
        TextWatcher antennaWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                rfidInfo.setAntenna(s.toString());
            }
        };
        TextWatcher powerWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                rfidInfo.setPower(s.toString());
            }
        };
        holder.etDoor.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.etDoor.addTextChangedListener(doorWatcher);
            } else {
                holder.etDoor.removeTextChangedListener(doorWatcher);
            }
        });
        holder.etAntenna.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.etAntenna.addTextChangedListener(antennaWatcher);
            } else {
                holder.etAntenna.removeTextChangedListener(antennaWatcher);
            }
        });
        holder.etPower.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.etPower.addTextChangedListener(powerWatcher);
            } else {
                holder.etPower.removeTextChangedListener(powerWatcher);
            }
        });
        holder.ivDelete.setOnClickListener(v -> {
            new TwoButtonDialog(context).setDetailText("删除改行配置吗？")
                    .setListener(() -> {
                        removeData(position);
                    }).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 添加数据
     */
    public void addData() {
//   在list中添加数据，并通知条目加入一条
        list.add(new RfidInfo());
        //添加动画
        notifyDataSetChanged();
    }

    /**
     * 删除数据
     *
     * @param position
     */
    public void removeData(int position) {
        list.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder的类，用于缓存控件
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        EditText etDoor, etAntenna, etPower;
        ImageView ivDelete;

        //因为删除有可能会删除中间条目，然后会造成角标越界，所以必须整体刷新一下！
        public MyViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.ll_item);
            etDoor = (EditText) view.findViewById(R.id.et_door);
            etAntenna = (EditText) view.findViewById(R.id.et_antenna);
            etPower = (EditText) view.findViewById(R.id.et_power);
            ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
        }
    }
}