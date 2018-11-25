package FrontEndCorba;

/**
* FrontEndCorba/FrontEndHolder.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从FrontEnd.idl
* 2018年11月16日 星期五 上午11时43分38秒 EST
*/

public final class FrontEndHolder implements org.omg.CORBA.portable.Streamable
{
  public FrontEndCorba.FrontEnd value = null;

  public FrontEndHolder ()
  {
  }

  public FrontEndHolder (FrontEndCorba.FrontEnd initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = FrontEndCorba.FrontEndHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    FrontEndCorba.FrontEndHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return FrontEndCorba.FrontEndHelper.type ();
  }

}
