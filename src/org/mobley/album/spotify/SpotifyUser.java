package org.mobley.album.spotify;

import java.util.Arrays;

public class SpotifyUser {

	private String country;
	private String display_name;
	private String email;
    private SpotifyExternalURL external_urls;
    private SpotifyFollowers followers;
    private String href;
    private String id;
    private SpotifyImage[] images;
    private String product;
    private String type;
    private String uri;
    
	public String getCountry() {
		return country;
	}
	public String getDisplay_name() {
		return display_name;
	}
	public String getEmail() {
		return email;
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
	public String getProduct() {
		return product;
	}
	public String getType() {
		return type;
	}
	public String getUri() {
		return uri;
	}
	@Override
	public String toString() {
		return "SpotifyUser [country=" + country + ", display_name="
				+ display_name + ", email=" + email + ", external_urls="
				+ external_urls + ", followers=" + followers + ", href=" + href
				+ ", id=" + id + ", images=" + Arrays.toString(images)
				+ ", product=" + product + ", type=" + type + ", uri=" + uri
				+ "]";
	}
    
    
}
