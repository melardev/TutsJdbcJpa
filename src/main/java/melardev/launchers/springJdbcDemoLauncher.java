package melardev.launchers;

import melardev.config.ConfigJdbc;
import melardev.dao.ArticleDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class springJdbcDemoLauncher {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigJdbc.class);
        ArticleDao dao = ctx.getBean(ArticleDao.class);
        dao.create();
        dao.insertUpdateDelete();
        dao.get();
    }
}
