package melardev.models;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Author {
    private long id;
    private String firstName;
    private String lastName;
    private int age;
    private List<Article> articles;

    public Author(String firstName, String lastName, int age) {
        this(-1, firstName, lastName, age);
    }

    public Author(long id, String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFirstName(String first_name) {
        this.firstName = first_name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Article> getArticles() {
        if (articles == null)
            articles = new ArrayList<>();
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstName + " " + lastName + " " + age + "\n");
        for (Article article : getArticles())
            sb.append(article.toString());
        return sb.toString();

    }
}
