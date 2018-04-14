package com.leafchild.scopequotas.categories;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.data.DatabaseService;
import com.leafchild.scopequotas.data.QuotaCategory;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryActivity extends AppCompatActivity {

	private EditText categoryName;
	private Button addCategory;
	private ArrayAdapter<String> catAdapter;
	private FloatingActionButton newCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		DatabaseService service = new DatabaseService(this);

		categoryName = findViewById(R.id.category_name);
		addCategory = findViewById(R.id.add_category);
		addCategory.setOnClickListener(v -> {
			if (categoryName.getText().length() > 0) {
				String toAdd = categoryName.getText().toString();
				//Then store it in the db
				service.createCategory(new QuotaCategory(toAdd));

				catAdapter.add(toAdd);
				toggleCategoryButtons(false);
				categoryName.setText("");
			}
		});
		List<String> data = service
			.findAllCategories()
			.stream().map(QuotaCategory::getName).collect(Collectors.toList());

		catAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.category_item, data);
		ListView categories = findViewById(R.id.categories_list);
		categories.setAdapter(catAdapter);

		newCategory = findViewById(R.id.new_category);
		newCategory.setOnClickListener(view -> toggleCategoryButtons(true));

		toggleCategoryButtons(false);
	}

	private void toggleCategoryButtons(boolean isAdding) {

		if (isAdding) {
			newCategory.setVisibility(View.GONE);
			categoryName.setVisibility(View.VISIBLE);
			addCategory.setVisibility(View.VISIBLE);
		}
		else {
			newCategory.setVisibility(View.VISIBLE);
			categoryName.setVisibility(View.GONE);
			addCategory.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return false;
	}
}
