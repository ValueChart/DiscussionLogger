import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class LogPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private JScrollPane scrollPane;

    JTable table;   
    DefaultTableModel tabModel;  
    Vector<Vector<String>> rows;
    Vector<String> cols;
    Vector<String> data;
    int width;
    int height;
        
    public LogPanel(int w, int h) {
        width = w;
        height = h;
        
        cols = new Vector<String>();
        cols.add("Time");
        cols.add("Type");
        cols.add("Action");
        rows = new Vector<Vector<String>>();
        
        tabModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
               //all cells false
               return false;
            }
        };
        tabModel.setDataVector(rows, cols);      
        table = new JTable(tabModel);        
        
        table.setAutoCreateColumnsFromModel(true);
        table.setRowSelectionAllowed(false);      
        table.setGridColor(Color.WHITE);
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(10);
        table.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(width-40);
        scrollPane = new JScrollPane(table);    
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(scrollPane);
    }
    
    public void addLog(String datetime, String type, String action) {
        data = new Vector<String>();
        String time = datetime.split(",")[1];
        data.add(time);
        data.add(type);
        data.add(action);

        rows.add(data);
        tabModel.setDataVector(rows, cols);
        tabModel.fireTableStructureChanged();
        
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
            public void adjustmentValueChanged(AdjustmentEvent e) {  
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
            }
        });
        table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(10);
        table.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(width-40);
        repaint();
    }
}
