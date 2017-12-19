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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

import com.amazonaws.services.s3.model.RequestPaymentConfiguration.Payer;
import com.google.common.eventbus.Subscribe;

import pt.lsts.imc.DesiredPath;
import pt.lsts.imc.EntityParameter;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.SetEntityParameters;
import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.comm.IMCSendMessageUtils;
import pt.lsts.neptus.comm.manager.imc.ImcSystem;
import pt.lsts.neptus.comm.manager.imc.ImcSystemsHolder;
import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.console.ConsolePanel;
import pt.lsts.neptus.console.ConsoleSystem;
import pt.lsts.neptus.console.events.ConsoleEventMainSystemChange;
import pt.lsts.neptus.console.events.ConsoleEventNewSystem;
import pt.lsts.neptus.console.events.ConsoleEventSystemAuthorityStateChanged;
import pt.lsts.neptus.console.events.ConsoleEventVehicleStateChanged;
import pt.lsts.neptus.mp.Maneuver;
import pt.lsts.neptus.mp.ManeuverLocation;
import pt.lsts.neptus.mp.maneuvers.Goto;
import pt.lsts.neptus.mp.maneuvers.Loiter;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.plugins.Popup;
import pt.lsts.neptus.plugins.Popup.POSITION;
import pt.lsts.neptus.plugins.update.Periodic;
import pt.lsts.neptus.types.coord.LocationType;
import pt.lsts.neptus.types.mission.MissionType;
import pt.lsts.neptus.types.mission.TransitionType;
import pt.lsts.neptus.types.mission.plan.PlanType;
import pt.lsts.neptus.util.ImageUtils;

/**
 * @author joao mesquita e pedro guedes
 *
 */
@PluginDescription(name = "Drone2U")
@Popup(pos = POSITION.CENTER, width = 442, height = 291, accelerator = 'Y')
@SuppressWarnings("serial")
public class Drone2uConsole extends ConsolePanel{

    private final String defaultCondition = "ManeuverIsDone";
    SQL_functions database = new SQL_functions();
    Weather data = new Weather();
    Vector<String> UAV_map = new Vector<String>();   // na primeira posição contém o nome do UAV que está disponível no sistema


    //private UavsState stateUavsPanel = new UavsState();
    private PainelInfo painelInfoPanel = new PainelInfo();
    //private JFrame stateUavsFrame;
    private JFrame painelInfoFrame;
    int last_order_id = 100;


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

        for(LocationType dest : destArray) {

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
        //System.out.println(neptusPlan.asIMCPlan().asXml(true));


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

    public PlanControl buildPlan_loiter (String vehicleID, MissionType mt, LocationType loc, double speed, double height, double radius) {

        PlanType neptusPlan = new PlanType(mt);
        neptusPlan.addVehicle(vehicleID);

        loc.convertToAbsoluteLatLonDepth();

        ManeuverLocation maneuverLoc = new ManeuverLocation(loc);
        maneuverLoc.setZ(height);
        maneuverLoc.setZUnits(ManeuverLocation.Z_UNITS.HEIGHT);

        Loiter point = new Loiter();
        point.setManeuverLocation(maneuverLoc);
        point.setSpeed(speed);
        point.setRadius(radius);
        point.setLoiterDuration(0);
        point.setDirection("Clockwise");
        point.setLoiterType("Circular");
        point.setLength(1.0);
        point.setBearing(0.0);

        neptusPlan.getGraph().addManeuver(point);


        PlanControl pc = new PlanControl();
        pc.setType(PlanControl.TYPE.REQUEST);
        pc.setOp(PlanControl.OP.START);
        pc.setRequestId(IMCSendMessageUtils.getNextRequestId());    // IMCSendMessageUtils.... é apenas para fins de sincronização
        pc.setPlanId(neptusPlan.getId());
        //System.out.println(neptusPlan.asIMCPlan().asXml(true));


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


    /**
     * Inicializa a GUI do plugin.
     */
    @Override
    public void initSubPanel() {
        removeAll();

        setBackground(Color.GRAY);

        JLabel drone2uLogo = new JLabel("");
        drone2uLogo.setBackground(Color.GRAY);
        ImageIcon drone2u = ImageUtils.getScaledIcon("images/drone2u.png", 157, 117);
        drone2uLogo.setIcon(drone2u);
        drone2uLogo.setOpaque(true);

        JButton testButton = new JButton("Envio rota (teste)");
        testButton.setForeground(Color.WHITE);
        testButton.setFont(new Font("DejaVu Sans", Font.PLAIN, 20));
        testButton.setBackground(Color.BLACK);

        JButton estadoUavsButton = new JButton("Estado UAVs...");
        estadoUavsButton.setForeground(Color.WHITE);
        estadoUavsButton.setBackground(Color.BLACK);
        estadoUavsButton.setFont(new Font("DejaVu Sans", Font.PLAIN, 20));

        JLabel titlePlugin = new JLabel("Neptus plugin");
        titlePlugin.setForeground(Color.WHITE);
        titlePlugin.setFont(new Font("DejaVu Sans", Font.PLAIN, 25));

        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                        .addGap(35)
                        .addComponent(drone2uLogo)
                        .addGap(18)
                        .addComponent(titlePlugin)
                        .addGap(87))
                .addGroup(groupLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                                .addComponent(testButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(estadoUavsButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))
                        .addContainerGap(41, Short.MAX_VALUE))
                );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(29)
                                        .addComponent(drone2uLogo))
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGap(69)
                                        .addComponent(titlePlugin)))
                        .addGap(37)
                        .addComponent(estadoUavsButton, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(testButton, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(16, Short.MAX_VALUE))
                );
        setLayout(groupLayout);




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

                //CHAMADA DA FUNÇAO PARA TESTE

                TesteRota();

                // teste para ver se vai buscar direito à base de dados
                //database.getPoints();

                //PlanControl pc = buildPlan("x8-02", getConsole().getMission(), destArray, 20, 200);

                //System.out.println(sendPlanToVehicle("x8-02", getConsole(), pc));

                //check_new_points();
            }
        });

        //se o botão estado uavs for clicado abre um JFrame com uma tabela que indica o estado dos UAVs
        estadoUavsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /*stateUavsFrame = new JFrame();
                stateUavsFrame.add(stateUavsPanel); //adiciona JPanel que contem a tabela ao JFrame criado
                stateUavsFrame.setSize(850, 505);
                stateUavsFrame.setVisible(true);*/
                painelInfoFrame = new JFrame();
                painelInfoFrame.add(painelInfoPanel);
                painelInfoFrame.setSize(1063, 671);
                painelInfoFrame.setVisible(true);
                painelInfoFrame.setResizable(false);


            }
        });
    }

    /*@Subscribe
    public void on(DesiredPath msg) {
        if (msg.getSourceName().equals(getConsole().getMainSystem())) {
            tSpeed = msg.getAsNumber("speed").doubleValue();
            speedLabelUpdate();
        }
    }*/


    @Periodic(millisBetweenUpdates=1000) // a cada 1 segundo é chamada a função
    public void update_gui() {
        painelInfoPanel.refreshTableEstadoUavs();
        painelInfoPanel.refreshTableEncomendas(); // atualiza GUI tabela encomendas
        painelInfoPanel.refreshOther();
    }

    /**
     * Função que verifica se alguma encomenda é colocada na
     * base de dados
     */
    //@Periodic(millisBetweenUpdates=1000*10) // a cada 10segundos é chamada a função
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

    /**
     * Função que verifica as condições meteorológicas
     */
    @Periodic(millisBetweenUpdates=1000*60) // a cada 60segundos é chamada a função
    public void check_weather() {

        Vector<Map<String, Object>> content = new Vector<>();

        content = data.getWeatherData();

        String[] weather_description = content.get(2).get("weather").toString().split(",");
        weather_description = weather_description[2].split("=");

        double temperature = Double.parseDouble(content.get(0).get("temp").toString());
        double wind_velocity = Double.parseDouble(content.get(1).get("speed").toString())*3.6;

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);


        System.out.println("Matosinhos:");
        System.out.println("    Temperatura: "+  temperature+" graus");
        System.out.println("    Humidade: "+ content.get(0).get("humidity"));
        System.out.println("    Velo. Vento: "+df.format(wind_velocity)+"Km/h");
        System.out.println("    Descrição: "+weather_description[1]);

        painelInfoPanel.refreshWeather(); // atualiza as condições meteorológicas na GUI

    }

    /**
     *
     * @param order_id
     * @return path
     */
    public LocationType[]  getPath(int order_id) {

        String[] latitudes;
        String[] longitudes;
        String[] path_fromBD = new String[2];
        int size;

        path_fromBD = database.getPathForOrder(order_id);

        latitudes = path_fromBD[1].split(";");
        longitudes = path_fromBD[0].split(";");

        size = latitudes.length;

        //        for(int i=0; i<size; i++) {
        //            System.out.println("latidute"+i+": "+latitudes[i]);
        //            System.out.println("longitude"+i+": "+longitudes[i]);
        //        }


        LocationType[] path = new LocationType[size];

        for(int i=0; i<size; i++) {
            path[i] = new LocationType();

            path[i].setLatitudeStr(latitudes[i]);;
            path[i].setLongitudeStr(longitudes[i]);
        }


        //        for(int i=0; i<size; i++) {
        //            System.out.println(path[i]);
        //        }


        // path contém a trajetória a fazer pelo drone
        return path;
    }

    /**
     * Função que verifica qual a manobra que um drone está a executar
     */
    @Periodic(millisBetweenUpdates=1000*5) // a cada 60segundos é chamada a função
    public void check_maneuver() {
        String raw_maneuver = null;
        String uav_name = "x8-02";
        String[] maneuver_aux;
        String maneuver = "Vehicle unavailable";

        ImcSystem vehicles_list[] = ImcSystemsHolder.lookupActiveSystemVehicles();

        for(int i = 0; i < vehicles_list.length; i++) {      
            if (vehicles_list[i].getName().equals(uav_name)) {

                if(vehicles_list[i].getActivePlan() == null) {
                    maneuver = "No maneuvers yet";
                }
                else {
                    raw_maneuver = vehicles_list[i].getActivePlan().toString(); // vem no formato: pl_btf1ym|Man:GOTO1 (nome_plano|man: nome_manobra)
                    maneuver_aux = raw_maneuver.split(":");

                    maneuver = maneuver_aux[1];

                    System.out.println("manobra: "+maneuver);
                }

            }
        }

        //return maneuver;
    }
    
    @Periodic(millisBetweenUpdates=1000*5) // a cada 5segundos é chamada a função
    public void LoiterNewVehicle() {
        ImcSystem vehicles_list[] = ImcSystemsHolder.lookupActiveSystemVehicles();
        
        for(int i=0; i<vehicles_list.length; i++) {
            if( UAV_map.indexOf(vehicles_list[i].getName()) == -1) {
                System.out.println("Ainda nao contém o UAV: "+vehicles_list[i].getName());
                UAV_map.add(vehicles_list[i].getName());
                
                // coloca em loiter num armazém
                
                // chamada da função para conetar à base de dados
                if(!database.isConnected()) {
                    Connection conn = database.connect();
                    database.setSchema();
                }
                
                LocationType armazem_loc = new LocationType();
                
                armazem_loc = database.getWarehouseLoc();
                
                PlanControl pc = buildPlan_loiter(vehicles_list[i].getName(), getConsole().getMission(),armazem_loc, 10, 700, 25.0);

                System.out.println(sendPlanToVehicle(vehicles_list[i].getName(), getConsole(), pc));
            }
        }
    }
    
    /**
     * Função de teste
     */
    public void TesteRota() {

        LocationType[] path;

        path = getPath(112);

        PlanControl pc = buildPlan("x8-02", getConsole().getMission(),path, 20, 700);
        
        //PlanControl pc = buildPlan_loiter("x8-02", getConsole().getMission(),path[0], 10, 700, 50.0);

        System.out.println(sendPlanToVehicle("x8-02", getConsole(), pc));

        //database.OrderStateUpdate(19, "enviada");
        // tenho de atualizar a disponibilidade do drone na BD

    }

}
