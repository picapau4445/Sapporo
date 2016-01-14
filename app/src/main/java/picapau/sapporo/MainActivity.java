package picapau.sapporo;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends ActionBarActivity implements LocationListener {

    private static final Integer SAPPORO = 1;
    private static final Integer ODORI = 2;
    private static final double SAPPORO_IDO = 43.06637;
    private static final double SAPPORO_KEIDO = 141.350299;
    private static final double ODORI_IDO = 43.060159;
    private static final double ODORI_KEIDO = 141.352233;
    private static final double SAPPORO_IDO_RANGE = 0.01;
    private static final double SAPPORO_KEIDO_RANGE = 0.01;
    private static final double ODORI_IDO_RANGE = 0.01;
    private static final double ODORI_KEIDO_RANGE = 0.02;

    LocationManager mLocationManager;

    TextView textView;
    Button button1;
    Button button2;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK){
            mLocationManager.removeUpdates(this);
            return super.onKeyDown(keyCode, event);
        }else{
            mLocationManager.removeUpdates(this);
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView    = (TextView)findViewById(R.id.textView);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        //textView.setVisibility(View.GONE);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTask(SAPPORO);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTask(ODORI);
            }
        });

        // LocationManagerを取得
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Criteriaオブジェクトを生成
        Criteria criteria = new Criteria();

        // Accuracyを指定(高精度)
        //criteria.setAccuracy(Criteria.ACCURACY_LOW);

        // PowerRequirementを指定(高消費電力)
        //criteria.setPowerRequirement(Criteria.POWER_HIGH);

        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);

        // ロケーションプロバイダの取得
        String provider = mLocationManager.getBestProvider(criteria, true);

        // 取得したロケーションプロバイダを表示
        textView.setText("Provider: "+provider);

        // 最後に取得できた位置情報が3分以内のものであれば有効とします
        final Location lastKnownLocation = mLocationManager.getLastKnownLocation(provider);
        if (lastKnownLocation != null )
        {
            long distance = new Date().getTime() - lastKnownLocation.getTime();
            textView.setText(textView.getText() + "最終:" + String.valueOf(distance/(60*1000L)) + "分前");

            if( distance <= (3 * 60 * 1000L) )
            {
                doLocationGotTask(lastKnownLocation);
                return;
            }
        }
        // LocationListenerを登録
        mLocationManager.requestLocationUpdates(provider, 60000, 10, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        doLocationGotTask(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO 自動生成されたメソッド・スタブ

    }

    void doLocationGotTask(Location location)
    {
        //一度位置情報を取得で来たらリスナを削除する
        mLocationManager.removeUpdates(this);

        // 位置の表示
        TextView textLocation = (TextView) findViewById(R.id.textLocation);
        textLocation.setText("緯度:"+location.getLatitude()+" "+"経度:"+location.getLongitude());

        // 現在位置が札幌か大通りか判定
        double ido = location.getLatitude();
        double keido = location.getLongitude();

        if( ( ido >= SAPPORO_IDO - SAPPORO_IDO_RANGE && ido <= SAPPORO_IDO + SAPPORO_IDO_RANGE ) &&
                ( keido >= SAPPORO_KEIDO - SAPPORO_KEIDO_RANGE && keido <= SAPPORO_KEIDO + SAPPORO_KEIDO_RANGE ) )
        {
            doTask(SAPPORO);
        }
        else if( ( ido >= ODORI_IDO - ODORI_IDO_RANGE && ido <= ODORI_IDO + ODORI_IDO_RANGE ) &&
                ( keido >= ODORI_KEIDO - ODORI_KEIDO_RANGE && keido <= ODORI_KEIDO + ODORI_KEIDO_RANGE ) )
        {
            doTask(ODORI);
        }
        else
        {
            textLocation.setText("さっぽろ駅、大通駅の範囲ではないと判断されました。" + " " +"緯度:"+location.getLatitude()+" "+"経度:"+location.getLongitude());
            //textLocation.setText("さっぽろ駅、大通駅の範囲ではないと判断されました。" + " " +"緯度:"+location.getLatitude()+" "+"経度:"+location.getLongitude());
        }

    }

    void doTask(int location)
    {
        //一度位置情報を取得で来たらリスナを削除する
        mLocationManager.removeUpdates(this);
        textView.setVisibility(View.VISIBLE);
        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        MyTask task = new MyTask(textView);
        task.execute(location);

    }

}
