package org.mobley.album.data;

import java.util.HashMap;
import java.util.Map;

public class ArtistCache {

	private static final ArtistCache INSTANCE = new ArtistCache();
	
	public static ArtistCache getInstance() {
		return INSTANCE;
	}
	
	private Map<Integer,Artist> artists = new HashMap<Integer,Artist>();
	
	private ArtistCache() {
		
	}
	
	public Artist getArtist(int id) {
		return artists.get(id);
	}
	
	public Artist addArtist(Artist artist) {
		return artists.put(artist.getId(), artist);
	}
}
