package com.aplicacion.guiaplayasgalicia.conexionweb;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.aplicacion.guiaplayasgalicia.AsyncTaskCompletedInterface;
import com.aplicacion.guiaplayasgalicia.objetos.Beach;

public class ReadMeteogaliciaWebAsync extends AsyncTask<Integer, Void, HashMap<Integer, Beach>> {
	
	private RSSFeed rssFeed = null;
	private AsyncTaskCompletedInterface asyncTaskCompletedInterface;
	
	private static final String urlMeteogalicia = "http://servizos.meteogalicia.es/rss/predicion/georssPraias.action?idZona=%beachId%&dia=-1";
	
	public ReadMeteogaliciaWebAsync(AsyncTaskCompletedInterface asyncTaskCompletedInterface) {
		this.asyncTaskCompletedInterface = asyncTaskCompletedInterface;
	}

	@SuppressLint("UseSparseArrays")
	@Override
	protected HashMap<Integer, Beach> doInBackground(Integer... params) {
		
		HashMap<Integer, Beach> beachesHashMap = null;
		
		try {
			
			String urlStr = urlMeteogalicia.replace("%beachId%", params[0].toString());
			
			URL url = new URL(urlStr);
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader myXMLReader = saxParser.getXMLReader();
			RSSHandler myRSSHandler = new RSSHandler();
			myXMLReader.setContentHandler(myRSSHandler);
			InputSource myInputSource = new InputSource(url.openStream());
			myXMLReader.parse(myInputSource);

			rssFeed = myRSSHandler.getFeed();
			
			beachesHashMap = new HashMap<Integer, Beach>();
			
			if (rssFeed != null) {
				
				// Go over the RssItem list, transforming this objects in objects of type Beach.
				// Put them in a SparseArray to return.
				Integer counter = 0;
				for (RSSItem rssItem : rssFeed.getItemList()) {
					Beach beach = new Beach(rssItem);
					beachesHashMap.put(counter, beach);
					counter++;
				}
				
				// TODO
				// Get the images to display from the web
				for (Integer key : beachesHashMap.keySet()) {
					
					Beach beach = beachesHashMap.get(key);
					
					// Sky status images
					String imageUrl = "http://www.meteogalicia.es/datosred/infoweb/meteo/imagenes/meteoros/ceo/" + beach.getMorningSky() + ".png";
					Bitmap bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
					beach.setBitmapMorningSky(bitmap);
					imageUrl = "http://www.meteogalicia.es/datosred/infoweb/meteo/imagenes/meteoros/ceo/" + beach.getEveningSky() + ".png";
					bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
					beach.setBitmapEveningSky(bitmap);
					imageUrl = "http://www.meteogalicia.es/datosred/infoweb/meteo/imagenes/meteoros/ceo/" + beach.getNightSky() + "_fondo.png";
					bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
					beach.setBitmapNightSky(bitmap);
					
					// Wind intensity images
					imageUrl = "http://www.meteogalicia.es/datosred/infoweb/meteo/imagenes/meteoros/vento/" + beach.getMorningWind() + ".png";
					bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
					beach.setBitmapMorningWind(bitmap);
					imageUrl = "http://www.meteogalicia.es/datosred/infoweb/meteo/imagenes/meteoros/vento/" + beach.getEveningWind() + ".png";
					bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
					beach.setBitmapEveningWind(bitmap);
					imageUrl = "http://www.meteogalicia.es/datosred/infoweb/meteo/imagenes/meteoros/vento/" + beach.getNightWind() + ".png";
					bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
					beach.setBitmapNightWind(bitmap);
				}
			}
			
		} catch (MalformedURLException e) {
			Log.e(this.getClass().toString(), "Error generating the URL");
		} catch (ParserConfigurationException e) {
			Log.e(this.getClass().toString(), "Error getting SAXParser");
		} catch (SAXException e) {
			Log.e(this.getClass().toString(), "Error getting SAXParser");
		} catch (IOException e) {
			Log.e(this.getClass().toString(), "Error reading the URL");
		}
		return beachesHashMap;
	}

	@Override
	// Method which is executed when the doInBackground method has finished, getting as a parameter the object returned by it.
	protected void onPostExecute(HashMap<Integer, Beach> result) {
		super.onPostExecute(result);
		this.asyncTaskCompletedInterface.asyncTaskCompleted(result);
	}

}
