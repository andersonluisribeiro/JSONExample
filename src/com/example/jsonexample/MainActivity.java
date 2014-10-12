package com.example.jsonexample;



import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends Activity {

	private ListView lista;
	private EditText texto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		lista = (ListView) findViewById(R.id.lista);
		texto = (EditText) findViewById(R.id.texto);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class BookTask extends AsyncTask<String, Void, String[]> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.show();
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (result != null) {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						MainActivity.this, android.R.layout.simple_list_item_1,
						result);

				lista.setAdapter(adapter);
			}
			dialog.dismiss();
		}

		@Override
		protected String[] doInBackground(String... arg0) {
			try {

				String filtro = arg0[0];

				if (TextUtils.isEmpty(filtro)) {
					return null;
				}

				String urlBooks = "https://www.googleapis.com/books/v1/volumes?q=android";

				String url = Uri.parse(urlBooks).toString();

				String conteudo = HttpRequest.get(url).body();

				JSONObject jsonObject = new JSONObject(conteudo);

				JSONArray resultados = jsonObject.getJSONArray("items");

				String[] books = new String[resultados.length()];
				for (int i = 0; i < resultados.length(); i++) {
					JSONObject book = resultados.getJSONObject(i);
					String codigo = book.getString("id");

					String titulo = book.getJSONObject("volumeInfo").getString(
							"title");
					books[i] = codigo + " - " + titulo;
				}
				return books;

			} catch (Exception e) {
				Log.e(getPackageName(), e.getMessage(), e);
				throw new RuntimeException(e);

			}

		}
	}

	public void buscar(View v) {
		String filtro = texto.getText().toString();		
		new BookTask().execute(filtro);
		
	}
}
