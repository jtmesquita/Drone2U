/*
 * Copyright (c) 2004-2017 Universidade do Porto - Faculdade de Engenharia
 * Laboratório de Sistemas e Tecnologia Subaquática (LSTS)
 * All rights reserved.
 * Rua Dr. Roberto Frias s/n, sala I203, 4200-465 Porto, Portugal
 *
 * This file is part of Neptus, Command and Control Framework.
 *
 * Commercial Licence Usage
 * Licencees holding valid commercial Neptus licences may use this file
 * in accordance with the commercial licence agreement provided with the
 * Software or, alternatively, in accordance with the terms contained in a
 * written agreement between you and Universidade do Porto. For licensing
 * terms, conditions, and further information contact lsts@fe.up.pt.
 *
 * Modified European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the Modified EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENSE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * https://github.com/LSTS/neptus/blob/develop/LICENSE.md
 * and http://ec.europa.eu/idabc/eupl.html.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: joao
 * 09/12/2017
 */
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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import pt.lsts.neptus.comm.manager.imc.ImcSystem;
import pt.lsts.neptus.comm.manager.imc.ImcSystemsHolder;
import pt.lsts.neptus.plugins.update.Periodic;
import pt.lsts.neptus.util.ImageUtils;

import java.awt.event.ActionListener;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Image;

import javax.swing.SwingConstants;
import java.awt.SystemColor;
import javax.swing.JDesktopPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.CardLayout;

public class PainelInfo extends JPanel {
    private JTable tableEncomendas;
    private JTable tableEstadoUavs;
    private JTextField uavsOpText;
    private JTextField uavsFalhaText;
    private JTextField entregasSucessoText;
    private JTextField entregasFalhaText;
    private JTextField tempText;
    private JTextField humidadeText;
    private JTextField ventoText;
    private JComboBox<String> comboBoxFiltroUav;
    private SQL_functions database;
    private JTextField weatherDescriptionText;
    private JTextField uavsLivresText;
    private JTextField uavsOcupadosText;
    private JTextField entregasPendentesText;
    private JTextField entregasCursoText;
    private JProgressBar progressBarOcupacao;
    private TimeSeries series;

    /**
     * Create the panel.
     */
    public PainelInfo() {
        database = new SQL_functions();      
        
        setForeground(Color.RED);
        setBackground(new Color(102, 102, 102));
        
        JLabel drone2uLogo = new JLabel("");
        drone2uLogo.setFont(new Font("DejaVu Sans", Font.BOLD, 20));
        drone2uLogo.setBackground(new Color(102, 102, 102));
        drone2uLogo.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon drone2u = ImageUtils.getScaledIcon("images/drone2u.png", 71, 53);
        drone2uLogo.setIcon(drone2u);
        drone2uLogo.setOpaque(true);
          
        JLabel lblPainelInfo = new JLabel("Painel Informativo");
        lblPainelInfo.setForeground(Color.WHITE);
        lblPainelInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblPainelInfo.setBackground(Color.DARK_GRAY);
        lblPainelInfo.setOpaque(true);
        lblPainelInfo.setFont(new Font("Monospaced", Font.BOLD, 25));
        
        JPanel panelEncomendas = new JPanel();
        panelEncomendas.setBackground(Color.GRAY);
        
        JPanel panelEstadoUavs = new JPanel();
        panelEstadoUavs.setBackground(Color.GRAY);
        
        JPanel panelCondMeteo = new JPanel();
        panelCondMeteo.setBackground(Color.GRAY);
        
        JPanel panelFalhas = new JPanel();
        panelFalhas.setBackground(Color.GRAY);
        
        JLabel lblUavsOperacionais = new JLabel("UAVs operacionais");
        lblUavsOperacionais.setForeground(Color.WHITE);
        lblUavsOperacionais.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JLabel lblEntregCSucessoas = new JLabel("Entreg. c/ sucesso");
        lblEntregCSucessoas.setForeground(Color.WHITE);
        lblEntregCSucessoas.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JLabel lblEntregFalhadas = new JLabel("Entreg. falhadas");
        lblEntregFalhadas.setForeground(Color.WHITE);
        lblEntregFalhadas.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JLabel lblUavsEmFalha = new JLabel("UAVs em falha");
        lblUavsEmFalha.setForeground(Color.WHITE);
        lblUavsEmFalha.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        uavsOpText = new JTextField();
        uavsOpText.setEditable(false);
        uavsOpText.setColumns(10);
        
        uavsFalhaText = new JTextField();
        uavsFalhaText.setEditable(false);
        uavsFalhaText.setColumns(10);
        
        entregasSucessoText = new JTextField();
        entregasSucessoText.setEditable(false);
        entregasSucessoText.setColumns(10);
        
        entregasFalhaText = new JTextField();
        entregasFalhaText.setEditable(false);
        entregasFalhaText.setColumns(10);
        
        JProgressBar progressBarFalhas = new JProgressBar();
        progressBarFalhas.setValue(25);
        progressBarFalhas.setToolTipText("");
        progressBarFalhas.setStringPainted(true);
        progressBarFalhas.setForeground(Color.RED);
        progressBarFalhas.setFont(new Font("Monospaced", Font.BOLD, 15));
        progressBarFalhas.setBackground(Color.GREEN);
        
        JLabel lblFalhas = new JLabel("Falhas");
        lblFalhas.setHorizontalAlignment(SwingConstants.CENTER);
        lblFalhas.setForeground(Color.WHITE);
        lblFalhas.setFont(new Font("Monospaced", Font.PLAIN, 20));
        GroupLayout gl_panelFalhas = new GroupLayout(panelFalhas);
        gl_panelFalhas.setHorizontalGroup(
            gl_panelFalhas.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelFalhas.createSequentialGroup()
                    .addGap(24)
                    .addGroup(gl_panelFalhas.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelFalhas.createParallelGroup(Alignment.LEADING, false)
                            .addComponent(lblEntregCSucessoas, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEntregFalhadas))
                        .addComponent(lblUavsEmFalha, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblUavsOperacionais, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_panelFalhas.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelFalhas.createParallelGroup(Alignment.TRAILING)
                            .addComponent(uavsOpText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                            .addComponent(uavsFalhaText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                            .addComponent(entregasSucessoText, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
                        .addComponent(entregasFalhaText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
                    .addGap(29))
                .addGroup(gl_panelFalhas.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(progressBarFalhas, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(20, Short.MAX_VALUE))
                .addGroup(gl_panelFalhas.createSequentialGroup()
                    .addComponent(lblFalhas, GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                    .addContainerGap())
        );
        gl_panelFalhas.setVerticalGroup(
            gl_panelFalhas.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelFalhas.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblFalhas, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(progressBarFalhas, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelFalhas.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUavsOperacionais, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(uavsOpText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(gl_panelFalhas.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUavsEmFalha, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(uavsFalhaText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(gl_panelFalhas.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblEntregCSucessoas, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(entregasFalhaText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(gl_panelFalhas.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblEntregFalhadas, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(entregasSucessoText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(25))
        );
        panelFalhas.setLayout(gl_panelFalhas);
        
        JPanel panel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(lblPainelInfo, GroupLayout.DEFAULT_SIZE, 1075, Short.MAX_VALUE)
                            .addGap(12)
                            .addComponent(drone2uLogo))
                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                            .addComponent(panel, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(panelEstadoUavs, GroupLayout.PREFERRED_SIZE, 771, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(panelFalhas, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(panelCondMeteo, GroupLayout.PREFERRED_SIZE, 292, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(panelEncomendas, GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(drone2uLogo)
                        .addComponent(lblPainelInfo, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(panelEstadoUavs, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(panelCondMeteo, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(panelFalhas, GroupLayout.PREFERRED_SIZE, 204, GroupLayout.PREFERRED_SIZE))
                        .addComponent(panelEncomendas, GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE))
                    .addContainerGap(13, Short.MAX_VALUE))
        );
        panel.setLayout(new CardLayout(0, 0));
        
        JPanel panelOcupacao = new JPanel();
        panelOcupacao.setBackground(Color.GRAY);
        panel.add(panelOcupacao, "panel1");
        
        JLabel lblUavsLivres = new JLabel("UAVs livres");
        lblUavsLivres.setForeground(Color.WHITE);
        lblUavsLivres.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JLabel label_1 = new JLabel("Entregas em curso");
        label_1.setForeground(Color.WHITE);
        label_1.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JLabel lblEntregasPendentes = new JLabel("Entregas pendentes");
        lblEntregasPendentes.setForeground(Color.WHITE);
        lblEntregasPendentes.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JLabel lblUavsOcupados = new JLabel("UAVs ocupados");
        lblUavsOcupados.setForeground(Color.WHITE);
        lblUavsOcupados.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        uavsLivresText = new JTextField();
        uavsLivresText.setEditable(false);
        uavsLivresText.setColumns(10);
        uavsLivresText.setBackground(Color.WHITE);
        
        uavsOcupadosText = new JTextField();
        uavsOcupadosText.setEditable(false);
        uavsOcupadosText.setColumns(10);
        
        entregasPendentesText = new JTextField();
        entregasPendentesText.setEditable(false);
        entregasPendentesText.setColumns(10);
        
        entregasCursoText = new JTextField();
        entregasCursoText.setEditable(false);
        entregasCursoText.setColumns(10);
        
        progressBarOcupacao = new JProgressBar();
        progressBarOcupacao.setValue(100);
        progressBarOcupacao.setToolTipText("");
        progressBarOcupacao.setStringPainted(true);
        progressBarOcupacao.setForeground(Color.RED);
        progressBarOcupacao.setFont(new Font("Monospaced", Font.BOLD, 15));
        progressBarOcupacao.setBackground(Color.GREEN);
        
        JLabel lblOcupacao = new JLabel("Ocupação");
        lblOcupacao.setHorizontalAlignment(SwingConstants.CENTER);
        lblOcupacao.setForeground(Color.WHITE);
        lblOcupacao.setFont(new Font("Monospaced", Font.PLAIN, 20));
        
        JButton button = new JButton("Plot");

        GroupLayout gl_panelOcupacao = new GroupLayout(panelOcupacao);
        gl_panelOcupacao.setHorizontalGroup(
            gl_panelOcupacao.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelOcupacao.createSequentialGroup()
                    .addGroup(gl_panelOcupacao.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelOcupacao.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(progressBarOcupacao, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE))
                        .addGroup(gl_panelOcupacao.createSequentialGroup()
                            .addGap(70)
                            .addGroup(gl_panelOcupacao.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblUavsLivres, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_panelOcupacao.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(label_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblEntregasPendentes))
                                .addComponent(lblUavsOcupados, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addGroup(gl_panelOcupacao.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panelOcupacao.createParallelGroup(Alignment.TRAILING)
                                    .addComponent(uavsLivresText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(uavsOcupadosText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(entregasPendentesText, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
                                .addComponent(entregasCursoText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap())
                .addGroup(Alignment.TRAILING, gl_panelOcupacao.createSequentialGroup()
                    .addContainerGap(98, Short.MAX_VALUE)
                    .addComponent(lblOcupacao, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(button)
                    .addGap(97))
        );
        gl_panelOcupacao.setVerticalGroup(
            gl_panelOcupacao.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelOcupacao.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelOcupacao.createParallelGroup(Alignment.BASELINE)
                        .addComponent(button)
                        .addComponent(lblOcupacao, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(progressBarOcupacao, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelOcupacao.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUavsLivres, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(uavsLivresText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(gl_panelOcupacao.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUavsOcupados, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(uavsOcupadosText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(gl_panelOcupacao.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label_1, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(entregasCursoText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(gl_panelOcupacao.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblEntregasPendentes, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(entregasPendentesText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(26, Short.MAX_VALUE))
        );
        panelOcupacao.setLayout(gl_panelOcupacao);
        
        JPanel panel_2 = new JPanel();
        panel_2.setBackground(Color.GRAY);
        panel.add(panel_2, "panel2");
        
        JLabel label = new JLabel("Ocupação");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.PLAIN, 20));
        
        JButton button_1 = new JButton("Plot");
        
        JPanel chartp = new JPanel();
        GroupLayout gl_panel_2 = new GroupLayout(panel_2);
        gl_panel_2.setHorizontalGroup(
            gl_panel_2.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panel_2.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_2.createSequentialGroup()
                            .addComponent(label, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                            .addComponent(button_1, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
                            .addGap(43))
                        .addGroup(gl_panel_2.createSequentialGroup()
                            .addComponent(chartp, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                            .addContainerGap())))
        );
        gl_panel_2.setVerticalGroup(
            gl_panel_2.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_2.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
                        .addComponent(label, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_1))
                    .addGap(7)
                    .addComponent(chartp, GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .addContainerGap())
        );
        panel_2.setLayout(gl_panel_2);
        
        JLabel lblCondiesMet = new JLabel("Condições meteo.");
        lblCondiesMet.setHorizontalAlignment(SwingConstants.CENTER);
        lblCondiesMet.setForeground(Color.WHITE);
        lblCondiesMet.setFont(new Font("Monospaced", Font.PLAIN, 20));
        
        JLabel lblTemperatura = new JLabel("Temperatura");
        lblTemperatura.setForeground(Color.WHITE);
        lblTemperatura.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        tempText = new JTextField();
        tempText.setEditable(false);
        tempText.setColumns(10);
        
        humidadeText = new JTextField();
        humidadeText.setEditable(false);
        humidadeText.setColumns(10);
        
        JLabel lblHumidade = new JLabel("Humidade");
        lblHumidade.setForeground(Color.WHITE);
        lblHumidade.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JLabel lblVento = new JLabel("Vento");
        lblVento.setForeground(Color.WHITE);
        lblVento.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        ventoText = new JTextField();
        ventoText.setEditable(false);
        ventoText.setColumns(10);
        
        weatherDescriptionText = new JTextField();
        weatherDescriptionText.setHorizontalAlignment(SwingConstants.CENTER);
        weatherDescriptionText.setEditable(false);
        weatherDescriptionText.setColumns(10);
        GroupLayout gl_panelCondMeteo = new GroupLayout(panelCondMeteo);
        gl_panelCondMeteo.setHorizontalGroup(
            gl_panelCondMeteo.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelCondMeteo.createSequentialGroup()
                    .addGap(45)
                    .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_panelCondMeteo.createSequentialGroup()
                            .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblHumidade)
                                .addComponent(lblVento))
                            .addGap(42))
                        .addGroup(gl_panelCondMeteo.createSequentialGroup()
                            .addComponent(lblTemperatura)
                            .addPreferredGap(ComponentPlacement.UNRELATED)))
                    .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_panelCondMeteo.createSequentialGroup()
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(ventoText, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                        .addComponent(humidadeText, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                        .addComponent(tempText, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                    .addGap(55))
                .addGroup(gl_panelCondMeteo.createSequentialGroup()
                    .addGap(24)
                    .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(lblCondiesMet, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(weatherDescriptionText, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
                    .addContainerGap(25, Short.MAX_VALUE))
        );
        gl_panelCondMeteo.setVerticalGroup(
            gl_panelCondMeteo.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelCondMeteo.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblCondiesMet, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                    .addGap(1)
                    .addComponent(weatherDescriptionText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblTemperatura, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(tempText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblVento, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(ventoText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblHumidade, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(humidadeText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        panelCondMeteo.setLayout(gl_panelCondMeteo);
        
        JScrollPane scrollPaneEstadoUavs = new JScrollPane();
        
        JLabel lblEstadoUavs = new JLabel("Estado UAVs");
        lblEstadoUavs.setHorizontalAlignment(SwingConstants.LEFT);
        lblEstadoUavs.setForeground(Color.WHITE);
        lblEstadoUavs.setFont(new Font("Monospaced", Font.PLAIN, 20));
        GroupLayout gl_panelEstadoUavs = new GroupLayout(panelEstadoUavs);
        gl_panelEstadoUavs.setHorizontalGroup(
            gl_panelEstadoUavs.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelEstadoUavs.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelEstadoUavs.createParallelGroup(Alignment.LEADING)
                        .addComponent(scrollPaneEstadoUavs, GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
                        .addComponent(lblEstadoUavs, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        gl_panelEstadoUavs.setVerticalGroup(
            gl_panelEstadoUavs.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelEstadoUavs.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblEstadoUavs, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPaneEstadoUavs, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        
        tableEstadoUavs = new JTable();
        tableEstadoUavs.setModel(new DefaultTableModel(
            new Object[][] {
            },
            new String[] {
                    "ID UAV", "Localização atual", "Altitude", "Plano atual"
            }
        ));
        tableEstadoUavs.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableEstadoUavs.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableEstadoUavs.getColumnModel().getColumn(2).setPreferredWidth(60);
        tableEstadoUavs.getColumnModel().getColumn(3).setPreferredWidth(100);
        scrollPaneEstadoUavs.setViewportView(tableEstadoUavs);
        panelEstadoUavs.setLayout(gl_panelEstadoUavs);
        
        JScrollPane scrollPaneEncomendas = new JScrollPane();
        
        
        //String [] uavsNames = {"x"}
        comboBoxFiltroUav = new JComboBox<>();        
        comboBoxFiltroUav.setForeground(Color.BLACK);
        comboBoxFiltroUav.setBackground(Color.WHITE);
        
        
        
        // chamada da função para conetar à base de dados
        if(!database.isConnected()) {
            Connection conn = database.connect();
            database.setSchema();       
        }
        
        ArrayList<String> names_db = database.getUavsNames();
        names_db.add(0, "All");
        String[] names = Arrays.copyOf(names_db.toArray(), names_db.toArray().length, String[].class);
        comboBoxFiltroUav.setModel(new DefaultComboBoxModel<String>(names));
        
        JLabel lblEncomendas = new JLabel("Encomendas");
        lblEncomendas.setHorizontalAlignment(SwingConstants.LEFT);
        lblEncomendas.setForeground(Color.WHITE);
        lblEncomendas.setFont(new Font("Monospaced", Font.PLAIN, 20));
        
        JLabel lblFiltro = new JLabel("Filtro (UAV):");
        lblFiltro.setHorizontalAlignment(SwingConstants.LEFT);
        lblFiltro.setForeground(Color.WHITE);
        lblFiltro.setFont(new Font("Monospaced", Font.PLAIN, 15));
        GroupLayout gl_panelEncomendas = new GroupLayout(panelEncomendas);
        gl_panelEncomendas.setHorizontalGroup(
            gl_panelEncomendas.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelEncomendas.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelEncomendas.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelEncomendas.createSequentialGroup()
                            .addComponent(lblEncomendas, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED, 359, Short.MAX_VALUE)
                            .addComponent(lblFiltro)
                            .addGap(3)
                            .addComponent(comboBoxFiltroUav, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE))
                        .addComponent(scrollPaneEncomendas, GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE))
                    .addContainerGap())
        );
        gl_panelEncomendas.setVerticalGroup(
            gl_panelEncomendas.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelEncomendas.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelEncomendas.createParallelGroup(Alignment.TRAILING)
                        .addGroup(gl_panelEncomendas.createParallelGroup(Alignment.BASELINE)
                            .addComponent(lblEncomendas, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboBoxFiltroUav, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(lblFiltro, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPaneEncomendas, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                    .addContainerGap())
        );
        
        tableEncomendas = new JTable();
        tableEncomendas.setRowSelectionAllowed(false);
        tableEncomendas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tableEncomendas.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                    "ID UAV", "ID Encom.", "Localização inicial", "Localização final", "Data/hora envio", "Data/hora entrega"
                }
            ));
        tableEncomendas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableEncomendas.getColumnModel().getColumn(1).setPreferredWidth(40);
        tableEncomendas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tableEncomendas.getColumnModel().getColumn(3).setPreferredWidth(150);
        tableEncomendas.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableEncomendas.getColumnModel().getColumn(5).setPreferredWidth(100);
        scrollPaneEncomendas.setViewportView(tableEncomendas);
        panelEncomendas.setLayout(gl_panelEncomendas);
        setLayout(groupLayout);
        
        series = new TimeSeries("Random Data", Millisecond.class);
        final TimeSeriesCollection dataset = new TimeSeriesCollection(series);        
       
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            null, 
            null, 
            null,
            dataset, 
            false, 
            false, 
            false
        );
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 100.0);      
        
        ChartPanel chartPanel = new ChartPanel( result );
        chartPanel.setPreferredSize( new java.awt.Dimension( 200 , 200 ) ); 
        
        
        
        
        chartp.setLayout(new java.awt.BorderLayout());
         
        chartp.add(chartPanel,BorderLayout.CENTER);
        chartp.validate();
        
        
        
        
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) panel.getLayout();
                cardLayout.show(panel, "panel2");
                System.out.println("oiiiiiiiii");
                
                
            }
        });
    
    
        comboBoxFiltroUav.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
               
            }
        });
    
    }
    
    /**
     * Atualiza a tabela de encomendas
     */
   
    public void refreshTableEncomendas () {
        // chamada da função para conetar à base de dados
        if(!database.isConnected()) {
            Connection conn = database.connect();
            database.setSchema();       
        }
        
        Object oselected = comboBoxFiltroUav.getSelectedItem();
        String filter = oselected.toString();
        ArrayList<ArrayList<String>> table_db;
        
        if(filter.equals("All"))
            table_db = database.getEncomendas();
        else
            table_db = database.getEncomendas(filter);
        
        DefaultTableModel tabelaEncomendas = (DefaultTableModel) tableEncomendas.getModel();
        Object rowDataEncomendas[] = new Object[6];
                       
     
        //apaga todos os valores da tabela 
        int tam = tabelaEncomendas.getRowCount();
        for(int i = 0; i < tam; i++) {
            tabelaEncomendas.removeRow(tabelaEncomendas.getRowCount()-1);            
            tabelaEncomendas.fireTableDataChanged();            
        }        
        //adiciona os valores atualizados a tabela
        for(int i = 0; i < table_db.size(); i++) {
            
                       
            rowDataEncomendas[0] = table_db.get(i).get(0);
            rowDataEncomendas[1] = table_db.get(i).get(1);
            
            if(table_db.get(i).get(2) != null)            
                    rowDataEncomendas[2] = table_db.get(i).get(2) + " ("+table_db.get(i).get(3) + ")";
            else
                rowDataEncomendas[2] = table_db.get(i).get(4) + " ("+table_db.get(i).get(5) + ")";                                      

            rowDataEncomendas[3] = table_db.get(i).get(6) + " ("+table_db.get(i).get(7) + ")";
            rowDataEncomendas[4] = table_db.get(i).get(8);    
            rowDataEncomendas[5] = table_db.get(i).get(9);                  
                        
            tabelaEncomendas.insertRow(i, rowDataEncomendas);
            tabelaEncomendas.fireTableDataChanged();
        }
    }
    
    /**
     * Atualiza a tabela de estados dos uavs
     */
    public void refreshTableEstadoUavs () {

        ImcSystem vehicles_list[] = ImcSystemsHolder.lookupActiveSystemVehicles();
        
        DefaultTableModel tabelaEstadoUavs = (DefaultTableModel) tableEstadoUavs.getModel();
        Object rowDataUavs[] = new Object[4];        
        //apaga todos os valores da tabela 
        int tam = tabelaEstadoUavs.getRowCount();
        for(int i = 0; i < tam; i++) {
            tabelaEstadoUavs.removeRow(tabelaEstadoUavs.getRowCount()-1);            
            tabelaEstadoUavs.fireTableDataChanged();            
        }        
        //adiciona os valores atualizados a tabela
        for(int i = 0; i < vehicles_list.length; i++) {      
            rowDataUavs[0] = vehicles_list[i].getName();
            rowDataUavs[1] = vehicles_list[i].getLocation().getLatitudeAsPrettyString()+" "+vehicles_list[i].getLocation().getLongitudeAsPrettyString();
            rowDataUavs[2] = vehicles_list[i].getLocation().getHeight() + " m"; 
            
            if(vehicles_list[i].getActivePlan() != null)
                rowDataUavs[3] = vehicles_list[i].getActivePlan().toString();
            else
                rowDataUavs[3] = " - ";
            
            tabelaEstadoUavs.insertRow(i, rowDataUavs);
            tabelaEstadoUavs.fireTableDataChanged();
        }
    }
    
    /**
     * Atualizar os componentes da GUI relacionados com a meteorologia 
     */
    public void refreshWeather() {
               
        Weather data = new Weather();
        Vector<Map<String, Object>> content = new Vector<>();

        content = data.getWeatherData();

        String[] weather_description = content.get(2).get("weather").toString().split(",");
        weather_description = weather_description[2].split("=");

        double temperature = Double.parseDouble(content.get(0).get("temp").toString());
        double wind_velocity = Double.parseDouble(content.get(1).get("speed").toString())*3.6;

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        
               
        ventoText.setText(df.format(wind_velocity)+" km/h");
        humidadeText.setText(df.format(content.get(0).get("humidity"))+" %");
        tempText.setText(df.format(temperature)+ " C");
        weatherDescriptionText.setText(weather_description[1]);
    }
    
    
    /**
     * Atualizar os restantes componentes da GUI (barras de utilização e textos)
     */
    public void refreshOther () {        
        // chamada da função para conetar à base de dados
        if(!database.isConnected()) {
            Connection conn = database.connect();
            database.setSchema();       
        }
        
        ImcSystem vehicles_list[] = ImcSystemsHolder.lookupActiveSystemVehicles();
        float inactive_vehicles = 0;
        float active_vehicles = 0;
        
        inactive_vehicles = database.getFreeUavs();
        active_vehicles = database.getBusyUavs();
        
                
        uavsLivresText.setText(String.valueOf(vehicles_list.length));
        
        for(int i = 0; i < vehicles_list.length; i++) {
            if(vehicles_list[i].getActivePlan() == null)
                inactive_vehicles++;
            else
                active_vehicles++;
        }       
      //  uavsLivresText.setText(String.valueOf((int)inactive_vehicles));
      //  uavsOcupadosText.setText(String.valueOf((int)active_vehicles));
        
        if(active_vehicles+inactive_vehicles == 0)
            progressBarOcupacao.setValue(100);
        else
            progressBarOcupacao.setValue((int)(active_vehicles/(active_vehicles+inactive_vehicles)*100));
        
        series.add(new Millisecond(), (active_vehicles/(active_vehicles+inactive_vehicles)*100));
    }
    
    
    JTextField getUavsLivresText() {
        return uavsLivresText;
    }
    JTextField getEntregasCursoText() {
        return entregasCursoText;
    }
    JTextField getUavsOcupadosText() {
        return uavsOcupadosText;
    }
    JTextField getEntregasPendentesText() {
        return entregasPendentesText;
    }
    JTextField getuavsOpText() {
        return uavsLivresText;
    }
    JTextField getUavsOpText() {
        return uavsOpText;        
    }
    JTextField geyUavsFalhaText(){
        return uavsFalhaText;
    }
    JTextField getEntregasSucessoText(){
        return entregasSucessoText;
    }
    JTextField getEntregasFalhaText(){
        return entregasFalhaText;
    }
    JTextField getTempText(){
        return tempText;
    }
    JTextField getHumidadeText(){
        return humidadeText;
    }
    JTextField getVentoText(){
        return ventoText;
    }
    JProgressBar getProgressBarOcupacao() {
        return progressBarOcupacao;        
    }
    JTable getTableEncomendas() {
        return tableEncomendas;        
    }
    JTable getTableEstadoUavs() {
        return tableEstadoUavs;
    }
}