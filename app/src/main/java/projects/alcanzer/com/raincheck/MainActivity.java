package projects.alcanzer.com.raincheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView cityName, windspeed, lastupdated, mDate;
    TextSwitcher temperature,  feelslike;
    ImageButton refresh;
    Button placeSelector;
    JSONObject jsonObject1;
    double lat, lng;
    RelativeLayout relativeLayout;
    boolean tempC, feelC;
    String WEB_SITE = "http://api.wunderground.com/api/fe1b8cf0f49d5162/conditions/q/";
    //"http://api.wunderground.com/api/fe1b8cf0f49d5162/conditions/q/17.228,52.554.json"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences latlng = this.getSharedPreferences("RainCheck_PREF", Context.MODE_PRIVATE);
        lat = (latlng.getString("latitude", null) != null) ? Double.parseDouble(latlng.getString("latitude", null)): 22.2;
        lng = (latlng.getString("longitude", null) !=null) ? Double.parseDouble(latlng.getString("longitude", null)): 87.3;
        tempC = true;
        feelC = true;
        relativeLayout = (RelativeLayout) findViewById(R.id.mainscreen);
        imageView = (ImageView) findViewById(R.id.imageView);
        cityName = (TextView) findViewById(R.id.fullname);
        mDate = (TextView) findViewById(R.id.date);
        placeSelector = (Button) findViewById(R.id.placeSelect);
        temperature = (TextSwitcher) findViewById(R.id.temperature);
        feelslike = (TextSwitcher) findViewById(R.id.feelslike);
        windspeed = (TextView) findViewById(R.id.windspeed);
        lastupdated = (TextView) findViewById(R.id.lastupdated);
        refresh = (ImageButton) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getItDone(lat,lng);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        temperature.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView myText = new TextView(MainActivity.this);
                myText.setTextColor(Color.parseColor("#f0f0f5"));
                myText.setTextSize(25);
                myText.setGravity(Gravity.CENTER);
                return myText;
            }
        });
        feelslike.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView myText = new TextView(MainActivity.this);
                myText.setTextColor(Color.parseColor("#f0f0f5"));
                myText.setTextSize(25);
                myText.setGravity(Gravity.CENTER);
                return myText;
            }
        });
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        temperature.setInAnimation(in);
        temperature.setOutAnimation(out);
        feelslike.setInAnimation(in);
        feelslike.setOutAnimation(out);
        try {
            getItDone(lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String temp = tempC ? jsonObject1.getString("temp_c")+"\u2103" : jsonObject1.getString("temp_f")+"\u2109";
                    temperature.setText(temp);
                    tempC = !tempC;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        feelslike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String temp = feelC ? jsonObject1.getString("feelslike_c")+"\u2103" : jsonObject1.getString("feelslike_f")+"\u2109";
                    feelslike.setText(temp);
                    feelC = !feelC;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        placeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlaceSelectorActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivityForResult(intent, 101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK){
            Bundle bundle = data.getParcelableExtra("bundle");
            LatLng position = bundle.getParcelable("position");
            lat = (float) position.latitude;
            lng = (float) position.longitude;
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("RainCheck_PREF",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("latitude", lat+"");
            editor.putString("longitude", lng+"");
            editor.commit();

            try {
                getItDone(lat, lng);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void getItDone(double lat, double lng) throws JSONException, ExecutionException, InterruptedException {
        String loc = lat+","+lng+".json";
        String response = new HttpConn().execute(WEB_SITE+loc).get();
        tempC = true;
        feelC = true;
        JSONObject jsonObject = new JSONObject(response);
        jsonObject1 = new JSONObject(jsonObject.getString("current_observation"));
        JSONObject jsonObject2 = new JSONObject(jsonObject1.getString("display_location"));
        String name = jsonObject2.getString("full");
        String url = jsonObject1.getString("icon_url");
        String weather = jsonObject1.getString("icon");
        String date = jsonObject1.getString("local_time_rfc822").substring(0, 16);
        mDate.setText(date);
        if(weather.equals("partlycloudy") || weather.equals("cloudy")){
            relativeLayout.setBackgroundResource(R.drawable.blur_cloudy);
        }else if(weather.equals("clear") || weather.equals("sunny")){
            relativeLayout.setBackgroundResource(R.drawable.blur_clear);
        }
        Picasso.with(this).load(url).into(imageView);
        cityName.setText(name);
        temperature.setText(jsonObject1.getString("temp_f")+"\u2109");
        feelslike.setText(jsonObject1.getString("feelslike_f")+"\u2109");
        windspeed.setText("Wind: "+jsonObject1.getString("wind_string"));
        lastupdated.setText(jsonObject1.getString("observation_time"));
    }
}
