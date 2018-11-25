package FrontEndCorba;


/**
* FrontEndCorba/FrontEndHelper.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从FrontEnd.idl
* 2018年11月16日 星期五 上午11时43分38秒 EST
*/

abstract public class FrontEndHelper
{
  private static String  _id = "IDL:FrontEndCorba/FrontEnd:1.0";

  public static void insert (org.omg.CORBA.Any a, FrontEndCorba.FrontEnd that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static FrontEndCorba.FrontEnd extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (FrontEndCorba.FrontEndHelper.id (), "FrontEnd");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static FrontEndCorba.FrontEnd read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_FrontEndStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, FrontEndCorba.FrontEnd value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static FrontEndCorba.FrontEnd narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof FrontEndCorba.FrontEnd)
      return (FrontEndCorba.FrontEnd)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      FrontEndCorba._FrontEndStub stub = new FrontEndCorba._FrontEndStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static FrontEndCorba.FrontEnd unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof FrontEndCorba.FrontEnd)
      return (FrontEndCorba.FrontEnd)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      FrontEndCorba._FrontEndStub stub = new FrontEndCorba._FrontEndStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
