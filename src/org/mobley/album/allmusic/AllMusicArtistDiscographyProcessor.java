package org.mobley.album.allmusic;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AllMusicArtistDiscographyProcessor {

	public static Set<String> get(String url, int limit) throws IOException {
		Set<String> hrefs = new HashSet<String>(limit);
		Document doc = AllMusicUtil
				.getDocument(url);
		Elements rows = doc.select(".discography tbody tr");
		Set<DiscographyEntry> discography = new TreeSet<DiscographyEntry>();
		for (Element row : rows) {
			Element t = row.select(".title").first();
			if (t != null) {
				String title = t.attr("data-sort-value");
				if (title != null) {
					Element a = t.select("a").first();
					if (a != null) {
						String href = a.attr("href");
						if (href != null) {
							int rating = 0;
							Element ratingElement = row.select(
									".all-rating div").first();
							if (ratingElement != null) {
								int index = ratingElement.attr("class")
										.lastIndexOf('-');
								if (index > -1) {
									try {
										rating = Short.parseShort(ratingElement
												.attr("class").substring(
														index + 1));
									} catch (NumberFormatException e) {
										rating = 0;
									}
								}
							}
							discography.add(new DiscographyEntry(title, href,
									rating));
						}
					}
				}
			}
		}
		for (DiscographyEntry entry : discography) {
			hrefs.add(entry.href);
			System.out.println("Added: " + entry);
			if (hrefs.size() >= limit)
				break;
		}
		return hrefs;
	}

	private static class DiscographyEntry implements
			Comparable<DiscographyEntry> {

		private final String title;
		private final String href;
		private final int rating;

		public DiscographyEntry(String title, String href, int rating) {
			super();
			this.title = title;
			this.href = href;
			this.rating = rating;
		}

		@Override
		public int compareTo(DiscographyEntry o) {
			int c = 0;
			if (rating == o.rating) {
				c = title.compareTo(o.title);
			} else {
				c = o.rating - rating;
			}
			return c;
		}

		@Override
		public String toString() {
			return "DiscographyEntry [title=" + title + ", href=" + href
					+ ", rating=" + rating + "]";
		}

	}
}
