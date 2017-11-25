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

import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.console.plugins.MissionChangeListener;
import pt.lsts.neptus.gui.VehicleChooser;
import pt.lsts.neptus.i18n.I18n;
import pt.lsts.neptus.plugins.ConfigurationListener;
import pt.lsts.neptus.renderer2d.InteractionAdapter;
import pt.lsts.neptus.renderer2d.Renderer2DPainter;
import pt.lsts.neptus.renderer2d.StateRenderer2D;
import pt.lsts.neptus.types.mission.MissionType;
import pt.lsts.neptus.types.mission.plan.PlanType;
import pt.lsts.neptus.types.vehicle.VehicleType;
import pt.lsts.neptus.types.vehicle.VehiclesHolder;

/**
 * @author pedro
 *
 */
public class Drone2uPlanEditor extends InteractionAdapter implements Renderer2DPainter,
                                                                      MissionChangeListener, ConfigurationListener{

    /**
     * @param console
     */
    public Drone2uPlanEditor(ConsoleLayout console) {
        super(console);
    }
    
    
    
    public void newPlan() {
        
        VehicleType choice = null;
        if (getConsole().getMainSystem() != null)
            choice = VehicleChooser.showVehicleDialog(null,
                    VehiclesHolder.getVehicleById(getConsole().getMainSystem()), getConsole());
        else
            choice = VehicleChooser.showVehicleDialog(null, null, getConsole());

        if (choice == null)
            return;
        
        System.out.println("Entro aqui");
    }
    
    

    

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see pt.lsts.neptus.plugins.ConfigurationListener#propertiesChanged()
     */
    @Override
    public void propertiesChanged() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see pt.lsts.neptus.console.plugins.MissionChangeListener#missionReplaced(pt.lsts.neptus.types.mission.MissionType)
     */
    @Override
    public void missionReplaced(MissionType mission) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see pt.lsts.neptus.console.plugins.MissionChangeListener#missionUpdated(pt.lsts.neptus.types.mission.MissionType)
     */
    @Override
    public void missionUpdated(MissionType mission) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see pt.lsts.neptus.renderer2d.Renderer2DPainter#paint(java.awt.Graphics2D, pt.lsts.neptus.renderer2d.StateRenderer2D)
     */
    @Override
    public void paint(Graphics2D g, StateRenderer2D renderer) {
        // TODO Auto-generated method stub
        
    }

}
