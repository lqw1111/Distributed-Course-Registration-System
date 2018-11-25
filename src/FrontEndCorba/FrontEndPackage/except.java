package FrontEndCorba.FrontEndPackage;


/**
* FrontEndCorba/FrontEndPackage/except.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从FrontEnd.idl
* 2018年11月16日 星期五 上午11时43分38秒 EST
*/

public final class except extends org.omg.CORBA.UserException
{
  public String reason = null;

  public except ()
  {
    super(exceptHelper.id());
  } // ctor

  public except (String _reason)
  {
    super(exceptHelper.id());
    reason = _reason;
  } // ctor


  public except (String $reason, String _reason)
  {
    super(exceptHelper.id() + "  " + $reason);
    reason = _reason;
  } // ctor

} // class except
