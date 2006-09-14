package org.freedesktop.Avahi;
import org.freedesktop.dbus.DBusException;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt32;
public interface ServiceResolver extends DBusInterface
{
   public static class Found extends DBusSignal
   {
      public final int _interface;
      public final int protocol;
      public final String name;
      public final int aprotocol;
      public final String address;
      public final UInt32 flags;
      public Found(String path, int _interface, int protocol, String name, int aprotocol, String address, UInt32 flags) throws DBusException
      {
         super(path, _interface, protocol, name, aprotocol, address, flags);
         this._interface = _interface;
         this.protocol = protocol;
         this.name = name;
         this.aprotocol = aprotocol;
         this.address = address;
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
