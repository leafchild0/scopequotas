package com.leafchild.scopequotas.common

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.SpinnerAdapter

/**
 * @author leafchild
 * @date 5/23/2017
 * @project ScopeQuotas
 */

open class QuotasWithDefaultAdapter
/**
 * Use this constructor to Define your 'Select One...' layout as the first
 * row in the returned choices.
 * If you do this, you probably don't want a prompt on your spinner or it'll
 * have two 'Select' rows.
 *
 * @param spinnerAdapter                wrapped Adapter. Should probably return false for isEnabled(0)
 * @param nothingSelectedLayout         layout for nothing selected, perhaps you want
 * text grayed out like a prompt...
 * @param nothingSelectedDropdownLayout layout for your 'Select an Item...' in
 * the dropdown.
 * @param context                       object where adapter is used
 */
(private val adapter: SpinnerAdapter,
 private val nothingSelectedLayout: Int,
 private val nothingSelectedDropdownLayout: Int,
 private var context: Context) : SpinnerAdapter, ListAdapter {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * Use this constructor to have NO 'Select One...' item, instead use
     * the standard prompt or nothing at all.
     *
     * @param spinnerAdapter        wrapped Adapter.
     * @param nothingSelectedLayout layout for nothing selected, perhaps
     * you want text grayed out like a prompt...
     * @param context               object where adapter is used
     */
    constructor(
            spinnerAdapter: SpinnerAdapter, nothingSelectedLayout: Int, context: Context)
            : this(spinnerAdapter, nothingSelectedLayout, -1, context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // This provides the View for the Selected Item in the Spinner, not
        // the dropdown (unless dropdownView is not set).
        return if (position == 0) getNothingSelectedView(parent)
        // Could re-use the convertView if possible.
        else adapter.getView(position - EXTRA, null, parent)
    }

    /**
     * View to show in Spinner with Nothing Selected
     *
     * @param parent - ViewGroup object
     * @return view
     */
    private fun getNothingSelectedView(parent: ViewGroup): View {

        return layoutInflater.inflate(nothingSelectedLayout, parent, false)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Android BUG! http://code.google.com/p/android/issues/detail?id=17128 -
        // Spinner does not support multiple view types
        return if (position == 0) {
            if (nothingSelectedDropdownLayout == -1)
                View(context)
            else
                getNothingSelectedDropdownView(parent)
            // Could re-use the convertView if possible, use setTag...
        } else adapter.getDropDownView(position - EXTRA, null, parent)

    }

    /**
     * @param parent - ViewGroup object
     * @return view
     */
    private fun getNothingSelectedDropdownView(parent: ViewGroup): View {

        return layoutInflater.inflate(nothingSelectedDropdownLayout, parent, false)
    }

    override fun getCount(): Int {

        val count = adapter.count
        return if (count == 0) 0 else count + EXTRA
    }

    override fun getItem(position: Int): Any? {

        return if (position == 0) null else adapter.getItem(position - EXTRA)
    }

    override fun getItemViewType(position: Int): Int {

        return 0
    }

    override fun getViewTypeCount(): Int {

        return 1
    }

    override fun getItemId(position: Int): Long {

        return if (position >= EXTRA) adapter.getItemId(position - EXTRA) else (position - EXTRA).toLong()
    }

    override fun hasStableIds(): Boolean {

        return adapter.hasStableIds()
    }

    override fun isEmpty(): Boolean {

        return adapter.isEmpty
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {

        adapter.registerDataSetObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {

        adapter.unregisterDataSetObserver(observer)
    }

    override fun areAllItemsEnabled(): Boolean {

        return false
    }

    override fun isEnabled(position: Int): Boolean {

        return position != 0 // Don't allow the 'nothing selected'
        // item to be picked.
    }

    companion object {

        private const val EXTRA = 1
    }
}
