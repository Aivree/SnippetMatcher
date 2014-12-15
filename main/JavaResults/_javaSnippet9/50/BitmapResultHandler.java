package main;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;

public abstract class BitmapResultHandler implements ResultHandler {

	@Override
	public void processResults(InputStream results) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		Bitmap bitmapResult = BitmapFactory.decodeStream(results);
		processBitmapResults(bitmapResult);
	}

	protected abstract void processBitmapResults(Bitmap results);

}
