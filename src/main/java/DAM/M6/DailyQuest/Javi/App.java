package DAM.M6.DailyQuest.Javi;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
public class App {
    public static void main(String[] args) {
        // Conectar a MongoDB
        ConexionMongoDB.conectar();
        
        // Verificar si la conexión está activa
        if (ConexionMongoDB.getMongoClient() != null) {
            System.out.println("La conexión está activa.");
        }
        
        // Cerrar la conexión
        ConexionMongoDB.cerrarConexion();
    }


    public class ConexionMongoDB {
        private static final String URI = "mongodb+srv://javiysergimarbol:123qwe@dailyquest.cbai7.mongodb.net/?retryWrites=true&w=majority&appName=DailyQuest";
        private static MongoClient mongoClient;

        // Método para conectar a la base de datos
        public static void conectar() {
            try {
                mongoClient = MongoClients.create(URI);
                System.out.println("Conectado a MongoDB");
            } catch (MongoException e) {
                System.err.println("Error de conexión: " + e.getMessage());
            }
        }

        // Método para obtener el cliente de MongoDB
        public static MongoClient getMongoClient() {
            return mongoClient;
        }

        // Método para cerrar la conexión
        public static void cerrarConexion() {
            if (mongoClient != null) {
                mongoClient.close();
                System.out.println("Conexión cerrada.");
            }
        }
    }
}
