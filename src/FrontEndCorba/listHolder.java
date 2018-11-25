package FrontEndCorba;


/**
* FrontEndCorba/listHolder.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从FrontEnd.idl
* 2018年11月16日 星期五 上午11时43分38秒 EST
*/

public final class listHolder implements org.omg.CORBA.portable.Streamable
{
  public String value[] = null;

  public listHolder ()
  {
  }

  public listHolder (String[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = FrontEndCorba.listHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    FrontEndCorba.listHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return FrontEndCorba.listHelper.type ();
  }

}
