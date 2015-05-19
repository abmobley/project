package org.mobley.album;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OwnedProcessor {

	public static List<OwnedTrack> getOwnedTracks(String title) throws IOException {
		SortedMap<Integer, SortedMap<Integer, OwnedTrack>> tracks = new TreeMap<Integer, SortedMap<Integer, OwnedTrack>>();
		System.out.println("Searching itunes library for: " + title);
		File file = new File("C:/Users/alan/Music/iTunes/iTunes Music Library.xml");

	      Document doc = Jsoup.parse(file, "UTF-8");

	      Elements elements = doc.select("dict dict dict");

		for (Element element : elements)

		{

			Element albumElement = element.select(
					":matchesOwn(^Album$) ~ string").first();

			String album = null;

			if (albumElement != null)

			{

				album = albumElement.ownText();

			}

			if (album != null && album.equals(title))

			{

				Element timeElement = element.select(
						":matchesOwn(^Total Time$) ~ integer").first();

				int time = 0;

				if (timeElement != null)

				{

					time = Integer.parseInt(timeElement.ownText());

				}

				Element discElement = element.select(
						":matchesOwn(^Disc Number$) ~ integer").first();

				int discNumber = 1;

				if (discElement != null)

				{

					discNumber = Integer.parseInt(discElement.ownText());

				}
				

				Element trackElement = element.select(
						":matchesOwn(^Track Number$) ~ integer").first();

				int trackNumber = 0;

				if (trackElement != null)

				{

					trackNumber = Integer.parseInt(trackElement.ownText());

				}

				SortedMap<Integer, OwnedTrack> discMap = tracks.get(discNumber);

				if (discMap == null)

				{

					discMap = new TreeMap<Integer, OwnedTrack>();

					tracks.put(discNumber, discMap);

				}

				Element nameElement = element.select(
						":matchesOwn(^Name$) ~ string").first();

				String name = null;

				if (nameElement != null)

				{

					name = nameElement.ownText();

				}

				discMap.put(trackNumber, new OwnedTrack(name, time));

			}

		}

		List<OwnedTrack> ownedTracks = new ArrayList<OwnedTrack>();

		for (SortedMap<Integer, OwnedTrack> discMap : tracks.values())

		{

			for (OwnedTrack ownedTrack : discMap.values()) {

				ownedTracks.add(ownedTrack);

			}

		}

		return ownedTracks;

	}

	public static class OwnedTrack {

		private final String name;

		private final int time;

		public OwnedTrack(String name, int time)

		{

			super();

			this.name = name;

			this.time = time;

		}

		public String getName()

		{

			return name;

		}

		public int getTime()

		{

			return time;

		}

		@Override
		public String toString()

		{

			return "OwnedTrack [name=" + name + ", time=" + time + "]";

		}

	}

}
