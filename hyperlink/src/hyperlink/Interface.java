package hyperlink;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//top panel that contains all the functionalities 
public class Interface extends JFrame{
    /**
     * constructor
     */
     String primary;
     String secondary;
     SliderDemo video1;
     SliderDemo video2;
     JComboBox linkList; 
     Map<String,String[]> hyperlinks;
     File myFile;
     FileWriter myWriter;
     List<int[]> boundingBoxList; // a list to store all the boudning boxs (int[4]{x,y,w,h})
	 JButton createButton;
    public Interface() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        JPanel featurePanel = new JPanel();
        add(featurePanel, BorderLayout.NORTH);
      
        //Add content to the window.
        //Display the window.
        featurePanel.setLayout(new FlowLayout());
        //Create the label.
        JLabel actionLable = new JLabel("ACTION:", JLabel.CENTER);
        String[] actionStrings = { "Import Primary Video", "Import Secondary Video"};

        JComboBox actionList = new JComboBox(actionStrings);
        actionList.setSelectedIndex(0);
        actionList.addActionListener(new ActionListener() { 
          public void actionPerformed(ActionEvent e) { 
            importVideo(actionList.getSelectedIndex());
          } 
        } );
        JLabel linkLable = new JLabel("SELECT LINK:", JLabel.CENTER);
        
        

        createButton = new JButton("Create HYPERLINK");
        createButton.addActionListener(new ActionListener() { 
          public void actionPerformed(ActionEvent e) { 
            createNewConnection();
          } 
        } );
        String[] linkNames = { "NONE"};
        linkList = new JComboBox(linkNames);
        linkList.setSelectedIndex(0);
        linkList.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) { 
            	if(linkList.getSelectedIndex() !=0) {
            		 jumpToLink((String) linkList.getSelectedItem());
            	}
             
            }

			
          } );
        JButton saveButton = new JButton("Save Video");
        saveButton.addActionListener(new ActionListener() { 
          public void actionPerformed(ActionEvent e) { 
            saveFile();
          } 
        });
        featurePanel.add(actionLable);
        featurePanel.add(actionList);
        featurePanel.add(createButton);
        featurePanel.add(linkLable);
        featurePanel.add(linkList);
        featurePanel.add(saveButton);

             //take in the first file path 
            video1 = new SliderDemo(primary);
            add(video1, BorderLayout.WEST);
        
       
            //take in the second file path 
            video2 = new SliderDemo(secondary);
            add(video2, BorderLayout.EAST);
        
        
        
        pack();
        setVisible(true);
        
    }
    private void jumpToLink(String link) {
		if(hyperlinks.containsKey(link)) {
			int startFrame = Integer.valueOf(hyperlinks.get(link)[0]);
			video1.frameNumber=startFrame;
			video1.updatePictureBoundingBox(startFrame, 0, 0, 0, 0);
		}
		
	} 
    public void createNewConnection(){
        JFrame frame = new JFrame();
        //enable the connect button
        String s = (String)JOptionPane.showInputDialog(
                frame,
                "Enter the name of the HyperLink link or Create a New Link:\n"
                + "\"Name the HyperLink: \"",
                "CREATE HYPERLINK",
                JOptionPane.WARNING_MESSAGE,
                null,
                null, linkList.getSelectedItem());
        if(s!=null) {
        	 if(video1.getCurrentBoundingBox() ==null) {
                 return;
             }
           
        	createLink(s);
        	//overwirte the new 
        	video1.linkedFrames.put(video1.frameNumber, video1.getCurrentFrame());
        }
    }
 
    public String importVideo(int mode) {
        JFrame frame = new JFrame();
        if(mode == 0) {
            //import primary video
          String s = (String)JOptionPane.showInputDialog(
                  frame,
                  "Enter the folder path for primary video :\n"
                  + "\"import video:\"",
                  "IMPORT VIDEO",
                  JOptionPane.WARNING_MESSAGE,
                  null,
                  null, "AIFilmOne");
          if(s==null) {
              return null;
          }
          primary = s;
           //take in the first file path 
          video1.updatePath(s);  ///////
          hyperlinks = new HashMap<String, String[]>();
          //for each primary video, create a metadata file with the video name 
          try {
              myFile = new File(s+"_metadata.txt");
              if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
              } else {
                System.out.println("File already exists.");
              }
            } catch (IOException e) {
              System.out.println("An error occurred.");
              e.printStackTrace();
            }
          clearLinks();

        }else if(mode == 1) {
            //import primary video
          String s = (String)JOptionPane.showInputDialog(
                  frame,
                  "Enter the folder path for secondary video :\n"
                  + "\"import video: \"",
                  "IMPORT VIDEO",
                  JOptionPane.WARNING_MESSAGE,
                  null,
                  null, "AIFilmTwo");
          if(s==null) {
              return null;
          }
          secondary = s;
          video2.updatePath(secondary);
          video2.sethasVideo();
            
        }
        return null;
    }
    public void createLink(String name) {
        
        //boundingBoxList.add(video1.getCurrentBoundingBox());
        //if no box is drawn, then ignore the button action
    	if(hyperlinks.containsKey(name)) {
    		//adding a frame to current hyperlinks 
    		String[] currentLink = hyperlinks.get(name);
    		if(Integer.valueOf(currentLink[0]) > video1.frameNumber){
    			//update the startframe
    			currentLink[0]=video1.frameNumber+"";
    		}
    		if(Integer.valueOf(currentLink[1]) < video1.frameNumber){
    			//update the endframe
    			currentLink[1]=video1.frameNumber+"";
    		}
    		currentLink[2] = ""+Math.min(video1.getCurrentBoundingBox()[0],Integer.valueOf(currentLink[2]));
    		currentLink[3] = ""+Math.min(video1.getCurrentBoundingBox()[1],Integer.valueOf(currentLink[3])); 
    		currentLink[4] = ""+Math.max(video1.getCurrentBoundingBox()[2],Integer.valueOf(currentLink[4])); 
    		currentLink[5] = ""+Math.max(video1.getCurrentBoundingBox()[3],Integer.valueOf(currentLink[5])); 
    		//wont not update the link cause on link can only linke to one subframe of one subvideo
    	}else {
   		 linkList.addItem(name);

    		hyperlinks.put(name, new String[] {video1.frameNumber+"",video1.frameNumber+"",
    				video1.getCurrentBoundingBox()[0]+"",
    				video1.getCurrentBoundingBox()[1]+"",
    				video1.getCurrentBoundingBox()[2]+"",
    				video1.getCurrentBoundingBox()[3]+"",
    				video2.videoPath,
    				video2.frameNumber+""});
    	}
        
    }
    public String getPrimaryPath () {
        return primary;
    }
    public String getSecondaryPath () {
        return secondary;
    }
    public void clearLinks() {
        linkList.removeAllItems();
        linkList.addItem("NONE");
    }
    public void saveFile() {
    	try{
    	 myWriter = new FileWriter(myFile.getName(),true);
    	 for(Entry<String, String[]> entry : hyperlinks.entrySet() ) {
    		 myWriter.write(entry.getKey()+"\n");
             myWriter.write("startFrame:"+entry.getValue()[0]+"\n");
             myWriter.write("endFrame:"+entry.getValue()[1]+"\n");
             myWriter.write("x:"+entry.getValue()[2]+"\n");
             myWriter.write("y:"+entry.getValue()[3]+"\n");
             myWriter.write("w:"+entry.getValue()[4]+"\n");
             myWriter.write("h:"+entry.getValue()[5]+"\n");
             myWriter.write("path:"+entry.getValue()[6]+"\n");
             myWriter.write("subFrame:"+entry.getValue()[7]+"\n");
     	}
        
         myWriter.close();
         System.out.println("Successfully wrote to the file.");
       } catch (IOException e) {
         System.out.println("An error occurred.");
         e.printStackTrace();
       }
    }
}
