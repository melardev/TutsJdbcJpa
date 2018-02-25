package melardev.dao;

import melardev.models.Article;
import melardev.models.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class ArticleDao {

    private static final String COL_FIRST_NAME = "first_name";
    private static final String COL_LAST_NAME = "last_name";
    private static final String COL_AGE = "age";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        /*
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/jdbc_db");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        JdbcTemplate jdbcSpring = new JdbcTemplate(dataSource);
        create(jdbcSpring);
        insertUpdateDelete(jdbcSpring);
        */
    }

    //DDL: CREATE ALTER DROP
    public void create(/*JdbcTemplate jdbcTemplate*/) {

        jdbcTemplate.execute("DROP TABLE IF EXISTS articles");
        jdbcTemplate.execute("DROP TABLE IF EXISTS authors");
        String sql = "CREATE TABLE IF NOT EXISTS `jdbc_db`.`authors`(`id` INT NOT NULL AUTO_INCREMENT ,"
                + "`first_name` VARCHAR(100) NOT NULL , `last_name` VARCHAR(100) NOT NULL ,"
                + "`age` INT NOT NULL, PRIMARY KEY (`id`)) ENGINE = InnoDB;";

        jdbcTemplate.execute(sql);

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `jdbc_db`.`articles`(`id` INT NOT NULL AUTO_INCREMENT, "
                + "`title` VARCHAR(100) NOT NULL, `author_id` INT NOT NULL, PRIMARY KEY (`id`), CONSTRAINT `fk_ar` " +
                "FOREIGN KEY (`author_id`) REFERENCES `jdbc_db`.`authors`(`id`) ON DELETE CASCADE " +
                "ON UPDATE CASCADE) ENGINE = InnoDB;");
    }

    public void insertUpdateDelete() {
        String sqlAuthors = "INSERT INTO authors (first_name, last_name, age) VALUES (?, ?, ?)";
        String sqlArticles = "INSERT INTO articles (title, author_id) VALUES (?, ?)";


        jdbcTemplate.update(sqlAuthors, "Felix", "Carlson", 33);
        jdbcTemplate.update(sqlAuthors, "Jenna", "Fletcher", 26);
        jdbcTemplate.update(sqlAuthors, "Irma", "Sutton", 22);
        jdbcTemplate.update(sqlAuthors, "Anne", "Blake", 27);
        jdbcTemplate.update(sqlAuthors, "Joyce", "Fletcher", 29);
        jdbcTemplate.update(sqlAuthors, "Mae", "Price", 29);
        jdbcTemplate.update(sqlAuthors, "Lula", "West", 20);
        jdbcTemplate.update(sqlAuthors, "Rodolfo", "Alexander", 21);


        jdbcTemplate.update(sqlArticles, "Jdbc Tutorials", 5);
        jdbcTemplate.update(sqlArticles, "JAX-RS Jersey Tutorials", 1);
        jdbcTemplate.update(sqlArticles, "Jpa Tutorials", 4);
        jdbcTemplate.update(sqlArticles, "Spring MVC Tutorials", 3);
        jdbcTemplate.update(sqlArticles, "Spring Core Tutorials", 2);
        jdbcTemplate.update(sqlArticles, "Python Tutorials", 1);


        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("INSERT INTO articles(title, author_id) values (?,?)");
                ps.setString(1, "Android Firebase Tutorials");
                ps.setInt(2, 2);
                return ps;
            }
        });

        jdbcTemplate.update("UPDATE authors SET first_name = ? where id = ?", "Shree", 2);
        jdbcTemplate.update("DELETE FROM authors where id = ?", 1);
    }

    public void get() {

        int rowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) from authors", Integer.class);
        System.out.println("Row count : " + rowCount);
        int anneAge = jdbcTemplate.queryForObject("SELECT age from authors where first_name= ?", Integer.class, "Anne");
        System.out.println("Anne age: " + anneAge);

        String authorFirstname = jdbcTemplate.queryForObject(
                "SELECT first_name from authors where id = ?",
                new Object[]{2}, String.class);

        System.out.println("First Name: " + authorFirstname);

        Author author = jdbcTemplate.queryForObject(
                "SELECT first_name, last_name, age from authors where id = ?",
                new Object[]{4},
                new RowMapper<Author>() {
                    public Author mapRow(ResultSet rs, int
                            rowNum) throws SQLException {
                        Author author = new Author(rs.getString(COL_FIRST_NAME),
                                rs.getString(COL_LAST_NAME), rs.getInt("age"));
                        return author;
                    }
                });

        List<Author> authorList = jdbcTemplate.query(
                "SELECT first_name, last_name, age from authors",
                new RowMapper<Author>() {
                    public Author mapRow(ResultSet rs, int
                            rowNum) throws SQLException {
                        Author emp = new Author(rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getInt("age"));
                        return emp;
                    }
                });

        String sql = "SELECT a.id, a.first_name, a.last_name, a.age, ar.id as article_id, ar.title, ar.author_id from authors a" +
                " LEFT JOIN articles ar ON ar.author_id = a.id";
        List<Author> authorsWithPosts = jdbcTemplate.query(sql, new ResultSetExtractor<List<Author>>() {
            @Override
            public List<Author> extractData(ResultSet rs) throws SQLException, DataAccessException {
                HashMap<Integer, Author> authors = new HashMap<>();

                while (rs.next()) {
                    int authorId = rs.getInt("id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    int age = rs.getInt("age");
                    int articleId = rs.getInt("article_id");
                    String articleTitle = rs.getString("title");

                    Author a = authors.get(authorId);
                    if (a == null) {
                        a = new Author(authorId, firstName, lastName, age);
                        authors.put(authorId, a);
                    }

                    if (articleId != 0) {
                        Article article = new Article(articleId, articleTitle);
                        a.getArticles().add(article);
                    }
                }

                return new ArrayList<Author>(authors.values());
            }
        });
        authorsWithPosts.forEach(System.out::println);

/*
        Author author1 = jdbcTemplate.queryForObject("SELECT first_name as firstName, last_name as lastName, age" +
                " from authors where id=?", new Object[]{1}, ParameterizedBeanPropertyRowMapper.newInstance(Author.class))
                */
    }

    public void batchSpringData(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.batchUpdate(new String[]{
                "UPDATE emp SET salary = salary * 1.5 where empId = 10101",
                "UPDATE emp SET salary = salary * 1.2 where empId = 10231",
                "UPDATE dept SET location = 'Bangalore' where deptNo = 304"
        });
    }

}
