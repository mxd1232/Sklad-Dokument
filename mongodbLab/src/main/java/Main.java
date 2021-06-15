import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.SneakyThrows;
import org.bson.Document;

import java.util.*;

import static com.mongodb.client.model.Filters.*;

public class Main {

    final private static Random r = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        String user = "student01";
        String password = "student01";
        String host = "localhost";
        int port = 27017;
        String database = "database01";

        String clientURI = "mongodb://" + user + ":" + password + "@" + host + ":" + port + "/" + database;
        MongoClientURI uri = new MongoClientURI(clientURI);

        System.out.println(clientURI);

        MongoClient mongoClient = new MongoClient(uri);

        MongoDatabase db = mongoClient.getDatabase(database);

        int select;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Welcome to UniSystem");
            System.out.println("Enter value to select menu");
            System.out.println("1 - create , 2 - update, 3 - delete, 4 - get, 5 - processing, 6 - exit");

            select = scanner.nextInt();
            switch (select) {
                case 1:
                    create(db);
                    break;
                case 2:
                    update(db);
                    break;
                case 3:
                    delete(db);
                    break;
                case 4:
                    get(db);
                    break;
                case 5:
                    processing(db);
                    break;
                case 6:
                    break;
            }

        } while (select != 6);
    }

    @SneakyThrows
    private static void processing(MongoDatabase db) {
        Scanner scanner = new Scanner(System.in);
        MongoCollection<Document> fire = db.getCollection("fire");
        System.out.println("Enter the fighterID to calculate its average time");
        String id = scanner.nextLine();
        ObjectMapper objectMapper = new ObjectMapper();
        FireFighter fireFighter1 = new FireFighter();

        Document byId = fire.find(eq("_id",id)).first();




        if (byId != null) {
            fireFighter1 =  objectMapper.readValue(Objects.requireNonNull(fire.find(eq("_id",id)).first()).toJson(), FireFighter.class);
            Map<String,Double> map = fireFighter1.getGetTasks();
            int counter = 0;
            double sum = 0;
            for(Map.Entry<String, Double> e : map.entrySet()){
                sum += e.getValue();
                counter++;
            }

            System.out.println("Avg = " + sum/counter);

        }
        else {
            System.out.println("There is no task with this ID");
        }    }

    private static void update(MongoDatabase db) {
        System.out.println("What u want to update:");
        System.out.println("1 - task, 2 - fire fighter, 3 - departement, 4 - exit");
        int select;

        Scanner scanner = new Scanner(System.in);

        select = scanner.nextInt();
        switch (select) {
            case 1:
                updateTask(db);
                break;
            case 2:
                updateFireFighter(db);
                break;
            case 3:
               updateDepartement(db);
                break;
            case 4:
                break;
        }
    }

    @SneakyThrows
    private static void updateDepartement(MongoDatabase db) {
        MongoCollection<Document> departement = db.getCollection("departement");
        Scanner scanner = new Scanner(System.in);
        ObjectMapper objectMapper = new ObjectMapper();
        String task;
        ArrayList<String> tasks = new ArrayList<>();

        Departement departementNew = new Departement();

        String _id;
        System.out.println("Enter id of departement");
        _id = scanner.nextLine();

        Document byId = departement.find(eq("_id",_id)).first();

        if (byId != null) {

            System.out.println("Enter new name");
            departementNew.setName(scanner.nextLine());

            System.out.println("Enter new longName");
            departementNew.setLongName(scanner.nextLine());

            while (true){
                System.out.println("Enter new task");
                task = scanner.nextLine();
                if(task.isBlank()){
                    break;
                }
                tasks.add(task);
            }
            departementNew.setFireFighters(tasks);

            System.out.println("Enter number of people");
            departementNew.setNumberOfPeople(scanner.nextDouble());
            departementNew.set_id(_id);
            Document doc = Document.parse(objectMapper.writeValueAsString(departementNew));
            departement.updateOne(eq("_id",_id),new Document("$set",doc));

        }
        else {
            System.out.println("There is no departement with this ID");
        }

    }

    @SneakyThrows
    private static void updateFireFighter(MongoDatabase db) {
        MongoCollection<Document> fireFighter = db.getCollection("fireFighter");
        Scanner scanner = new Scanner(System.in);
        ObjectMapper objectMapper = new ObjectMapper();
        String name;
        String _id;
        Map<String,Double> newGrade = new HashMap<>();

        FireFighter fireFighterNew = new FireFighter();

        System.out.println("Enter id of task");
        _id = scanner.nextLine();

        if(fireFighter.find(eq("_id", _id)).first() != null) {
            System.out.println("Enter new name");
            fireFighterNew.setName(scanner.nextLine());

            System.out.println("Enter new surname");
            fireFighterNew.setSurname(scanner.nextLine());


            while (true) {
                System.out.println("Enter new task");
                name = scanner.nextLine();
                if (name.isBlank()) {
                    break;
                }
                System.out.println("Enter time it took");
                Double grade = Double.parseDouble(scanner.nextLine());
                newGrade.put(name, grade);
            }
            fireFighterNew.setGetTasks(newGrade);
            fireFighterNew.set_id(_id);
            Document doc = Document.parse(objectMapper.writeValueAsString(fireFighterNew));
            fireFighter.updateOne(eq("_id",_id),new Document("$set",doc));
        }else {
            System.out.println("There is no fire Fighter with this ID");
        }
    }

    @SneakyThrows
    private static void updateTask(MongoDatabase db) {
        String _id;
        ObjectMapper objectMapper = new ObjectMapper();
        MongoCollection<Document> task = db.getCollection("task");
        Scanner scanner = new Scanner(System.in);
        Task taskNew = new Task();
        System.out.println("Enter id of task to update");
        _id = scanner.nextLine();

        if(task.find(eq("_id",_id)).first() != null){

            System.out.println("Enter name");
            taskNew.setName(scanner.nextLine());

            System.out.println("Enter time it took");
            taskNew.setTimeTaken(scanner.nextInt());
            taskNew.set_id(_id);

            Document doc = Document.parse(objectMapper.writeValueAsString(taskNew));
            task.updateOne(eq("_id",_id),new Document("$set",doc));
        }
        else {
            System.out.println("There is no task with this ID");
        }

    }

    private static void delete(MongoDatabase db) {
        System.out.println("What you want to delete:");
        System.out.println("1 - task, 2 - fire fighter, 3 - departement, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);
        select = scanner.nextInt();
        switch (select) {
            case 1:
                deleteTask(db);
                break;
            case 2:
                deleteFireFighter(db);
                break;
            case 3:
                deleteDepartement(db);
                break;
            case 4:
                break;
        }
    }

    private static void deleteDepartement(MongoDatabase db) {
        MongoCollection<Document> departement = db.getCollection("departement");
        System.out.println("Enter id to delete departement");
        Scanner scanner = new Scanner(System.in);
        String id = scanner.nextLine();

        Document byId = departement.find(eq("_id", id)).first();
        if(byId == null){
            System.out.println("There is no departement with this ID");
        }else {
            System.out.println("Deleted:" + byId.toJson());
            departement.deleteOne(eq("_id",id));
        }
    }

    private static void deleteFireFighter(MongoDatabase db) {

        MongoCollection<Document> fireFighter = db.getCollection("fireFighter");
        System.out.println("Enter id to delete fireFighter");
        Scanner scanner = new Scanner(System.in);
        String id = scanner.nextLine();

        Document byId = fireFighter.find(eq("_id", id)).first();
        if(byId == null){
            System.out.println("There is no fireFighter with this ID");
        }else {
            System.out.println("Deleted:" + byId.toJson());
            fireFighter.deleteOne(eq("_id",id));
        }
    }

    private static void deleteTask(MongoDatabase db) {
        MongoCollection<Document> task = db.getCollection("task");
        System.out.println("Enter id to delete task");
        Scanner scanner = new Scanner(System.in);
        String id = scanner.nextLine();

        Document byId = task.find(eq("_id", id)).first();
        if(byId == null){
            System.out.println("There is no task with this ID");
        }else {
            System.out.println("Deleted:" + byId.toJson());
            task.deleteOne(eq("_id",id));
        }


    }

    private static void get(MongoDatabase db) {
        System.out.println("What u want to get:");
        System.out.println("1 - task, 2 - fire fighter, 3 - departement, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);
        select = scanner.nextInt();
        switch (select) {
            case 1:
                getTask(db);
                break;
            case 2:
                getFireFighter(db);
                break;
            case 3:
                getDepartement(db);
                break;
            case 4:
                break;
        }
    }

    private static void getDepartement(MongoDatabase db) {
        System.out.println("What you want to do:");
        System.out.println("1 - get all, 2 - get by ID,3 - get by task ,4 - exit:");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();

        switch (select){
            case 1:
                getDepartementAll(db);
                break;
            case 2:
                getDepartementByID(db);
                break;
            case 3:
                getDepartementByTask(db);
                break;
            case 4:
                break;
        }
    }

    private static void getDepartementByTask(MongoDatabase db) {
        MongoCollection<Document> departement = db.getCollection("departement");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter task");
        String task = scanner.nextLine();

        Document byTask = departement.find(eq("tasks", task)).first();

        if(byTask != null){
            for(Document d : departement.find(eq("tasks", task))){
                System.out.println("departement: " + d.toJson());
            }
        }
        else {
            System.out.println("There is no departement with this task");
        }
    }

    private static void getDepartementByID(MongoDatabase db) {
        MongoCollection<Document> departement = db.getCollection("departement");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter id");
        String id = scanner.nextLine();

        Document byId = departement.find(eq("_id",id)).first();

        if (byId != null) {
            System.out.println("departement:" + byId.toJson());
        }
        else {
            System.out.println("There is no departement with this ID");
        }
    }

    private static void getDepartementAll(MongoDatabase db) {
        MongoCollection<Document> departement = db.getCollection("departement");

        for(Document doc : departement.find())
            System.out.println("departement: " + doc.toJson());
    }

    private static void getFireFighter(MongoDatabase db) {
        System.out.println("What you want to do:");
        System.out.println("1 - get all, 2 - get by ID, 3 - get by surname, 4 - exit");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();

        switch (select){
            case 1:
                getFireFighterAll(db);
                break;
            case 2:
                getFireFighterByID(db);
                break;
            case 3:
                getFireFighterBySurname(db);
                break;
            case 4:
                break;
        }
    }

    private static void getFireFighterBySurname(MongoDatabase db) {
        MongoCollection<Document> fireFighter = db.getCollection("fireFighter");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter surname");
        String surname = scanner.nextLine();

        Document bySurname = fireFighter.find(eq("surname",surname)).first();

        if(bySurname != null){
            for(Document d : fireFighter.find(eq("surname",surname))){
            System.out.println("FireFighter: " + d.toJson());
            }
        }
        else {
            System.out.println("There is no FireFighter with this Surname");
        }
    }

    @SneakyThrows
    private static void getFireFighterByID(MongoDatabase db) {
        MongoCollection<Document> fireFighter = db.getCollection("fireFighter");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter id");

        String id = scanner.nextLine();

        Document byId = fireFighter.find(eq("_id",id)).first();

        if (byId != null) {
            System.out.println("FireFighter:" + byId.toJson());
        }
        else {
            System.out.println("There is no FireFighter with this ID");
        }
    }

    private static void getFireFighterAll(MongoDatabase db) {

        MongoCollection<Document> fireFighter = db.getCollection("fireFighter");

        for(Document doc : fireFighter.find())
            System.out.println("fireFighter: " + doc.toJson());
    }

    private static void getTask(MongoDatabase db) {
        System.out.println("What you want to do:");
        System.out.println("1 - get all, 2 - get by ID, 3 - exit");
        Scanner scanner = new Scanner(System.in);
        int select = scanner.nextInt();

        switch (select){
            case 1:
                getTaskAll(db);
                break;
            case 2:
                getTaskByID(db);
                break;
            case 3:
                break;
        }
    }

    private static void getTaskByID(MongoDatabase db) {
        MongoCollection<Document> task = db.getCollection("task");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter id");

        Document byId = task.find(eq("_id", scanner.nextLine())).first();
        if(byId == null){
            System.out.println("There is no task with this ID");
        }else {
            System.out.println("task:" + byId.toJson());
        }
    }

    private static void getTaskAll(MongoDatabase db) {
        MongoCollection<Document> task = db.getCollection("task");

        for(Document doc : task.find())
            System.out.println("task: " + doc.toJson());
    }

    private static void create(MongoDatabase db) {
        System.out.println("What you want to create");
        System.out.println("1 - task , 2 - fire fighter, 3 - departement, 4 - exit");
        int select;
        Scanner scanner = new Scanner(System.in);

        select = scanner.nextInt();
        switch (select) {
            case 1:
                createTask(db);
                break;
            case 2:
                createFireFighters(db);
                break;
            case 3:
                createDepartement(db);
                break;
            case 4:
                break;
        }
    }

    @SneakyThrows
    private static void createDepartement(MongoDatabase db) {
        MongoCollection<Document> departement = db.getCollection("departement");
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> tasks = new ArrayList<>();
        String task;

        Long newId = (long) Math.abs(r.nextInt());
        Scanner scanner = new Scanner(System.in);
        Departement departementNew = new Departement();

        System.out.println("Enter name");
        departementNew.setName(scanner.nextLine());

        System.out.println("Enter long name");
        departementNew.setLongName(scanner.nextLine());

        while (true){
            System.out.println("Enter task");
            task = scanner.nextLine();
            if(task.isBlank()){
                break;
            }
            tasks.add(task);
        }
        departementNew.setFireFighters(tasks);

        System.out.println("Enter salary");
        departementNew.setNumberOfPeople(scanner.nextDouble());
        departementNew.set_id(newId.toString());

        departement.insertOne(Document.parse(objectMapper.writeValueAsString(departementNew)));
    }

    @SneakyThrows
    private static void createFireFighters(MongoDatabase db) {
        MongoCollection<Document> fireFighters = db.getCollection("fireFighters");
        ObjectMapper objectMapper = new ObjectMapper();
        String task;
        Map<String,Double> newGrade = new HashMap<>();
        Long newId = (long) Math.abs(r.nextInt());
        Scanner scanner = new Scanner(System.in);
        FireFighter fireFighterNew = new FireFighter();

        System.out.println("Enter name");
        fireFighterNew.setName(scanner.nextLine());

        System.out.println("Enter surname");
        fireFighterNew.setSurname(scanner.nextLine());



        while (true){
            System.out.println("Enter task name");
            task = scanner.nextLine();
            if(task.isBlank()) {
                break;
            }
            System.out.println("Enter time");
            Double grade = Double.parseDouble(scanner.nextLine());
            newGrade.put(task,grade);
        }
        fireFighterNew.setGetTasks(newGrade);


        fireFighterNew.set_id(newId.toString());

        fireFighters.insertOne(Document.parse(objectMapper.writeValueAsString(fireFighterNew)));
    }

    @SneakyThrows
    private static void createTask(MongoDatabase db) {
        MongoCollection<Document> task = db.getCollection("task");
        ObjectMapper objectMapper = new ObjectMapper();

        Long newId = (long) Math.abs(r.nextInt());

        Scanner scanner = new Scanner(System.in);
        Task taskNew = new Task();

        System.out.println("Enter name");
        taskNew.setName(scanner.nextLine());

        System.out.println("Enter ETCS");
        taskNew.setTimeTaken(scanner.nextInt());

        taskNew.set_id(newId.toString());

        task.insertOne(Document.parse(objectMapper.writeValueAsString(taskNew)));
    }
}
