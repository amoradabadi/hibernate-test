package com.example.demo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import static java.util.logging.Logger.getLogger;

public class Application {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        var registry = new StandardServiceRegistryBuilder().configure().build();
        var sources = new MetadataSources(registry);
        var metadata = sources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    public static void main(String[] args) throws InterruptedException {
        getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);

        sessionFactory = getSessionFactory();

        var data1 = new Data(null, null);
        var data2 = new Data(null, null);

        insert(data1);
        insert(data2);
        findUnexpired();

        var thread1 = new Thread(() -> update(new Data(1, System.currentTimeMillis())), "thread 1");
        var thread2 = new Thread(() -> {
            findUnexpired();

            try (var session = sessionFactory.openSession()) {
                var transaction = session.beginTransaction();

                find(session, 2);

                session.merge(new Data(2, System.currentTimeMillis()));
                transaction.commit();

                find(session, 2);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, "thread 2");

        thread1.start();
        Thread.sleep(1000);
        thread2.start();

    }

    private static void find(Session session, int id) {
        var query = session.createQuery("from Data where id = :id", Data.class);
        query.setParameter("id", id);
        System.out.println(Thread.currentThread().getName() + ": [find]   " + query.uniqueResult());
    }

    private static void insert(Data data) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            session.persist(data);
            transaction.commit();
            System.out.println(Thread.currentThread().getName() + ": [insert] " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void update(Data data) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            session.merge(data);
            transaction.commit();
            System.out.println(Thread.currentThread().getName() + ": [update] " + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void findUnexpired() {
        try (var session = sessionFactory.openSession()) {
            var query = session.createQuery("from Data where expiredAt is null ", Data.class);
            query.list().forEach(d -> System.out.println(Thread.currentThread().getName() + ": [find unexpired] " + d));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
