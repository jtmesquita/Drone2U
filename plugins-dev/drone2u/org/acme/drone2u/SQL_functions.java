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
     * Executa uma consulta do tipo <code>INSERT</code>, <code>UPDATE</code> ou <code>DELETE</code>
     * @return <code>int</code> Retorna 1 em caso de sucesso; 0 caso contrário. 
     */    
    private int update (String insert) {        
        try {
            stmt = con.createStatement();            
            return stmt.executeUpdate(insert);         

        } catch (SQLException e) {
            System.err.println(e);
            return 0;
        }        
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
}
