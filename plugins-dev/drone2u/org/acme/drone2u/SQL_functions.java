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

/**
 * @author pedro
 *
 */
public class SQL_functions {

    private final String url = "jdbc:postgresql://db.fe.up.pt/ee12299";
    private final String user = "ee12299";
    private final String password = "drone";

    Statement stmt;
    ResultSet rs;

    public Connection connect() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return con;
    }

    /**
     * Executa uma consulta diretamente na base de dados
     * @return <code>ResultSet</code> Retorna o resultado da consulta a base de dados. 
     */   
    private ResultSet execute(String query, Connection con) {
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
    private int update (String insert, Connection con) {        
        try {
            stmt = con.createStatement();            
            return stmt.executeUpdate(insert);         

        } catch (SQLException e) {
            System.err.println(e);
            return 0;
        }        
    } 

}
