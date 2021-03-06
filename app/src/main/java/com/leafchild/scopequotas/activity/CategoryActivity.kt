package com.leafchild.scopequotas.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.leafchild.scopequotas.R
import com.leafchild.scopequotas.data.DatabaseService
import com.leafchild.scopequotas.data.QuotaCategory

class CategoryActivity : AppCompatActivity() {

    private lateinit var categoryName: EditText
    private lateinit var addCategory: Button
    private lateinit var newCategory: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val service = DatabaseService(this)
        val data = service.findAllCategories().map( { it.name })
        val catAdapter = ArrayAdapter(applicationContext, R.layout.category_item, data)

        categoryName = findViewById(R.id.category_name)
        addCategory = findViewById(R.id.add_category)
        addCategory.setOnClickListener {
            if (categoryName.text.isNotEmpty()) {
                val toAdd = categoryName.text.toString()
                //Then store it in the db
                service.createCategory(QuotaCategory(toAdd))

                catAdapter.add(toAdd)
                toggleCategoryButtons(false)
                categoryName.setText("")
            }
        }

        val categories = findViewById<ListView>(R.id.categories_list)
        categories.adapter = catAdapter

        newCategory = findViewById(R.id.new_category)
        newCategory.setOnClickListener { toggleCategoryButtons(true) }

        toggleCategoryButtons(false)
    }

    private fun toggleCategoryButtons(isAdding: Boolean) {

        if (isAdding) {
            newCategory.visibility = View.GONE
            categoryName.visibility = View.VISIBLE
            addCategory.visibility = View.VISIBLE
        } else {
            newCategory.visibility = View.VISIBLE
            categoryName.visibility = View.GONE
            addCategory.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }
}
