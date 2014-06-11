import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class DiscussionLogger  extends JPanel implements ActionListener{

    
    private static final long serialVersionUID = 1L;
    protected static JFrame frame;
    static JComponent newContentPane;
    public static String datafilename;
        
    ArrayList<String> vc_files;
    
    String filename;
    
    DefaultListModel<String> listModel;
    JList<String> lstFiles;  
    JScrollPane scrList; 
    JButton btnOpen;
    JButton btnCancel;   
    JPanel pnlButtons;

    public DiscussionLogger(){  
        //Set up the File List
        listModel = new DefaultListModel<String>(); 
        lstFiles = new JList<String>(listModel);
        lstFiles.setEnabled(false);
        lstFiles.setVisibleRowCount(10);       
        lstFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
        scrList = new JScrollPane(lstFiles);
        lstFiles.setPreferredSize(new Dimension(200,500));
      
        scrList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setList();

        //Set up file Open/Create and Cancel command buttons 
        btnOpen = new JButton("  Open ");
        btnOpen.addActionListener(this);
        btnOpen.setActionCommand("btnOpen");
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        btnCancel.setActionCommand("btnCancel");
        
        pnlButtons = new JPanel();
        pnlButtons.setLayout(new BoxLayout(pnlButtons, BoxLayout.LINE_AXIS));
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pnlButtons.add(btnOpen);
        pnlButtons.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlButtons.add(btnCancel);        
 
        //Layout all panels
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        scrList.setAlignmentX(LEFT_ALIGNMENT);
        add(scrList);
        add(Box.createRigidArea(new Dimension(0,5)));
        pnlButtons.setAlignmentX(LEFT_ALIGNMENT);
        add(pnlButtons);

        setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
    }
    
    public void actionPerformed(ActionEvent e) {        
        if ("btnOpen".equals(e.getActionCommand())) {
            frame.setVisible(false);
            filename = lstFiles.getSelectedValue(); 
        } else if ("btnCancel".equals(e.getActionCommand())) {
                System.exit(0);
        }
    }
 
    //List all the vc files
    void setList() {
        vc_files = new ArrayList<String>();
        // String[] filenames;
        File f = new File(".");
        String files[] = f.list();
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".vc")) {
                vc_files.add(files[i]);
            }
        }
        lstFiles.setEnabled(true);
        listModel.clear();
        // sort the list
        for (int i = 0; i < vc_files.size(); i++)
            listModel.addElement(vc_files.get(i).toString());
        int numItems = listModel.getSize();
        String[] a = new String[numItems];
        for (int i = 0; i < numItems; i++) {
            a[i] = listModel.getElementAt(i);
        }
        sortArray(a);
        lstFiles.setListData(a);
        lstFiles.revalidate();

        lstFiles.setSelectedIndex(0);
    }
    
    //Get the names of the VC files from all the participants
    public Vector<String> getFileNames(String name, Vector<String> list){
        try{
            FileReader fr = new FileReader(name);
            BufferedReader br = new BufferedReader(fr);
            StreamTokenizer st = new StreamTokenizer(br);
            st.whitespaceChars(',', ',');
            while(st.nextToken() != StreamTokenizer.TT_EOF) {
                if(st.ttype==StreamTokenizer.TT_WORD)
                    list.add(st.sval);
            }
            fr.close();         
        }catch(Exception e) {
            System.out.println("Exception: " + e);
        }
        return list;
    }     
    
    //Count number of alternatives
    public int countEntries(String name){
        int count=0;
        try{
            FileReader fr = new FileReader(name);
            BufferedReader br = new BufferedReader(fr);
            StreamTokenizer st = new StreamTokenizer(br);
            st.whitespaceChars(',', ',');
            while(st.nextToken() != StreamTokenizer.TT_EOF) {
                if(st.ttype==StreamTokenizer.TT_WORD)
                    if(st.sval.equals("entry")){
                        count++;
                    }
            }
            fr.close();         
        }catch(Exception e) {
            System.out.println("Exception: " + e);
        }
        return count;       
    }
    
    public static void showStartView() {
        //Create and set up the window.
        frame = new JFrame("Discussion Logger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newContentPane = new DiscussionLogger();
        frame.setContentPane(newContentPane);      
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    //sorts an array for the listview
    void sortArray(String[] strArray)
    {
      if (strArray.length == 1)    // no need to sort one item
        return;
      Collator collator = Collator.getInstance();
      String strTemp;
      for (int i=0;i<strArray.length;i++)
      {
        for (int j=i+1;j<strArray.length;j++)
        {
          if (collator.compare(strArray[i], strArray[j]) > 0)
          {
            strTemp = strArray[i];
            strArray[i] = strArray[j];
            strArray[j] = strTemp;
          }
        }
      }
    }

    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showStartView();
            }
        });
    }
}
