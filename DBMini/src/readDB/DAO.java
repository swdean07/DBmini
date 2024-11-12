package readDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DAO {
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
    private String userid = "scott";
    private String passwd = "tiger";
    private String sql;
    private Map<String, ArrayList<String>> dataMap = new HashMap<>();
    public DAO() {
    }
    public Map<String, ArrayList<String>> OracleInputSql(String sql) {
    	dataMap.clear();
        try (Connection con = DriverManager.getConnection(url, userid, passwd);
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = rs.getString(i);
                    if (dataMap.containsKey(columnName)) {
                    	dataMap.get(columnName).add(columnValue);
                    }
                    else {
                    	ArrayList<String> aList = new ArrayList<>();
                    	aList.add(columnValue);
                    	dataMap.put(columnName, aList);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataMap;
    }
    public static void main(String[] args) {
		DAO myOracleDAO = new DAO();
		Map<String, String> sqlMap = new Sql().sql();
		Map<String, ArrayList<String>> myMap;
		myMap = myOracleDAO.OracleInputSql(sqlMap.get("그룹 함수 조회 예제"));
		for (Map.Entry<String, ArrayList<String>> entry : myMap.entrySet()) {
			boolean a = true;
		    System.out.println(entry.getKey() + ": " + entry.getValue());
		    if (a) System.out.println("index:" + entry.getValue().size()); a = false;
		}
	}
}
