package org.mobley.album;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mobley.album.DBUtils.PreparedStatementCBH;
import org.mobley.album.allmusic.AllMusicTrack;

public class TrackProcessor {

	public static void addTracksToDB(List<AllMusicTrack> tracks, String albumId)
			throws Exception {

		System.out.println("Adding tracks: " + tracks);
		AddTracksCBH cbh = new AddTracksCBH(tracks, albumId);
		DBUtils.executePreparedStatement(cbh, false, true, "spotify");
	}

	private static class AddTracksCBH implements PreparedStatementCBH {

		private static final String statement = "INSERT INTO tracks (id,title,duration,pick,album_id,spotifyid) VALUES (?,?,?,?,?,?)";
		private List<AllMusicTrack> tracks;
		private String album_id;

		public AddTracksCBH(List<AllMusicTrack> tracks, String album_id) {
			this.tracks = tracks;
			this.album_id = album_id;
		}

		@Override
		public void prepareStatement(PreparedStatement pstmt)
				throws SQLException {

			for (AllMusicTrack track : tracks) {
				String title = track.getTitle();
				if (title.length() > 255) {
					title = title.substring(0, 255);
				}
				pstmt.setString(1, track.getId());
				pstmt.setString(2, title);
				pstmt.setLong(3, track.getDuration());
				pstmt.setBoolean(4, track.isPick());
				pstmt.setString(5, album_id);
				pstmt.setString(6, track.getSpotifyId());
				pstmt.addBatch();
			}
		}

		@Override
		public String getQuery() {
			return statement;
		}

		@Override
		public void setAutoIncrementKey(int key) {

		}

		@Override
		public int getAutoIncrementKey() {
			return 0;
		}

		@Override
		public void processResultSet(ResultSet rs) throws SQLException {

		}

	}

}
