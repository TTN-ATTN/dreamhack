package freemarker.debug;

import freemarker.template.TemplateModelException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/DebugModel.class */
public interface DebugModel extends Remote {
    public static final int TYPE_SCALAR = 1;
    public static final int TYPE_NUMBER = 2;
    public static final int TYPE_DATE = 4;
    public static final int TYPE_BOOLEAN = 8;
    public static final int TYPE_SEQUENCE = 16;
    public static final int TYPE_COLLECTION = 32;
    public static final int TYPE_HASH = 64;
    public static final int TYPE_HASH_EX = 128;
    public static final int TYPE_METHOD = 256;
    public static final int TYPE_METHOD_EX = 512;
    public static final int TYPE_TRANSFORM = 1024;
    public static final int TYPE_ENVIRONMENT = 2048;
    public static final int TYPE_TEMPLATE = 4096;
    public static final int TYPE_CONFIGURATION = 8192;

    String getAsString() throws TemplateModelException, RemoteException;

    Number getAsNumber() throws TemplateModelException, RemoteException;

    boolean getAsBoolean() throws TemplateModelException, RemoteException;

    Date getAsDate() throws TemplateModelException, RemoteException;

    int getDateType() throws TemplateModelException, RemoteException;

    int size() throws TemplateModelException, RemoteException;

    DebugModel get(int i) throws TemplateModelException, RemoteException;

    DebugModel[] get(int i, int i2) throws TemplateModelException, RemoteException;

    DebugModel get(String str) throws TemplateModelException, RemoteException;

    DebugModel[] get(String[] strArr) throws TemplateModelException, RemoteException;

    DebugModel[] getCollection() throws TemplateModelException, RemoteException;

    String[] keys() throws TemplateModelException, RemoteException;

    int getModelTypes() throws RemoteException;
}
