package org.freedesktop.Avahi;
import java.util.List;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
public interface EntryGroup extends DBusInterface
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

  public void Free();
  public void Commit();
  public void Reset();
  public int GetState();
  public boolean IsEmpty();
  public void AddService(int _interface, int protocol, UInt32 flags, String name, String type, String domain, String host, UInt16 port, List<List<Byte>> txt);
  public void AddServiceSubtype(int _interface, int protocol, UInt32 flags, String name, String type, String domain, String subtype);
  public void UpdateServiceTxt(int _interface, int protocol, UInt32 flags, String name, String type, String domain, List<List<Byte>> txt);
  public void AddAddress(int _interface, int protocol, UInt32 flags, String name, String address);
  public void AddRecord(int _interface, int protocol, UInt32 flags, String name, UInt16 clazz, UInt16 type, UInt32 ttl, List<Byte> rdata);

}
