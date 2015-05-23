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

import org.mobley.album.data.Album;
import org.mobley.album.data.Artist;
import org.mobley.album.data.Release;
import org.mobley.album.data.Track;

public class AllMusicAlbumPanel extends JPanel
{

   public class ReleaseComboActionListener implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent e)
      {
         Release r = (Release) releaseCombo.getSelectedItem();
         if(r == null) return;
         // get the AllMusicAlbum for this url
         SwingUtilities.invokeLater(new Runnable()
         {

            @Override
            public void run()
            {
               frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               try
               {
                  ImageIcon icon = null;
                  try
                  {
                     icon = new ImageIcon(getImage(r.getImageSrc(), 300), r.getTitle());
                  }
                  catch (Exception e)
                  {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                     try
                     {
                        icon = new ImageIcon(getImage(album.getImagesrc(), 300), r.getTitle());
                     }
                     catch (Exception e1)
                     {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                     }
                  }

                  if (icon != null)
                  {
                     allMusicAlbumCoverLabel.setIcon(icon);
                  }
                  titleLabel.setText(album.getTitle());
                  StringBuilder artists = new StringBuilder();
                  int i = 0;
                  for (Artist artist : album.getArtists())
                  {
                     if (i > 0)
                        artists.append("/");
                     artists.append(artist.getName());
                     i++;
                  }
                  artistLabel.setText(artists.toString());

                  trackListModel.clear();
                  trackListModel.clear();
                  int j = 1;
                  if (r != null)
                  {
                     for (Track t : r.getTracks())
                     {
                        trackListModel.addElement(new ListedTrack(j++, t.getTitle()));
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

   private Album album;
   private JLabel allMusicAlbumCoverLabel = new JLabel();
   private JLabel artistLabel = new JLabel();
   private JLabel titleLabel = new JLabel();
   DefaultListModel<ListedTrack> trackListModel = new DefaultListModel<ListedTrack>();
   private JList<ListedTrack> trackList = new JList<ListedTrack>(trackListModel);
   DefaultComboBoxModel<Release> releaseComboModel = new DefaultComboBoxModel<Release>();
   private JComboBox<Release> releaseCombo = new JComboBox<Release>(releaseComboModel);
   private JFrame frame;

   public AllMusicAlbumPanel(JFrame frame)
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
      this.add(allMusicAlbumCoverLabel, c);

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
      this.add(releaseCombo, c);

   }

   public Album getAlbum()
   {
      return album;
   }

   public void setAlbum(Album album)
   {
      this.album = album;

      releaseComboModel.removeAllElements();

      int anIndex = -1;
      int j = 0;
      for (Release r : album.getReleases())
      {
         if (r.isMain())
            anIndex = j;
         releaseComboModel.addElement(r);
         j++;
      }

      releaseCombo.addActionListener(new ReleaseComboActionListener());

      if (anIndex > -1)
         releaseCombo.setSelectedIndex(anIndex);
   }

   private Image getImage(String url, int size) throws Exception
   {
      if (size == 75)
      {
         String url75 = url.replace("JPG_400", "JPG_75");
         try
         {
            Image i75 = ImageIO.read(new URL(url75));
            return i75;
         }
         catch (Exception e)
         {
            System.out.println("No jpg 75: " + url75);
            e.printStackTrace();
         }
      }
      Image i = ImageIO.read(new URL(url));
      if (size != 400)
      {
         i = i.getScaledInstance(size, size, Image.SCALE_SMOOTH);
      }
      return i;
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

   public Release getSelectedRelease()
   {
      return (Release)releaseCombo.getSelectedItem();
   }
}
