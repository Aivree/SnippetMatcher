package lib.func.screensaver;

import lib.func.system_service.ManageKeyguard;
import lib.func.system_service.PowerManagerWakeLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * @ClassName:  ScreenSaverBroadcast.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-7-31 下午2:11:03
 * @Copyright: 版权由 HundSun 拥有
 */
public class ScreenSaverBroadcast extends BroadcastReceiver
{

    private String TAG = ScreenSaverBroadcast.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Log.i(TAG,Intent.ACTION_SCREEN_OFF+"............");
            
            PowerManagerWakeLock.acquire(context);  
            ManageKeyguard.disableKeyguard(context);
            
            Intent _intent = new Intent(context,ScreenSaverActivity.class);
            _intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                             |Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                             |Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(_intent);
            
            Log.i(TAG, " after context.startActivity ....");
            PowerManagerWakeLock.release();
        }
    }

}
