# java-jdbc
I explain Java JDBC, DAO Interface and ORM concepts and widely used SQL Concepts Such as BloB,CloB,CRUD,Transaction,StoredProcedures,View.


# **Java Veritabanı Erişim Teknolojileri: JDBC, DAO ve ORM (Hibernate)**

![](https://miro.medium.com/v2/resize:fit:1400/1*_4B2qcl8Q5yPZ6JhD2i5-Q.png)

*Java veritabanı erişim teknolojileri hiyerarşisi*

## **1. JDBC (Java Database Connectivity)**

JDBC, Java'nın veritabanlarına bağlanmak için sunduğu temel API'dir.

![](https://www.tutorialspoint.com/jdbc/images/jdbc-architecture.jpg)

*JDBC mimarisi - DriverManager üzerinden veritabanı bağlantısı*

java

Copy

```
// Temel JDBC bağlantı örneği
public class JdbcExample {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydb";
        String user = "user";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employees")) {

            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

## **2. DAO (Data Access Object) Pattern**

DAO pattern, veri erişim katmanını uygulama mantığından ayırmak için kullanılır.

![](https://www.baeldung.com/wp-content/uploads/2018/11/DAO.png)

*DAO deseni - Business katmanı ile veri kaynağı arasında aracı*

java

Copy

```
// Employee DAO örneği
public interface EmployeeDao {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(int id);
    void addEmployee(Employee employee);
    void updateEmployee(Employee employee);
    void deleteEmployee(int id);
}

public class EmployeeDaoImpl implements EmployeeDao {
    private final Connection connection;

    public EmployeeDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        // JDBC kodları...
        return employees;
    }
    // Diğer metod implementasyonları...
}
```

## **3. ORM (Hibernate)**

ORM (Object-Relational Mapping), nesneler ile veritabanı tabloları arasında köprü kurar.

![](https://www.tutorialspoint.com/hibernate/images/hibernate_architecture.jpg)

*Hibernate mimarisi - Veritabanı bağımsız çalışma*

### **Hibernate Temel Özellikleri:**

java

Copy

```
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    // Getter ve Setter metodları...
}

// Hibernate ile sorgulama örneği
public class HibernateExample {
    public static void main(String[] args) {
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");

        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();

        Transaction t = session.beginTransaction();

        Employee emp = new Employee();
        emp.setFirstName("John");
        emp.setLastName("Doe");

        session.save(emp);
        t.commit();

        session.close();
        factory.close();
    }
}
```

## **JDBC vs DAO vs ORM Karşılaştırması**

![](https://javaconceptoftheday.com/wp-content/uploads/2019/08/JDBCVsHibernate.png)

*JDBC, DAO ve ORM karşılaştırması*

| **Özellik** | **JDBC** | **DAO Pattern** | **ORM (Hibernate)** |
| --- | --- | --- | --- |
| Veri Erişim | Düşük seviye | Soyutlanmış | Tam otomatik |
| SQL Bilgisi | Gerekli | Gerekli | Çoğunlukla gerekmez |
| Performans | Yüksek | Yüksek | Orta |
| Kod Miktarı | Çok | Orta | Az |
| Veritabanı Bağımlılık | Yüksek | Düşük | Çok düşük |
| Öğrenme Eğrisi | Kolay | Orta | Dik |

## **SQL Cheat Sheet**

### **Temel SQL Komutları**

sql

Copy

```
-- Veri sorgulama
SELECT * FROM table_name WHERE condition;
SELECT column1, column2 FROM table_name ORDER BY column1 ASC|DESC;

-- Veri ekleme
INSERT INTO table_name (column1, column2) VALUES (value1, value2);

-- Veri güncelleme
UPDATE table_name SET column1 = value1 WHERE condition;

-- Veri silme
DELETE FROM table_name WHERE condition;

-- Tablo oluşturma
CREATE TABLE table_name (
    column1 datatype constraints,
    column2 datatype constraints,
    PRIMARY KEY (column1)
);

-- Join işlemleri
SELECT a.column1, b.column2
FROM table1 a
INNER JOIN table2 b ON a.key = b.key;
```

### **Transaction Yönetimi**

sql

Copy

```
BEGIN TRANSACTION;
-- SQL komutları
COMMIT; -- veya ROLLBACK;
```

### **Sık Kullanılan Fonksiyonlar**

sql

Copy

```
-- Metin fonksiyonları
CONCAT(str1, str2), SUBSTRING(str, start, length), UPPER(str), LOWER(str)

-- Sayısal fonksiyonlar
COUNT(), SUM(), AVG(), MAX(), MIN(), ROUND(number, decimals)

-- Tarih fonksiyonları
NOW(), CURDATE(), DATE_FORMAT(date, format), DATEDIFF(date1, date2)
```

## **Hibernate Query Language (HQL) Cheat Sheet**

java

Copy

```
// Temel sorgu
Query query = session.createQuery("FROM Employee");
List<Employee> employees = query.list();

// Parametreli sorgu
Query query = session.createQuery("FROM Employee WHERE id = :empId");
query.setParameter("empId", 1);
Employee emp = (Employee) query.uniqueResult();

// Sayfalama
Query query = session.createQuery("FROM Employee");
query.setFirstResult(10); // 11. kayıttan başla
query.setMaxResults(5);   // 5 kayıt getir

// Native SQL sorgusu
SQLQuery sqlQuery = session.createSQLQuery("SELECT * FROM employees");
sqlQuery.addEntity(Employee.class);
List<Employee> employees = sqlQuery.list();
```

## **Performans Optimizasyonu**

![](https://d1jnx9ba8s6j9r.cloudfront.net/blog/wp-content/uploads/2019/08/JDBC-vs-Hibernate-Performance-Comparison-Edureka.png)

*JDBC ve Hibernate performans karşılaştırması*

1. **JDBC'de Optimizasyon**:
    - Batch işlemleri kullanın (**`addBatch()`**, **`executeBatch()`**)
    - Connection pooling kullanın (HikariCP gibi)
    - Uygun Statement tiplerini seçin (PreparedStatement)
2. **Hibernate'de Optimizasyon**:
    - Lazy loading kullanın
    - Second-level cache etkinleştirin
    - N+1 sorgu problemini önleyin (JOIN FETCH kullanarak)
    - Uygun ID generation stratejisi seçin

## **Güvenlik Önlemleri**

1. **SQL Injection Önleme**:
    - JDBC'de her zaman **`PreparedStatement`** kullanın
    - Hibernate'de parametreli sorgular kullanın
2. **Transaction Yönetimi**:
    - Uygun isolation level seçin
    - Deadlock'lardan kaçının
3. **Bağlantı Güvenliği**:
    - Credential'ları kod içinde saklamayın
    - SSL/TLS ile şifreli bağlantı kullanın

## **Sonuç**

Java'da veritabanı erişimi için farklı seviyelerde teknolojiler mevcuttur. Basit uygulamalar için JDBC yeterli olabilirken, daha karmaşık sistemlerde DAO pattern veya ORM çözümleri tercih edilebilir. Hibernate gibi ORM araçları veritabanı işlemlerini kolaylaştırsa da, performans kritik uygulamalarda JDBC'nin daha verimli olabileceği unutulmamalıdır.
