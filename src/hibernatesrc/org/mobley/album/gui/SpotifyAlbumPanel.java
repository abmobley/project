package org.mobley.album.gui;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.mobley.album.spotify.SpotifyAlbum;
import org.mobley.album.spotify.SpotifyAlbumsPagingObject;
import org.mobley.album.spotify.SpotifyArtist;
import org.mobley.album.spotify.SpotifyTrack;
import org.mobley.album.spotify.SpotifyUtil;

public class SpotifyAlbumPanel extends JPanel
{
   public class ReleaseComboActionListener implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent e)
      {
         SearchResultItem result = (SearchResultItem) searchResultCombo.getSelectedItem();
         if(result == null) return;
         // get the AllMusicAlbum for this url
         SwingUtilities.invokeLater(new Runnable()
         {

            @Override
            public void run()
            {
               frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               try
               {
                  selectedAlbum = SpotifyUtil.getAlbum(result.album.getHref());
                  System.out.println(selectedAlbum.getTracks().getTotal() + " " + result.album.getHref());
                  ImageIcon icon = null;
                  try
                  {
                     if (selectedAlbum.getImages() != null && selectedAlbum.getImages().length > 0)
                     {
                        icon = new ImageIcon(getImage(selectedAlbum.getImages()[0].getUrl(), 300), selectedAlbum.getName());
                     }
                  }
                  catch (Exception e)
                  {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                  }
                  if (icon != null)
                  {
                     spotifyAlbumCoverLabel.setIcon(icon);
                  }
                  String title = selectedAlbum.getName();
                  if(selectedAlbum.getCopyrights() != null && selectedAlbum.getCopyrights().length > 0)
                  {
                     title = title + " " + selectedAlbum.getCopyrights()[0].getText();
                  }
                  titleLabel.setText(selectedAlbum.getName());
                  StringBuilder artists = new StringBuilder();
                  int i = 0;
                  if (selectedAlbum.getArtists() != null)
                  {
                     for (SpotifyArtist artist : selectedAlbum.getArtists())
                     {
                        if (i > 0)
                           artists.append("/");
                        artists.append(artist.getName());
                        i++;
                     }
                  }
                  artistLabel.setText(artists.toString());

                  trackListModel.clear();

                  
                  int j = 1;
                  if (selectedAlbum != null)
                  {
                     List<SpotifyTrack> tracks = SpotifyUtil.getTracks(selectedAlbum);
                     for (SpotifyTrack t : tracks)
                     {
                        trackListModel.addElement(new ListedTrack(j++, t.getName()));
                     }
                  }
               }
               catch (Exception e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               finally
               {
                  frame.getContentPane().setCursor(Cursor.getDefaultCursor());
               }
            }

         });

      }

   }

   private SpotifyAlbumsPagingObject albums;
   private JLabel spotifyAlbumCoverLabel = new JLabel();
   private JLabel artistLabel = new JLabel();
   private JLabel titleLabel = new JLabel();
   DefaultListModel<ListedTrack> trackListModel = new DefaultListModel<ListedTrack>();
   private JList<ListedTrack> trackList = new JList<ListedTrack>(trackListModel);
   DefaultComboBoxModel<SearchResultItem> searchResultComboModel = new DefaultComboBoxModel<SearchResultItem>();
   private JComboBox<SearchResultItem> searchResultCombo = new JComboBox<SearchResultItem>(searchResultComboModel);
   private JFrame frame;
   private SpotifyAlbum selectedAlbum;

   public SpotifyAlbumPanel(JFrame frame)
   {
      super();
      this.setLayout(new GridBagLayout());
      this.frame = frame;
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 0;
      c.gridheight = 3;
      c.gridwidth = 1;
      c.insets = new Insets(5, 5, 5, 5);
      c.anchor = GridBagConstraints.NORTHWEST;
      this.add(spotifyAlbumCoverLabel, c);

      c.gridx = 1;
      c.gridheight = 1;
      this.add(titleLabel, c);

      c.gridy = 1;
      this.add(artistLabel, c);

      c.gridy = 2;
      c.gridheight = 3;
      this.add(trackList, c);
      titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
      artistLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

      c.gridx = 0;
      c.gridy = 3;
      c.gridheight = 1;
      this.add(searchResultCombo, c);
      searchResultCombo.addActionListener(new ReleaseComboActionListener());

   }

   public SpotifyAlbumsPagingObject getAlbums()
   {
      return albums;
   }

   public void setAlbums(SpotifyAlbumsPagingObject albums)
   {
      this.albums = albums;

      searchResultComboModel.removeAllElements();

      if (albums != null && albums.getItems() != null)
      {
         for (SpotifyAlbum album : albums.getItems())
         {
            searchResultComboModel.addElement(new SearchResultItem(album));
         }
      }
   }

   private Image getImage(String url, int size) throws Exception
   {

      Image i = ImageIO.read(new URL(url));
      return i.getScaledInstance(size, size, Image.SCALE_SMOOTH);
   }

   private class ListedTrack
   {
      private String display;

      public ListedTrack(int number, String title)
      {
         super();
         this.display = number + ". " + title;
      }

      @Override
      public String toString()
      {
         return display;
      }

   }

   private class SearchResultItem
   {
      private SpotifyAlbum album;

      public SearchResultItem(SpotifyAlbum album)
      {
         this.album = album;
      }

      @Override
      public String toString()
      {
         return album.getName() + " " + album.getType();
      }

   }

   public SpotifyAlbum getSelectedSpotifyAlbum()
   {
      return selectedAlbum;
   }
}
