package org.env;

import bitronix.tm.resource.jdbc.PoolingDataSource;

import java.util.Properties;

public enum DatabaseProduct {

    H2(
            new DataSourceConfiguration() {
                @Override
                public void configure(PoolingDataSource ds, String connectionURL) {
                    ds.setClassName("org.h2.jdbcx.JdbcDataSource");

                    // External instance: jdbc:h2:tcp://localhost/mem:test;USER=sa
                    ds.getDriverProperties().put(
                            "URL",
                            connectionURL != null
                                    ? connectionURL :
                                    "jdbc:h2:mem:test"
                    );

                    // TODO: http://code.google.com/p/h2database/issues/detail?id=502
                    ds.getDriverProperties().put("user", "sa");

                    // TODO: Don't trace log values larger than X bytes (especially useful for
                    // debugging LOBs, which are accessed in toString()!)
                    // System.setProperty("h2.maxTraceDataLength", "256"); 256 bytes, default is 64 kilobytes
                }
            },
            org.shared.ImprovedH2Dialect.class.getName()
    ),

    ORACLE(
            new DataSourceConfiguration() {
                @Override
                public void configure(PoolingDataSource ds, String connectionURL) {
                    ds.setClassName("oracle.jdbc.xa.client.OracleXADataSource");
                    ds.getDriverProperties().put(
                            "URL",
                            connectionURL != null
                                    ? connectionURL :
                                    "jdbc:oracle:thin:test/test@127.0.0.1:1521:xe"
                    );
                    Properties connectionProperties = new Properties();
                    connectionProperties.put("useFetchSizeWithLongColumn", "true");
                    ds.getDriverProperties().put("connectionProperties", connectionProperties);
                }
            },
            org.hibernate.dialect.Oracle10gDialect.class.getName()
    ),

    POSTGRESQL(
            new DataSourceConfiguration() {
                @Override
                public void configure(PoolingDataSource ds, String connectionURL) {
                    ds.setClassName("org.postgresql.xa.PGXADataSource");
                    if (connectionURL != null) {
                        throw new IllegalArgumentException(
                                "PostgreSQL XADataSource doesn't support connection URLs"
                        );
                    }
                    ds.getDriverProperties().put("serverName", "10.0.0.2");
                    ds.getDriverProperties().put("databaseName", "test");
                    ds.getDriverProperties().put("user", "test");
                    ds.getDriverProperties().put("password", "test");
                }
            },
            org.hibernate.dialect.PostgreSQL82Dialect.class.getName()
    ),

    MYSQL(
            new DataSourceConfiguration() {
                @Override
                public void configure(PoolingDataSource ds, String connectionURL) {
                    // TODO: MySQL XA support is completely broken, we use the BTM XA wrapper
                    //ds.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
//                    ds.setClassName("com.mysql.cj.jdbc.MysqlXADataSource");
                    ds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
                    ds.getDriverProperties().put(
                            "url",
                            connectionURL != null
                                    ? connectionURL :
                                    "jdbc:mysql://127.0.0.1:3306/hibernate_action"
                    );

                    ds.getDriverProperties().put("driverClassName", "com.mysql.cj.jdbc.Driver");
                }
            },
            org.hibernate.dialect.MySQL8Dialect.class.getName()
    );

    public DataSourceConfiguration configuration;
    public String hibernateDialect;

    private DatabaseProduct(DataSourceConfiguration configuration, String hibernateDialect) {
        this.configuration = configuration;
        this.hibernateDialect = hibernateDialect;
    }

    public interface DataSourceConfiguration {

        void configure(PoolingDataSource ds, String connectionURL);
    }

}
