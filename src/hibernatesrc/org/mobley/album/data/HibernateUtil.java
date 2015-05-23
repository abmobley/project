package org.mobley.album.data;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil
{
   private static final SessionFactory sessionFactory = buildSessionFactory();

   private static SessionFactory buildSessionFactory()
   {
      Configuration cfg = new Configuration().configure();
      ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().
            applySettings(cfg.getProperties()).build();
      SessionFactory sf = cfg.buildSessionFactory(serviceRegistry);
      return sf;
   }
   
   public static Session getCurrentSession()
   {
      return sessionFactory.getCurrentSession();
   }
}
