package projects.alcanzer.com.raincheck;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Joseph on 9/3/2017.
 */
public class HttpConn extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(strings[0])
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
