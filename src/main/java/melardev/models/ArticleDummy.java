package melardev.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "articles_dummy")
@NamedQuery(name = "ArticleDummy.getAll", query = "Select a FROM ArticleDummy a")
@NamedQueries({
        @NamedQuery(name = "ArticleDummy.getByTitleContains", query = "Select a From ArticleDummy a Where a.title like :title"),
        @NamedQuery(name = "ArticleDummy.getById", query = "Select a From ArticleDummy a Where a.id = :id")
})
public class ArticleDummy {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String title;
    private String body;
    private Date createdAt;

    public ArticleDummy() {
    }

    public ArticleDummy(String title, String body) {

        this.title = title;
        this.body = body;
        createdAt = new Date();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Article %d, created at %s\n%s\n%s\n", id, createdAt, title, body);
    }
}
