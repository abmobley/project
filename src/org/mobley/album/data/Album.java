package org.mobley.album.data;

import java.util.ArrayList;
import java.util.List;


public class Album {

	private String id;
	private String title;
	private int rating;
	private String url;
	private String spotifyid;
	private List<Track> tracks = new ArrayList<Track>();
	
	public Album(String id, String title, int rating, String spotifyid) {
		super();
		this.id = id;
		this.title = title;
		this.rating = rating;
		this.spotifyid = spotifyid;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getRating() {
		return rating;
	}
	
	public String getSpotifyid() {
		return spotifyid;
	}

	public int getNumberOfTracks() {
		return tracks.size();
	}
	
	public Track getTrack(int index) {
		return tracks.get(index);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other = (Album) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Album [id=" + id + ", title=" + title + ", rating=" + rating
				+ ", url=" + url + ", spotifyid=" + spotifyid + ", tracks="
				+ tracks + "]";
	}

	public void add(Track track) {
		tracks.add(track);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	

}
