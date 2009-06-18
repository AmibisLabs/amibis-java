package org.freedesktop.Avahi;
import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Tuple;
/** Just a typed container class */
public final class Sextuple <A,B,C,D,E,F> extends Tuple
{
   @Position(0)
   public final A a;
   @Position(1)
   public final B b;
   @Position(2)
   public final C c;
   @Position(3)
   public final D d;
   @Position(4)
   public final E e;
   @Position(5)
   public final F f;
   public Sextuple(A a, B b, C c, D d, E e, F f)
   {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
      this.e = e;
      this.f = f;
   }
}
