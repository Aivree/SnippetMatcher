package garrobo.lcgt.toastcustom;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void showCustomToast(View v)
	{
		Toast toast = new Toast(this);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		
		//Para convertir el xml a java
		//El xml (toast_layout) tiene los elementos de vista que queremos mostrar
		//Con el segundo parámetro le indicamos el elemento de raiz de la vista
		LayoutInflater inflater = getLayoutInflater();
		View apperance = inflater.inflate(R.layout.toast_layout, 
						(ViewGroup) findViewById(R.id.rootLayout) );
		
		toast.setView(apperance);
		toast.show();
	}
}
