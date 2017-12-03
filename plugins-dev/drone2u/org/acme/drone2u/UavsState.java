package org.acme.drone2u;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class UavsState extends JPanel {
	public JTable table;

	/**
	 * Create the panel.
	 */
	public UavsState() {
		setBackground(Color.WHITE);
		
		JScrollPane scrollPane = new JScrollPane();
		
		JButton testButton = new JButton("Test button");
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});
		
		JLabel lblNewLabel = new JLabel("");
		//lblNewLabel.setIcon(new ImageIcon(Teste.class.getResource("/img/drone2u_r.png")));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(71, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(testButton)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 708, GroupLayout.PREFERRED_SIZE))
					.addGap(71))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(358)
					.addComponent(lblNewLabel)
					.addContainerGap(375, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(99)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(testButton)
					.addPreferredGap(ComponentPlacement.RELATED, 141, Short.MAX_VALUE)
					.addComponent(lblNewLabel)
					.addGap(29))
		);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
	        new Object[][] {
                {null, null, null, null},
                {null, null, null, null},
            },
			new String[] {
				"ID Drone", "Localização atual", "Altura", "Velocidade"
			}
		));
		scrollPane.setViewportView(table);
		setLayout(groupLayout);	

	}
	
	public JTable getTable() {
        return table;	    
	}
}