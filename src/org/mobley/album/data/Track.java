package org.mobley.album.data;

public class Track {

	private String id;
	private String title;
	private int duration;
	private boolean isPick;
	private String albumId;
	private String spotifyid;
	
	public Track(String id, String title, int duration, boolean isPick, String albumId, String spotifyid) {
		super();
		this.id = id;
		this.title = title;
		this.duration = duration;
		this.isPick = isPick;
		this.albumId = albumId;
		this.spotifyid = spotifyid;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getDuration() {
		return duration;
	}

	public boolean isPick() {
		return isPick;
	}

	public String getAlbumId() {
		return albumId;
	}

	public String getSpotifyid() {
		return spotifyid;
	}

	@Override
	public String toString() {
		return "Track [id=" + id + ", title=" + title + ", duration="
				+ duration + ", isPick=" + isPick + ", albumId=" + albumId
				+ ", spotifyid=" + spotifyid + "]";
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
		Track other = (Track) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
