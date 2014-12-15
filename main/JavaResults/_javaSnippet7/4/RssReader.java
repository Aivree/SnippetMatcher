/*
 * Copyright (C) 2011 Mats Hofman <http://matshofman.nl/contact/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.robopod;

import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.os.AsyncTask;
import android.util.Log;

public class RssReader extends AsyncTask<URL, Void, RssFeed> {

	@Override
	protected RssFeed doInBackground(URL... url) {
		RssFeed feed;
		InputStream stream;
		
		try {
			stream = url[0].openStream();
			SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            RssHandler handler = new RssHandler();
            InputSource input = new InputSource(stream);

            reader.setContentHandler(handler);
            reader.parse(input);

            feed = handler.getResult();
		}
		catch (Exception e) {
			Log.e("Error getting feed", "An error occurred while getting the rss feed: " + e.getMessage());
			feed = null;
		}
		return feed;
	}
	
	@Override
    protected void onPostExecute(RssFeed result) {
		
	}

}
