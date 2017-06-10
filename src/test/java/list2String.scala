/**
  * Created by cluster on 2017/6/9.
  */
object list2String {
  def main(args: Array[String]) {
    val l1 =  List(1, 2, 3, 4, 5)
    val s1 = l1.toString
    val s2 = l1.mkString(",")
println(l1)
    println(s1)
    println(s2)
  }
}
