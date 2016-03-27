import co.semanticiris.model.{Caption, ImageCollection, ImageDocument, TermEntry}
import org.apache.lucene.document.Document

import scala.collection.immutable.HashMap

val ar : Array[String] = Array("bob", "ch", "12", "ch")
val x = for (i <- 1 to ar.length-1) yield ar(i)

ar.toList.foldLeft(new HashMap[String,Int]) { (z, w) => if (z.get(w) == None) z + (w->1) else z + (w-> (z(w)+1)) }


val s = ",asss. is Ploo, and by the way; 90.23 you"
val t = s.replaceAll("""[\p{Punct}]""", "")


val te : TermEntry = TermEntry("dog", "pic1",2)+TermEntry("dog","pic2",3)+TermEntry("dog","pic3",3)
val t12 = TermEntry("boo", "eqwewq", 3) + TermEntry ("boo", "blaa", 2)

val cap = new Caption("photo1", 1, List("dog","man","sky", "sky", "blue", "man"))
val idoc = ImageDocument(cap.photoId,cap)

val tes = TermEntry(idoc)

var coll = new ImageCollection
coll = coll + idoc
coll.termCount()
coll.documentCount()

val cap1 = "anna.jpg#0\t\t\tAnna sleeping during lectures"
val components = cap1.split("\\t+") // split on all whitespace

val cap2 = "997722733_0cb5439472.jpg#2\tA person in a red shirt climbing up a rock face covered in assist handles ."
val components1 = cap2.split("\\t+") // split on all whitespace

