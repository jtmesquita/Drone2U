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
 * Author: pedro
 * 21/11/2017
 */
package org.acme.drone2u;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.google.common.eventbus.Subscribe;
import com.google.protobuf.DescriptorProtos.SourceCodeInfo.Location;

import pt.lsts.imc.EntityParameter;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanDB;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.SetEntityParameters;
import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.comm.IMCSendMessageUtils;
import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.console.ConsolePanel;
import pt.lsts.neptus.console.events.ConsoleEventMissionChanged;
import pt.lsts.neptus.console.events.ConsoleEventNewSystem;
import pt.lsts.neptus.console.events.ConsoleEventPlanChange;
import pt.lsts.neptus.console.plugins.PlanChangeListener;
import pt.lsts.neptus.i18n.I18n;
import pt.lsts.neptus.mp.Maneuver;
import pt.lsts.neptus.mp.ManeuverLocation;
import pt.lsts.neptus.mp.MissionChangeEvent;
import pt.lsts.neptus.mp.maneuvers.Goto;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.plugins.Popup;
import pt.lsts.neptus.plugins.Popup.POSITION;
import pt.lsts.neptus.plugins.update.Periodic;
import pt.lsts.neptus.types.coord.LocationType;
import pt.lsts.neptus.types.mission.MissionType;
import pt.lsts.neptus.types.mission.TransitionType;
import pt.lsts.neptus.types.mission.plan.PlanType;
import pt.lsts.neptus.util.GuiUtils;

/**
 * @author joao
 *
 */


@PluginDescription(name = "Drone2U")
@Popup(pos = POSITION.CENTER, width = 585, height = 360, accelerator = 'Y')
@SuppressWarnings("serial")
public class Drone2uConsole extends ConsolePanel{

    private final String defaultCondition = "ManeuverIsDone";
    SQL_functions database = new SQL_functions();

    private JTable table;
    int last_order_id = 41;


    /**
     * @param console
     */
    public Drone2uConsole(ConsoleLayout console) {
        super(console);
    }


    @Override
    public void cleanSubPanel() {
        // TODO Auto-generated method stub
    }

    public boolean sendPlanToVehicle (String vehicleID, ConsoleLayout cl, PlanControl pspec) {
        return cl.getImcMsgManager().sendMessageToVehicle(pspec, vehicleID, null);
    }

    private String getNewManeuverName(PlanType plan, String manType) {

        int i = 1;
        while (plan.getGraph().getManeuver(manType + i) != null)
            i++;

        return manType + i;
    }

    public PlanControl buildPlan (String vehicleID, MissionType mt, LocationType [] destArray, double speed, double height) {

        PlanType neptusPlan = new PlanType(mt);
        neptusPlan.addVehicle(vehicleID);

        int aux = 0;

        for(LocationType dest : destArray) {      //para futura implementação de multiplos pontos (para já destArray tem apenas uma posiçao)

            if (aux == 0) {             //se for a primeira manobra adiciona a primeira manobra
                dest.convertToAbsoluteLatLonDepth();

                ManeuverLocation maneuverLoc = new ManeuverLocation(dest);
                maneuverLoc.setZ(height);
                maneuverLoc.setZUnits(ManeuverLocation.Z_UNITS.HEIGHT);

                Goto point = new Goto();
                point.setManeuverLocation(maneuverLoc);
                point.setSpeed(speed);

                neptusPlan.getGraph().addManeuver(point);

                aux++;
            }

            else {

                ManeuverLocation maneuverLoc = new ManeuverLocation(dest);
                maneuverLoc.setZ(height);
                maneuverLoc.setZUnits(ManeuverLocation.Z_UNITS.HEIGHT);

                //Maneuver nextMan = neptusPlan.getGraph().getFollowingManeuver(manID);
                Maneuver lastMan = neptusPlan.getGraph().getLastManeuver();

                Goto point = new Goto();

                try {
                    point.setProperties(lastMan.getProperties());     // coloco no novo ponto as propriedades do anterior
                }
                catch (Exception e) {
                    NeptusLog.pub().error(e, e);
                }
                point.cloneActions(lastMan);

                point.setId(getNewManeuverName(neptusPlan, "GOTO"));

                point.setManeuverLocation(maneuverLoc);

                neptusPlan.getGraph().addManeuver(point);

                Vector<TransitionType> addedTransitions = new Vector<TransitionType>();
                Vector<TransitionType> removedTransitions = new Vector<TransitionType>();

                /*verifico se o novo ponto criado não é nulo
                if (point == null) {
                    GuiUtils.errorMessage(this, I18n.text("Error adding maneuver"),
                            I18n.textf("The maneuver %maneuverType can't be added", point.getType()));
                    return null;
                }*/

                /* verifico se a ultima manobra não é nula e se o novo ponto
                   não é a manobra que já foi adicionada
                   se não for adiciono as transições*/
                if (lastMan != null && lastMan != point) {

                    if (neptusPlan.getGraph().getExitingTransitions(lastMan).size() != 0) {
                        for (TransitionType exitingTrans : neptusPlan.getGraph().getExitingTransitions(lastMan)) {
                            removedTransitions.add(neptusPlan.getGraph().removeTransition(exitingTrans.getSourceManeuver(),
                                    exitingTrans.getTargetManeuver()));
                        }
                    }

                    addedTransitions.add(neptusPlan.getGraph().addTransition(lastMan.getId(), point.getId(), defaultCondition));
                }

                neptusPlan.getGraph().addManeuver(point);

            }

        }



        PlanControl pc = new PlanControl();
        pc.setType(PlanControl.TYPE.REQUEST);
        pc.setOp(PlanControl.OP.START);
        pc.setRequestId(IMCSendMessageUtils.getNextRequestId());    // IMCSendMessageUtils.... é apenas para fins de sincronização
        pc.setPlanId(neptusPlan.getId());
        System.out.println(neptusPlan.asIMCPlan().asXml(true));


        PlanSpecification pSpec = new PlanSpecification(neptusPlan.asIMCPlan());    // criação da mensagem IMC para ser mandada para p drone

        SetEntityParameters heightc = new SetEntityParameters();
        heightc.setName("Height Control");
        EntityParameter activeP = new EntityParameter("Active", "true");
        heightc.setParams(Arrays.asList(new EntityParameter[]{activeP}));

        SetEntityParameters autopilot = new SetEntityParameters();
        autopilot.setName("Autopilot");
        autopilot.setParams(Arrays.asList(new EntityParameter[]{
                new EntityParameter("Ardupilot Tracker", "false"),
                new EntityParameter("Formation Flight", "false")}));

        SetEntityParameters pathController = new SetEntityParameters();
        pathController.setName("Path Control");
        pathController.setParams(Arrays.asList(new EntityParameter[]{
                new EntityParameter("Use controller", "true")
        }));


        // alteração da configuração do drone de modo a desligar a opção Ardupilot
        // e ativação das opções Height control e PathControl
        try {
            pSpec.setStartActions(Arrays.asList(new SetEntityParameters[] {
                    heightc,
                    autopilot,
                    pathController
            }));
        }

        catch (Exception e1) {
            e1.printStackTrace();
        }

        pc.setArg(pSpec);     // envio do plano para o drone

        return pc;
    }


    @Override
    public void initSubPanel() {
        removeAll();


        JScrollPane scrollPane = new JScrollPane();

        JButton testButton = new JButton("Test button");
        testButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                LocationType[] destArray = new LocationType[4];

                destArray[0] = new LocationType();
                destArray[0].setLongitudeStr("6W42'34.78''");
                destArray[0].setLatitudeStr("41N51'27.53''");

                destArray[1] = new LocationType();
                destArray[1].setLongitudeStr("6W42'34.28''");
                destArray[1].setLatitudeStr("41N51'32.09''");

                destArray[2] = new LocationType();
                destArray[2].setLongitudeStr("6W42'23.93''");
                destArray[2].setLatitudeStr("41N51'31.21''");

                destArray[3] = new LocationType();
                destArray[3].setLongitudeStr("6W42'25.65''");
                destArray[3].setLatitudeStr("41N51'26.49''");

                // chamada da função para conetar à base de dados
                if(!database.isConnected()) {
                    Connection conn = database.connect();
                    database.setSchema();
                }

                // teste para ver se vai buscar direito à base de dados
                //database.getPoints();

                //PlanControl pc = buildPlan("x8-02", getConsole().getMission(), destArray, 20, 200);

                //System.out.println(sendPlanToVehicle("x8-02", getConsole(), pc));

                //check_new_points();
            }
        });
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                    .addContainerGap(56, Short.MAX_VALUE)
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(testButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE))
                    .addGap(52))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(27)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 227, GroupLayout.PREFERRED_SIZE)
                    .addGap(18)
                    .addComponent(testButton)
                    .addContainerGap(59, Short.MAX_VALUE))
        );

        table = new JTable();
        table.setModel(new DefaultTableModel(
            new Object[][] {
                {null, null, null},
                {null, null, null},
            },
            new String[] {
                    "ID Drone", "Localização atual", "Velocidade"
            }
        ));
        scrollPane.setViewportView(table);
        setLayout(groupLayout);

    }

    // esta função permite ver quando um sistema entra no neptus
    // para se quisermos guardar novos sistemas que se conectam
    @Subscribe
    public void on(ConsoleEventNewSystem event) {
        System.out.println(event.getSystem().getVehicleId());
    }

    /**
     * Função que verifica se alguma encomenda é colocada na
     * base de dados
     */
   @Periodic(millisBetweenUpdates=1000*10) // a cada 10segundos é chamada a função
    public void check_new_points() {

        // chamada da função para conetar à base de dados
       if(!database.isConnected()) {
            Connection conn = database.connect();
            database.setSchema();
        }

        // se detetar uma nova encomenda no site vai ter de lidar com as que ainda não foram resolvidas
        int new_order_id = database.getId_last_order();

        if( new_order_id> last_order_id) {
            System.out.println("Nova encomenda na bd. ID= "+database.getId_last_order());

            //last_order_id = database.getId_last_order();

            Vector<Integer> list_ids;

            list_ids = database.get_order_IDs(last_order_id);

            // Simula o armazém de origem porque na base de dados ainda nao tem as coordenadas
            LocationType warehouse = new LocationType();
            warehouse.setLongitudeStr("6W42'34.78''");
            warehouse.setLatitudeStr("41N51'27.53''");

            for(Integer id : list_ids) {
                System.out.println(id);

                //vou ter de ler o local de recolha e o local de entrega
                LocationType final_location;

                final_location = database.getLocation_byId(id);

                LocationType[] destArray = new LocationType[2];

                destArray[0] = warehouse;
                destArray[1] = final_location;

                PlanControl pc = buildPlan("x8-02", getConsole().getMission(),destArray, 20, 200);

                System.out.println(sendPlanToVehicle("x8-02", getConsole(), pc));
            }

            last_order_id = new_order_id;

        }
    }

}
