package model.bookshelf;

import com.example.bookshelfmanager.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebViewClient;

import android.webkit.WebView;

import android.widget.Button;


public class GoogleBooksActivity extends Activity {

	private String book;
	WebView myBrowser;
	public String linkToRead;
	private MyJavaScriptInterface myJavaScriptInterface;

	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_book);
		Button viewDetails = (Button) findViewById(R.id.btnDetails);
		Button btnRead = (Button) findViewById(R.id.btnRead);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			book = b.getString("book");
			myBrowser = (WebView) findViewById(R.id.webView);

			myJavaScriptInterface = new MyJavaScriptInterface(this);
			myBrowser.addJavascriptInterface(myJavaScriptInterface,
					"AndroidFunction");

			myBrowser.getSettings().setJavaScriptEnabled(true);
			myBrowser.loadUrl("file:///android_asset/jbooks.html");
			viewDetails.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (myBrowser.getUrl().contains("google")) {
						myBrowser.clearView();
						myBrowser.loadUrl("file:///android_asset/jbooks.html");
						myBrowser.loadUrl("javascript:submitQuery(" + "'"
								+ book + "'" + ")");
					} else {
						myBrowser.clearView();
						myBrowser.loadUrl("javascript:submitQuery(" + "'"
								+ book + "'" + ")");
					}
				}
			});
		}
		btnRead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myBrowser.clearView();
				myBrowser.loadUrl("javascript:submitReadingQuery(" + "'" + book
						+ "'" + ")");
			}
		});
	}

	class MyJavaScriptInterface {
		Context mContext;

		MyJavaScriptInterface(Context c) {
			mContext = c;
		}

		/** Gets books attributes from JavaScript */
		@JavascriptInterface
		public void ObtainBookAttributes(String title, String authors,
				String year, String isbn) {
			Bundle bundle;
			System.out.println(title + "" + authors + "" + year + "" + isbn);
			Book b = new Book(isbn, title, authors, year);

			if (b != null)

			{
				Intent intent = new Intent(mContext, NewBookActivity.class);
				bundle = new Bundle();
				bundle.putString("isbn", isbn.toString());
				bundle.putString("title", title.toString());
				bundle.putString("authors", authors.toString());
				bundle.putString("year", year.toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}

		/** Loads url from JavaScript for book to be read */
		@JavascriptInterface
		public void ReadBook(String link) {
			if (link != null) {
				linkToRead = link;
				myBrowser.setWebViewClient(new WebViewClient());
				myBrowser.loadUrl(linkToRead);
			}
		}
	}

}
