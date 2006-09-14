package org.freedesktop.Avahi;
import org.freedesktop.dbus.DBusException;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt32;
public interface AddressResolver extends DBusInterface
{
   public static class Found extends DBusSignal
   {
      public final int _interface;
      public final int protocol;
      public final int aprotocol;
      public final String address;
      public final String name;
      public final UInt32 flags;
      public Found(String path, int _interface, int protocol, int aprotocol, String address, String name, UInt32 flags) throws DBusException
      {
         super(path, _interface, protocol, aprotocol, address, name, flags);
         this._interface = _interface;
         this.protocol = protocol;
         this.aprotocol = aprotocol;
         this.address = address;
         this.name = name;
         this.flags = flags;
      }
   }
   public static class Failure extends DBusSignal
   {
      public final String error;
      public Failure(String path, String error) throws DBusException
      {
         super(path, error);
         this.error = error;
      }
   }

  public void Free();

}
