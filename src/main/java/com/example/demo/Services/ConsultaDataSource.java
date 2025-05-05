package com.example.demo.Services;

import java.sql.*;
import javax.sql.DataSource;
import org.springframework.stereotype.Service;

@Service
public class ConsultaDataSource {

    private final DataSource dataSource;

    public ConsultaDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void consultaManual(){
        final String query = "SELECT * FROM estudiante";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);){

            while (rs.next()){
                System.out.println(
                    "ID: "+ rs.getInt("id") +
                    ", Nombre: " + rs.getString("nombre")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }








    }


}
