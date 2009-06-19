package org.freedesktop.Avahi;
import java.util.List;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
public interface ServiceResolver extends DBusInterface
{
   public static class Found extends DBusSignal
   {
      public final int _interface;
      public final int protocol;
      public final String name;
      public final String type;
      public final String domain;
      public final String host;
      public final int aprotocol;
      public final String address;
      public final UInt16 port;
      public final List<List<Byte>> txt;
      public final UInt32 flags;
      public Found(String path, int _interface, int protocol, String name, String type, String domain, String host, int aprotocol, String address, UInt16 port, List<List<Byte>> txt, UInt32 flags) throws DBusException
      {
         super(path, _interface, protocol, name, type, domain, host, aprotocol, address, port, txt, flags);
         this._interface = _interface;
         this.protocol = protocol;
         this.name = name;
         this.type = type;
         this.domain = domain;
         this.host = host;
         this.aprotocol = aprotocol;
         this.address = address;
         this.port = port;
         this.txt = txt;
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
