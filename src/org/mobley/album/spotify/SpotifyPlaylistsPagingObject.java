package org.mobley.album.spotify;

import java.util.Arrays;

public class SpotifyPlaylistsPagingObject extends SpotifyPagingObject {

	private SpotifyPlaylist[] items;

	public SpotifyPlaylist[] getItems() {
		return items;
	}

	@Override
	public String toString() {
		return "SpotifyPlaylistsPagingObject [items=" + Arrays.toString(items)
				+ ", getHref()=" + getHref() + ", getLimit()=" + getLimit()
				+ ", getNext()=" + getNext() + ", getOffset()=" + getOffset()
				+ ", getPrevious()=" + getPrevious() + ", getTotal()="
				+ getTotal() + "]";
	}
	
	
}
