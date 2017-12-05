package org.acme.drone2u;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import pt.lsts.neptus.util.ImageUtils;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class UavsState extends JPanel {
	public JTable table;

	/**
	 * Cria o JPanel com a tabela "Estado UAVs"
	 */	
	
	public UavsState() {

	    setBackground(Color.GRAY);
        
        JScrollPane scrollPane = new JScrollPane();
        
        JLabel drone2uLogo = new JLabel("");
        drone2uLogo.setBackground(Color.GRAY);
        drone2uLogo.setHorizontalAlignment(SwingConstants.CENTER);       
        ImageIcon drone2u = ImageUtils.getScaledIcon("images/drone2u.png", 94, 70);
        drone2uLogo.setIcon(drone2u);
        drone2uLogo.setOpaque(true);
 
        
        JLabel estadoUavsTitle = new JLabel("Estado UAVs");
        estadoUavsTitle.setForeground(Color.WHITE);
        estadoUavsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        estadoUavsTitle.setBackground(Color.DARK_GRAY);
        estadoUavsTitle.setOpaque(true);
        estadoUavsTitle.setFont(new Font("DejaVu Sans", Font.PLAIN, 25));
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(estadoUavsTitle, GroupLayout.PREFERRED_SIZE, 719, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(drone2uLogo)
                    .addContainerGap(25, Short.MAX_VALUE))
                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                    .addContainerGap(14, Short.MAX_VALUE)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 821, GroupLayout.PREFERRED_SIZE)
                    .addGap(15))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(16)
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(drone2uLogo)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addPreferredGap(ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                            .addComponent(estadoUavsTitle, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)))
                    .addGap(31)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 372, GroupLayout.PREFERRED_SIZE)
                    .addGap(16))
        );
        
        table = new JTable();
        table.setFont(new Font("Ubuntu Light", Font.PLAIN, 15));
        table.setModel(new DefaultTableModel(
            new Object[][] {
            },
            new String[] {
                "ID Drone", "Localização atual", "Altitude", "Plano atual"
            }
        ));
        table.getColumnModel().getColumn(1).setPreferredWidth(126);
        table.getColumnModel().getColumn(2).setPreferredWidth(91);
        table.getColumnModel().getColumn(3).setPreferredWidth(105);
        scrollPane.setViewportView(table);
        setLayout(groupLayout);
        
	}
	
	public JTable getTable() {
        return table;	    
	}
}