package melardev.launchers;

import melardev.models.ArticleDummy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class JpaLauncher {

    static List<ArticleDummy> articles = new ArrayList();

    static {
        articles.add(new ArticleDummy("Welcome to JPA writing transaction demo", "You are in a Java data tutorial,"
                + "this one is about JPA, but JDBC will come soon as well"));
        articles.add(new ArticleDummy("Django tutorials", "You are welcome to watch my django tutorials on youtube!"));
        articles.add(new ArticleDummy("Servlet/Jsp", "Have you seen my servlet/jsp tutorials?"));
        articles.add(new ArticleDummy("What about Spring Boot?", "comming soon"));
    }

    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("TutsJpa");
        EntityManager entityManager = factory.createEntityManager();

        writeBasic(entityManager);
        jpqlBasicDemo(entityManager);
        entityManager.close();
        factory.close();
    }

    private static void writeBasic(EntityManager entityManager) {
        entityManager.getTransaction().begin();
        entityManager.persist(articles.get(0));
        entityManager.persist(articles.get(1));
        entityManager.persist(articles.get(2));
        entityManager.persist(articles.get(3));
        entityManager.getTransaction().commit();
    }

    public static void readBasicDemo(EntityManager entityManager) {
        TypedQuery<ArticleDummy> query = entityManager.createQuery("From ArticleDummy", ArticleDummy.class);
        List<ArticleDummy> articleList = query.getResultList();
        for (ArticleDummy article : articleList)
            System.out.println(article);
    }

    public static void updateBasicDemo(EntityManager entityManager) {
        entityManager.getTransaction().begin();

        // find the Entity with the id = 1
        ArticleDummy article = entityManager.find(ArticleDummy.class, 1);
        String oldTitle = article.getTitle();
        System.out.println("Before Updating");
        System.out.println(article);

        // Update that article
        article.setTitle("New Title, why not");
        entityManager.getTransaction().commit();

        //Make sure it worked
        System.out.println("After Updating");

        ArticleDummy articleUpdated = entityManager.find(ArticleDummy.class, 1);
        System.out.println(articleUpdated);

        // Reset its original title
        entityManager.getTransaction().begin();
        article.setTitle(oldTitle);
        entityManager.getTransaction().commit();

        //Make sure it worked
        System.out.println("After resetting");

        ArticleDummy articleReset = entityManager.find(ArticleDummy.class, 1);
        System.out.println(articleReset);
    }

    public static void deleteBasicDemo(EntityManager entityManager) {
        entityManager.getTransaction().begin();
        ArticleDummy article = entityManager.find(ArticleDummy.class, 3);
        entityManager.remove(article);
        entityManager.getTransaction().commit();
    }


    public static void jpqlBasicDemo(EntityManager entityManager) {

        System.out.println("All articles");
        Query sqlAll = entityManager.createQuery("From ArticleDummy"); // Select a From ArticleDummy a
        List<ArticleDummy> articlesAll = (List<ArticleDummy>) sqlAll.getResultList();

        for (ArticleDummy a : articlesAll) {
            System.out.println(a.getTitle());
        }

        // length(), trim(), substring()
        Query query = entityManager.createQuery("Select UPPER(a.title) From ArticleDummy a"); //Select UPPER(a.title) From ArticleDummy as a
        List<String> titles = query.getResultList();

        for (String title : titles) {
            System.out.println("Title: " + title);
        }

        System.out.println("Selecting the Article with id 2");
        Query query2 = entityManager.createQuery("From ArticleDummy Where id=2"); //Select a From ArticleDummy a Where id=2
        ArticleDummy article = (ArticleDummy) query2.getSingleResult();
        System.out.println(article);

        System.out.println("Searching for articles with django keyword in title");
        Query query1 = entityManager.createQuery("Select a From ArticleDummy a Where lower(a.title) Like '%django%'");
        List<ArticleDummy> articlesDjango = (List<ArticleDummy>) query1.getResultList();

        //ArticleDummy articlesDjango =(ArticleDummy)query1.getSingleResult(); works only if returned 1 object, otherwise exception

        for (ArticleDummy a : articlesDjango) {
            System.out.println(a.getBody());
        }

        System.out.println("Getting articles sorted by title");
        Query queryOrder = entityManager.createQuery("Select a From ArticleDummy a ORDER BY a.title ASC");
        List<ArticleDummy> articlesOrdered = (List<ArticleDummy>) queryOrder.getResultList();

        for (ArticleDummy a : articlesOrdered) {
            System.out.println(a.getTitle());
        }

    }

}
