package DAM.M6.DailyQuest.Javi;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

public class App {
    public static void main(String[] args) {
        // Conectar a MongoDB
        ConexionMongoDB.conectar();

        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n===== DAILY QUEST MENU =====");
            System.out.println("1️⃣ Crear un usuario con tareas");
            System.out.println("2️⃣ Leer todos los usuarios con sus tareas");
            System.out.println("3️⃣ Buscar usuarios con tareas en un rango de fechas");
            System.out.println("0️⃣ Salir");
            System.out.print("🟢 Elige una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea

            switch (opcion) {
                case 1 -> crearUsuarioConTareas(scanner);
                case 2 -> leerUsuarios();
                case 3 -> buscarTareasEntreFechas(scanner);
                case 0 -> System.out.println("👋 Saliendo del programa...");
                default -> System.out.println("❌ Opción inválida. Inténtalo de nuevo.");
            }

        } while (opcion != 0);

        scanner.close();
        ConexionMongoDB.cerrarConexion();
    }

    // MÉTODO PARA CREAR UN USUARIO Y SUS TAREAS
    public static void crearUsuarioConTareas(Scanner scanner) {
        MongoClient mongoClient = ConexionMongoDB.getMongoClient();
        if (mongoClient == null) {
            System.out.println("❌ No hay conexión a MongoDB.");
            return;
        }

        MongoDatabase database = mongoClient.getDatabase("DailyQuestUserDB");
        MongoCollection<Document> coleccion = database.getCollection("dailyq");

        System.out.print("👤 Ingresa el ID del usuario: ");
        String usuarioID = scanner.nextLine();
        System.out.print("📝 Ingresa el nombre del usuario: ");
        String nombre = scanner.nextLine();
        System.out.print("⭐ Ingresa los puntos totales del usuario: ");
        int puntos = scanner.nextInt();
        scanner.nextLine(); // Consumir salto de línea

        List<Document> tareas = new ArrayList<>();
        System.out.print("📌 ¿Cuántas tareas quieres añadir? ");
        int numTareas = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numTareas; i++) {
            System.out.println("\n🔹 Añadiendo tarea " + (i + 1));
            System.out.print("📌 Título de la tarea: ");
            String titulo = scanner.nextLine();
            System.out.print("📂 Categoría: ");
            String categoria = scanner.nextLine();
            System.out.print("✅ Estado (Pendent/Completa): ");
            String estado = scanner.nextLine();
            System.out.print("🎯 Puntuación: ");
            int puntuacion = scanner.nextInt();
            scanner.nextLine(); // Consumir salto de línea
            System.out.print("📅 Fecha de creación (YYYY-MM-DD): ");
            String fechaCreacion = scanner.nextLine();

            tareas.add(new Document("title", titulo)
                    .append("category", categoria)
                    .append("status", estado)
                    .append("score", puntuacion)
                    .append("creation_date", fechaCreacion));
        }

        Document nuevoUsuario = new Document("usuari_id", usuarioID)
                .append("nom", nombre)
                .append("punts_totals", puntos)
                .append("tasques", tareas);

        coleccion.insertOne(nuevoUsuario);
        System.out.println("✅ Usuario y tareas guardadas con éxito.");
    }

    // MÉTODO PARA LEER TODOS LOS USUARIOS CON SUS TAREAS
    public static void leerUsuarios() {
        MongoClient mongoClient = ConexionMongoDB.getMongoClient();
        if (mongoClient == null) {
            System.out.println("❌ No hay conexión a MongoDB.");
            return;
        }

        MongoDatabase database = mongoClient.getDatabase("DailyQuestUserDB");
        MongoCollection<Document> coleccion = database.getCollection("dailyq");

        for (Document usuario : coleccion.find()) {
            System.out.println("\n👤 Usuario: " + usuario.getString("nom") + " (ID: " + usuario.getString("usuari_id") + ")");
            System.out.println("⭐ Puntos Totales: " + usuario.getInteger("punts_totals"));
            List<Document> tareas = usuario.getList("tasques", Document.class);

            for (Document tarea : tareas) {
                System.out.println("   🔸 Tarea: " + tarea.getString("title"));
                System.out.println("      📂 Categoría: " + tarea.getString("category"));
                System.out.println("      ✅ Estado: " + tarea.getString("status"));
                System.out.println("      🎯 Puntuación: " + tarea.getInteger("score"));
                System.out.println("      📅 Fecha de creación: " + tarea.getString("creation_date"));
            }
        }
    }

    // MÉTODO PARA BUSCAR USUARIOS POR RANGO DE FECHAS
    public static void buscarTareasEntreFechas(Scanner scanner) {
        System.out.print("📅 Ingresa la fecha de inicio (YYYY-MM-DD): ");
        String fechaInicio = scanner.nextLine();
        System.out.print("📅 Ingresa la fecha de fin (YYYY-MM-DD): ");
        String fechaFin = scanner.nextLine();

        MongoClient mongoClient = ConexionMongoDB.getMongoClient();
        if (mongoClient == null) {
            System.out.println("❌ No hay conexión activa a MongoDB.");
            return;
        }

        MongoDatabase database = mongoClient.getDatabase("DailyQuestUserDB");
        MongoCollection<Document> coleccion = database.getCollection("dailyq");

        boolean encontrado = false;

        for (Document usuario : coleccion.find()) {
            String usuarioID = usuario.getString("usuari_id");
            String nombre = usuario.getString("nom");

            for (Document tarea : usuario.getList("tasques", Document.class)) {
                String fechaTarea = tarea.getString("creation_date");
                
                if (fechaTarea.compareTo(fechaInicio) >= 0 && fechaTarea.compareTo(fechaFin) <= 0) {
                    if (!encontrado) {
                        System.out.println("\n🔍 Usuarios con tareas entre " + fechaInicio + " y " + fechaFin + ":");
                        encontrado = true;
                    }
                    System.out.println("👤 Usuario: " + nombre + " (ID: " + usuarioID + ")");
                    System.out.println("   🔹 Tarea: " + tarea.getString("title") + " | 📅 Fecha: " + fechaTarea);
                }
            }
        }

        if (!encontrado) {
            System.out.println("❌ No se encontraron tareas en ese rango de fechas.");
        }
    }
}

