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
     * Vai buscar o ultimo id da lista de encomendas prontas a serem enviadas
     */
    public int getId_last_order() {
        ResultSet rsaux;

        String query = "SELECT id_wr FROM wprota ORDER BY id_wr DESC";     // vai buscar o ultimo id das encomendas

        rsaux = execute(query);

        try {
            rsaux.next();
            return rsaux.getInt("id_wr");
        }
        catch (Exception e){
            System.err.println("getId_last_order "+e);
            return -1;
        }
    }


    /**
     * Função que retorna os ids das encomendas que são com um
     * id superior ao last_id
     * @param last_id
     * @return
     */
    public Vector<Integer> get_order_IDs (int last_id) {
        ResultSet rsaux;
        Vector<Integer> Ids = new Vector<>();

        String query = "SELECT id_wr FROM wprota WHERE id_wr >" +last_id + "ORDER BY id_wr ASC";

        rsaux = execute(query);
        try {
            while (rsaux.next())
            {
                Ids.add(rsaux.getInt("id_wr"));
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
            System.err.println("getWarehouseLoc "+e);
            return null;
        }
    }

    /**
     * Função que atualiza na BD a localização de um dado drone
     * @param UAV_id
     * @param loc
     */
    public void InserUAVlocation(int UAV_id, LocationType loc) {
        String latitude = String.valueOf(loc.getLatitudeDegs());
        String longitude = String.valueOf(loc.getLongitudeDegs());

        String query = "UPDATE drone SET  longitude = '" + longitude + "', latitude = '"+latitude+"' WHERE id_d = "+UAV_id;

        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            System.err.println("InserUAVlocation "+e);
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
            System.err.println("getPathForOrder "+e);
            return null;
        }
    }

    /**
     * Retorna o caminho para o aramzém que o drone tem de seguir após
     * finalizar uma entrega
     * @param order_id
     * @return path[]
     */
    public String[] getPathToWhareHouse(int order_id) {
        ResultSet rsaux;

        String query = "SELECT longitude2, latitude2 FROM wprota WHERE id_wr ="+order_id;

        rsaux = execute(query);

        String[] path = new String[2];

        try {
            rsaux.next();

            path[0] = rsaux.getString("longitude2");
            path[1] = rsaux.getString("latitude2");

            return path;

        }
        catch (Exception e){
            System.err.println("getPathToWhareHouse "+e);
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

        String query = "UPDATE faz SET  estado = '"+State+"' WHERE id_e = "+order_id;

        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            return 1;

        } catch (SQLException e) {
            System.err.println("OrderStateUpdate "+e);
            return 0;
        }

    }

    /**
     * Retorda o estado de uma encomenda
     * @param order_id
     * @return
     */
    public String getOrderState(int order_id) {
        ResultSet rsaux;

        String query = "SELECT estado FROM faz WHERE id_e ="+order_id;     // vai buscar o ultimo id das encomendas

        rsaux = execute(query);

        try {
            rsaux.next();
            return rsaux.getString("estado");
        }
        catch (Exception e){
            System.err.println("getOrderState "+e);
            return null;
        }
    }

    /**
     * Atualiza na base de dados a disponibilidade do drone
     * @param UAV_name
     * @param State
     */
    public void UAVstateUpdate(String UAV_name, String State){
        String query;

        if(State.equals("TRUE")) {
            query = "UPDATE drone SET  disponibilidade = "+State+", reservado= 'FALSE' WHERE nome_drone = '"+UAV_name+"'";
        }
        else {
            query = "UPDATE drone SET  disponibilidade = "+State+", reservado = 'TRUE' WHERE nome_drone = '"+UAV_name+"'";
        }



        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            System.err.println("UAVstateUpdate "+e);
        }
    }

    /**
     * Atualiza a hora e data de envio de uma encomenda
     * @param id_encomenda
     * @param data_hora
     */
    public void UpdateDateSend(int id_encomenda, String[] data_hora) {

        String query = "UPDATE encomenda SET  data_env = '"+data_hora[0]+"', hora_env = '"+data_hora[1]+"' WHERE id_e ="+id_encomenda;

        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            System.err.println("UpdateDateSend "+e);
        }

    }

    public void UpdateDateDelivered(int id_encomenda, String[] data_hora) {

        String query = "UPDATE encomenda SET  data_entr = '"+data_hora[0]+"', hora_entr = '"+data_hora[1]+"' WHERE id_e ="+id_encomenda;

        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            System.err.println("UpdateDateDelivered "+e);
        }

    }

    /**
     * Função que devevolve o nome do drone associado a uma encomenda
     * @param order_id
     * @return
     */
    public String getDroneForOrder(int order_id) {
        ResultSet rsaux;

        String query = "SELECT nome_drone FROM wprota JOIN drone USING (id_d) WHERE id_wr = "+order_id;

        rsaux = execute(query);

        try {
            rsaux.next();
            return rsaux.getString("nome_drone");
        }
        catch (Exception e){
            System.err.println("getDroneForOrder "+e);
            return null;
        }
    }


    /**
     * Devolve o número da ultima encomenda feita pelo drone (presente na tabela entrega)
     * @param UAV_name
     * @return
     */
    public int getLastOrderIdDrone(String UAV_name) {
        ResultSet rsaux;

        String query = "SELECT id_e FROM entrega JOIN drone USING (id_d) WHERE nome_drone = '"+UAV_name+"' ORDER BY id_e DESC";

        rsaux = execute(query);

        try {
            rsaux.next();
            return rsaux.getInt("id_e");
        }
        catch (Exception e){
            System.err.println("getLastOrderIdDrone "+e);
            return -1;
        }
    }

    /**
     * Retorna o id do drone
     * @param UAV_name
     * @return
     */
    public int getDroneId(String UAV_name) {
        ResultSet rsaux;

        String query = "SELECT id_d FROM drone WHERE nome_drone='"+UAV_name+"'";

        rsaux = execute(query);

        try {
            rsaux.next();
            return rsaux.getInt("id_d");
        }
        catch (Exception e){
            System.err.println("getDroneId "+e);
            return -1;
        }
    }

    /**
     * Verifica qual a disponibilidade do drone
     * @param DroneId
     * @return true/false
     */
    public boolean getUAVavailability(int DroneId) {
        ResultSet rsaux;
        String query = "SELECT disponibilidade FROM drone WHERE id_d ="+DroneId;

        rsaux = execute(query);

        try {
            rsaux.next();
            return rsaux.getBoolean("disponibilidade");
        }
        catch (Exception e) {
            System.err.println("getUAVavailability "+e);
            return false;
        }
    }

    /**
     * Retorna a altura que um drone tem de circular
     * @param UAV_name
     * @return height that the drone must run
     */
    public int getUAVheight(String UAV_name) {
        ResultSet rsaux;
        String query = "SELECT altura FROM drone WHERE nome_drone = '"+UAV_name+"'";
        rsaux = execute(query);

        try {
            rsaux.next();
            return rsaux.getInt("altura");
        }
        catch (Exception e) {
            System.err.println("getUAVheight "+e);
            return 0;
        }
    }
    
    /**
     * Insere na tabela entrega os dados respetivos
     * @param Order_id
     * @param DroneID
     * @param meteo
     */
    public void InsertEntrega(int Order_id, int DroneID, String meteo) {
        String query = "INSERT INTO entrega (id_d, id_e, cond_meteo) VALUES("+DroneID+", "+Order_id+",'"+meteo+"')";

        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);

        } catch (SQLException e) {
            System.err.println("InsertEntrega "+e);
        }

    }

    /**
     * Função que retorna as encomendas que já foram processadas pelo algoritmo mas
     * ainda nao foram enviadas
     * @return Vector<Integer> Orders
     */
    public Vector<Integer> getNewOrders() {
        ResultSet rsaux;
        String query = "SELECT id_wr FROM wprota WHERE id_wr NOT IN ( SELECT id_e FROM entrega) ORDER BY id_wr ASC";
        Vector<Integer> Orders = new Vector<Integer>();

        rsaux = execute(query);

        try {
            while (rsaux.next())
            {
                Orders.add(rsaux.getInt("id_wr"));
            }
            return Orders;
        }
        catch (Exception e){
            System.err.println("getNewOrders "+e);
            return null;
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
            System.err.println("getUavsNames "+e);
            return null;
        }
    }

    /**
     * Consulta na base de dados o numero de UAVs disponiveis
     * @return
     */
    public int getFreeUavs() {

        ResultSet rs1;

        String query = "SELECT COUNT(*) FROM drone WHERE disponibilidade = TRUE";
        rs1 = execute(query);
        try {
            rs1.next();
            String res = rs1.getString("count");
            return Integer.parseInt(res);
        }
        catch (Exception e){
            System.err.println("getFreeUavs "+e);
            return -1;
        }
    }

    /**
     * Consulta na base de dados o numero de UAVs ocupados
     * @return
     */
    public int getBusyUavs() {

        ResultSet rs1;

        String query = "SELECT COUNT(*) FROM drone WHERE disponibilidade = FALSE";
        rs1 = execute(query);
        try {

            rs1.next();
            String res = rs1.getString("count");
            return Integer.parseInt(res);
        }
        catch (Exception e){
            System.err.println("getBusyUavs "+e);
            return -1;
        }
    }


    /**
     * Consulta na base de dados o numero de encomendas pendentes de envio (data de envio = NULL)
     * @return
     */
    public int getPendingSend() {

        ResultSet rs1;

        String query = "SELECT COUNT(*) FROM encomenda WHERE data_env IS NULL";
        rs1 = execute(query);
        try {

            rs1.next();
            String res = rs1.getString("count");
            return Integer.parseInt(res);
        }
        catch (Exception e){
            System.err.println("getPendingSend "+e);
            return -1;
        }
    }

    /**
     * Consulta na base de dados o numero de encomendas pendentes de entrega (data de envio != NULL AND data de entrega = NULL)
     * @return
     */
    public int getPendingDelivery() {

        ResultSet rs1;

        String query = "SELECT COUNT(*) FROM encomenda WHERE data_env IS NOT NULL AND data_entr IS NULL";
        rs1 = execute(query);
        try {

            rs1.next();
            String res = rs1.getString("count");
            return Integer.parseInt(res);
        }
        catch (Exception e){
            System.err.println("getPendingDelivery "+e);
            return -1;
        }
    }

    /**
     * Consulta na base de dados o numero de entregas com sucesso (data_env != NULL AND data_entr != NULL)
     * @return
     */
    public int getSuccessfulDeliveries() {

        ResultSet rs1;

        String query = "SELECT COUNT(*) FROM encomenda WHERE data_env IS NOT NULL AND data_entr IS NOT NULL";
        rs1 = execute(query);
        try {

            rs1.next();
            String res = rs1.getString("count");
            return Integer.parseInt(res);
        }
        catch (Exception e){
            System.err.println("getSuccessfulDeliveries "+e);
            return -1;
        }
    }

    /**
     * Consulta na base de dados o numero de UAVs operacionais
     * @return
     */
    public int getOperationalUavs() {

        ResultSet rs1;

        String query = "SELECT COUNT(*) FROM drone WHERE falha = FALSE";
        rs1 = execute(query);
        try {

            rs1.next();
            String res = rs1.getString("count");
            return Integer.parseInt(res);
        }
        catch (Exception e){
            System.err.println("getOperationalUavs"+e);
            return -1;
        }
    }

    /**
     * Consulta na base de dados o numero de UAVs em falha
     * @return
     */
    public int getFailureUavs() {

        ResultSet rs1;

        String query = "SELECT COUNT(*) FROM drone WHERE falha = TRUE";
        rs1 = execute(query);
        try {

            rs1.next();
            String res = rs1.getString("count");
            return Integer.parseInt(res);
        }
        catch (Exception e){
            System.err.println("getFailureUavs "+e);
            return -1;
        }
    }


    /**
     * Consulta na base de dados o numero total de UAVs
     * @return
     */
    public int getTotalUavs() {

        ResultSet rs1;

        String query = "SELECT COUNT(*) FROM drone";
        rs1 = execute(query);
        try {

            rs1.next();
            String res = rs1.getString("count");
            return Integer.parseInt(res);
        }
        catch (Exception e){
            System.err.println("getTotalUavs "+e);
            return -1;
        }
    }






    /**
     * Consulta de informação relativa as encomendas (ID_Uav, ID_Encomenda, Localização inicial, Localização final, Data/hora envio e entrega)
     * @return uma ArrayList de uma ArrayList com toda a info da tabela consultada
     */
    public ArrayList<ArrayList<String>> getEncomendas() {

        ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();

        ResultSet rs1;


        /*String query = "SELECT nome_drone, " +
                "encomenda.id_e," +
                "armazem.nome as arm_nome, " +
                "concat(armazem.latitude, ' ', armazem.longitude) as loc_inicial_arm, " +
                "pt_recol.nome as pt_recol_nome, " +
                "concat(pt_recol.latitude, ' ', pt_recol.longitude) as loc_inicial_ponto, " +
                "pt_entr.nome as pt_entr_nome, " +
                "concat(pt_entr.latitude, ' ', pt_entr.longitude) as loc_final, " +
                "concat(encomenda.data_env,' ', encomenda.hora_env) as data_env, " +
                "concat(encomenda.data_entr,' ', encomenda.hora_entr) as data_entr " +
                "FROM entrega  " +
                "LEFT JOIN encomenda USING(id_e) " +
                "LEFT JOIN armazem ON encomenda.armazem_recolha = armazem.id_a " +
                "LEFT JOIN ponto_entrega_recolha pt_entr ON ponto_entrega = pt_entr.id_er " +
                "LEFT JOIN ponto_entrega_recolha pt_recol ON ponto_recolha = pt_recol.id_er " +
                "LEFT JOIN drone USING(id_d)";*/

        String query = "SELECT nome_drone, " +
                "encomenda.id_e," +
                "armazem.nome as arm_nome, " +
                "concat(armazem.latitude, ' ', armazem.longitude) as loc_inicial_arm, " +
                "pt_recol.nome as pt_recol_nome, " +
                "concat(pt_recol.latitude, ' ', pt_recol.longitude) as loc_inicial_ponto, " +
                "pt_entr.nome as pt_entr_nome, " +
                "concat(pt_entr.latitude, ' ', pt_entr.longitude) as loc_final, " +
                "concat(encomenda.data_env,' ', encomenda.hora_env) as data_env, " +
                "concat(encomenda.data_entr,' ', encomenda.hora_entr) as data_entr " +
                "FROM wprota  " +
                "LEFT JOIN encomenda ON wprota.id_wr = id_e " +
                "LEFT JOIN armazem ON encomenda.armazem_recolha = armazem.id_a " +
                "LEFT JOIN ponto_entrega_recolha pt_entr ON ponto_entrega = pt_entr.id_er " +
                "LEFT JOIN ponto_entrega_recolha pt_recol ON ponto_recolha = pt_recol.id_er " +
                "LEFT JOIN drone USING(id_d) " +
                "ORDER BY id_e DESC";

        rs1 = execute(query);
        try {

            while(rs1.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs1.getString("nome_drone"));
                row.add(rs1.getString("id_e"));
                row.add(rs1.getString("arm_nome"));
                row.add(rs1.getString("loc_inicial_arm"));
                row.add(rs1.getString("pt_recol_nome"));
                row.add(rs1.getString("loc_inicial_ponto"));
                row.add(rs1.getString("pt_entr_nome"));
                row.add(rs1.getString("loc_final"));
                row.add(rs1.getString("data_env"));
                row.add(rs1.getString("data_entr"));

                table.add(row);
            }

            return table;
        }
        catch (Exception e){
            System.err.println("getEncomendas "+e);
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


        String query = "SELECT nome_drone, " +
                "encomenda.id_e," +
                "armazem.nome as arm_nome, " +
                "concat(armazem.latitude, ' ', armazem.longitude) as loc_inicial_arm, " +
                "pt_recol.nome as pt_recol_nome, " +
                "concat(pt_recol.latitude, ' ', pt_recol.longitude) as loc_inicial_ponto, " +
                "pt_entr.nome as pt_entr_nome, " +
                "concat(pt_entr.latitude, ' ', pt_entr.longitude) as loc_final, " +
                "concat(encomenda.data_env,' ', encomenda.hora_env) as data_env, " +
                "concat(encomenda.data_entr,' ', encomenda.hora_entr) as data_entr " +
                "FROM wprota  " +
                "LEFT JOIN encomenda ON wprota.id_wr = id_e " +
                "LEFT JOIN armazem ON encomenda.armazem_recolha = armazem.id_a " +
                "LEFT JOIN ponto_entrega_recolha pt_entr ON ponto_entrega = pt_entr.id_er " +
                "LEFT JOIN ponto_entrega_recolha pt_recol ON ponto_recolha = pt_recol.id_er " +
                "LEFT JOIN drone USING(id_d) " +
                "WHERE drone.nome_drone = '"+filter+"' " +
                "ORDER BY id_e DESC";

        rs1 = execute(query);
        try {

            while(rs1.next()) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(rs1.getString("nome_drone"));
                row.add(rs1.getString("id_e"));
                row.add(rs1.getString("arm_nome"));
                row.add(rs1.getString("loc_inicial_arm"));
                row.add(rs1.getString("pt_recol_nome"));
                row.add(rs1.getString("loc_inicial_ponto"));
                row.add(rs1.getString("pt_entr_nome"));
                row.add(rs1.getString("loc_final"));
                row.add(rs1.getString("data_env"));
                row.add(rs1.getString("data_entr"));

                table.add(row);
            }

            return table;
        }
        catch (Exception e){
            System.err.println("getEncomendas "+e);
            return null;
        }
    }

}
