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

import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;

import com.google.common.eventbus.Subscribe;

import pt.lsts.imc.EntityParameter;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanDB;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.SetEntityParameters;
import pt.lsts.neptus.comm.IMCSendMessageUtils;
import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.console.ConsolePanel;
import pt.lsts.neptus.console.events.ConsoleEventMissionChanged;
import pt.lsts.neptus.console.events.ConsoleEventNewSystem;
import pt.lsts.neptus.console.events.ConsoleEventPlanChange;
import pt.lsts.neptus.console.plugins.PlanChangeListener;
import pt.lsts.neptus.i18n.I18n;
import pt.lsts.neptus.mp.ManeuverLocation;
import pt.lsts.neptus.mp.MissionChangeEvent;
import pt.lsts.neptus.mp.maneuvers.Goto;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.plugins.Popup;
import pt.lsts.neptus.plugins.Popup.POSITION;
import pt.lsts.neptus.types.coord.LocationType;
import pt.lsts.neptus.types.mission.MissionType;
import pt.lsts.neptus.types.mission.plan.PlanType;

/**
 * @author joao
 *
 */


@PluginDescription(name = "Drone2U")
@Popup(pos = POSITION.CENTER, width = 200, height = 200, accelerator = 'Y')
@SuppressWarnings("serial")
public class Drone2uConsole extends ConsolePanel{

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
    
   

    
    
    public PlanControl buildPlan (String vehicleID, MissionType mt, LocationType [] destArray, double speed, double height) {   
        
        PlanType neptusPlan = new PlanType(mt);
        neptusPlan.addVehicle(vehicleID); 
        
        for(LocationType dest : destArray) {      //para futura implementação de multiplos pontos (para já destArray tem apenas uma posiçao)    
            dest.convertToAbsoluteLatLonDepth();           
            
            ManeuverLocation maneuverLoc = new ManeuverLocation(dest);
            maneuverLoc.setZ(height);
            maneuverLoc.setZUnits(ManeuverLocation.Z_UNITS.HEIGHT); 
            
            Goto point = new Goto();
            point.setManeuverLocation(maneuverLoc);           
            point.setSpeed(speed);     
            
            neptusPlan.getGraph().addManeuver(point);     
                
            //neptusPlan.getGraph().addManeuverAtEnd(point);                
            //neptusPlan.getGraph().addTransition(smid, maneuverLoc.getId(), true);
            System.out.println(neptusPlan.getGraph().getExitingTransitions(neptusPlan.getGraph().getManeuver(maneuverLoc.getId())));            
                
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

        Action newPointAction = new AbstractAction(I18n.text("New Goto point...")) {
            @Override
            public void actionPerformed(ActionEvent e) {                
                LocationType[] destArray = new LocationType[1];
                
                destArray[0] = new LocationType();
                destArray[0].setLongitudeStr("6W42'31.15''");
                destArray[0].setLatitudeStr("41N51'27.24''");
                
                /*destArray[1] = new LocationType();
                destArray[1].setLongitudeStr("6W43'31.15''");
                destArray[1].setLatitudeStr("41N51'27.24''");*/             
                
                
                PlanControl pc = buildPlan("x8-02", getConsole().getMission(),
                        destArray, 20, 200);
                
                System.out.println(sendPlanToVehicle("x8-02", getConsole(), pc));               
            }
        };

        JButton newPointButton = new JButton(newPointAction);        
        add(newPointButton);

    }
    
    // esta função permite ver quando um sistema entra no neptus
    // para se quisermos guardar novos sistemas que se conectam
    @Subscribe
    public void on(ConsoleEventNewSystem event) {
            System.out.println(event.getSystem().getVehicleId());
    }

}
