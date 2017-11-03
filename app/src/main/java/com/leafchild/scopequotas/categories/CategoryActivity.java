package com.leafchild.scopequotas.categories;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.leafchild.scopequotas.R;
import com.leafchild.scopequotas.data.DatabaseService;
import com.leafchild.scopequotas.data.QuotaCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

	private EditText categoryName;
	private Button addCategory;
	private ArrayAdapter<String> catAdapter;
	private FloatingActionButton newCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		DatabaseService service = new DatabaseService(this);

		categoryName = (EditText) findViewById(R.id.category_name);
		addCategory = (Button) findViewById(R.id.add_category);
		addCategory.setOnClickListener(v -> {
			if (categoryName.getText().length() > 0) {
				String toAdd = categoryName.getText().toString();
				//Then store it in the db
				service.createCategory(new QuotaCategory(toAdd));

				catAdapter.add(toAdd);
				toggleCategoryButtons(false);
			}
		});
		ArrayList<String> data = new ArrayList<>();
		List<QuotaCategory> existing = service.findAllCategories();
		for (QuotaCategory category : existing) {
			data.add(category.getName());
		}

		catAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.category_item, data);
		ListView categories = (ListView) findViewById(R.id.categories_list);
		categories.setAdapter(catAdapter);

		newCategory = (FloatingActionButton) findViewById(R.id.new_category);
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

}
