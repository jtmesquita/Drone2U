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

import pt.lsts.neptus.util.ImageUtils;

import java.awt.event.ActionListener;
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

public class PainelInfo extends JPanel {
    private JTextField uavsLivresText;
    private JTextField entregasCursoText;
    private JTextField uavsOcupadosText;
    private JTextField entregasPendentesText;
    private JTable tableEncomendas;
    private JTable tableEstadoUavs;
    private JTextField uavsOpText;
    private JTextField uavsFalhaText;
    private JTextField entregasSucessoText;
    private JTextField entregasFalhaText;
    private JTextField tempText;
    private JTextField humidadeText;
    private JTextField ventoText;

    /**
     * Create the panel.
     */
    public PainelInfo() {
        setForeground(Color.RED);
        setBackground(new Color(102, 102, 102));
        
        JLabel drone2uLogo = new JLabel("");
        drone2uLogo.setFont(new Font("DejaVu Sans", Font.BOLD, 20));
        drone2uLogo.setBackground(new Color(102, 102, 102));
        drone2uLogo.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon drone2u = ImageUtils.getScaledIcon("images/drone2u.png", 94, 70);
        drone2uLogo.setIcon(drone2u);
        drone2uLogo.setOpaque(true);
          
        JLabel lblPainelInfo = new JLabel("Painel Informativo");
        lblPainelInfo.setForeground(Color.WHITE);
        lblPainelInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblPainelInfo.setBackground(Color.DARK_GRAY);
        lblPainelInfo.setOpaque(true);
        lblPainelInfo.setFont(new Font("Monospaced", Font.BOLD, 25));
        
        JPanel panelTaxaOcupacao = new JPanel();
        panelTaxaOcupacao.setBackground(Color.GRAY);
        
        JLabel label = new JLabel("Taxa de ocupação");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.PLAIN, 20));
        
        JProgressBar progressBarOcupacao = new JProgressBar();
        progressBarOcupacao.setValue(90);
        progressBarOcupacao.setToolTipText("");
        progressBarOcupacao.setStringPainted(true);
        progressBarOcupacao.setForeground(Color.RED);
        progressBarOcupacao.setFont(new Font("Monospaced", Font.BOLD, 15));
        progressBarOcupacao.setBackground(Color.GREEN);
        
        JLabel lblUavsLivres = new JLabel("UAVs livres");
        lblUavsLivres.setForeground(Color.WHITE);
        lblUavsLivres.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        uavsLivresText = new JTextField();
        uavsLivresText.setEditable(false);
        uavsLivresText.setColumns(10);
        
        JLabel lblUavsOcupados = new JLabel("UAVs ocupados");
        lblUavsOcupados.setForeground(Color.WHITE);
        lblUavsOcupados.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        entregasCursoText = new JTextField();
        entregasCursoText.setEditable(false);
        entregasCursoText.setColumns(10);
        
        JLabel lblEntregasCurso = new JLabel("Entregas em curso");
        lblEntregasCurso.setForeground(Color.WHITE);
        lblEntregasCurso.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        uavsOcupadosText = new JTextField();
        uavsOcupadosText.setEditable(false);
        uavsOcupadosText.setColumns(10);
        
        JLabel lblEntregasPendentes = new JLabel("Entregas pendentes");
        lblEntregasPendentes.setForeground(Color.WHITE);
        lblEntregasPendentes.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        entregasPendentesText = new JTextField();
        entregasPendentesText.setEditable(false);
        entregasPendentesText.setColumns(10);
        GroupLayout gl_panelTaxaOcupacao = new GroupLayout(panelTaxaOcupacao);
        gl_panelTaxaOcupacao.setHorizontalGroup(
            gl_panelTaxaOcupacao.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelTaxaOcupacao.createSequentialGroup()
                    .addGap(24)
                    .addGroup(gl_panelTaxaOcupacao.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblUavsLivres, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                        .addGroup(gl_panelTaxaOcupacao.createParallelGroup(Alignment.LEADING, false)
                            .addComponent(lblEntregasCurso, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEntregasPendentes))
                        .addComponent(lblUavsOcupados, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_panelTaxaOcupacao.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelTaxaOcupacao.createParallelGroup(Alignment.TRAILING)
                            .addComponent(uavsLivresText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                            .addComponent(uavsOcupadosText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                            .addComponent(entregasPendentesText, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
                        .addComponent(entregasCursoText, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
                    .addGap(29))
                .addGroup(gl_panelTaxaOcupacao.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(progressBarOcupacao, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(20, Short.MAX_VALUE))
                .addComponent(label, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
        );
        gl_panelTaxaOcupacao.setVerticalGroup(
            gl_panelTaxaOcupacao.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panelTaxaOcupacao.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(progressBarOcupacao, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panelTaxaOcupacao.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUavsLivres, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(uavsLivresText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(gl_panelTaxaOcupacao.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblUavsOcupados, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(uavsOcupadosText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(gl_panelTaxaOcupacao.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblEntregasCurso, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(entregasCursoText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(gl_panelTaxaOcupacao.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblEntregasPendentes, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                        .addComponent(entregasPendentesText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(25))
        );
        panelTaxaOcupacao.setLayout(gl_panelTaxaOcupacao);
        
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
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(lblPainelInfo, GroupLayout.DEFAULT_SIZE, 925, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(drone2uLogo))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                                .addComponent(panelFalhas, 0, 0, Short.MAX_VALUE)
                                .addComponent(panelCondMeteo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(panelTaxaOcupacao, GroupLayout.PREFERRED_SIZE, 285, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(panelEstadoUavs, GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                                .addComponent(panelEncomendas, GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(28)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(drone2uLogo)
                        .addComponent(lblPainelInfo, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
                    .addGap(18)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(panelEstadoUavs, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelTaxaOcupacao, GroupLayout.PREFERRED_SIZE, 204, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(panelCondMeteo, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(panelFalhas, GroupLayout.PREFERRED_SIZE, 204, GroupLayout.PREFERRED_SIZE))
                        .addComponent(panelEncomendas, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap(22, Short.MAX_VALUE))
        );
        
        JLabel lblCondiesMet = new JLabel("Condições meteo.");
        lblCondiesMet.setHorizontalAlignment(SwingConstants.CENTER);
        lblCondiesMet.setForeground(Color.WHITE);
        lblCondiesMet.setFont(new Font("Monospaced", Font.PLAIN, 20));
        
        JLabel lblTemperatura = new JLabel("Temp.");
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
        GroupLayout gl_panelCondMeteo = new GroupLayout(panelCondMeteo);
        gl_panelCondMeteo.setHorizontalGroup(
            gl_panelCondMeteo.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelCondMeteo.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelCondMeteo.createSequentialGroup()
                            .addComponent(lblCondiesMet, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                            .addContainerGap())
                        .addGroup(gl_panelCondMeteo.createSequentialGroup()
                            .addGap(12)
                            .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_panelCondMeteo.createSequentialGroup()
                                    .addComponent(lblTemperatura, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                                    .addGap(24)
                                    .addComponent(tempText, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                                .addGroup(gl_panelCondMeteo.createSequentialGroup()
                                    .addComponent(lblHumidade)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(humidadeText, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                            .addComponent(lblVento, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                            .addGap(3)
                            .addComponent(ventoText, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                            .addGap(24))))
        );
        gl_panelCondMeteo.setVerticalGroup(
            gl_panelCondMeteo.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panelCondMeteo.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblCondiesMet, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                    .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panelCondMeteo.createSequentialGroup()
                            .addGap(9)
                            .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblTemperatura, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                .addComponent(tempText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblHumidade, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                .addComponent(humidadeText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(gl_panelCondMeteo.createSequentialGroup()
                            .addGap(18)
                            .addGroup(gl_panelCondMeteo.createParallelGroup(Alignment.BASELINE)
                                .addComponent(ventoText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblVento, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))))
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
        
        JComboBox comboBoxFiltroUav = new JComboBox();
        comboBoxFiltroUav.setForeground(Color.BLACK);
        comboBoxFiltroUav.setBackground(Color.WHITE);
        
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
                    .addGroup(gl_panelEncomendas.createParallelGroup(Alignment.TRAILING)
                        .addComponent(scrollPaneEncomendas, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
                        .addGroup(gl_panelEncomendas.createSequentialGroup()
                            .addComponent(lblEncomendas, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED, 351, Short.MAX_VALUE)
                            .addComponent(lblFiltro)
                            .addGap(3)
                            .addComponent(comboBoxFiltroUav, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)))
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
        

    }
}