package db.migration.java;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class V4__JavaMigration extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(
                new SingleConnectionDataSource(context.getConnection(), true));
        
        // Example: Update descriptions of all active products using Java logic
        jdbcTemplate.update("UPDATE products SET description = 'Updated Description by Java Migration' WHERE status = 'ACTIVE'");
    }
}
