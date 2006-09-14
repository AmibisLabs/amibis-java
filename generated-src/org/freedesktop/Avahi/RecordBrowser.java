package org.freedesktop.Avahi;
import java.util.List;

import org.freedesktop.dbus.DBusException;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.UInt32;
public interface RecordBrowser extends DBusInterface
{
   public static class ItemNew extends DBusSignal
   {
      public final int _interface;
      public final int protocol;
      public final String name;
      public final UInt16 clazz;
      public final UInt16 type;
      public final List<Byte> rdata;
      public final UInt32 flags;
      public ItemNew(String path, int _interface, int protocol, String name, UInt16 clazz, UInt16 type, List<Byte> rdata, UInt32 flags) throws DBusException
      {
         super(path, _interface, protocol, name, clazz, type, rdata, flags);
         this._interface = _interface;
         this.protocol = protocol;
         this.name = name;
         this.clazz = clazz;
         this.type = type;
         this.rdata = rdata;
         this.flags = flags;
      }
   }
   public static class ItemRemove extends DBusSignal
   {
      public final int _interface;
      public final int protocol;
      public final String name;
      public final UInt16 clazz;
      public final UInt16 type;
      public final List<Byte> rdata;
      public final UInt32 flags;
      public ItemRemove(String path, int _interface, int protocol, String name, UInt16 clazz, UInt16 type, List<Byte> rdata, UInt32 flags) throws DBusException
      {
         super(path, _interface, protocol, name, clazz, type, rdata, flags);
         this._interface = _interface;
         this.protocol = protocol;
         this.name = name;
         this.clazz = clazz;
         this.type = type;
         this.rdata = rdata;
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
   public static class AllForNow extends DBusSignal
   {
      public AllForNow(String path) throws DBusException
      {
         super(path);
      }
   }
   public static class CacheExhausted extends DBusSignal
   {
      public CacheExhausted(String path) throws DBusException
      {
         super(path);
      }
   }

  public void Free();

}
