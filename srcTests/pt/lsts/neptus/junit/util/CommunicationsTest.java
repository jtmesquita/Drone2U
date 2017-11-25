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
 * Version 1.1 only (the "Licence"), appearing in the file LICENCE.md
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
 * Author: 
 * 20??/??/??
 */
package pt.lsts.neptus.junit.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.LinkedList;

import junit.framework.TestCase;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.util.conf.ConfigFetch;

public class CommunicationsTest extends TestCase {

	
	public static void main(String[] args) {
		
		ConfigFetch.initialize();

		IMCDefinition imcDef = IMCDefinition.getInstance();
		Collection<String> messages = imcDef.getMessageNames();

		
		LinkedList<byte[]> msgs = new LinkedList<byte[]>();
		
		for (String msg : messages) {
			System.out.print("Testing serialization of message '"+msg+"'...");
			IMCMessage m = imcDef.create(msg);

			try {
			    ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    IMCOutputStream imcos = new IMCOutputStream(baos);
				int size = m.serialize(imcos);
				byte[] ser = baos.toByteArray();				
				msgs.add(ser);
				//FIXME Zé podes ver como fazer isto?? (pdias 20111019)
                assertEquals(m.getHeader().getPayloadSize() + m.getPayloadSize() + 2/* footer */,
                        size);
				NeptusLog.pub().info("<###>OK (size is "+size+")");
			}
			catch (Exception e) {
				e.printStackTrace();
				assertEquals(false, true);
			}
		}
		
		NeptusLog.pub().info("<###>\n * SERIALIZED ALL MESSAGES... NOW GOING FOR DESEREALIZATION * \n");
		
		for (byte[] buffer : msgs) {
			System.out.print("Testing deserialization of message...");
	        IMCMessage m;
			try {
				m = imcDef.unserialize(new ByteArrayInputStream(buffer));
                NeptusLog.pub().info("<###>OK (type is " + m.getMessageType().getShortName() + ")");
			}
			catch (Exception e) {
				e.printStackTrace();
				assertEquals(false, true);
			}
		}
	}	
}