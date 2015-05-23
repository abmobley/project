package org.mobley.album.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.mobley.album.data.Album;
import org.mobley.album.data.AlbumManager;
import org.mobley.album.data.Release;
import org.mobley.album.spotify.SpotifyAlbum;
import org.mobley.album.spotify.SpotifyAlbumSearchResult;
import org.mobley.album.spotify.SpotifyUtil;

public class AlbumGUI
{

   private AllMusicAlbumPanel allMusicAlbumPanel;
   private SpotifyAlbumPanel spotifyAlbumPanel;
   private JFrame frame;
   private JComboBox<String> missingCombo;
   
   public static void main(String[] args)
   {
      AlbumGUI gui = new AlbumGUI();
      gui.createAndShowGUI();
   }

   private void createAndShowGUI() {
      // Schedule a job for the event-dispatching thread:
      // creating and showing this application's GUI.
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            // Create and set up the window.
            frame = new JFrame("Spotify Album DB");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(new BorderLayout());

            allMusicAlbumPanel = new AllMusicAlbumPanel(frame);
            allMusicAlbumPanel.setMinimumSize(new Dimension(200,500));
            spotifyAlbumPanel = new SpotifyAlbumPanel(frame);
            spotifyAlbumPanel.setMinimumSize(new Dimension(200,500));
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                  new JScrollPane(allMusicAlbumPanel), new JScrollPane(spotifyAlbumPanel));
            JPanel topPanel = createTopPanel();
            frame.getContentPane().add(topPanel, BorderLayout.NORTH);
            frame.getContentPane().add(splitPane);
            
            JPanel panel = new JPanel();
            JButton acceptButton = new JButton("Accept");
            acceptButton.addActionListener(new AcceptActionListener());
            panel.add(acceptButton);
            JButton removeButton = new JButton("Remove");
            removeButton.addActionListener(new RemoveActionListener());
            panel.add(removeButton);
            
            frame.getContentPane().add(panel, BorderLayout.SOUTH);

            // Display the window.
            frame.pack();
            frame.setVisible(true);
         }
      });
   }
   
   private JPanel createTopPanel() {
      JPanel topPanel = new JPanel();
      topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));

      JLabel missingLabel = new JLabel("Missing Albums: ", JLabel.LEADING);
      missingLabel.setVerticalTextPosition(JLabel.BOTTOM);
      List<String> missingurls = AlbumManager.findAlbumsNotProcessed();
      missingCombo = new JComboBox<String>(missingurls.toArray(new String[missingurls.size()]));

      missingCombo.addActionListener(new MissingComboActionListener());
      
      topPanel.add(missingLabel);
      topPanel.add(missingCombo);

      topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      
     missingCombo.setSelectedIndex(0);
      return topPanel;
   }
   
   
   private class MissingComboActionListener implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent e)
      {
         JComboBox<String> cb = (JComboBox<String>)e.getSource();
         String missingURL = (String)cb.getSelectedItem();
         //get the AllMusicAlbum for this url
         SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run()
            {
               frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               try
               {
                  Album album = AlbumManager.findAlbumFromURL(missingURL);
                  allMusicAlbumPanel.setAlbum(album);
                  SpotifyAlbumSearchResult result = SpotifyUtil.searchAlbum(album.getTitle(), album.getArtists());
                  spotifyAlbumPanel.setAlbums(result.getAlbums());
                  System.out.println(allMusicAlbumPanel.getAlbum().getTitle());
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
   
   private class AcceptActionListener implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent e)
      {
         frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         JButton b = (JButton)e.getSource();
         b.setEnabled(false);
         Release release = allMusicAlbumPanel.getSelectedRelease();
         SpotifyAlbum spotifyAlbum = spotifyAlbumPanel.getSelectedSpotifyAlbum();
         if(release != null && spotifyAlbum != null)
         {
            AlbumManager.setSpotifyIdsAndDurations(release, spotifyAlbum.getId(), SpotifyUtil.getTracks(spotifyAlbum));
         }
         b.setEnabled(true);
         frame.getContentPane().setCursor(Cursor.getDefaultCursor());
      }
      
   }

   private class RemoveActionListener implements ActionListener
   {

      @Override
      public void actionPerformed(ActionEvent e)
      {
         frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         JButton b = (JButton)e.getSource();
         b.setEnabled(false);
         Album album = allMusicAlbumPanel.getAlbum();
         AlbumManager.setProcessed(album);
         missingCombo.removeItem(album.getUrl());
         missingCombo.setSelectedIndex(0);
         b.setEnabled(true);
         frame.getContentPane().setCursor(Cursor.getDefaultCursor());
      }

   }
}
