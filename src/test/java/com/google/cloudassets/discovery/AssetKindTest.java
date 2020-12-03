package com.google.cloudassets.discovery;

import com.google.cloudassets.discovery.exceptions.NoTableConfigException;
import com.google.cloudassets.discovery.exceptions.TooManyTablesConfigException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class AssetKindTest {
    /*
    This function initializes the DB variables in the Main class which are needed in order to
    execute queries.
     */
    @BeforeAll
    private static void initializeDb() {
        Main.initializeDbVars();
    }

    /**
     * This function validates that a TooManyTablesConfigException is thrown when a certain
     * asset has two table names configured in the Asset_Tables_Config table.
     */
    @Test
    public void testTwoAssetTables() {
        String sqlQuery = "SELECT \"App_App_Engine_Assets\" as assetTableName, " +
                        "\"authDomain\" as columnName, \"appengine#app\" as assetKind, " +
                        "\"STRING(MAX)\" as columnType, False as isMainTable, False as isNotNull, " +
                        "False as isPrimaryKey, 0 as primaryKeyIndex, True as toDisplay, False as allowCommitTimestamp " +
                        "UNION ALL " +
                        "SELECT \"App_App_Engine_Assets2\", \"codeBucket\", \"appengine#app\", " +
                        "\"STRING(MAX)\", False, False, False, 0, True, False;";

        try {
            AssetKind.getTableName(sqlQuery);
        } catch (TooManyTablesConfigException exception) {
            return;
        }
        catch (NoTableConfigException exception) {
            fail("Wrong exception was thrown. Expected: TooManyTablesConfigException, Actual: NoTableConfigException");
        }
    }

    /*
    This function closes the DB variables in the Main class.
     */
    @AfterAll
    private static void closeDb() {
        Main.closeDbVars();
    }
}
