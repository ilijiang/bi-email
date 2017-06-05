package com.fangdd.esf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by lijiang on 5/24/17.
 */
public class DBHelper {

    public static final String url = "jdbc:mysql://10.50.23.208/fangdd_esf_data";
    public static final String name = "com.mysql.jdbc.Driver";
    public static final String user = "root";
    public static final String password = "fdd#bigdata@2015";
    //本地MySQL
//    public static final String url = "jdbc:mysql://127.0.0.1/fangdd_bi";
//    public static final String name = "com.mysql.jdbc.Driver";
//    public static final String user = "root";
//    public static final String password = "root";

    public Connection conn = null;
    public PreparedStatement pst = null;

    public DBHelper(String sql) {
        try {
            Class.forName(name);//指定连接类型
            conn = DriverManager.getConnection(url, user, password);//获取连接
            pst = conn.prepareStatement(sql);//准备执行语句
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.conn.close();
            this.pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
