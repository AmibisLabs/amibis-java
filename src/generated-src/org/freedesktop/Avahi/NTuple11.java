package org.freedesktop.Avahi;
import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Tuple;
/** Just a typed container class */
public final class NTuple11 <A,B,C,D,E,F,G,H,I,J,K> extends Tuple
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
   @Position(6)
   public final G g;
   @Position(7)
   public final H h;
   @Position(8)
   public final I i;
   @Position(9)
   public final J j;
   @Position(10)
   public final K k;
   public NTuple11(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k)
   {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
      this.e = e;
      this.f = f;
      this.g = g;
      this.h = h;
      this.i = i;
      this.j = j;
      this.k = k;
   }
}
