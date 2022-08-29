package com.texeasy.base.app

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.common.base.BaseModel

class BaseViewModelFactory(var application: Application, var model: BaseModel) :
    ViewModelProvider.NewInstanceFactory() {

    @NonNull
    override fun <T : ViewModel?> create(@NonNull modelClass: Class<T>): T {
        return when {

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}