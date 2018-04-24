
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class RegexTable extends JFrame {
	private static JPanel filterPanel;
	private static JTextField filterText = new JTextField(30);
	
	public RegexTable (){
		
		Runnable runner = new Runnable(){
			String[][] fileReader() throws FileNotFoundException{
				List<Object> rows = new ArrayList<>();
				
				File file = new File("courses.txt");
				Scanner fileReader = new Scanner(file);
				String A, B, C, D, E, F, G, H, I;
				int index = 0; 
				while (fileReader.hasNext()) {
					int i2 = 0;
					A = fileReader.next();
					B = fileReader.next();
					C = fileReader.next();
					D = fileReader.next();
					E = fileReader.next();
					F = fileReader.next();
					G = fileReader.next();
					H = fileReader.next();
					I = fileReader.next();
					
					String[] titles = {A, B, C, D, E, F, G, H, I};
					rows.add(titles);

					index++;
				} 
				String[][] returnVal = new String[index][9];
				for(int i = 0; i < index; i++) {
					Object[] temp = (Object[]) rows.get(i);
					for(int k = 0; k < 9; k++){
						returnVal[i][k] = (String)temp[k];
					}
				}
				return returnVal;
			}
			
			
			public void run(){
				
				JFrame frame = new JFrame("Regexing JTable");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				String columns[] = {"Department", "Number", "days", "seats", "begin", "end", " ", "Professor", "cost"};
				TableModel model = null;
				try {
					model = new DefaultTableModel(fileReader(), columns){
						//@SuppressWarnings("unchecked")
						public Class getColumnClass(int column){
							Class returnValue;
							if ((column>=0)&&(column<getColumnCount())){
								returnValue = getValueAt(0, column).getClass();
							}else {
								returnValue = Object.class;
							}
							return returnValue;
						}
					};
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				final JTable table = new JTable (model);
				final TableRowSorter<TableModel>sorter =
				new TableRowSorter<TableModel>(model);
				table.setRowSorter(sorter);
				

				JScrollPane pane = new JScrollPane(table);
				frame.add(pane, BorderLayout.CENTER);
				JPanel panel = new JPanel(new BorderLayout());
				
				filterPanel = new JPanel();
				filterPanel.setLayout(new BorderLayout());
				filterPanel.setBorder(BorderFactory.createEtchedBorder());
				filterPanel.add(filterText, BorderLayout.SOUTH);
				
				
				
				

				JLabel label = new JLabel ("Filter");
				panel.add(label, BorderLayout.WEST);
				//final JTextField filterText = new JTextField("41");
				//panel.add(filterText, BorderLayout.CENTER);
				frame.add(pane, BorderLayout.NORTH);
				
				JButton button = new JButton("Filter");
				
				
				button.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						filterText.addActionListener(this);
						String text = filterText.getText();
						// Object textArea;
						//textArea.append(text);
						 filterText.selectAll();
						
						if(text.length() == 0){
							sorter.setRowFilter(null);
						}else{
							sorter.setRowFilter(RowFilter.regexFilter(text));
						}
					}
					
					
				});
				frame.add(filterPanel);
				frame.add(button, BorderLayout.SOUTH);
				frame.setVisible(true);
				
				
				
			}
		};
		EventQueue.invokeLater(runner);
		setSize(400, 200);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RegexTable r = new RegexTable();

	}

}
