import java.util.Scanner;
import java.sql.*;


class mainsql{
    
    public static void CrearTablas(conexionUGR conn ){
        try{
            conn.execSQL(
			"CREATE TABLE Stock("+
        		"Cproducto INT NOT NULL,"+
    			"Cantidad INT,"+
    			"PRIMARY KEY (Cproducto)"+
    		")");
    		conn.execSQL(
        	"CREATE TABLE Pedido("+
    			"Cpedido INT NOT NULL,"+
    			"Ccliente INT,"+
    			"Fecha_pedido DATE,"+
        	    "PRIMARY KEY (Cpedido)"+
        	")");
        	conn.execSQL(
        	"CREATE TABLE Detalle_Pedido("+
        		"Cpedido INT NOT NULL,"+
        		"Cproducto INT NOT NULL,"+
        		"Cantidad int,"+
        		"FOREIGN KEY (Cpedido) REFERENCES Pedido(Cpedido),"+
        		"FOREIGN KEY (Cproducto) REFERENCES Stock(Cproducto),"+
        		"PRIMARY KEY (Cpedido,Cproducto)"+
        	")");
        }
        catch(Exception e){
            System.out.print("CrearTablas: " + e.toString()+"\n");
        }
        commit(conn);
    }
    
        public static void borrarTabla( String nombre, conexionUGR conn ){
        String st = "DROP TABLE " + nombre ;
        conn.execSQL( st );
    }
    
    public static void commit( conexionUGR conn ){
        String st = "COMMIT";
        conn.execSQL( st );
        conn.commit();
    }
    
    public static void crearPedido(int Cpedido, int Ccliente, conexionUGR conn ){
        String st = "INSERT INTO Pedido VALUES ('" + String.valueOf(Cpedido) + "', '" + String.valueOf(Ccliente) + "', SYSDATE)";
        conn.execSQL( st );
    }
    
    public static void borrarPedido( int Cpedido, conexionUGR conn ){
        String st = "DELETE FROM Pedido WHERE Cpedido = "+String.valueOf(Cpedido);
        conn.execSQL( st );
    }
    
    public static void crearStock( int CProducto, int Cantidad, conexionUGR conn ){
        String st = "INSERT INTO Stock VALUES ('" + String.valueOf(CProducto) + "', '" + String.valueOf(Cantidad) + "')";
        conn.execSQL( st );
    }
    
    public static void borrarStock( int CProducto, conexionUGR conn ){
        String st = "DELETE FROM Stock WHERE CProducto = "+String.valueOf(CProducto);
        conn.execSQL( st );
    }
    
    public static void crearDetallePedido( int Cpedido, int Cproducto, int cantidad,  conexionUGR conn ){
        String st = "INSERT INTO Detalle_Pedido VALUES ('" + String.valueOf(Cpedido) + "','" + String.valueOf(Cproducto)+ "','" + String.valueOf(cantidad) + "')";
        conn.execSQL( st );
    }
    
    public static void borrarDetallesPedidos( int Cpedido, conexionUGR conn ){
        String st = "DELETE FROM Detalle_Pedido WHERE Cpedido = "+String.valueOf(Cpedido);
        conn.execSQL( st );
    }
    
    public static  int cantidadStock( int Cproducto, conexionUGR conn ){
        int cant=-1;
        String st = " SELECT cantidad FROM Stock WHERE Cproducto="+String.valueOf(Cproducto);
        try{
            ResultSet r = conn.execSQL( st );
            while ( r.next() )
            {
                cant = r.getInt( 1 );
            }
        }
        catch(Exception e){
            System.out.print("cantidadStock: "+e.toString()+"\n");
        }
        
        return cant;
    }
    
    
    public static void actualizarStock( int Cproducto, int cantidad, conexionUGR conn ){
        String st = " UPDATE Stock SET cantidad="+String.valueOf(cantidad)+" WHERE Cproducto="+String.valueOf(Cproducto);
        conn.execSQL( st );
    }
    

    public static void primeros10Stocks( conexionUGR conn ){
        for(int i = 0; i < 10; i++){
            crearStock( i, 10 + i, conn);
        }
    }
    
    public static boolean aniadirDetallePedido(int Cpedido, int Cproducto, int cantRequerida, conexionUGR conn){
        boolean result = false;
        int cantidad = cantidadStock(Cproducto,conn);
        
        //Comprueba si la cantidad de stock para Cproducto es suficiente
        if( cantidad >= cantRequerida){
            result = true;
        }
        
        //Si lo es entonces crea DetallePedido correspondiente y actualiza stock
        if( result){
            //crearDetallePedido
            crearDetallePedido(Cpedido,Cproducto,cantRequerida,conn);
            int cantResultante = cantidad - cantRequerida;
            //actualizarStock
            actualizarStock(Cproducto,cantResultante,conn);
            
        }
        
        return result;
    }

public static void main(String[] args) throws SQLException {
 
		conexionUGR con = new conexionUGR();
		Scanner scanner = new Scanner(System.in);
		String user, passwd;
		System.out.println("Introduzca su usuario\n");
		user=scanner.nextLine();
		System.out.println("Introduzca su contraseña\n");
		passwd=scanner.nextLine();
		con.newConn(user,passwd);
		int a=0;

		while(a!=5){
    		System.out.println("Opciones:\n");
    		System.out.println("1: Creacion de las tablas e inserción de tuplas predefinidas.\n");
    		System.out.println("2: Borrado de las tablas\n");
    		System.out.println("3: Dar de alta un pedido\n");
    		System.out.println("4: Borrar pedido\n");
    		System.out.println("5: Salir\n");
    		
            a=scanner.nextInt();
            int Cpedido,Cproducto,Cantidad,Ccliente = 0,b=0;

    		switch(a) {
            case 1:
                borrarTabla("Detalle_Pedido",con);
                borrarTabla("Stock",con);
                borrarTabla("Pedido",con);
                CrearTablas(con);
                primeros10Stocks(con);
                commit(con);
            break;
            case 2:
                borrarTabla("Detalle_Pedido",con);
                borrarTabla("Stock",con);
                borrarTabla("Pedido",con);
                commit(con);
            break;
            case 3:
                System.out.println("Alta de pedido\n");
                System.out.println("Introduce un código de pedido:");
                Cpedido = scanner.nextInt();
                System.out.println("\nIntroduce un código de cliente:");
                Ccliente = scanner.nextInt();
                
                Savepoint sp = con.setSavepoint("Savepoint1");
                
                crearPedido(Cpedido,Ccliente,con);
                
                while(b!=3 && b!=4){
                    System.out.println("Detalles del pedido\n");
                    System.out.println("1: Añadir detalle\n");
                    System.out.println("2: Eliminar todos los detalles del producto\n");
                    System.out.println("3: Cancelar pedido\n");
                    System.out.println("4: Finalizar pedido\n");
                    
                    b=scanner.nextInt();
                    switch(b){
                        case 1:
                            System.out.println("Inserte el codigo del producto:\n");
                            Cproducto=scanner.nextInt();
                            System.out.println("Inserte la cantidad a pedir:\n");
                            Cantidad=scanner.nextInt();
                            if( aniadirDetallePedido(Cpedido,Cproducto,Cantidad,con) ){
                                System.out.println("Detalle de pedido añadido, quedan " + String.valueOf(cantidadStock(Cproducto,con)) + " del producto " + String.valueOf(Cproducto) + "\n");
                            }
                            else{
                                System.out.println("No hay suficiente stock de dicho elemento ( faltan " + String.valueOf(Cantidad - cantidadStock(Cproducto,con) ) + " elementos )\n");
                            }
                        break;
                        
                        case 2:
                            borrarDetallesPedidos(Cpedido, con);
                            System.out.println("Detalles de pedido borrados\n");
                        break;
                        case 3:
                            con.rollback(sp);
                        break;
                        case 4:
                            commit(con);
                        break;
                    }
                }
            break;
            case 4:
                System.out.println("Borrado de pedido\n");
                System.out.println("Introduce un código de pedido:");
                Cpedido = scanner.nextInt();
                borrarDetallesPedidos(Cpedido, con);
                borrarPedido(Cpedido,con);
                commit(con);
            break;
            case 5:
                // Exit
            break;
            default:
                System.out.println("ERROR: Opcion no valida\n");
            }
		}
		scanner.close();
		con.terminarConexion();
        
	}
	
}