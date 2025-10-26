package com.eq.eqefcore;

import com.easy.query.api.proxy.client.DefaultEasyEntityQuery;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.api.client.EasyQueryClient;
import com.easy.query.core.basic.api.database.CodeFirstCommand;
import com.easy.query.core.basic.api.database.DatabaseCodeFirst;
import com.easy.query.core.bootstrapper.EasyQueryBootstrapper;
import com.easy.query.core.enums.EasyBehaviorEnum;
import com.easy.query.core.logging.LogFactory;
import com.easy.query.core.proxy.core.draft.Draft2;
import com.easy.query.core.proxy.sql.Select;
import com.easy.query.mysql.config.MySQLDatabaseConfiguration;
import com.eq.eqefcore.domain.Category;
import com.eq.eqefcore.domain.Comment;
import com.eq.eqefcore.domain.Post;
import com.eq.eqefcore.domain.User;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static EasyEntityQuery entityQuery;

    public static void main(String[] args) {

        LogFactory.useStdOutLogging();
        DataSource dataSource = getDataSource();
        EasyQueryClient easyQueryClient = EasyQueryBootstrapper.defaultBuilderConfiguration()
                .setDefaultDataSource(dataSource)
                .useDatabaseConfigure(new MySQLDatabaseConfiguration())
                .build();
        entityQuery = new DefaultEasyEntityQuery(easyQueryClient);
//        initData();
        for (int i = 0; i < 10; i++) {
            querySubQuery();
        }
        for (int i = 0; i < 10; i++) {
            queryGroupJoin();
        }
    }

    private static void querySubQuery(){
        long begin = System.currentTimeMillis();
        LocalDateTime dateTime = LocalDateTime.now().plusDays(-7);
        List<User> list = entityQuery.queryable(User.class)
//                .configure(s->s.getBehavior().add(EasyBehaviorEnum.ALL_SUB_QUERY_GROUP_JOIN))
                .where(u -> {
                    u.comments().any();
                })
                .orderBy(u -> {
                    u.comments().where(c -> {
                        c.createAt().isAfter(dateTime);
                        c.post().category().name().eq(".NET");
                    }).count().desc();
                })
                .limit(5).toList();

        long end = System.currentTimeMillis();
        System.out.println("SubQuery查询耗时：" + (end - begin) + "ms");
    }
    private static void queryGroupJoin(){
        long begin = System.currentTimeMillis();
        LocalDateTime dateTime = LocalDateTime.now().plusDays(-7);
        List<User> list = entityQuery.queryable(User.class)
                .configure(s->s.getBehavior().add(EasyBehaviorEnum.ALL_SUB_QUERY_GROUP_JOIN))
                .where(u -> {
                    u.comments().any();
                })
                .orderBy(u -> {
                    u.comments().where(c -> {
                        c.createAt().isAfter(dateTime);
                        c.post().category().name().eq(".NET");
                    }).count().desc();
                })
                .limit(5).toList();

        long end = System.currentTimeMillis();
        System.out.println("GroupJoin查询耗时：" + (end - begin) + "ms");
    }


    private static void initData() {

        DatabaseCodeFirst databaseCodeFirst = entityQuery.getDatabaseCodeFirst();
        databaseCodeFirst.createDatabaseIfNotExists();
        CodeFirstCommand codeFirstCommand1 = databaseCodeFirst.dropTableIfExistsCommand(Arrays.asList(User.class, Post.class, Category.class, Comment.class));
        codeFirstCommand1.executeWithTransaction(s->s.commit());
        CodeFirstCommand codeFirstCommand = databaseCodeFirst.syncTableCommand(Arrays.asList(User.class, Post.class, Category.class, Comment.class));
        codeFirstCommand.executeWithTransaction(s -> {
            System.out.println(s.getSQL());
            s.commit();
        });
        String[] categories = {
                ".NET", "Java", "Python", "PHP", "Go", "Ruby", "Swift", "Kotlin", "JavaScript"
        };
        String[] users = {
                "张三", "李四", "王五", "赵六", "孙七", "周八", "吴九", "郑十", "小王", "小李", "小张"
                , "小赵", "小孙", "小周", "小吴", "小郑"
        };
        // === 检查并插入分类 ===
        boolean hasCategories = entityQuery.queryable(Category.class).any();
        List<Category> insertCategories = new ArrayList<>();
        if (!hasCategories) {
            for (String item : categories) {
                Category category = new Category();
                category.setId(UUID.randomUUID().toString());
                category.setName(item);
                insertCategories.add(category);
            }
            entityQuery.insertable(insertCategories).executeRows();
        } else {
            insertCategories = entityQuery.queryable(Category.class).toList();
        }

        // === 检查并插入用户 ===
        boolean hasUsers = entityQuery.queryable(User.class).any();
        List<User> insertUsers = new ArrayList<>();
        if (!hasUsers) {
            for (String item : users) {
                User user = new User();
                user.setId(UUID.randomUUID().toString());
                user.setUsername(item);
                insertUsers.add(user);
            }
            entityQuery.insertable(insertUsers).executeRows();
        } else {
            insertUsers = entityQuery.queryable(User.class).toList();
        }

        // === 生成帖子 ===
        int postCount = 150_000;
        List<Post> posts = new ArrayList<>(postCount);
        Random random = new Random();

        for (int i = 0; i < postCount; i++) {
            Post post = new Post();
            post.setId(UUID.randomUUID().toString());
            post.setContent("这是第 " + (i + 1) + " 篇帖子，主题是关于 " + categories[random.nextInt(categories.length)]);
            post.setUserId(insertUsers.get(random.nextInt(insertUsers.size())).getId());
            post.setCategoryId(insertCategories.get(random.nextInt(insertCategories.size())).getId());
            posts.add(post);
        }


        // === 生成评论 ===
        int commentCount = 1000_000;
        LocalDateTime now = LocalDateTime.now();
        List<Comment> comments = new ArrayList<>(commentCount);
        for (int i = 0; i < commentCount; i++) {
            Comment comment = new Comment();
            comment.setId(UUID.randomUUID().toString());
            comment.setPostId(posts.get(random.nextInt(posts.size())).getId());
            comment.setUserId(insertUsers.get(random.nextInt(insertUsers.size())).getId());
            comment.setText("评论内容 " + (i + 1));

            // 生成近30天内随机时间（包含随机天数、小时、分钟、秒）
            long randomSeconds = random.nextInt(30 * 24 * 3600); // 30天内的随机秒数
            comment.setCreateAt(now.minusSeconds(randomSeconds));

            comments.add(comment);
        }


        System.out.println("✅ 数据初始化完成：用户=" + insertUsers.size() + " 分类=" + insertCategories.size() +
                " 帖子=" + posts.size() + " 评论=" + comments.size());

        entityQuery.insertable(posts).batch().executeRows();
        entityQuery.insertable(comments).batch().executeRows();

    }

    /**
     * 初始化数据源
     *
     * @return
     */
    private static DataSource getDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3316/eq_ef_db?serverTimezone=GMT%2B8&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setMaximumPoolSize(20);

        return dataSource;
    }
}