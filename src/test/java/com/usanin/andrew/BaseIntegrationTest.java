package com.usanin.andrew;


import com.google.inject.Inject;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.concurrent.Callable;

public abstract class BaseIntegrationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test.config.yml");

    @ClassRule
    public static final DropwizardAppRule<HedgehogConfiguration> RULE = new DropwizardAppRule<>(
            HedgehogTestApplication.class, CONFIG_PATH);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Inject
    protected SessionFactory sessionFactory;

    protected Session session;

    @BeforeClass
    public static void migrateDb() throws Exception {
        RULE.getApplication().run("db", "migrate", CONFIG_PATH);
    }

    @Before
    public void setUp() throws Exception {
        HedgehogApplication.getGuiceInjector().injectMembers(this);
        session = sessionFactory.openSession();
        ManagedSessionContext.bind(session);
    }

    @After
    public void tearDown() throws Exception {
        session.close();
        ManagedSessionContext.unbind(sessionFactory);
    }

    protected <T> T inTransaction(Callable<T> call) {
        final Transaction transaction = session.beginTransaction();
        try {
            final T result = call.call();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }

    protected void inTransaction(Runnable action) {
        inTransaction(() -> {
            action.run();
            return true;
        });
    }
}
