package org.mobley.album.spotify;

import java.util.Arrays;

public class SpotifyPlaylist {

	private boolean collaborative;
	private String description;
	private SpotifyExternalURL external_urls;
	private SpotifyFollowers followers;
	private String href;
	private String id;
	private SpotifyImage[] images;
	private String name;
	private SpotifyUser owner;
	private String snapshot_id;
	private SpotifyTracksPagingObject tracks;
	private String type;
	private String uri;

	public boolean isCollaborative() {
		return collaborative;
	}

	public String getDescription() {
		return description;
	}

	public SpotifyExternalURL getExternal_urls() {
		return external_urls;
	}

	public SpotifyFollowers getFollowers() {
		return followers;
	}

	public String getHref() {
		return href;
	}

	public String getId() {
		return id;
	}

	public SpotifyImage[] getImages() {
		return images;
	}

	public String getName() {
		return name;
	}

	public SpotifyUser getOwner() {
		return owner;
	}

	public String getSnapshot_id() {
		return snapshot_id;
	}

	public SpotifyTracksPagingObject getTracks() {
		return tracks;
	}

	public String getType() {
		return type;
	}

	public String getUri() {
		return uri;
	}

	@Override
	public String toString() {
		return "SpotifyPlaylist [collaborative=" + collaborative
				+ ", description=" + description + ", external_urls="
				+ external_urls + ", followers=" + followers + ", href=" + href
				+ ", id=" + id + ", images=" + Arrays.toString(images)
				+ ", name=" + name + ", owner=" + owner + ", snapshot_id="
				+ snapshot_id + ", tracks=" + tracks + ", type=" + type
				+ ", uri=" + uri + "]";
	}

}
