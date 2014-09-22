
/**
 * <p>RowHasBogusDataReplacement class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
package org.opennms.netmgt.dao.db.columnchanges;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.opennms.netmgt.dao.db.ColumnChange;
import org.opennms.netmgt.dao.db.ColumnChangeReplacement;
public class RowHasBogusDataReplacement implements ColumnChangeReplacement {
    private final String m_table;
    private final String m_column;
    
    /**
     * <p>Constructor for RowHasBogusDataReplacement.</p>
     *
     * @param table a {@link java.lang.String} object.
     * @param column a {@link java.lang.String} object.
     */
    public RowHasBogusDataReplacement(String table, String column) {
        m_table = table;
        m_column = column;
    }
    
    /** {@inheritDoc} */
    public Object getColumnReplacement(ResultSet rs, Map<String, ColumnChange> columnChanges) throws SQLException {
        throw new IllegalArgumentException("The '" + m_column
                                           + "' column in the '"
                                           + m_table
                                           + "' table should never be "
                                           + "null, but the entry for this "
                                           + "row does have a null '"
                                           + m_column + "' column.  "
                                           + "It needs to be "
                                           + "removed or udpated to "
                                           + "reflect a valid '"
                                           + m_column + "' value.");
    }

    /**
     * <p>addColumnIfColumnIsNew</p>
     *
     * @return a boolean.
     */
    public boolean addColumnIfColumnIsNew() {
        return true;
    }
    
    /**
     * <p>close</p>
     */
    public void close() {
    }
}