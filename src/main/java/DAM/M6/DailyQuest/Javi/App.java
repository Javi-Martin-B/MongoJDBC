package DAM.M6.DailyQuest.Javi;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.Arrays;
import java.util.Scanner;
import static com.mongodb.client.model.Filters.eq;

public class App {
    private static final String DATABASE_NAME = "DailyQuestUserDB"; // Nombre de la BBDD
    private static final String COLLECTION_NAME = "dailyq"; // Nombre de la colección
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Conectar a MongoDB
        ConexionMongoDB.conectar();
        
        if (ConexionMongoDB.getMongoClient() != null) {
            System.out.println("La conexión está activa.");

            // Obtener la base de datos y la colección
            MongoDatabase database = ConexionMongoDB.getMongoClient().getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            while (true) {
                System.out.println("\nSelecciona una opción:");
                System.out.println("1. Insertar un nuevo usuario");
                System.out.println("2. Buscar un usuario por ID");
                System.out.println("3. Salir");
                System.out.print("Opción: ");
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir salto de línea

                switch (opcion) {
                    case 1:
                        insertarUsuario(collection);
                        break;
                    case 2:
                        System.out.print("Ingrese el ID del usuario a buscar: ");
                        String userId = scanner.nextLine();
                        leerUsuario(collection, userId);
                        break;
                    case 3:
                        System.out.println("Saliendo del programa...");
                        ConexionMongoDB.cerrarConexion();
                        return;
                    default:
                        System.out.println("Opción no válida. Inténtalo de nuevo.");
                }
            }
        }

        // Cerrar la conexión
        ConexionMongoDB.cerrarConexion();
    }

    // Método para insertar un usuario en la base de datos con Scanner
    public static void insertarUsuario(MongoCollection<Document> collection) {
        try {
            System.out.print("Ingrese el ID del usuario: ");
            String usuarioId = scanner.nextLine();

            System.out.print("Ingrese el nombre del usuario: ");
            String nombre = scanner.nextLine();

            System.out.print("Ingrese los puntos totales: ");
            int puntosTotales = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            System.out.print("Ingrese cuántas tareas va a añadir: ");
            int numTareas = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            Document usuario = new Document("usuari_id", usuarioId)
                    .append("nom", nombre)
                    .append("punts_totals", puntosTotales);

            for (int i = 0; i < numTareas; i++) {
                System.out.println("\nTarea #" + (i + 1));
                System.out.print("Título de la tarea: ");
                String titulo = scanner.nextLine();

                System.out.print("Categoría de la tarea: ");
                String categoria = scanner.nextLine();

                System.out.print("Estado (Completa/Pendent): ");
                String estado = scanner.nextLine();

                System.out.print("Puntuación de la tarea: ");
                int puntuacion = scanner.nextInt();
                scanner.nextLine(); // Consumir salto de línea

                System.out.print("Fecha de creación (YYYY-MM-DD): ");
                String fechaCreacion = scanner.nextLine();

                Document tarea = new Document("title", titulo)
                        .append("category", categoria)
                        .append("status", estado)
                        .append("score", puntuacion)
                        .append("creation_date", fechaCreacion);

                usuario.append("tasques", Arrays.asList(tarea));
            }

            collection.insertOne(usuario);
            System.out.println("\n✅ Usuario insertado correctamente.");
        } catch (MongoException e) {
            System.err.println("❌ Error al insertar usuario: " + e.getMessage());
        }
    }

    // Método para leer un usuario por su ID con Scanner
    public static void leerUsuario(MongoCollection<Document> collection, String userId) {
        try {
            Bson filtro = eq("usuari_id", userId);
            Document usuario = collection.find(filtro).first();

            if (usuario != null) {
                System.out.println("\n🔍 Usuario encontrado:\n" + usuario.toJson());
            } else {
                System.out.println("\n⚠️ Usuario no encontrado.");
            }
        } catch (MongoException e) {
            System.err.println("❌ Error al leer usuario: " + e.getMessage());
        }
    }
}
