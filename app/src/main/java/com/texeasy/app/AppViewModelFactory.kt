package com.texeasy.app

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseModel
import com.texeasy.repository.Injection
import com.texeasy.view.activity.home.HomeModel
import com.texeasy.view.activity.home.HomeViewModel
import com.texeasy.view.fragment.basicsetting.BasicSettingModel
import com.texeasy.view.fragment.basicsetting.BasicSettingViewModel
import com.texeasy.view.fragment.clothlist.CabinetViewModel
import com.texeasy.view.fragment.departmentlinen.DepartmentLinenViewModel
import com.texeasy.view.fragment.devicesetting.DeviceSettingModel
import com.texeasy.view.fragment.devicesetting.DeviceSettingViewModel
import com.texeasy.view.fragment.linen.LinenInfoViewModel
import com.texeasy.view.fragment.linenindoor.LinenInDoorInfoViewModel
import com.texeasy.view.fragment.putcabinet.PutCabinetModel
import com.texeasy.view.fragment.putcabinet.PutCabinetViewModel
import com.texeasy.view.fragment.putlinen.PutLinenViewModel
import com.texeasy.view.fragment.rfid.RFidSettingModel
import com.texeasy.view.fragment.rfid.RFidSettingViewModel

class AppViewModelFactory(var application: Application) :
    ViewModelProvider.NewInstanceFactory() {
    private lateinit var model: BaseModel

    constructor(application: Application, model: BaseModel) : this(application) {
        this.model = model
    }

    @NonNull
    override fun <T : ViewModel?> create(@NonNull modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(application, model as HomeModel) as T
            }
            modelClass.isAssignableFrom(CabinetViewModel::class.java) -> {
                CabinetViewModel(application, Injection.provideCabinetRepository()) as T
            }
            modelClass.isAssignableFrom(PutCabinetViewModel::class.java) -> {
                PutCabinetViewModel(application, model as PutCabinetModel) as T
            }
            modelClass.isAssignableFrom(LinenInfoViewModel::class.java) -> {
                LinenInfoViewModel(application, Injection.provideCabinetRepository()) as T
            }
            modelClass.isAssignableFrom(LinenInDoorInfoViewModel::class.java) -> {
                LinenInDoorInfoViewModel(application, Injection.provideCabinetRepository()) as T
            }
            modelClass.isAssignableFrom(PutLinenViewModel::class.java) -> {
                PutLinenViewModel(application, Injection.provideCabinetRepository()) as T
            }
            modelClass.isAssignableFrom(DepartmentLinenViewModel::class.java) -> {
                DepartmentLinenViewModel(application, Injection.provideCabinetRepository()) as T
            }
            modelClass.isAssignableFrom(DeviceSettingViewModel::class.java) -> {
                DeviceSettingViewModel(application, model as DeviceSettingModel) as T
            }
            modelClass.isAssignableFrom(BasicSettingViewModel::class.java) -> {
                BasicSettingViewModel(application, model as BasicSettingModel) as T
            }
            modelClass.isAssignableFrom(RFidSettingViewModel::class.java) -> {
                RFidSettingViewModel(application, model as RFidSettingModel) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}