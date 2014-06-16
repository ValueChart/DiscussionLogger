import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.EOFException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;

import acme.misc.ScanfReader;


public class MainInterface extends JFrame implements MouseListener{
    private static final long serialVersionUID = 1L;
    
    private String filename = "";
    private String logFile;
    SimpleDateFormat df;

    private TreeSet<String> criteria;
    private LinkedHashSet<String> alternatives;
    private TreeSet<String> users;
    private String problem = null;
    private LogPanel logPanel;
    
    private int colWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.25);
    private int buttonHeight = 40;
    private Font font = new Font("Arial", Font.PLAIN, 14);
    
    FileReader initReader;
    
    public MainInterface(String file, ArrayList<String> usrs) {
        filename = file;
        
        users = new TreeSet<String>();
        for (String u : usrs) {
            users.add(u);
        }
        
        criteria = new TreeSet<String>();
        alternatives = new LinkedHashSet<String>();
        parseFile();

        df = new SimpleDateFormat("ddMMMyyyy-HHmmss");
        logFile = "discuss_" + problem + "_" + df.format(Calendar.getInstance().getTime()) + ".csv";
        df = new SimpleDateFormat("ddMMMyyyy, HH:mm:ss");
        
        showInterface();
    }
    
    private void showInterface() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.X_AXIS));
        
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.add(new JLabel("Users"));
        for (String u : users) {
            userPanel.add(Box.createVerticalStrut(10));
            JLabel button = new JLabel("<html>" + u + "</html>");
            button.setName("user");
            button.addMouseListener(this);
            button.setPreferredSize(new Dimension(colWidth, buttonHeight));
            button.setMaximumSize(new Dimension(colWidth, buttonHeight));
            button.setMinimumSize(new Dimension(colWidth, buttonHeight));
            button.setFont(font);
            button.setBorder(BorderFactory.createLineBorder(Color.gray));
            userPanel.add(button);
        }
        userPanel.setAlignmentY(TOP_ALIGNMENT);
        userPanel.setPreferredSize(new Dimension(colWidth, userPanel.getPreferredSize().height));
        mainPanel.add(userPanel);
        mainPanel.add(Box.createHorizontalStrut(20));
        
        JPanel criteriaPanel = new JPanel();
        criteriaPanel.setLayout(new BoxLayout(criteriaPanel, BoxLayout.Y_AXIS));
        criteriaPanel.add(new JLabel("Criteria"));
        for (String c : criteria) {
            criteriaPanel.add(Box.createVerticalStrut(10));
            JLabel button = new JLabel("<html>" + c + "</html>");
            button.setName("crit");
            button.addMouseListener(this);
            button.setPreferredSize(new Dimension(colWidth, buttonHeight));
            button.setMaximumSize(new Dimension(colWidth, buttonHeight));
            button.setMinimumSize(new Dimension(colWidth, buttonHeight));
            button.setFont(font);
            button.setBorder(BorderFactory.createLineBorder(Color.gray));
            criteriaPanel.add(button);
        }
        criteriaPanel.setAlignmentY(TOP_ALIGNMENT);
        criteriaPanel.setPreferredSize(new Dimension(colWidth, criteriaPanel.getPreferredSize().height));
        mainPanel.add(criteriaPanel);
        mainPanel.add(Box.createHorizontalStrut(20));
        
        JPanel alternativePanel = new JPanel();
        alternativePanel.setLayout(new BoxLayout(alternativePanel, BoxLayout.Y_AXIS));
        alternativePanel.add(new JLabel("Alternatives"));
        int i = 1;
        for (String a : alternatives) {
            alternativePanel.add(Box.createVerticalStrut(10));
            JLabel button = new JLabel("<html>(" + i + ") " + a + "</html>");
            button.addMouseListener(this);
            button.setName("alt");
            button.setPreferredSize(new Dimension(colWidth, buttonHeight));
            button.setMaximumSize(new Dimension(colWidth, buttonHeight));
            button.setMinimumSize(new Dimension(colWidth, buttonHeight));
            button.setFont(font);
            button.setBorder(BorderFactory.createLineBorder(Color.gray));
            alternativePanel.add(button);
            i++;
        }
        alternativePanel.setAlignmentY(TOP_ALIGNMENT);
        alternativePanel.setPreferredSize(new Dimension(colWidth, alternativePanel.getPreferredSize().height));
        mainPanel.add(alternativePanel);
        mainPanel.add(Box.createHorizontalStrut(30));
        
        logPanel = new LogPanel(colWidth,userPanel.getPreferredSize().height);
        alternativePanel.setAlignmentY(TOP_ALIGNMENT);
        mainPanel.add(logPanel);
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        setContentPane(scrollPane);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void parseFile() {
        try{
            initReader = new FileReader(filename);
            try {
                read(initReader);
            } catch (IOException e) {
                System.out.println("Error in input: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void read(Reader reader) throws IOException {
        ScanfReader scanReader = new ScanfReader(reader);
    
        while (true) {
            String keyword = null;
            try {
                keyword = scanReader.scanString();
            } catch (EOFException e) {
                break;
            }
            if (keyword.equals("attributes")) {
                readAttributes(scanReader);
            } else if (keyword.equals("entry")) {
                alternatives.add(readEntry(scanReader));
            }
        }
    }
    
    private String readAttributes(ScanfReader scanReader) throws IOException{
        String next = null;
        String attr = null;
        while (!(next = scanReader.scanString()).equals("end")) {
            if (next.equals("attributes")) { //if keyword=attributes, then read abstract name and ratio
                next = scanReader.scanString();
                attr = next; // abstract name
                scanReader.scanString(); // ratio
                if (problem == null)
                    problem = attr;
                else
                    criteria.add(attr);
                attr = readAttributes(scanReader);
                if (attr != null)
                    criteria.add(attr);
            } else { // primitive, just read to end
                attr = next;
                while (!(next = scanReader.scanString()).equals("end")) {}
                criteria.add(attr);
            }
        }
        return null;
    }
    
    private String readEntry(ScanfReader scanReader) throws IOException{
        String entryName = scanReader.scanString("%S");
        while (!scanReader.scanString().equals("end")) {}
        return entryName;
    }
    
    private void logEvent(String datetime, String msg) {
        if (msg.isEmpty()) return;
        try {
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(datetime);
            fw.write(",");
            fw.write(msg);
            fw.write("\n");
            fw.close();
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent ae) {
        if (ae.getSource() instanceof JLabel) {
            JLabel button = (JLabel) ae.getSource();
            String datetime = df.format(Calendar.getInstance().getTime());
            String text = button.getText();
            if (button.getName().equals("user")) {
                logEvent(datetime, "user," + text);
                logPanel.addLog(datetime, "U", text);
            } else if (button.getName().equals("crit")) {
                logEvent(datetime, "criteria," + text);
                logPanel.addLog(datetime, "C", text);
            } else if (button.getName().equals("alt")) {
                logEvent(datetime, "alternative," + text);
                logPanel.addLog(datetime, "A", text);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }
    
    
}
