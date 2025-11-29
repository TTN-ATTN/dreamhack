package org.apache.catalina.session;

import ch.qos.logback.core.CoreConstants;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Session;
import org.apache.juli.logging.Log;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/session/DataSourceStore.class */
public class DataSourceStore extends JDBCStore {
    @Override // org.apache.catalina.session.JDBCStore, org.apache.catalina.session.StoreBase
    public String[] expiredKeys() throws IOException {
        return keys(true);
    }

    @Override // org.apache.catalina.session.JDBCStore, org.apache.catalina.Store
    public String[] keys() throws IOException {
        return keys(false);
    }

    private String[] keys(boolean expiredOnly) throws IOException {
        PreparedStatement preparedKeysSql;
        String[] keys = null;
        int numberOfTries = 2;
        while (numberOfTries > 0) {
            Connection _conn = getConnection();
            if (_conn == null) {
                return new String[0];
            }
            try {
                try {
                    String keysSql = "SELECT " + this.sessionIdCol + " FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
                    if (expiredOnly) {
                        keysSql = keysSql + " AND (" + this.sessionLastAccessedCol + " + " + this.sessionMaxInactiveCol + " * 1000 < ?)";
                    }
                    preparedKeysSql = _conn.prepareStatement(keysSql);
                } catch (SQLException e) {
                    this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".SQLException", e));
                    keys = new String[0];
                    release(_conn);
                }
                try {
                    preparedKeysSql.setString(1, getName());
                    if (expiredOnly) {
                        preparedKeysSql.setLong(2, System.currentTimeMillis());
                    }
                    ResultSet rst = preparedKeysSql.executeQuery();
                    try {
                        List<String> tmpkeys = new ArrayList<>();
                        if (rst != null) {
                            while (rst.next()) {
                                tmpkeys.add(rst.getString(1));
                            }
                        }
                        keys = (String[]) tmpkeys.toArray(new String[0]);
                        numberOfTries = 0;
                        if (rst != null) {
                            rst.close();
                        }
                        if (preparedKeysSql != null) {
                            preparedKeysSql.close();
                        }
                        release(_conn);
                        numberOfTries--;
                    } finally {
                    }
                } catch (Throwable th) {
                    if (preparedKeysSql != null) {
                        try {
                            preparedKeysSql.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                release(_conn);
                throw th3;
            }
        }
        return keys;
    }

    @Override // org.apache.catalina.session.JDBCStore, org.apache.catalina.Store
    public int getSize() throws IOException {
        PreparedStatement preparedSizeSql;
        int size = 0;
        String sizeSql = "SELECT COUNT(" + this.sessionIdCol + ") FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
        int numberOfTries = 2;
        while (numberOfTries > 0) {
            Connection _conn = getConnection();
            if (_conn == null) {
                return size;
            }
            try {
                try {
                    preparedSizeSql = _conn.prepareStatement(sizeSql);
                } catch (Throwable th) {
                    release(_conn);
                    throw th;
                }
            } catch (SQLException e) {
                this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".SQLException", e));
                release(_conn);
            }
            try {
                preparedSizeSql.setString(1, getName());
                ResultSet rst = preparedSizeSql.executeQuery();
                try {
                    if (rst.next()) {
                        size = rst.getInt(1);
                    }
                    numberOfTries = 0;
                    if (rst != null) {
                        rst.close();
                    }
                    if (preparedSizeSql != null) {
                        preparedSizeSql.close();
                    }
                    release(_conn);
                    numberOfTries--;
                } catch (Throwable th2) {
                    if (rst != null) {
                        try {
                            rst.close();
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                        }
                    }
                    throw th2;
                }
            } catch (Throwable th4) {
                if (preparedSizeSql != null) {
                    try {
                        preparedSizeSql.close();
                    } catch (Throwable th5) {
                        th4.addSuppressed(th5);
                    }
                }
                throw th4;
            }
        }
        return size;
    }

    @Override // org.apache.catalina.session.JDBCStore, org.apache.catalina.Store
    public Session load(String id) throws ClassNotFoundException, IOException {
        PreparedStatement preparedLoadSql;
        ResultSet rst;
        StandardSession _session = null;
        Context context = getManager().getContext();
        Log contextLog = context.getLogger();
        int numberOfTries = 2;
        String loadSql = "SELECT " + this.sessionIdCol + ", " + this.sessionDataCol + " FROM " + this.sessionTable + " WHERE " + this.sessionIdCol + " = ? AND " + this.sessionAppCol + " = ?";
        while (numberOfTries > 0) {
            Connection _conn = getConnection();
            if (_conn == null) {
                return null;
            }
            ClassLoader oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, null);
            try {
                try {
                    preparedLoadSql = _conn.prepareStatement(loadSql);
                    try {
                        preparedLoadSql.setString(1, id);
                        preparedLoadSql.setString(2, getName());
                        rst = preparedLoadSql.executeQuery();
                    } catch (Throwable th) {
                        if (preparedLoadSql != null) {
                            try {
                                preparedLoadSql.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                    release(_conn);
                    throw th3;
                }
            } catch (SQLException e) {
                contextLog.error(sm.getString(getStoreName() + ".SQLException", e));
                context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                release(_conn);
            }
            try {
                if (rst.next()) {
                    ObjectInputStream ois = getObjectInputStream(rst.getBinaryStream(2));
                    try {
                        if (contextLog.isDebugEnabled()) {
                            contextLog.debug(sm.getString(getStoreName() + ".loading", id, this.sessionTable));
                        }
                        _session = (StandardSession) this.manager.createEmptySession();
                        _session.readObjectData(ois);
                        _session.setManager(this.manager);
                        if (ois != null) {
                            ois.close();
                        }
                    } finally {
                    }
                } else if (context.getLogger().isDebugEnabled()) {
                    contextLog.debug(getStoreName() + ": No persisted data object found");
                }
                numberOfTries = 0;
                if (rst != null) {
                    rst.close();
                }
                if (preparedLoadSql != null) {
                    preparedLoadSql.close();
                }
                context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                release(_conn);
                numberOfTries--;
            } catch (Throwable th4) {
                if (rst != null) {
                    try {
                        rst.close();
                    } catch (Throwable th5) {
                        th4.addSuppressed(th5);
                    }
                }
                throw th4;
            }
        }
        return _session;
    }

    @Override // org.apache.catalina.session.JDBCStore, org.apache.catalina.Store
    public void remove(String id) throws IOException {
        int numberOfTries = 2;
        while (numberOfTries > 0) {
            Connection _conn = getConnection();
            if (_conn == null) {
                return;
            }
            try {
                try {
                    remove(id, _conn);
                    numberOfTries = 0;
                    release(_conn);
                } catch (SQLException e) {
                    this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".SQLException", e));
                    release(_conn);
                }
                numberOfTries--;
            } catch (Throwable th) {
                release(_conn);
                throw th;
            }
        }
        if (this.manager.getContext().getLogger().isDebugEnabled()) {
            this.manager.getContext().getLogger().debug(sm.getString(getStoreName() + ".removing", id, this.sessionTable));
        }
    }

    private void remove(String id, Connection _conn) throws SQLException {
        String removeSql = "DELETE FROM " + this.sessionTable + " WHERE " + this.sessionIdCol + " = ?  AND " + this.sessionAppCol + " = ?";
        PreparedStatement preparedRemoveSql = _conn.prepareStatement(removeSql);
        try {
            preparedRemoveSql.setString(1, id);
            preparedRemoveSql.setString(2, getName());
            preparedRemoveSql.execute();
            if (preparedRemoveSql != null) {
                preparedRemoveSql.close();
            }
        } catch (Throwable th) {
            if (preparedRemoveSql != null) {
                try {
                    preparedRemoveSql.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.apache.catalina.session.JDBCStore, org.apache.catalina.Store
    public void clear() throws IOException {
        Connection _conn;
        PreparedStatement preparedClearSql;
        String clearSql = "DELETE FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
        int numberOfTries = 2;
        while (numberOfTries > 0 && (_conn = getConnection()) != null) {
            try {
                try {
                    preparedClearSql = _conn.prepareStatement(clearSql);
                } catch (SQLException e) {
                    this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".SQLException", e));
                    release(_conn);
                }
                try {
                    preparedClearSql.setString(1, getName());
                    preparedClearSql.execute();
                    numberOfTries = 0;
                    if (preparedClearSql != null) {
                        preparedClearSql.close();
                    }
                    release(_conn);
                    numberOfTries--;
                } catch (Throwable th) {
                    if (preparedClearSql != null) {
                        try {
                            preparedClearSql.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                release(_conn);
                throw th3;
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.session.JDBCStore, org.apache.catalina.Store
    public void save(Session session) throws IOException {
        String saveSql = "INSERT INTO " + this.sessionTable + " (" + this.sessionIdCol + ", " + this.sessionAppCol + ", " + this.sessionDataCol + ", " + this.sessionValidCol + ", " + this.sessionMaxInactiveCol + ", " + this.sessionLastAccessedCol + ") VALUES (?, ?, ?, ?, ?, ?)";
        synchronized (session) {
            int numberOfTries = 2;
            while (numberOfTries > 0) {
                Connection _conn = getConnection();
                if (_conn == null) {
                    return;
                }
                try {
                    try {
                        remove(session.getIdInternal(), _conn);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));
                        try {
                            ((StandardSession) session).writeObjectData(oos);
                            oos.close();
                            byte[] obs = bos.toByteArray();
                            int size = obs.length;
                            ByteArrayInputStream bis = new ByteArrayInputStream(obs, 0, size);
                            try {
                                InputStream in = new BufferedInputStream(bis, size);
                                try {
                                    PreparedStatement preparedSaveSql = _conn.prepareStatement(saveSql);
                                    try {
                                        preparedSaveSql.setString(1, session.getIdInternal());
                                        preparedSaveSql.setString(2, getName());
                                        preparedSaveSql.setBinaryStream(3, in, size);
                                        preparedSaveSql.setString(4, session.isValid() ? CustomBooleanEditor.VALUE_1 : CustomBooleanEditor.VALUE_0);
                                        preparedSaveSql.setInt(5, session.getMaxInactiveInterval());
                                        preparedSaveSql.setLong(6, session.getLastAccessedTime());
                                        preparedSaveSql.execute();
                                        numberOfTries = 0;
                                        if (preparedSaveSql != null) {
                                            preparedSaveSql.close();
                                        }
                                        in.close();
                                        bis.close();
                                        release(_conn);
                                    } catch (Throwable th) {
                                        if (preparedSaveSql != null) {
                                            try {
                                                preparedSaveSql.close();
                                            } catch (Throwable th2) {
                                                th.addSuppressed(th2);
                                            }
                                        }
                                        throw th;
                                    }
                                } catch (Throwable th3) {
                                    try {
                                        in.close();
                                    } catch (Throwable th4) {
                                        th3.addSuppressed(th4);
                                    }
                                    throw th3;
                                }
                            } catch (Throwable th5) {
                                try {
                                    bis.close();
                                } catch (Throwable th6) {
                                    th5.addSuppressed(th6);
                                }
                                throw th5;
                            }
                        } catch (Throwable th7) {
                            try {
                                oos.close();
                            } catch (Throwable th8) {
                                th7.addSuppressed(th8);
                            }
                            throw th7;
                        }
                    } catch (Throwable th9) {
                        release(_conn);
                        throw th9;
                    }
                } catch (IOException e) {
                    release(_conn);
                } catch (SQLException e2) {
                    this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".SQLException", e2));
                    release(_conn);
                }
                numberOfTries--;
            }
            if (this.manager.getContext().getLogger().isDebugEnabled()) {
                this.manager.getContext().getLogger().debug(sm.getString(getStoreName() + ".saving", session.getIdInternal(), this.sessionTable));
            }
        }
    }

    @Override // org.apache.catalina.session.JDBCStore
    protected Connection open() throws SQLException {
        if (this.dataSourceName != null && this.dataSource == null) {
            Context context = getManager().getContext();
            ClassLoader oldThreadContextCL = null;
            if (getLocalDataSource()) {
                oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, null);
            }
            try {
                try {
                    javax.naming.Context envCtx = (javax.naming.Context) new InitialContext().lookup(CoreConstants.JNDI_COMP_PREFIX);
                    this.dataSource = (DataSource) envCtx.lookup(this.dataSourceName);
                    if (getLocalDataSource()) {
                        context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                    }
                } catch (NamingException e) {
                    context.getLogger().error(sm.getString(getStoreName() + ".wrongDataSource", this.dataSourceName), e);
                    if (getLocalDataSource()) {
                        context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                    }
                }
            } catch (Throwable th) {
                if (getLocalDataSource()) {
                    context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                }
                throw th;
            }
        }
        if (this.dataSource != null) {
            return this.dataSource.getConnection();
        }
        throw new IllegalStateException(sm.getString(getStoreName() + ".missingDataSource"));
    }

    @Override // org.apache.catalina.session.JDBCStore
    protected void close(Connection dbConnection) throws SQLException {
        if (dbConnection == null) {
            return;
        }
        try {
            if (!dbConnection.getAutoCommit()) {
                dbConnection.commit();
            }
        } catch (SQLException e) {
            this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".commitSQLException"), e);
        }
        try {
            dbConnection.close();
        } catch (SQLException e2) {
            this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".close", e2.toString()));
        }
    }
}
