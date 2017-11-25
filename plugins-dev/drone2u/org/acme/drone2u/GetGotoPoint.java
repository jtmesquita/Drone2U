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
 * 15/11/2017
 */
package org.acme.drone2u;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.neptus.comm.IMCSendMessageUtils;
import pt.lsts.neptus.gui.PropertiesProvider;
import pt.lsts.neptus.mp.ManeuverLocation;
import pt.lsts.neptus.mp.Maneuver.SPEED_UNITS;
import pt.lsts.neptus.mp.maneuvers.Goto;
import pt.lsts.neptus.mp.templates.PlanCreator;
import pt.lsts.neptus.plugins.NeptusProperty;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.plugins.PluginUtils;
import pt.lsts.neptus.types.coord.LocationType;
import pt.lsts.neptus.types.mission.MissionType;
import pt.lsts.neptus.types.mission.plan.PlanType;


/**
 * @author joao
 *
 */
@PluginDescription(name = "Define point")
public class GetGotoPoint implements PropertiesProvider {   


    @NeptusProperty(name = "Destination")
    LocationType dest = new LocationType();
    
    @NeptusProperty
    double depth = 0;
    
    @NeptusProperty
    double speed = 1.2;    
    
    public String getCommand() {
        return "go";
    }
    
    public DefaultProperty[] getProperties() {
        return PluginUtils.getPluginProperties(this);
    }
    
    public PlanControl buildCommand(MissionType mt) {
        Property[] props = getProperties();
        Goto ponto1 = new Goto();
        ManeuverLocation maneuverLoc = null;
        
        for (Property p : props) {            
            if (p.getType() == LocationType.class) {
                LocationType loc = (LocationType)p.getValue();
                loc.convertToAbsoluteLatLonDepth();
                
                maneuverLoc = new ManeuverLocation(loc);                
                ponto1.setManeuverLocation(maneuverLoc);
            }
            
            if(p.getName() == "speed") {
                ponto1.setSpeed((double)p.getValue());
            }
            
            if(p.getName() == "depth") {
                maneuverLoc.setZ(10);
                maneuverLoc.setZUnits(ManeuverLocation.Z_UNITS.DEPTH);                
            } 
        }
        
        int reqId = IMCSendMessageUtils.getNextRequestId(); 
        PlanControl pc = new PlanControl(); 
        pc.setType(PlanControl.TYPE.REQUEST);
        pc.setOp(PlanControl.OP.START);
        pc.setRequestId(reqId);
        
        PlanType neptusPlan = new PlanType(mt);
        neptusPlan.getGraph().addManeuver(ponto1);
        neptusPlan.addVehicle("x8-02");
        
        PlanSpecification pSpec = new PlanSpecification(neptusPlan.asIMCPlan());
        pc.setPlanId(neptusPlan.getId());
        pc.setArg(pSpec);
 
        return pc;       
    }    
    
    public PlanType resultingPlan(MissionType mt) {
        PlanCreator planCreator = new PlanCreator(mt);
        planCreator.setSpeed(speed, SPEED_UNITS.METERS_PS);
        planCreator.setLocation(dest);
        planCreator.setDepth(depth);
        planCreator.addGoto(null);        
        PlanType pt = planCreator.getPlan();
        pt.setId("go");
        return pt;        
    }

   
    public void setCenter(LocationType loc) {
        dest = new LocationType(loc);
    }

    public static void main(String[] args) {
        GetGotoPoint gt = new GetGotoPoint();        
        PluginUtils.editPluginProperties(gt, true);
        //gt.buildCommand(getConsole());
    }

    
    
    
    
    /* (non-Javadoc)
     * @see pt.lsts.neptus.gui.PropertiesProvider#setProperties(com.l2fprod.common.propertysheet.Property[])
     */
    @Override
    public void setProperties(Property[] properties) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see pt.lsts.neptus.gui.PropertiesProvider#getPropertiesDialogTitle()
     */
    @Override
    public String getPropertiesDialogTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see pt.lsts.neptus.gui.PropertiesProvider#getPropertiesErrors(com.l2fprod.common.propertysheet.Property[])
     */
    @Override
    public String[] getPropertiesErrors(Property[] properties) {
        // TODO Auto-generated method stub
        return null;
    }

}