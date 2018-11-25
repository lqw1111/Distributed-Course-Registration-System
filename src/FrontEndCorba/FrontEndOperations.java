package FrontEndCorba;


/**
* FrontEndCorba/FrontEndOperations.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从FrontEnd.idl
* 2018年11月16日 星期五 上午11时43分38秒 EST
*/

public interface FrontEndOperations 
{
  String addCourse (String courseId, String semester);
  String removeCourse (String courseId, String semester);
  String[] listCourseAvailability (String semester);
  String enrolCourse (String studentId, String courseId, String semester);
  String dropCourse (String studentId, String courseId);
  String[] getClassSchedule (String studentId);
  String swapCourse (String studentID, String newCourseID, String oldCourseID);
  void shutdown ();
} // interface FrontEndOperations
