package picapau.sapporo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import android.os.AsyncTask;
import android.os.Debug;
import android.widget.TextView;
 
public class MyTask extends AsyncTask<Integer, Integer, Integer> {
 
	private TextView textView;
    private String strLine;
    private Integer nDistance;
    
    private static final Integer nTrainMinute = 2;
    private static final Integer nWalkMinute = 9;
 
	private static final Integer SAPPORO = 1;
	private static final Integer ODORI = 1;
	
   /**
     * コンストラクタ
     */
    public MyTask(TextView textView) {
        super();
        this.textView   = textView;
        nDistance = -1;
    }
 
    /**
     * バックグランドで行う処理
     */
    @Override
    protected Integer doInBackground(Integer... value) {
        try {
        	//現在の日時を取得します
        	Calendar calendar = Calendar.getInstance();
        	int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        	
        	// 現在時刻と位置から読み込む時刻表を変えます
        	String strURL = "";
        	
        	if( dayofweek != Calendar.SATURDAY && dayofweek != Calendar.SUNDAY ){
        		if( value[0] == SAPPORO ){
        			strURL = "https://googledrive.com/host/0B5ApbrgDUXuQR3JDU0s2TFRsX3c/sapporo_weekday.html";
        		}
        		else{
        			strURL = "https://googledrive.com/host/0B5ApbrgDUXuQR3JDU0s2TFRsX3c/odori_weekday.html";
        		}
        	}
        	else{
        		if( value[0] == SAPPORO ){
        			strURL = "https://googledrive.com/host/0B5ApbrgDUXuQR3JDU0s2TFRsX3c/sapporo_holiday.html";
        		}
        		else{
        			strURL = "https://googledrive.com/host/0B5ApbrgDUXuQR3JDU0s2TFRsX3c/odori_holiday.html";
        		}
        	}
        		
    		URL url = new URL(strURL);
    		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
    		InputStream in = httpConn.getInputStream();
    		
    		BufferedReader r = new BufferedReader(new InputStreamReader(in,"UTF-8"));
    		
    		
    		boolean bResult = false;

			
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			if( hour == 0 ) hour = 24;
			
			//	hour = 24;
			//	minute = 2;
			
			
    		for (;;) {
    			String line = r.readLine();
    			if (line == null) {
    				break;
    			}
    			
    			if(bResult){
    				break;
    			}
    			
    			String[] strAry = line.split(" ");
    			
    			int scheduleHour = Integer.valueOf(strAry[0]);
    			if( scheduleHour == 0 ) scheduleHour = 24;
    			int scheduleMinute;
    			
    			if( scheduleHour < hour ){
    				continue;
    			}
    			else if( scheduleHour == hour || scheduleHour+1 == hour ){
	    			for( int i=strAry.length-1; i>0; i--){
	    				scheduleMinute = Integer.parseInt(strAry[i]);
	    				if( scheduleMinute <= minute ){
	    					break;
	    				}
	    				
	    				nDistance = scheduleMinute - minute;
	    				bResult = true;
	    			}
    			}
    		}
    		
        } catch (IOException e) {
        	nDistance = -2;
        }
        return value[0] + 2;
    }
 
    /**
     * バックグランド処理が完了し、UIスレッドに反映する
     */
    @Override
    protected void onPostExecute(Integer result) {
    	if( nDistance < 0 ){
    		if( nDistance == -2 ){
        		textView.setText("時刻表データが取得できませんでした。");    			
    		}
    		else{
        		textView.setText("本日はもう電車がありません。");
    		}
    	}
    	else{
    		
	    	if( nTrainMinute + nDistance < nWalkMinute ){
	    		textView.setText("電車に乗って下さい。" + " " + String.valueOf(nDistance) + "分後に来ます。");
	    	}
	    	else if( nTrainMinute + nDistance == nWalkMinute ){
	    		textView.setText("お好きな方を。" + " " + "なお地下鉄は" + String.valueOf(nDistance) + "分後に来ます。");
	    	}
	    	else{
	    		textView.setText("地下歩行空間を歩いて下さい。" + " " + "なお地下鉄は" + String.valueOf(nDistance) + "分後に来ます。");
	    	}
    	}
    	this.cancel(true);
        //textView.setText(String.valueOf(result));
    }
    
}