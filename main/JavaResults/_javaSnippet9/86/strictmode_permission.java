//ohne die 2 folgenden Zeilen würde es nicht gehen. Kann wohl nicht im gleichen Thread UI + Internetanfrage machen
//wird gebraucht damit man in der app im gleichen Thread aufs Internet zugreifen kann!
  
   StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
   StrictMode.setThreadPolicy(policy);

<uses-permission android:name="android.permission.INTERNET" /> 
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
