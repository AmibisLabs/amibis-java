package org.freedesktop.Avahi;
import java.util.List;
import org.freedesktop.DBus.Method.NoReply;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
public interface Server extends DBusInterface
{
   public static class StateChanged extends DBusSignal
   {
      public final int state;
      public final String error;
      public StateChanged(String path, int state, String error) throws DBusException
      {
         super(path, state, error);
         this.state = state;
         this.error = error;
      }
   }

  public String GetVersionString();
  public UInt32 GetAPIVersion();
  public String GetHostName();
  public void SetHostName(String name);
  public String GetHostNameFqdn();
  public String GetDomainName();
  public boolean IsNSSSupportAvailable();
  public int GetState();
  public UInt32 GetLocalServiceCookie();
  public String GetAlternativeHostName(String name);
  public String GetAlternativeServiceName(String name);
  public String GetNetworkInterfaceNameByIndex(int index);
  public int GetNetworkInterfaceIndexByName(String name);
  public Sextuple<Integer, Integer, String, Integer, String, UInt32> ResolveHostName(int _interface, int protocol, String name, int aprotocol, UInt32 flags);
  public Sextuple<Integer, Integer, Integer, String, String, UInt32> ResolveAddress(int _interface, int protocol, String address, UInt32 flags);
  public NTuple11<Integer, Integer, String, String, String, String, Integer, String, UInt16, List<List<Byte>>, UInt32> ResolveService(int _interface, int protocol, String name, String type, String domain, int aprotocol, UInt32 flags);
  public EntryGroup EntryGroupNew();
  public DBusInterface DomainBrowserNew(int _interface, int protocol, String domain, int btype, UInt32 flags);
  public DBusInterface ServiceTypeBrowserNew(int _interface, int protocol, String domain, UInt32 flags);
  @NoReply public DBusInterface ServiceBrowserNew(int _interface, int protocol, String type, String domain, UInt32 flags);
  public DBusInterface ServiceResolverNew(int _interface, int protocol, String name, String type, String domain, int aprotocol, UInt32 flags);
  public DBusInterface HostNameResolverNew(int _interface, int protocol, String name, int aprotocol, UInt32 flags);
  public DBusInterface AddressResolverNew(int _interface, int protocol, String address, UInt32 flags);
  public DBusInterface RecordBrowserNew(int _interface, int protocol, String name, UInt16 clazz, UInt16 type, UInt32 flags);

}
