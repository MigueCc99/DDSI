import java.sql.*;

class conexionUGR{

    protected Connection con=null;
    
	public void newConn( String username, String passwd ){
		String host = "jdbc:oracle:thin:@oracle0.ugr.es:1521/practbd.oracle0.ugr.es";
		
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");  
			con = DriverManager.getConnection( host, username, passwd );
			
			if (con != null) {
                System.out.println("Conexión realizada con éxito");
                con.setAutoCommit(false);
            } else {
                System.out.println("Conexión fallida");
            }
		}
		
		catch (Exception e) {
			System.out.print("newConn: "+e.toString()+"\n");
		}
	}
	
	public ResultSet execSQL( String statement ){
	    ResultSet rs = null;
	    try {
	        Statement stmt=con.createStatement();  
		    rs=stmt.executeQuery( statement );
	    }
	    catch(SQLException e){
	        if( e.getErrorCode() != 942 ){ //Fallo de drop table que no existe
                     System.out.print( e.getErrorCode());

	            System.out.print("execSQL: "+e.toString()+"\n");
	        }
	    }
	    catch (Exception e) {
			System.out.print("execSQL: "+e.toString()+"\n");
	    }
	    return rs;
	}
	
	public void terminarConexion(){
        
        if (con != null) {
            try{
                con.close();
                if (con.isClosed()) {
                    System.out.println("Conexion finalizada con exito");
                } else {
                    System.out.println("Conexion finalizada sin exito");
                }
            }
            catch(Exception e){
                System.out.print("terminarConexion: "+e.toString()+"\n");
            }
        }
  
    }
    public void commit(){
        try{
            con.commit();
        }
        catch(Exception e){
            System.out.print("commit: "+e.toString()+"\n");
        }
    }
    public Savepoint setSavepoint(String name){
        Savepoint sp = null;
        try{
            sp = con.setSavepoint(name);   
        }
        catch(Exception e){
            System.out.print("setSavepoint: "+e.toString()+"\n");
        }
        return sp;
    }
    public void rollback(Savepoint sp){
        try{
            con.rollback(sp);
        }
        catch(Exception e){
            System.out.print("rollback: "+e.toString()+"\n");
        }
    }
}