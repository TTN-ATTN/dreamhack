package org.apache.catalina.session;

import ch.qos.logback.classic.ClassicConstants;
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
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Session;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/session/JDBCStore.class */
public class JDBCStore extends StoreBase {
    protected static final String storeName = "JDBCStore";
    protected static final String threadName = "JDBCStore";
    private String name = null;
    protected String connectionName = null;
    protected String connectionPassword = null;
    protected String connectionURL = null;
    private Connection dbConnection = null;
    protected Driver driver = null;
    protected String driverName = null;
    protected String dataSourceName = null;
    private boolean localDataSource = false;
    protected DataSource dataSource = null;
    protected String sessionTable = "tomcat$sessions";
    protected String sessionAppCol = "app";
    protected String sessionIdCol = "id";
    protected String sessionDataCol = "data";
    protected String sessionValidCol = "valid";
    protected String sessionMaxInactiveCol = "maxinactive";
    protected String sessionLastAccessedCol = "lastaccess";
    protected PreparedStatement preparedSizeSql = null;
    protected PreparedStatement preparedSaveSql = null;
    protected PreparedStatement preparedClearSql = null;
    protected PreparedStatement preparedRemoveSql = null;
    protected PreparedStatement preparedLoadSql = null;

    public String getName() {
        if (this.name == null) {
            Container container = this.manager.getContext();
            String contextName = container.getName();
            if (!contextName.startsWith("/")) {
                contextName = "/" + contextName;
            }
            String hostName = "";
            String engineName = "";
            if (container.getParent() != null) {
                Container host = container.getParent();
                hostName = host.getName();
                if (host.getParent() != null) {
                    engineName = host.getParent().getName();
                }
            }
            this.name = "/" + engineName + "/" + hostName + contextName;
        }
        return this.name;
    }

    public String getThreadName() {
        return "JDBCStore";
    }

    @Override // org.apache.catalina.session.StoreBase
    public String getStoreName() {
        return "JDBCStore";
    }

    public void setDriverName(String driverName) {
        String oldDriverName = this.driverName;
        this.driverName = driverName;
        this.support.firePropertyChange("driverName", oldDriverName, this.driverName);
        this.driverName = driverName;
    }

    public String getDriverName() {
        return this.driverName;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionPassword() {
        return this.connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public void setConnectionURL(String connectionURL) {
        String oldConnString = this.connectionURL;
        this.connectionURL = connectionURL;
        this.support.firePropertyChange("connectionURL", oldConnString, this.connectionURL);
    }

    public String getConnectionURL() {
        return this.connectionURL;
    }

    public void setSessionTable(String sessionTable) {
        String oldSessionTable = this.sessionTable;
        this.sessionTable = sessionTable;
        this.support.firePropertyChange("sessionTable", oldSessionTable, this.sessionTable);
    }

    public String getSessionTable() {
        return this.sessionTable;
    }

    public void setSessionAppCol(String sessionAppCol) {
        String oldSessionAppCol = this.sessionAppCol;
        this.sessionAppCol = sessionAppCol;
        this.support.firePropertyChange("sessionAppCol", oldSessionAppCol, this.sessionAppCol);
    }

    public String getSessionAppCol() {
        return this.sessionAppCol;
    }

    public void setSessionIdCol(String sessionIdCol) {
        String oldSessionIdCol = this.sessionIdCol;
        this.sessionIdCol = sessionIdCol;
        this.support.firePropertyChange("sessionIdCol", oldSessionIdCol, this.sessionIdCol);
    }

    public String getSessionIdCol() {
        return this.sessionIdCol;
    }

    public void setSessionDataCol(String sessionDataCol) {
        String oldSessionDataCol = this.sessionDataCol;
        this.sessionDataCol = sessionDataCol;
        this.support.firePropertyChange("sessionDataCol", oldSessionDataCol, this.sessionDataCol);
    }

    public String getSessionDataCol() {
        return this.sessionDataCol;
    }

    public void setSessionValidCol(String sessionValidCol) {
        String oldSessionValidCol = this.sessionValidCol;
        this.sessionValidCol = sessionValidCol;
        this.support.firePropertyChange("sessionValidCol", oldSessionValidCol, this.sessionValidCol);
    }

    public String getSessionValidCol() {
        return this.sessionValidCol;
    }

    public void setSessionMaxInactiveCol(String sessionMaxInactiveCol) {
        String oldSessionMaxInactiveCol = this.sessionMaxInactiveCol;
        this.sessionMaxInactiveCol = sessionMaxInactiveCol;
        this.support.firePropertyChange("sessionMaxInactiveCol", oldSessionMaxInactiveCol, this.sessionMaxInactiveCol);
    }

    public String getSessionMaxInactiveCol() {
        return this.sessionMaxInactiveCol;
    }

    public void setSessionLastAccessedCol(String sessionLastAccessedCol) {
        String oldSessionLastAccessedCol = this.sessionLastAccessedCol;
        this.sessionLastAccessedCol = sessionLastAccessedCol;
        this.support.firePropertyChange("sessionLastAccessedCol", oldSessionLastAccessedCol, this.sessionLastAccessedCol);
    }

    public String getSessionLastAccessedCol() {
        return this.sessionLastAccessedCol;
    }

    public void setDataSourceName(String dataSourceName) {
        if (dataSourceName == null || dataSourceName.trim().isEmpty()) {
            this.manager.getContext().getLogger().warn(sm.getString(getStoreName() + ".missingDataSourceName"));
        } else {
            this.dataSourceName = dataSourceName;
        }
    }

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public boolean getLocalDataSource() {
        return this.localDataSource;
    }

    public void setLocalDataSource(boolean localDataSource) {
        this.localDataSource = localDataSource;
    }

    @Override // org.apache.catalina.session.StoreBase
    public String[] expiredKeys() throws IOException {
        return keys(true);
    }

    public String[] keys() throws IOException {
        return keys(false);
    }

    /* JADX WARN: Finally extract failed */
    private String[] keys(boolean expiredOnly) throws IOException {
        PreparedStatement preparedKeysSql;
        ResultSet rst;
        String[] keys = null;
        synchronized (this) {
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
                        try {
                            preparedKeysSql.setString(1, getName());
                            if (expiredOnly) {
                                preparedKeysSql.setLong(2, System.currentTimeMillis());
                            }
                            rst = preparedKeysSql.executeQuery();
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
                    } catch (SQLException e) {
                        this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".SQLException", e));
                        keys = new String[0];
                        if (this.dbConnection != null) {
                            close(this.dbConnection);
                        }
                        release(_conn);
                    }
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
                    } catch (Throwable th3) {
                        if (rst != null) {
                            try {
                                rst.close();
                            } catch (Throwable th4) {
                                th3.addSuppressed(th4);
                            }
                        }
                        throw th3;
                    }
                } catch (Throwable th5) {
                    release(_conn);
                    throw th5;
                }
            }
            return keys;
        }
    }

    /* JADX WARN: Finally extract failed */
    public int getSize() throws IOException {
        int size = 0;
        synchronized (this) {
            int numberOfTries = 2;
            while (numberOfTries > 0) {
                Connection _conn = getConnection();
                if (_conn == null) {
                    return size;
                }
                try {
                    try {
                        if (this.preparedSizeSql == null) {
                            String sizeSql = "SELECT COUNT(" + this.sessionIdCol + ") FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
                            this.preparedSizeSql = _conn.prepareStatement(sizeSql);
                        }
                        this.preparedSizeSql.setString(1, getName());
                        ResultSet rst = this.preparedSizeSql.executeQuery();
                        try {
                            if (rst.next()) {
                                size = rst.getInt(1);
                            }
                            numberOfTries = 0;
                            if (rst != null) {
                                rst.close();
                            }
                            release(_conn);
                        } catch (Throwable th) {
                            if (rst != null) {
                                try {
                                    rst.close();
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
                } catch (SQLException e) {
                    this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".SQLException", e));
                    if (this.dbConnection != null) {
                        close(this.dbConnection);
                    }
                    release(_conn);
                }
                numberOfTries--;
            }
            return size;
        }
    }

    /* JADX WARN: Finally extract failed */
    public Session load(String id) throws ClassNotFoundException, IOException {
        StandardSession _session = null;
        Context context = getManager().getContext();
        Log contextLog = context.getLogger();
        synchronized (this) {
            int numberOfTries = 2;
            while (numberOfTries > 0) {
                Connection _conn = getConnection();
                if (_conn == null) {
                    return null;
                }
                ClassLoader oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, null);
                try {
                    try {
                        if (this.preparedLoadSql == null) {
                            String loadSql = "SELECT " + this.sessionIdCol + ", " + this.sessionDataCol + " FROM " + this.sessionTable + " WHERE " + this.sessionIdCol + " = ? AND " + this.sessionAppCol + " = ?";
                            this.preparedLoadSql = _conn.prepareStatement(loadSql);
                        }
                        this.preparedLoadSql.setString(1, id);
                        this.preparedLoadSql.setString(2, getName());
                        ResultSet rst = this.preparedLoadSql.executeQuery();
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
                                } catch (Throwable th) {
                                    if (ois != null) {
                                        try {
                                            ois.close();
                                        } catch (Throwable th2) {
                                            th.addSuppressed(th2);
                                        }
                                    }
                                    throw th;
                                }
                            } else if (context.getLogger().isDebugEnabled()) {
                                contextLog.debug(getStoreName() + ": No persisted data object found");
                            }
                            numberOfTries = 0;
                            if (rst != null) {
                                rst.close();
                            }
                            context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                            release(_conn);
                        } catch (Throwable th3) {
                            if (rst != null) {
                                try {
                                    rst.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            }
                            throw th3;
                        }
                    } catch (Throwable th5) {
                        context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                        release(_conn);
                        throw th5;
                    }
                } catch (SQLException e) {
                    contextLog.error(sm.getString(getStoreName() + ".SQLException", e));
                    if (this.dbConnection != null) {
                        close(this.dbConnection);
                    }
                    context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                    release(_conn);
                }
                numberOfTries--;
            }
            return _session;
        }
    }

    /* JADX WARN: Finally extract failed */
    public void remove(String id) throws IOException {
        synchronized (this) {
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
                    } catch (Throwable th) {
                        release(_conn);
                        throw th;
                    }
                } catch (SQLException e) {
                    this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".SQLException", e));
                    if (this.dbConnection != null) {
                        close(this.dbConnection);
                    }
                    release(_conn);
                }
                numberOfTries--;
            }
            if (this.manager.getContext().getLogger().isDebugEnabled()) {
                this.manager.getContext().getLogger().debug(sm.getString(getStoreName() + ".removing", id, this.sessionTable));
            }
        }
    }

    private void remove(String id, Connection _conn) throws SQLException {
        if (this.preparedRemoveSql == null) {
            String removeSql = "DELETE FROM " + this.sessionTable + " WHERE " + this.sessionIdCol + " = ?  AND " + this.sessionAppCol + " = ?";
            this.preparedRemoveSql = _conn.prepareStatement(removeSql);
        }
        this.preparedRemoveSql.setString(1, id);
        this.preparedRemoveSql.setString(2, getName());
        this.preparedRemoveSql.execute();
    }

    /* JADX WARN: Finally extract failed */
    public void clear() throws IOException {
        synchronized (this) {
            int numberOfTries = 2;
            while (numberOfTries > 0) {
                Connection _conn = getConnection();
                if (_conn == null) {
                    return;
                }
                try {
                    try {
                        if (this.preparedClearSql == null) {
                            String clearSql = "DELETE FROM " + this.sessionTable + " WHERE " + this.sessionAppCol + " = ?";
                            this.preparedClearSql = _conn.prepareStatement(clearSql);
                        }
                        this.preparedClearSql.setString(1, getName());
                        this.preparedClearSql.execute();
                        numberOfTries = 0;
                        release(_conn);
                    } catch (Throwable th) {
                        release(_conn);
                        throw th;
                    }
                } catch (SQLException e) {
                    this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".SQLException", e));
                    if (this.dbConnection != null) {
                        close(this.dbConnection);
                    }
                    release(_conn);
                }
                numberOfTries--;
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    public void save(Session session) throws IOException {
        synchronized (this) {
            int numberOfTries = 2;
            while (numberOfTries > 0) {
                Connection _conn = getConnection();
                if (_conn == null) {
                    return;
                }
                try {
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
                                        if (this.preparedSaveSql == null) {
                                            String saveSql = "INSERT INTO " + this.sessionTable + " (" + this.sessionIdCol + ", " + this.sessionAppCol + ", " + this.sessionDataCol + ", " + this.sessionValidCol + ", " + this.sessionMaxInactiveCol + ", " + this.sessionLastAccessedCol + ") VALUES (?, ?, ?, ?, ?, ?)";
                                            this.preparedSaveSql = _conn.prepareStatement(saveSql);
                                        }
                                        this.preparedSaveSql.setString(1, session.getIdInternal());
                                        this.preparedSaveSql.setString(2, getName());
                                        this.preparedSaveSql.setBinaryStream(3, in, size);
                                        this.preparedSaveSql.setString(4, session.isValid() ? CustomBooleanEditor.VALUE_1 : CustomBooleanEditor.VALUE_0);
                                        this.preparedSaveSql.setInt(5, session.getMaxInactiveInterval());
                                        this.preparedSaveSql.setLong(6, session.getLastAccessedTime());
                                        this.preparedSaveSql.execute();
                                        numberOfTries = 0;
                                        in.close();
                                        bis.close();
                                        release(_conn);
                                    } catch (Throwable th) {
                                        try {
                                            in.close();
                                        } catch (Throwable th2) {
                                            th.addSuppressed(th2);
                                        }
                                        throw th;
                                    }
                                } catch (Throwable th3) {
                                    try {
                                        bis.close();
                                    } catch (Throwable th4) {
                                        th3.addSuppressed(th4);
                                    }
                                    throw th3;
                                }
                            } catch (Throwable th5) {
                                try {
                                    oos.close();
                                } catch (Throwable th6) {
                                    th5.addSuppressed(th6);
                                }
                                throw th5;
                            }
                        } catch (SQLException e) {
                            this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".SQLException", e));
                            if (this.dbConnection != null) {
                                close(this.dbConnection);
                            }
                            release(_conn);
                        }
                    } catch (IOException e2) {
                        release(_conn);
                    }
                    numberOfTries--;
                } catch (Throwable th7) {
                    release(_conn);
                    throw th7;
                }
            }
            if (this.manager.getContext().getLogger().isDebugEnabled()) {
                this.manager.getContext().getLogger().debug(sm.getString(getStoreName() + ".saving", session.getIdInternal(), this.sessionTable));
            }
        }
    }

    protected Connection getConnection() throws ClassNotFoundException {
        Connection conn = null;
        try {
            conn = open();
            if (conn == null || conn.isClosed()) {
                this.manager.getContext().getLogger().info(sm.getString(getStoreName() + ".checkConnectionDBClosed"));
                conn = open();
                if (conn == null || conn.isClosed()) {
                    this.manager.getContext().getLogger().info(sm.getString(getStoreName() + ".checkConnectionDBReOpenFail"));
                }
            }
        } catch (SQLException ex) {
            this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".checkConnectionSQLException", ex.toString()));
        }
        return conn;
    }

    protected Connection open() throws SQLException, ClassNotFoundException {
        if (this.dbConnection != null) {
            return this.dbConnection;
        }
        if (this.dataSourceName != null && this.dataSource == null) {
            Context context = getManager().getContext();
            ClassLoader oldThreadContextCL = null;
            if (this.localDataSource) {
                oldThreadContextCL = context.bind(Globals.IS_SECURITY_ENABLED, null);
            }
            try {
                try {
                    javax.naming.Context envCtx = (javax.naming.Context) new InitialContext().lookup(CoreConstants.JNDI_COMP_PREFIX);
                    this.dataSource = (DataSource) envCtx.lookup(this.dataSourceName);
                    if (this.localDataSource) {
                        context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                    }
                } catch (NamingException e) {
                    context.getLogger().error(sm.getString(getStoreName() + ".wrongDataSource", this.dataSourceName), e);
                    if (this.localDataSource) {
                        context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                    }
                }
            } catch (Throwable th) {
                if (this.localDataSource) {
                    context.unbind(Globals.IS_SECURITY_ENABLED, oldThreadContextCL);
                }
                throw th;
            }
        }
        if (this.dataSource != null) {
            return this.dataSource.getConnection();
        }
        if (this.driver == null) {
            try {
                Class<?> clazz = Class.forName(this.driverName);
                this.driver = (Driver) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (ReflectiveOperationException e2) {
                this.manager.getContext().getLogger().error(sm.getString(getStoreName() + ".checkConnectionClassNotFoundException", e2.toString()));
                throw new SQLException(e2);
            }
        }
        Properties props = new Properties();
        if (this.connectionName != null) {
            props.put(ClassicConstants.USER_MDC_KEY, this.connectionName);
        }
        if (this.connectionPassword != null) {
            props.put("password", this.connectionPassword);
        }
        this.dbConnection = this.driver.connect(this.connectionURL, props);
        if (this.dbConnection == null) {
            throw new SQLException(sm.getString(getStoreName() + ".connectError", this.connectionURL));
        }
        this.dbConnection.setAutoCommit(true);
        return this.dbConnection;
    }

    protected void close(Connection dbConnection) throws SQLException {
        if (dbConnection == null) {
            return;
        }
        try {
            this.preparedSizeSql.close();
        } catch (Throwable f) {
            ExceptionUtils.handleThrowable(f);
        }
        this.preparedSizeSql = null;
        try {
            this.preparedSaveSql.close();
        } catch (Throwable f2) {
            ExceptionUtils.handleThrowable(f2);
        }
        this.preparedSaveSql = null;
        try {
            this.preparedClearSql.close();
        } catch (Throwable f3) {
            ExceptionUtils.handleThrowable(f3);
        }
        try {
            this.preparedRemoveSql.close();
        } catch (Throwable f4) {
            ExceptionUtils.handleThrowable(f4);
        }
        this.preparedRemoveSql = null;
        try {
            this.preparedLoadSql.close();
        } catch (Throwable f5) {
            ExceptionUtils.handleThrowable(f5);
        }
        this.preparedLoadSql = null;
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
        } finally {
            this.dbConnection = null;
        }
    }

    protected void release(Connection conn) throws SQLException {
        if (this.dataSource != null) {
            close(conn);
        }
    }

    @Override // org.apache.catalina.session.StoreBase, org.apache.catalina.util.LifecycleBase
    protected synchronized void startInternal() throws LifecycleException {
        if (this.dataSourceName == null) {
            this.dbConnection = getConnection();
        }
        super.startInternal();
    }

    @Override // org.apache.catalina.session.StoreBase, org.apache.catalina.util.LifecycleBase
    protected synchronized void stopInternal() throws SQLException, LifecycleException {
        super.stopInternal();
        if (this.dbConnection != null) {
            try {
                this.dbConnection.commit();
            } catch (SQLException e) {
            }
            close(this.dbConnection);
        }
    }
}
