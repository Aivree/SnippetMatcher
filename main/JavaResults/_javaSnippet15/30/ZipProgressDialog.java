package cn.klspta.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import cn.tdbg.android.R;

public class ZipProgressDialog extends ProgressDialog{

    public ZipProgressDialog(Context context) {
        super(context);
    }
    
    public ZipProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zip_dialog_progress);
    }
    
    public static ZipProgressDialog show(Context ctx,String mes){
        ZipProgressDialog d = new ZipProgressDialog(ctx);
        d.setCanceledOnTouchOutside(false);
        d.show();
        LayoutInflater inflater = ((Activity)ctx).getLayoutInflater();
        View layout = inflater.inflate(R.layout.zip_dialog_progress, null);
        Window window= d.getWindow();
        window.setContentView(layout);
        ((TextView)window.findViewById(R.id.textView01)).setText(mes);
        return d;
    }
}