import org.apache.lucene.document.Document

import scala.collection.immutable.HashMap


val ar : Array[String] = Array("bob", "ch", "12", "ch")

val x = for (i <- 1 to ar.length-1) yield ar(i)


ar.toList.foldLeft(new HashMap[String,Int]) { (z, w) => if (z.get(w) == None) z + (w->1) else z + (w-> (z(w)+1)) }



val s = ",asss. is Ploo"
val t = s.replaceAll("""[\p{Punct}]""", "")