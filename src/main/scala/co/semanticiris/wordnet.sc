import edu.smu.tspell.wordnet._

// Assumes you have WordNet installed via brew.
System.setProperty("wordnet.database.dir", "/usr/local/Cellar/wordnet/3.1/dict/")

var wordSet : Array[Synset] = WordNetDatabase.getFileInstance.getSynsets("dog")








