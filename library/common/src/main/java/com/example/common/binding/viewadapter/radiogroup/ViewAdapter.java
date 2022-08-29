package com.example.common.binding.viewadapter.radiogroup;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.databinding.BindingAdapter;

import com.example.common.binding.command.BindingCommand;

/**
 * Created by goldze on 2017/6/18.
 */
public class ViewAdapter {
    @BindingAdapter(value = {"onCheckedChangedCommand"}, requireAll = false)
    public static void onCheckedChangedCommand(final RadioGroup radioGroup, final BindingCommand<String> bindingCommand) {
        radioGroup.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) (group, checkedId) -> {
            RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
            bindingCommand.execute(radioButton.getText().toString());
        });
    }

    @BindingAdapter(value = {"onCheckCommand", "checkId"}, requireAll = false)
    public static void onCheckCommand(final RadioGroup radioGroup, final BindingCommand<Integer> bindingCommand, int checkId) {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> bindingCommand.execute(checkedId));
        if (checkId != 0) {
            RadioButton radioButton = radioGroup.findViewById(checkId);
            if (radioButton != null) {
                radioButton.setChecked(true);
            }
        }
    }
}
