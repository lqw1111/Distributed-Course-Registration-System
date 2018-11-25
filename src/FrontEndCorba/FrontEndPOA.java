package FrontEndCorba;


/**
* FrontEndCorba/FrontEndPOA.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从FrontEnd.idl
* 2018年11月16日 星期五 上午11时43分38秒 EST
*/

public abstract class FrontEndPOA extends org.omg.PortableServer.Servant
 implements FrontEndCorba.FrontEndOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("addCourse", new java.lang.Integer (0));
    _methods.put ("removeCourse", new java.lang.Integer (1));
    _methods.put ("listCourseAvailability", new java.lang.Integer (2));
    _methods.put ("enrolCourse", new java.lang.Integer (3));
    _methods.put ("dropCourse", new java.lang.Integer (4));
    _methods.put ("getClassSchedule", new java.lang.Integer (5));
    _methods.put ("swapCourse", new java.lang.Integer (6));
    _methods.put ("shutdown", new java.lang.Integer (7));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // FrontEndCorba/FrontEnd/addCourse
       {
         String courseId = in.read_string ();
         String semester = in.read_string ();
         String $result = null;
           try {
               $result = this.addCourse (courseId, semester);
           } catch (Exception e) {
               e.printStackTrace();
           }
           out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // FrontEndCorba/FrontEnd/removeCourse
       {
         String courseId = in.read_string ();
         String semester = in.read_string ();
         String $result = null;
         $result = this.removeCourse (courseId, semester);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // FrontEndCorba/FrontEnd/listCourseAvailability
       {
         String semester = in.read_string ();
         String $result[] = null;
         $result = this.listCourseAvailability (semester);
         out = $rh.createReply();
         FrontEndCorba.listHelper.write (out, $result);
         break;
       }

       case 3:  // FrontEndCorba/FrontEnd/enrolCourse
       {
         String studentId = in.read_string ();
         String courseId = in.read_string ();
         String semester = in.read_string ();
         String $result = null;
         $result = this.enrolCourse (studentId, courseId, semester);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 4:  // FrontEndCorba/FrontEnd/dropCourse
       {
         String studentId = in.read_string ();
         String courseId = in.read_string ();
         String $result = null;
         $result = this.dropCourse (studentId, courseId);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 5:  // FrontEndCorba/FrontEnd/getClassSchedule
       {
         String studentId = in.read_string ();
         String $result[] = null;
         $result = this.getClassSchedule (studentId);
         out = $rh.createReply();
         FrontEndCorba.listHelper.write (out, $result);
         break;
       }

       case 6:  // FrontEndCorba/FrontEnd/swapCourse
       {
         String studentID = in.read_string ();
         String newCourseID = in.read_string ();
         String oldCourseID = in.read_string ();
         String $result = null;
         $result = this.swapCourse (studentID, newCourseID, oldCourseID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 7:  // FrontEndCorba/FrontEnd/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:FrontEndCorba/FrontEnd:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public FrontEnd _this() 
  {
    return FrontEndHelper.narrow(
    super._this_object());
  }

  public FrontEnd _this(org.omg.CORBA.ORB orb) 
  {
    return FrontEndHelper.narrow(
    super._this_object(orb));
  }


} // class FrontEndPOA
