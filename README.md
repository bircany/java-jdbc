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


JDBC (Java Database Connectivity) Nedir?
JDBC, Java uygulamalarının veritabanlarıyla iletişim kurmasını sağlayan bir API'dir. Standart bir arayüz sunarak farklı veritabanı sistemleriyle tutarlı bir şekilde çalışmamızı sağlar.

Temel Konseptler
1. Bağlantı Yönetimi (Connection Management)
DriverManager: Veritabanı bağlantısı oluşturur

Connection: Veritabanı bağlantısını temsil eder

Önemli: Kaynakları uygun şekilde kapatmak için try-with-resources kullanın

2. Sorgu Çalıştırma
Statement: Basit SQL sorguları için

PreparedStatement: Parametreli sorgular için (SQL Injection'a karşı güvenli)

CallableStatement: Stored procedure çağrıları için

3. Sonuç İşleme
ResultSet: Sorgu sonuçlarını okumak için

ResultSetMetaData: Sonuç kümesi hakkında meta veri sağlar

4. Transaction Yönetimi
ACID özelliklerini sağlamak için

commit() ve rollback() metodları

5. Batch İşlemler
Çoklu sorguları tek seferde çalıştırmak için

CRUD İşlemleri
CRUD (Create, Read, Update, Delete) temel veritabanı operasyonlarını ifade eder:

Create (Oluşturma)
java
Copy
String sql = "INSERT INTO employees (first_name, last_name) VALUES (?, ?)";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setString(1, "John");
    pstmt.setString(2, "Doe");
    pstmt.executeUpdate();
}
Read (Okuma)
java
Copy
String sql = "SELECT * FROM employees";
try (Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery(sql)) {
    while (rs.next()) {
        System.out.println(rs.getString("first_name"));
    }
}
Update (Güncelleme)
java
Copy
String sql = "UPDATE employees SET last_name = ? WHERE id = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setString(1, "Smith");
    pstmt.setInt(2, 1);
    pstmt.executeUpdate();
}
Delete (Silme)
java
Copy
String sql = "DELETE FROM employees WHERE id = ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setInt(1, 1);
    pstmt.executeUpdate();
}
Transaction Yönetimi
Transaction'lar, bir grup veritabanı işlemini atomik bir birim olarak çalıştırmamızı sağlar:

java
Copy
try {
    conn.setAutoCommit(false); // Auto-commit'i kapat
    
    // Transaction işlemleri
    updateAccount(conn, "account1", -100);
    updateAccount(conn, "account2", 100);
    
    conn.commit(); // Tüm işlemler başarılı ise commit
} catch (SQLException e) {
    conn.rollback(); // Hata olursa rollback
} finally {
    conn.setAutoCommit(true); // Auto-commit'i tekrar aç
}
Stored Procedure Kullanımı
Stored procedure'ler veritabanı sunucusunda saklanan önceden derlenmiş SQL kodlarıdır:

IN Parametreli
java
Copy
String sql = "{call increase_salaries_for_department(?, ?)}";
try (CallableStatement cstmt = conn.prepareCall(sql)) {
    cstmt.setString(1, "Engineering");
    cstmt.setDouble(2, 10000);
    cstmt.execute();
}
OUT Parametreli
java
Copy
String sql = "{call get_count_for_department(?, ?)}";
try (CallableStatement cstmt = conn.prepareCall(sql)) {
    cstmt.setString(1, "Engineering");
    cstmt.registerOutParameter(2, Types.INTEGER);
    cstmt.execute();
    int count = cstmt.getInt(2);
}
INOUT Parametreli
java
Copy
String sql = "{call greet_the_department(?)}";
try (CallableStatement cstmt = conn.prepareCall(sql)) {
    cstmt.registerOutParameter(1, Types.VARCHAR);
    cstmt.setString(1, "Engineering");
    cstmt.execute();
    String result = cstmt.getString(1);
}
ResultSet Döndüren
java
Copy
String sql = "{call get_employees_for_department(?)}";
try (CallableStatement cstmt = conn.prepareCall(sql)) {
    cstmt.setString(1, "Engineering");
    cstmt.execute();
    try (ResultSet rs = cstmt.getResultSet()) {
        while (rs.next()) {
            // Sonuçları işle
        }
    }
}
Önemli JDBC Konseptleri
Kaynak Yönetimi: Connection, Statement ve ResultSet nesnelerini her zaman kapatın

SQL Injection: PreparedStatement kullanarak önleyin

Connection Pooling: Verimli bağlantı yönetimi için

Batch İşlemler: Çoklu ekleme/güncelleme işlemlerinde performans için

Transaction Isolation Levels: Veri tutarlılığı için uygun seviyeyi seçin

MetaData API: Veritabanı şeması hakkında bilgi almak için

JDBC Cheat Sheet'leri
1. Temel JDBC İşlemleri
markdown
Copy
1. Bağlantı oluşturma:
   Connection conn = DriverManager.getConnection(url, user, pass);

2. Sorgu çalıştırma:
   Statement stmt = conn.createStatement();
   ResultSet rs = stmt.executeQuery("SELECT * FROM table");

3. Parametreli sorgu:
   PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM table WHERE id=?");
   pstmt.setInt(1, 123);
   ResultSet rs = pstmt.executeQuery();
2. Transaction Yönetimi
markdown
Copy
1. Transaction başlatma:
   conn.setAutoCommit(false);

2. Commit:
   conn.commit();

3. Rollback:
   conn.rollback();

4. Savepoint:
   Savepoint savepoint = conn.setSavepoint();
   conn.rollback(savepoint);
3. Stored Procedure Kullanımı
markdown
Copy
1. IN parametre:
   CallableStatement cstmt = conn.prepareCall("{call proc_name(?)}");
   cstmt.setString(1, "value");

2. OUT parametre:
   cstmt.registerOutParameter(2, Types.INTEGER);

3. INOUT parametre:
   cstmt.registerOutParameter(1, Types.VARCHAR);
   cstmt.setString(1, "input");

4. ResultSet döndürme:
   cstmt.execute();
   ResultSet rs = cstmt.getResultSet();
4. Best Practices
markdown
Copy
1. Her zaman try-with-resources kullanın
2. PreparedStatement ile SQL Injection'ı önleyin
3. Connection pool kullanın (HikariCP gibi)
4. Büyük ResultSet'ler için fetchSize ayarlayın
5. Batch işlemler için addBatch() ve executeBatch() kullanın
6. Veritabanı kaynaklarını (Connection, Statement, ResultSet) uygun şekilde kapatın
Performans İpuçları
Batch İşlemler: Çoklu ekleme/güncelleme işlemlerinde addBatch() ve executeBatch() kullanın

Fetch Size: Büyük sonuç kümeleri için uygun fetch size belirleyin

Connection Pooling: Bağlantı oluşturma maliyetini azaltmak için

Statement Caching: Sık kullanılan sorgular için

MetaData Kullanımı: Gereksiz meta data sorgularından kaçının

Hata Yönetimi
SQLException yakalayın ve uygun şekilde işleyin

Hata kodlarını ve durumlarını kontrol edin

Transaction'larda hata durumunda mutlaka rollback yapın

Kaynakları finally bloğunda veya try-with-resources ile kapatın

Bu rehber, Java JDBC ile veritabanı programlamanın temellerini kapsamaktadır. Uygulamalarınızda bu konseptleri kullanarak verimli ve güvenli veritabanı işlemleri gerçekleştirebilirsiniz.
