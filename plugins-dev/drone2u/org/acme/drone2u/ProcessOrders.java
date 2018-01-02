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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import com.google.common.eventbus.Subscribe;
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
import java.util.Date;

/**
 * @author João Mesquita e Pedro Guedes
 *
 */
@PluginDescription(name = "Drone2U")
@Popup(pos = POSITION.CENTER, width = 287, height = 225, accelerator = 'Y')
@SuppressWarnings("serial")
public class ProcessOrders extends ConsolePanel {

    private final String defaultCondition = "ManeuverIsDone";
    Database db = new Database();
    Weather data = new Weather();
    Vector<String> UAV_map = new Vector<String>(); // na primeira posição contém o nome do UAV que está disponível no sistema                                                

    private GUI guiPanel = new GUI();
    private JFrame guiFrame;

    int vel = 40;
    int height = 700;
    int last_order_id = 256;

    public ProcessOrders(ConsoleLayout console) {
        super(console);        
    }

    @Override
    public void cleanSubPanel() {
        // TODO Auto-generated method stub
    }

    /**
     * Inicializa a GUI do plugin.
     */
    @Override
    public void initSubPanel() {
        removeAll();

        setBackground(Color.GRAY);

        JLabel drone2uLogo = new JLabel("");
        drone2uLogo.setFont(new Font("DejaVu Sans", Font.BOLD, 20));
        drone2uLogo.setBackground(Color.GRAY);
        drone2uLogo.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon drone2u = ImageUtils.getScaledIcon("images/drone2u.png", 157, 117);
        drone2uLogo.setIcon(drone2u);
        drone2uLogo.setOpaque(true);

        JLabel lblPlugin = new JLabel("Plugin");
        lblPlugin.setOpaque(true);
        lblPlugin.setHorizontalAlignment(SwingConstants.CENTER);
        lblPlugin.setForeground(Color.BLACK);
        lblPlugin.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblPlugin.setBackground(Color.GRAY);

        JButton btnOpen = new JButton("Open GUI...");

        // Cria o JFrame para a GUI quando o botão é pressionado 
        btnOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                guiFrame = new JFrame();
                guiFrame.add(guiPanel);
                guiFrame.setSize(1120, 695);
                guiFrame.setVisible(true);
                guiFrame.setResizable(true);            

            }
        });


        btnOpen.setForeground(Color.WHITE);
        btnOpen.setBackground(Color.DARK_GRAY);
        btnOpen.setFont(new Font("Monospaced", Font.BOLD, 20));

        JLabel lblSeai = new JLabel("Equipa C - SEAI (2017/2018) - FEUP");
        lblSeai.setOpaque(true);
        lblSeai.setHorizontalAlignment(SwingConstants.CENTER);
        lblSeai.setForeground(Color.BLACK);
        lblSeai.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblSeai.setBackground(Color.GRAY);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                .addComponent(btnOpen, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                                                .addComponent(lblSeai, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE))
                                        .addContainerGap())
                                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                                        .addComponent(drone2uLogo)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(lblPlugin, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                                        .addGap(28))))
                );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                .addComponent(drone2uLogo)
                                .addComponent(lblPlugin, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
                        .addGap(18)
                        .addComponent(btnOpen, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(lblSeai, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
        setLayout(groupLayout);
    }

    @Periodic(millisBetweenUpdates = 1000) // a cada 1 segundo é chamada a função
    public void updateGui() {
        guiPanel.refreshTableEstadoUavs();
        guiPanel.refreshTableEncomendas(); // atualiza GUI tabela encomendas
        guiPanel.refreshOther();
        guiPanel.refreshWeather(); // atualiza as condições meteorológicas na GUI
    }

    public boolean sendPlanToVehicle(String vehicleID, ConsoleLayout cl, PlanControl pspec) {
        return cl.getImcMsgManager().sendMessageToVehicle(pspec, vehicleID, null);
    }

    private String getNewManeuverName(PlanType plan, String manType) {

        int i = 1;
        while (plan.getGraph().getManeuver(manType + i) != null)
            i++;

        return manType + i;
    }

    public PlanControl buildPlan(String vehicleID, int OrderId, MissionType mt, LocationType[] destArray, double speed,
            double height) {

        PlanType neptusPlan = new PlanType(mt);
        neptusPlan.addVehicle(vehicleID);

        if (OrderId != -1) {
            neptusPlan.setId("Order:" + OrderId);
        }
        else
            neptusPlan.setId("Return2WH");

        int aux = 0;

        for (LocationType dest : destArray) {

            if (aux == 0) { // se for a primeira manobra adiciona a primeira manobra
                dest.convertToAbsoluteLatLonDepth();

                ManeuverLocation maneuverLoc = new ManeuverLocation(dest);
                maneuverLoc.setZ(height);
                maneuverLoc.setZUnits(ManeuverLocation.Z_UNITS.HEIGHT);

                Goto point = new Goto();
                point.setManeuverLocation(maneuverLoc);
                point.setSpeed(speed);
                point.setId(getNewManeuverName(neptusPlan, "GOTO"));

                neptusPlan.getGraph().addManeuver(point);

                aux++;
            }

            else {

                ManeuverLocation maneuverLoc = new ManeuverLocation(dest);
                maneuverLoc.setZ(height);
                maneuverLoc.setZUnits(ManeuverLocation.Z_UNITS.HEIGHT);

                // Maneuver nextMan = neptusPlan.getGraph().getFollowingManeuver(manID);
                Maneuver lastMan = neptusPlan.getGraph().getLastManeuver();

                Goto point = new Goto();

                try {
                    point.setProperties(lastMan.getProperties()); // coloco no novo ponto as propriedades do anterior
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

                /*
                 * verifico se o novo ponto criado não é nulo if (point == null) { GuiUtils.errorMessage(this,
                 * I18n.text("Error adding maneuver"), I18n.textf("The maneuver %maneuverType can't be added",
                 * point.getType())); return null; }
                 */

                /*
                 * verifico se a ultima manobra não é nula e se o novo ponto não é a manobra que já foi adicionada se
                 * não for adiciono as transições
                 */
                if (lastMan != null && lastMan != point) {

                    if (neptusPlan.getGraph().getExitingTransitions(lastMan).size() != 0) {
                        for (TransitionType exitingTrans : neptusPlan.getGraph().getExitingTransitions(lastMan)) {
                            removedTransitions.add(neptusPlan.getGraph().removeTransition(
                                    exitingTrans.getSourceManeuver(), exitingTrans.getTargetManeuver()));
                        }
                    }

                    addedTransitions
                    .add(neptusPlan.getGraph().addTransition(lastMan.getId(), point.getId(), defaultCondition));
                }

                neptusPlan.getGraph().addManeuver(point);
            }
        }

        PlanControl pc = new PlanControl();
        pc.setType(PlanControl.TYPE.REQUEST);
        pc.setOp(PlanControl.OP.START);
        pc.setRequestId(IMCSendMessageUtils.getNextRequestId()); // IMCSendMessageUtils.... é apenas para fins de
        // sincronização
        pc.setPlanId(neptusPlan.getId());
        // System.out.println(neptusPlan.asIMCPlan().asXml(true));

        PlanSpecification pSpec = new PlanSpecification(neptusPlan.asIMCPlan()); // criação da mensagem IMC para ser
        // mandada para p drone

        SetEntityParameters heightc = new SetEntityParameters();
        heightc.setName("Height Control");
        EntityParameter activeP = new EntityParameter("Active", "true");
        heightc.setParams(Arrays.asList(new EntityParameter[] { activeP }));

        SetEntityParameters autopilot = new SetEntityParameters();
        autopilot.setName("Autopilot");
        autopilot.setParams(Arrays.asList(new EntityParameter[] { new EntityParameter("Ardupilot Tracker", "false"),
                new EntityParameter("Formation Flight", "false") }));

        SetEntityParameters pathController = new SetEntityParameters();
        pathController.setName("Path Control");
        pathController
        .setParams(Arrays.asList(new EntityParameter[] { new EntityParameter("Use controller", "true") }));

        // alteração da configuração do drone de modo a desligar a opção Ardupilot
        // e ativação das opções Height control e PathControl
        try {
            pSpec.setStartActions(Arrays.asList(new SetEntityParameters[] { heightc, autopilot, pathController }));
        }

        catch (Exception e1) {
            e1.printStackTrace();
        }

        pc.setArg(pSpec); // envio do plano para o drone

        return pc;
    }

    public PlanControl buildPlanLoiter(String vehicleID, MissionType mt, LocationType loc, double speed, double height,
            double radius) {

        PlanType neptusPlan = new PlanType(mt);
        neptusPlan.addVehicle(vehicleID);

        neptusPlan.setId("Loiter");

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
        point.setId("Loiter");

        neptusPlan.getGraph().addManeuver(point);

        PlanControl pc = new PlanControl();
        pc.setType(PlanControl.TYPE.REQUEST);
        pc.setOp(PlanControl.OP.START);
        pc.setRequestId(IMCSendMessageUtils.getNextRequestId()); // IMCSendMessageUtils.... é apenas para fins de
        // sincronização
        pc.setPlanId(neptusPlan.getId());
        // System.out.println(neptusPlan.asIMCPlan().asXml(true));

        PlanSpecification pSpec = new PlanSpecification(neptusPlan.asIMCPlan()); // criação da mensagem IMC para ser
        // mandada para p drone

        SetEntityParameters heightc = new SetEntityParameters();
        heightc.setName("Height Control");
        EntityParameter activeP = new EntityParameter("Active", "true");
        heightc.setParams(Arrays.asList(new EntityParameter[] { activeP }));

        SetEntityParameters autopilot = new SetEntityParameters();
        autopilot.setName("Autopilot");
        autopilot.setParams(Arrays.asList(new EntityParameter[] { new EntityParameter("Ardupilot Tracker", "false"),
                new EntityParameter("Formation Flight", "false") }));

        SetEntityParameters pathController = new SetEntityParameters();
        pathController.setName("Path Control");
        pathController
        .setParams(Arrays.asList(new EntityParameter[] { new EntityParameter("Use controller", "true") }));

        // alteração da configuração do drone de modo a desligar a opção Ardupilot
        // e ativação das opções Height control e PathControl
        try {
            pSpec.setStartActions(Arrays.asList(new SetEntityParameters[] { heightc, autopilot, pathController }));
        }

        catch (Exception e1) {
            e1.printStackTrace();
        }

        pc.setArg(pSpec); // envio do plano para o drone

        return pc;
    }

    /*
     * @Subscribe public void on(DesiredPath msg) { if (msg.getSourceName().equals(getConsole().getMainSystem())) {
     * tSpeed = msg.getAsNumber("speed").doubleValue(); speedLabelUpdate(); } }
     */

    /**
     * Função que verifica se alguma encomenda é colocada na base de dados
     */
    // @Periodic(millisBetweenUpdates=1000*10) // a cada 10segundos é chamada a função
    public void checkNewPoints() {

        // chamada da função para conetar à base de dados
        if (!db.isConnected()) {
            Connection conn = db.connect();
            db.setSchema();
        }

        // se detetar uma nova encomenda no site vai ter de lidar com as que ainda não foram resolvidas
        int new_order_id = db.getIdLastOrder();

        if (new_order_id > last_order_id) {
            System.out.println("Nova encomenda na bd. ID= " + db.getIdLastOrder());

            // last_order_id = database.getId_last_order();

            Vector<Integer> list_ids;

            list_ids = db.getOrderIds(last_order_id);

            // Simula o armazém de origem porque na base de dados ainda nao tem as coordenadas
            LocationType warehouse = new LocationType();
            warehouse.setLongitudeStr("6W42'34.78''");
            warehouse.setLatitudeStr("41N51'27.53''");

            for (Integer id : list_ids) {
                System.out.println(id);

                // vou ter de ler o local de recolha e o local de entrega
                LocationType final_location;

                final_location = db.getLocationById(id);

                LocationType[] destArray = new LocationType[2];

                destArray[0] = warehouse;
                destArray[1] = final_location;



                PlanControl pc = buildPlan("x8-02", id, getConsole().getMission(), destArray, vel, height);

                System.out.println(sendPlanToVehicle("x8-02", getConsole(), pc));
            }

            last_order_id = new_order_id;
        }
    }

    /**
     * Função que verifica as condições meteorológicas
     * 
     * @return weather_info[temperature, wind_veloc., weather_id]
     */
    // @Periodic(millisBetweenUpdates=1000*60) // a cada 60segundos é chamada a função
    public double[] checkWeather() {

        Vector<Map<String, Object>> content = new Vector<>();
        double[] weather_info = new double[3];

        content = data.getWeatherData();

        String[] weather_description = content.get(2).get("weather").toString().split(",");
        double weather_id;

        String[] aux;
        aux = weather_description[0].split("=");
        weather_id = Double.parseDouble(aux[1]);

        weather_description = weather_description[2].split("=");

        double temperature = Double.parseDouble(content.get(0).get("temp").toString());
        double wind_velocity = Double.parseDouble(content.get(1).get("speed").toString()) * 3.6;

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        // System.out.println("Matosinhos:");
        // System.out.println(" Temperatura: "+ temperature+" graus");
        // System.out.println(" Humidade: "+ content.get(0).get("humidity"));
        // System.out.println(" Velo. Vento: "+df.format(wind_velocity)+"Km/h");
        // System.out.println(" Descrição: "+weather_description[1]);

        weather_info[0] = temperature;
        weather_info[1] = wind_velocity;
        weather_info[2] = weather_id;

        return weather_info;
    }

    /**
     *
     * @param order_id
     * @return path
     */
    public LocationType[] getPath(int order_id, String way) {

        String[] latitudes;
        String[] longitudes;
        String[] path_fromBD = new String[2];
        int size;

        if (way.equals("entrega")) {
            path_fromBD = db.getPathForOrder(order_id);
        }
        else if (way.equals("regresso")) {
            path_fromBD = db.getPathToWareHouse(order_id);
        }

        latitudes = path_fromBD[1].split(";");
        longitudes = path_fromBD[0].split(";");

        if (!latitudes[0].equals("-")) {
            size = latitudes.length;

            // for(int i=0; i<size; i++) {
            // System.out.println("latidute"+i+": "+latitudes[i]);
            // System.out.println("longitude"+i+": "+longitudes[i]);
            // }

            LocationType[] path = new LocationType[size];

            for (int i = 0; i < size; i++) {
                path[i] = new LocationType();

                path[i].setLatitudeStr(latitudes[i]);
                ;
                path[i].setLongitudeStr(longitudes[i]);
            }

            // path contém a trajetória a fazer pelo drone
            return path;
        }
        else {
            System.out.println("é para fazer loiter ao armazem - nao volta para tras");
            path_fromBD = db.getPathForOrder(order_id);

            latitudes = path_fromBD[1].split(";");
            longitudes = path_fromBD[0].split(";");

            size = latitudes.length;

            LocationType[] path = new LocationType[1];

            path[0] = new LocationType();

            path[0].setLatitudeStr(latitudes[size - 1]);
            ;
            path[0].setLongitudeStr(longitudes[size - 1]);

            return path;
        }

    }

    /**
     * Função que verifica qual a manobra que um drone está a executar e atualiza a disponibilidade de cada drone
     */
    @Periodic(millisBetweenUpdates = 1000 * 5) // a cada 60segundos é chamada a função
    public void checkManeuver() {
        String raw_maneuver = null;
        String[] maneuver_aux;
        String maneuver = "Vehicle unavailable";

        ImcSystem vehicles_list[] = ImcSystemsHolder.lookupActiveSystemVehicles();

        // chamada da função para conetar à base de dados
        if (!db.isConnected()) {
            db.connect();
            db.setSchema();
        }       

        for (int i = 0; i < vehicles_list.length; i++) {
            if (vehicles_list[i].getActivePlan() == null) {
                maneuver = "No maneuvers";
            }
            else {
                raw_maneuver = vehicles_list[i].getActivePlan().toString(); // vem no formato: pl_btf1ym|Man:GOTO1
                // (nome_plano|man: nome_manobra)
                maneuver_aux = raw_maneuver.split(":");

                maneuver = maneuver_aux[1];
            }

            //System.out.println("Manobra: " + maneuver);
            // Verificar se a manobra é Loiter para verififar disponibilidade do drone
            if (maneuver.equals("Loiter")) {
                db.uavStateUpdate(vehicles_list[i].getName(), "TRUE");
            }
            else
                db.uavStateUpdate(vehicles_list[i].getName(), "FALSE");

        }

        // System.out.println("Maneuver: "+maneuver);
        // return maneuver;
    }

    /**
     * Função que deteta a transição entre o estado de ececução de uma manobra (nosso caso entrega) e o estado livre.
     * Atualiza a hora e data de uma entregua assim como atualiza o estado da ecomenda para entregue
     * 
     * @param event
     */
    @Subscribe
    public void on(ConsoleEventVehicleStateChanged event) {
        if (event.getState().toString().equals("SERVICE")) {
            // ou seja deve ter terminado uma manobra ou entrou em serviço pela primeira vez
            // só faz algo se estiver na lista UAV_Map
            if (UAV_map.indexOf(event.getVehicle().toString()) != -1) {

                int OrderId = db.getLastOrderIdDrone(event.getVehicle().toString());

                // vai guardar a hora de finalização de encomenda
                // para isso vai buscar qual a última encomenda que o drone fez (na base de dados na tabela entrega)

                if (db.getOrderState(OrderId).equals("enviada")) {
                    // verificar o estado da encomenda - se estiver "enviada" significa que acabou de entregar
                    // então vai atualizar a hora de fim e vai ter de enviar o drone para o armazém mais próximo

                    // vou então atualizar as horas de entrega
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    String[] data_fim;

                    data_fim = dateFormat.format(date).split(" ");

                    // atualiza hora e data de entrega
                    db.updateDateDelivered(OrderId, data_fim);

                    // altera o estado para entregue

                    db.orderStateUpdate(OrderId, "entregue");

                    // vou ter de ir buscar o percurso para o drone ir para o armazém e enviar esse plano para o drone
                    LocationType[] path;
                    path = getPath(OrderId, "regresso");

                    // significa que fica a fazer loiter logo no armazém
                    if(path.length == 1) {
                        // atualiza na base de dados a nova localização do drone
                        int UAV_id = db.getDroneId(event.getVehicle().toString());
                        db.insertUavLocation(UAV_id, path[0]);

                        height = db.getUavHeight(event.getVehicle().toString());
                        PlanControl pc = buildPlanLoiter(event.getVehicle().toString(), getConsole().getMission(),
                                path[path.length - 1], 10, height, 25.0);

                        System.out.println(sendPlanToVehicle(event.getVehicle().toString(), getConsole(), pc));
                    }
                    else {
                        // Envio o caminho para o drone
                        height = db.getUavHeight(event.getVehicle().toString());
                        PlanControl pc = buildPlan(event.getVehicle().toString(), -1, getConsole().getMission(), path, vel,
                                height);
                        System.out.println(sendPlanToVehicle(event.getVehicle().toString(), getConsole(), pc));
                    }


                }
                else if (db.getOrderState(OrderId).equals("entregue")) {
                    // caso o estado da ecomenda já estaja "entregue" então o drone vai fazer loiter ao armazém
                    // para saber qual o armazém a qual fazer loiter vou ter de ir à rota de regresso do drone
                    // à última posição e retirar de lá o localização do armazém e manda fazer loiter
                    LocationType[] path;
                    LocationType whareHouse = new LocationType();

                    path = getPath(OrderId, "regresso");

                    whareHouse = path[path.length - 1];

                    height = db.getUavHeight(event.getVehicle().toString());
                    PlanControl pc = buildPlanLoiter(event.getVehicle().toString(), getConsole().getMission(),
                            whareHouse, 10, height, 25.0);

                    System.out.println(sendPlanToVehicle(event.getVehicle().toString(), getConsole(), pc));

                    // atualiza na base de dados a nova localização do drone
                    int UAV_id = db.getDroneId(event.getVehicle().toString());
                    db.insertUavLocation(UAV_id, whareHouse);
                }
            }

        }
    }

    //@Periodic(millisBetweenUpdates = 1000 * 5) // a cada 5segundos é chamada a função
    @Subscribe
    public void checkVehicles(ConsoleEventVehicleStateChanged event) {
        ImcSystem vehicles_list[] = ImcSystemsHolder.lookupActiveSystemVehicles();

        // significa que algum UAV deixou de estar em serviço
        if (vehicles_list.length < UAV_map.size()) {
            Vector<String> aux = new Vector<>();

            for (int i = 0; i < vehicles_list.length; i++) {
                aux.add(vehicles_list[i].getName());
            }

            Vector<String> toRemove = new Vector<String>();

            for (String uav_name : UAV_map) {
                if (aux.indexOf(uav_name) == -1) {
                    // significa que deixou de estar em serviço
                    db.uavStateUpdate(uav_name, "FALSE");
                    toRemove.add(uav_name);
                }
            }

            // remove do map de UAVs

            for(String uav_name: toRemove) {
                UAV_map.remove(UAV_map.indexOf(uav_name));
                System.out.println("UAV "+uav_name +" removido");
            }
        }

        // faz loiter a um novo veículo
        for (int i = 0; i < vehicles_list.length; i++) {
            if (UAV_map.indexOf(vehicles_list[i].getName()) == -1) {
                System.out.println("Ainda nao contém o UAV: " + vehicles_list[i].getName());
                UAV_map.add(vehicles_list[i].getName());

                // coloca em loiter num armazém

                // chamada da função para conetar à base de dados
                if (!db.isConnected()) {
                    db.connect();
                    db.setSchema();
                }

                LocationType armazem_loc = new LocationType();

                armazem_loc = db.getWarehouseLoc();

                height = db.getUavHeight(vehicles_list[i].getName());
                System.out.println("Altura: "+height);

                PlanControl pc = buildPlanLoiter(vehicles_list[i].getName(), getConsole().getMission(), armazem_loc,
                        10, height, 25.0);

                System.out.println(sendPlanToVehicle(vehicles_list[i].getName(), getConsole(), pc));

                // atualiza localização na base de dados
                int UAV_id = db.getDroneId(vehicles_list[i].getName().toString());
                db.insertUavLocation(UAV_id, armazem_loc);

            }
        }
    }

    /**
     * Função que verifica se alguma encomenda está pronta para ser enviada
     */
    @Periodic(millisBetweenUpdates = 1000 * 10) // a cada 10segundos é chamada a função
    public void checkNewOrders() {

        // chamada da função para conetar à base de dados
        if (!db.isConnected()) {
            db.connect();
            db.setSchema();
        }

        // antes de enviar o drone verificar condições meteorológicas

        double[] weather_info = checkWeather();

        /*
         * Temperatura entre -10graus e 50graus
         * 
         * Após pesquisa ventos acima de 15mph -> 24Kpm não é aconselhavel voar
         * 
         * Voar em tempos de chuva, ou seja, grupo 5xx, não é bom para voar pois está a chover
         * 
         */

        // if(weather_info[0]<50 && weather_info[0]>-10 && weather_info[1] < 24 && (int)(weather_info[2]/100) != 5) {
        
        Vector<Integer> Orders;

        Orders = db.getNewOrders();

        for (Integer id : Orders) {
            System.out.println("Condições atmosféricas dentro dos limites");
            System.out.println(id);

            // ver o caminho que a entrega tem de fazer
            String drone;
            LocationType[] path;

            drone = db.getDroneForOrder(id);

            /*
             * Antes de enviar o drone verififar mesmo a disponibilidade dele
             *
             */
            if (db.getUavAvailability(db.getDroneId(drone))) {
                System.out.println("drone disponivel");
                path = getPath(id, "entrega");

                height = db.getUavHeight(drone);

                PlanControl pc = buildPlan(drone, id, getConsole().getMission(), path, vel, height);

                System.out.println(sendPlanToVehicle(drone, getConsole(), pc));

                // tenho de inserir na tabela entrega

                int DroneId = db.getDroneId(drone);

                db.insertDelivery(id, DroneId, "TRUE");

                // atualiza data e hora de envio

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                String[] data_envio;

                data_envio = dateFormat.format(date).split(" ");

                db.updateDateSend(id, data_envio);

                // atualiza o estado da encomenda
                db.orderStateUpdate(id, "enviada");
            }
            else
                System.out.println(drone + " não está disponivel");

        }
        // }
        // else
        // System.out.println("Condições atomosféricas adversas");

    }

    /**
     * Função de teste
     */
    public void TesteRota() {

        checkNewOrders();

        /*
         * LocationType[] path;
         * 
         * path = getPath(251);
         * 
         * PlanControl pc = buildPlan("x8-02", getConsole().getMission(),path, 20, 700);
         * 
         * System.out.println(sendPlanToVehicle("x8-02", getConsole(), pc));
         * 
         * database.OrderStateUpdate(151, "enviada");
         */

    }

    // @Subscribe
    // public void on2(ConsoleEventVehicleStateChanged event) {
    // System.out.println(event.getState().toString());
    // }
}
