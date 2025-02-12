package DAM.M6.DailyQuest.Javi;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class ConexionMongoDB {
    private static final String URI = "mongodb+srv://javiysergimarbol:123qwe@dailyquest.cbai7.mongodb.net/?retryWrites=true&w=majority&appName=DailyQuest";
    private static MongoClient mongoClient;

    // Método para conectar a la base de datos
    public static void conectar() {
        try {
            mongoClient = MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new com.mongodb.ConnectionString(URI))
                    .build());
            System.out.println("✅ Conectado a MongoDB correctamente.");
        } catch (MongoException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
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
            System.out.println("🔌 Conexión cerrada correctamente.");
        }
    }
}
