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
 * 01/12/2017
 */
package org.acme.drone2u;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import pt.lsts.neptus.types.coord.LocationType;

/**
 * @author pedro
 *
 */
public class SQL_functions {

    private final String url = "jdbc:postgresql://192.168.50.131/ee12299";
    private final String user = "ee12299";
    private final String password = "drone";

    Connection con;
    Statement stmt;
    ResultSet rs;

    public Connection connect() {    
        
        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            //System.out.println(con.toString());
            System.out.println(e.getMessage());
            
        }

        return con;
    }

    /**
     * Verifica se já foi estabelecida uma conexão a base de dados
     * @return <code>true</code> caso já existe uma conexão estabelecida;
     * <code>false</code> caso contrário
     */    
    public boolean isConnected(){
        if(con == null) return false;
        try{
            return con.isValid(0);
        } catch (SQLException e){
            return false;
        }
    }  

    /**
     * Executa uma consulta diretamente na base de dados
     * @return <code>ResultSet</code> Retorna o resultado da consulta a base de dados. 
     */   
    private ResultSet execute(String query) {
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);            
        } catch (SQLException e) {
            System.err.println(e);
        }        
        return rs;
    }


    /**
     * Coloca o schema respetivo ao Drone2U na BD 
     */   
    public void setSchema(){
        String query = "SET search_path TO ola";

        execute(query);       
    }

    /**
     * Apenas para testar ir buscar pontos (ainda sem nenhum propósito especifico) 
     */
    public void getPoints() {
        ResultSet rsaux;

        String query = "SELECT localizacao,latitude, longitude\n" + 
                "FROM waypoint";

        rsaux = execute(query);

        try {
            while(rsaux.next()){
                System.out.println(rsaux.getString("localizacao") + " " + rsaux.getString("latitude") + " " + rsaux.getString("longitude"));
            }

        }
        catch (Exception e) {
            System.err.println(e);
        }

    }


    /**
     * Vai buscar o ultimo id da lista de encomendas
     */
    public int getId_last_order() {
        ResultSet rsaux;

        String query = "SELECT id_e FROM encomenda ORDER BY id_e DESC";     // vai buscar o ultimo id das encomendas

        rsaux = execute(query);

        try {
            rsaux.next();
            return rsaux.getInt("id_e");
        }
        catch (Exception e){
            System.err.println(e);
            return -1;
        }
    }


    /**
     * Função que retorna os ids das encomendas que são com um
     * id superior ao last_id
     */
    public Vector<Integer> get_order_IDs (int last_id) {
        ResultSet rsaux;
        Vector<Integer> Ids = new Vector<>();

        String query = "SELECT id_e FROM encomenda WHERE id_e >" +last_id + "ORDER BY id_e ASC";

        rsaux = execute(query);
        try {
            while (rsaux.next())
            {
                Ids.add(rsaux.getInt("id_e"));
            }
            return Ids;
        }
        catch (Exception e){
            System.err.println(e);
            return null;
        }
    }

    /**
     * Função que retorna as coordenadas do ponto de entrega para uma encomenda
     * na forma de LocationType
     */
    public LocationType getLocation_byId(int id) {
        LocationType location = new LocationType();
        ResultSet rsaux;

        String query = "SELECT latitude, longitude FROM encomenda JOIN waypoint ON (morada_destino = localizacao) WHERE id_e = " + id;

        rsaux = execute(query);

        try {
            rsaux.next();
            location.setLatitudeStr(rsaux.getString("latitude"));
            location.setLongitudeStr(rsaux.getString("longitude"));

            return location;
        }
        catch (Exception e){
            System.err.println(e);
            return null;
        }
    }
    
    /**
     * Função que rotorna a localização de um armazém no formato LocationType
     * @return
     */
    public LocationType getWarehouseLoc() {
        LocationType location = new LocationType();
        ResultSet rsaux;

        String query = "SELECT latitude, longitude FROM armazem WHERE nome = 'arm_1'";

        rsaux = execute(query);

        try {
            rsaux.next();
            location.setLatitudeStr(rsaux.getString("latitude"));
            location.setLongitudeStr(rsaux.getString("longitude"));

            return location;
        }
        catch (Exception e){
            System.err.println(e);
            return null;
        } 
    }
    
    /**
     * Função que atualiza na BD a localização de um dado drone
     * @param UAV_id
     * @param loc
     */
    public void InserUAVlocation(int UAV_id, LocationType loc) {
        String latitude = loc.getLatitudeAsPrettyString();
        String longitude = loc.getLongitudeAsPrettyString();
        
        String query = "UPDATE drone SET  longitude = '" + longitude + "', latitude = '"+latitude+"' WHERE id_d = "+UAV_id;
        
        try {
            stmt = con.createStatement();            
            stmt.executeUpdate(query);         

        } catch (SQLException e) {
            System.err.println(e);
        }    
    }

    /**
     * Função que retorna a informação na base de dados relativamente
     * a um caminho para uma dada encomenda
     * 
     * @param order_id
     * @return String path[longitude, latitude]
     */
    public String[] getPathForOrder(int order_id) {
        ResultSet rsaux;
        
        String query = "SELECT longitude, latitude FROM wprota WHERE id_wr ="+order_id;
        
        rsaux = execute(query);
        
        String[] path = new String[2];
             
        try {
            rsaux.next();
            
            path[0] = rsaux.getString("longitude");
            path[1] = rsaux.getString("latitude");
            
            return path;
            
        }
        catch (Exception e){
            System.err.println(e);
            return null;
        }
    }
    
    /**
     * Atualiza o estado de uma encomenda (order_id) com o estado
     * "state"
     * @param order_id
     * @param State
     * @return
     */
    public int OrderStateUpdate(int order_id, String State) {
        
        String query = "UPDATE faz SET  estado = '"+State+"' WHERE id_d = "+order_id;
        
        try {
            stmt = con.createStatement();            
            stmt.executeUpdate(query);
            return 1;

        } catch (SQLException e) {
            System.err.println(e);
            return 0;
        }    
        
    }
    
    /**
     * Consulta na base de dados o nome dos UAVs
     * @return uma ArrayList com o nome dos UAVs
     */    
    public ArrayList<String> getUavsNames() {    
        
        ResultSet rs1;    
        ArrayList<String> names = new ArrayList<String>();   
                   
        String query = "SELECT DISTINCT nome_drone FROM drone";
        rs1 = execute(query);
        try {            
            
            while(rs1.next()) {
                names.add(rs1.getString("nome_drone"));
            }
            
            return names;            
        }
        catch (Exception e){
            System.err.println(e);
            return null;
        }
    }
    
    
    /**
     * Consulta de informação relativa as encomendas (ID_Uav, ID_Encomenda, Localização inicial, Localização final, Data/hora envio e entrega)
     * @return uma ArrayList de uma ArrayList com toda a info da tabela consultada 
     */ 
    public ArrayList<ArrayList<String>> getEncomendas() {     
        
        ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
        
        ResultSet rs1;    
        
        
        String query = "SELECT nome_drone, encomenda.id_e, concat(armazem.latitude, ' ', armazem.longitude) as loc_inicial, concat(ponto_entrega_recolha.latitude, ' ', ponto_entrega_recolha.longitude) as loc_final\n" + 
                            "FROM entrega " + 
                            "JOIN encomenda USING(id_e) " + 
                            "JOIN armazem ON encomenda.armazem_recolha = armazem.id_a " + 
                            "JOIN ponto_entrega_recolha ON ponto_entrega = ponto_entrega_recolha.id_er " + 
                            "JOIN drone USING(id_d) ";

        rs1 = execute(query);
        try {            
            
            while(rs1.next()) {
                ArrayList<String> row = new ArrayList<String>();                
                row.add(rs1.getString("nome_drone"));
                row.add(rs1.getString("id_e"));
                row.add(rs1.getString("loc_inicial"));
                row.add(rs1.getString("loc_final"));
                
                table.add(row);                
            }
            
            return table;            
        }
        catch (Exception e){
            System.err.println(e);
            return null;
        }       
    }
    
    
    
    /**
     * Consulta de informação (filtrada) relativa as encomendas (ID_Uav, ID_Encomenda, Localização inicial, Localização final, Data/hora envio e entrega)
     * @return uma ArrayList de uma ArrayList com toda a info da tabela consultada 
     */ 
    public ArrayList<ArrayList<String>> getEncomendas(String filter) {     
        
        ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
        
        ResultSet rs1;    
        
        
        String query = "SELECT nome_drone, encomenda.id_e, concat(armazem.latitude, ' ', armazem.longitude) as loc_inicial, concat(ponto_entrega_recolha.latitude, ' ', ponto_entrega_recolha.longitude) as loc_final\n" + 
                            "FROM entrega " + 
                            "JOIN encomenda USING(id_e) " + 
                            "JOIN armazem ON encomenda.armazem_recolha = armazem.id_a " + 
                            "JOIN ponto_entrega_recolha ON ponto_entrega = ponto_entrega_recolha.id_er " + 
                            "JOIN drone USING(id_d) "+
                            "WHERE drone.nome_drone = '"+filter+"'";                            

        rs1 = execute(query);
        try {            
            
            while(rs1.next()) {
                ArrayList<String> row = new ArrayList<String>();                
                row.add(rs1.getString("nome_drone"));
                row.add(rs1.getString("id_e"));
                row.add(rs1.getString("loc_inicial"));
                row.add(rs1.getString("loc_final"));
                
                table.add(row);                
            }
            
            return table;            
        }
        catch (Exception e){
            System.err.println(e);
            return null;
        }       
    }
    
}
