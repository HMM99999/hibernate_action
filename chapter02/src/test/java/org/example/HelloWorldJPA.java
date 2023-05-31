package org.example;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HelloWorldJPA {

    public void test01() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HelloWorldPU");


    }

}
