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
 * 08/12/2017
 */
package org.acme.drone2u;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.google.gson.*;
import com.google.gson.reflect.*;
/**
 * @author Pedro Guedes
 *
 */
public class Weather {

    private static String API_KEY = "002a045185180d89ad400a8da226507c";    // chave necessária para poder fazer pedidos de materiologia
    private static String Location = "Matosinhos,Portugal";                // Localização para onde queremos ver a meteriologia
    private static String urlString = "http://api.openweathermap.org/data/2.5/weather?q="+ Location +"&APPID=" +API_KEY +"&units=Metric";   // link para obter os dados metereológios em formato JSON - Nota: usamos o parámetro metric para colocar a resposta nas unidades que queremos.
    /**
     * Função para converter uma resposta JSON num objeto map
     * para depois podermos aceder às condĩções atmosféricas
     * @param str
     * @return map
     */
    public static Map<String, Object> jsonToMap(String str){
        Map<String, Object> map = new Gson().fromJson(str, new TypeToken<HashMap<String, Object>>() {}.getType());  

        return map;
    }      

    //public static void main(String[] args) {
    public Vector<Map<String, Object>> getWeatherData() {

        Vector<Map<String, Object>> data = new Vector<>();

        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            /*para guardar a resposta do website*/
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();

            /*Conversão da resposta do website para um Map*/

            Map<String, Object> respMap = jsonToMap(result.toString());                 // primeiro converto para o formato e só depois é que posso aceder ao seu conteúdo
            Map<String, Object> mainMap = jsonToMap(respMap.get("main").toString());    // no main contém: temperatura, pressão, humidad
            Map<String, Object> windMap = jsonToMap(respMap.get("wind").toString());    // no wind: velocidade e ângulo;
            //Map<String, Object> tudo = jsonToMap(respMap.toString());

            
            data.addElement(mainMap);
            data.addElement(windMap);
            data.addElement(respMap);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return data;
    }


}
