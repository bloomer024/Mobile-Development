package model.bookshelf;

import com.example.bookshelfmanager.R;

import dao.bookshelf.BookContentProvider;
import database.bookshelf.BookTable;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ListBooksActivity extends Activity {
	private SimpleCursorAdapter adapter;
	private boolean recommend = false;
	private Button buttonSearch;
	private EditText textSearch;
	private String searchTerm;
	private ListView listBooks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_books);
		buttonSearch = (Button) findViewById(R.id.buttonSearch);
		textSearch = (EditText) findViewById(R.id.editText_Search);
		buttonSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						GoogleBooksActivity.class);
				searchTerm = textSearch.getText().toString();
				if (searchTerm.length() >= 3) {
					Bundle b = new Bundle();
					b.putString("book", searchTerm);
					intent.putExtras(b);
					startActivity(intent);
				} else {
					Toast.makeText(
							getBaseContext(),
							"Please add something to search for first. Minimum 3 letters",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_list_books, menu);
		return true;
	}

	@Override
	protected void onResume() {

		super.onResume();
		fillListWithBooks();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.new_book:
			Intent intent = new Intent(this, NewBookActivity.class);
			startActivity(intent);
			return true;
		case R.id.render_recommended:
			recommend = recommend ? false : true;
			fillListWithBooks();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// fills list with users books
	private void fillListWithBooks() {
		listBooks = (ListView) findViewById(R.id.listView1);
		String where = null;
		String[] param = null;
		if (recommend) {
			where = BookTable.COLUMN_RECOMMEND + " = ?";
			param = new String[] { "1" };
		}
		Cursor cursor = getContentResolver().query(
				BookContentProvider.CONTENT_URI, null, where, param, null);

		String[] columns = new String[] { BookTable.COLUMN_ISBN,
				BookTable.COLUMN_TITLE, BookTable.COLUMN_AUTHORS,
				BookTable.COLUMN_YEAR, BookTable.COLUMN_RECOMMEND };

		int[] names = new int[] { android.R.id.text2, android.R.id.text1, };
		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_activated_2, cursor, columns,
				names);
		listBooks.setAdapter(adapter);
		listBooks.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Cursor cursor = adapter.getCursor();
				cursor.moveToPosition(arg2);
				String id = cursor.getString(cursor
						.getColumnIndex(BookTable.COLUMN_ID));

				cursor.close();
				Intent intent = new Intent(arg1.getContext(),
						NewBookActivity.class);
				Bundle b = new Bundle();
				b.putInt("bookId", Integer.parseInt(id));
				intent.putExtras(b);
				startActivity(intent);

			}

		});

	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
	}
}
