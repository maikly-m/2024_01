/*
 * Copyright 2017-2023 Guilin Zhishen.
 * All Rights Reserved.
 */
package com.example.u.ui.viewolder;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    public ViewDataBinding mBinding;

    public ViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }
}
