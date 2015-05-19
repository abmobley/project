package org.mobley.album;

import java.io.IOException;

import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXException;

public class DocumentUtils {

	private static DOMParser parser = new DOMParser();
	private static DOMReader reader = new DOMReader();
	
	public static Document getDocument(String url) throws SAXException, IOException {
		int attempts = 0;
		boolean success = false;
		
		while (!success && attempts < 5) {
			try {
				attempts++;
				parser.parse(url);
				success = true;
			} catch (Exception e) {
				System.out.println(e.getMessage() + ", attempts = " + attempts);
				success = false;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					
				}
			}
		}

        org.w3c.dom.Document document = parser.getDocument();
        return reader.read(document);
	}
}
