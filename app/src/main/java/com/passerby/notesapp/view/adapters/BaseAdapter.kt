package com.passerby.notesapp.view.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<D> : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>() {

    private val _mData by lazy { mutableListOf<D>() }
    protected val mData: List<D> = _mData

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }
}