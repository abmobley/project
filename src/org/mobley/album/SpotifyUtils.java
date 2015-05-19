package org.mobley.album;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mobley.album.data.Album;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SpotifyUtils {

	private static final String ALBUM_SEARCH_URL = "http://ws.spotify.com/search/1/album?q=";

	private static final String ALBUM_LOOKUP_URL = "http://ws.spotify.com/lookup/1/?uri=";
	
	public static boolean isAlbumAvailable(Album album) {
		return false;
	}
	
	
	private static void lookup(String uri) throws Exception {

		Document doc = getDocument(ALBUM_LOOKUP_URL + uri + "&extras=trackdetail");
		NodeList trackNodes = doc.getElementsByTagName("track");
		for(int i = 0; i < trackNodes.getLength(); i++) {
			Node trackNode = trackNodes.item(i);
			if(trackNode instanceof Element) {
				String title = ((Element)trackNode).getElementsByTagName("name").item(0).getTextContent();
				double length  = Double.parseDouble(((Element)trackNode).getElementsByTagName("length").item(0).getTextContent());
				System.out.println(title + "|" + Math.round(length) + "|0");
			}
		}
		System.out.println(trackNodes.getLength());
	}
	
	private static Document getDocument(String uriString) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(uriString);
		doc.getDocumentElement().normalize();


		return doc;
	}
	
	public static void main(String[] args) {
		try {
			lookup("spotify:album:0VMty5eHa365B377YHMAFA");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
