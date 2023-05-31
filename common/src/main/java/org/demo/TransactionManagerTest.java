package org.demo;

import org.env.DatabaseProduct;
import org.env.TransactionManagerSetup;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.Locale;

public class TransactionManagerTest {
    // Static single database connection manager per test suite
    static public TransactionManagerSetup TM;

    @Parameters({"database", "connectionURL"})
    @BeforeSuite()
    public void beforeSuite(@Optional String database, @Optional String connectionURL) throws Exception {
        TM = new TransactionManagerSetup(
                database != null
                        ? DatabaseProduct.valueOf(database.toUpperCase(Locale.CHINA))
                        : DatabaseProduct.MYSQL,
                connectionURL
        );
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() throws Exception {
        if (TM != null)
            TM.stop();
    }
}
