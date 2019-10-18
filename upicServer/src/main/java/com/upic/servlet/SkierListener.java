package com.upic.servlet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;

@WebListener()
public class SkierListener implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {

  private static final String PERSISTENCE_UNIT_NAME = "skiers";
  // Public constructor is required by servlet spec
  public SkierListener() {
  }

  // -------------------------------------------------------
  // ServletContextListener implementation
  // -------------------------------------------------------
  public void contextInitialized(ServletContextEvent sce) {
      /* This method is called when the servlet context is
         initialized(when the Web application is deployed). 
         You can initialize servlet context related data here.
      */

    EntityManagerFactory factory= Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    EntityManager emf = factory.createEntityManager();
    sce.getServletContext().setAttribute("emf", emf);
  }

  public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context 
         (the Web application) is undeployed or 
         Application Server shuts down.
      */
    EntityManagerFactory emf =
            (EntityManagerFactory)sce.getServletContext().getAttribute("emf");
    emf.close();
  }

  // -------------------------------------------------------
  // HttpSessionListener implementation
  // -------------------------------------------------------
  public void sessionCreated(HttpSessionEvent se) {
    /* Session is created. */
  }

  public void sessionDestroyed(HttpSessionEvent se) {
    /* Session is destroyed. */
  }

  // -------------------------------------------------------
  // HttpSessionAttributeListener implementation
  // -------------------------------------------------------

  public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute 
         is added to a session.
      */
  }

  public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
  }

  public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
  }
}
