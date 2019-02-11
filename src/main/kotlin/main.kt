import java.util.regex.PatternSyntaxException
import kotlin.math.max
import kotlin.random.Random

// goto 1
const val POP_SIZE = 600
const val REGEX_CHARS = "abcdefghijklmnopqrstuvwxyz!\"#\$%&\'()*+,-./:;<=>?@[\\]^_`{|}~"
val regex_start_size_range = 1..2
const val MUT_PROB = 0.5

fun buildRegex(string: String, size: Int): Regex {
    while (true) {
        try {
            return (0 until size).map { string.random() }.joinToString("").toRegex()
        } catch (e: PatternSyntaxException) {}
    }
}

fun combineRegex(father: String, mother: String): String {
//    println("Combine $father and  $mother")
    val size = listOf(father, mother).random().length
    val firstPart = father.substring((0 until father.length).random())
//    println("firstPart : $firstPart")
    val secondPart = try {
        mother.substring((0 until max(mother.length, 0)).random())
    } catch (e: Exception) {
        "wsdvc"
    }
    //    println("secondPart $secondPart")
    val rep = (firstPart + secondPart)
    if (rep.length > size) {
        return rep.substring(0..size)
    }
//    println("Result : $rep")
    return rep
}

fun addRandom(string: String, addon: String): String {
    val splitIndex = (0 until string.length).random()
    return string.substring(0, splitIndex) + addon + string.substring(splitIndex, string.length)
}

fun removeRandom(string: String): String {
    val index = (1..string.length).random()
    return string.removeRange(index - 1, index)
}


fun mutateRegex(regex: String): String {
    val rep = when ((0..5).random()) {
        0 -> addRandom(regex, REGEX_CHARS.random().toString())
        1 -> removeRandom(regex)
        else -> regex
    }
//    println("mutate $regex into $rep")
    return rep
}


val validMails = listOf(
//    "louis.saglio@sfr.fr",
//    "alainevrard@hotmail.fr",
//    "alainsaillard@wanadoo.fr",
//    "geralain08160@hotmail.fr",
//    "aline.czuba@wanadoo.fr",
//    "annette.ponsin@mairie-charlevillemezieres.fr",
//    "annie.pigeot@cegetel.net",
//    "cbourguignon@club-internet.fr",
//    "jean-francois.lemasson119@orange.fr"
    "a",
    "aaaa",
    "aa",
    "aaaaaaaaaaaaa"
)

val invalidMails = listOf(
//    "plainaddress",
//    "#@%^%#\$@#\$@#.com",
//    "@domain.com",
//    "Joe Smith <email@domain.com>",
//    "email.domain.com",
//    "email@domain@domain.com",
//    ".email@domain.com",
//    "email.@domain.com"
    "b",
    "bb",
    "bbb",
    "bbbb",
    "bbbbbbbbbbbbbbbbbbb"
)


private fun generateRandomPop(): MutableMap<Regex, Int> {
    // generate random pop
    val population = mutableMapOf<Regex, Int>()
    repeat((0 until POP_SIZE).count()) {
        population[buildRegex(REGEX_CHARS, regex_start_size_range.random())] = 0
    }

    // evaluate pop
    for ((regex, _) in population) {
        var note = 0
        for (mail in validMails) {
            if (regex.matches(mail)) {
                note += 1
            }
        }
        for (mail in invalidMails) {
            if (regex.matches(mail)) {
                note -= 1
            }
        }
        population[regex] = note
    }
    return population
}

private fun reproducePop(population: MutableMap<Regex, Int>): MutableMap<Regex, Int> {
    // reproduce pop
    val newPopulation = mutableMapOf<Regex, Int>()
    while (newPopulation.size < POP_SIZE * 2) {
        val father = population.keys.random()
        val mother = population.keys.random()
        try {
            if (father !in newPopulation) {
                newPopulation[father] = 0
            } else newPopulation[combineRegex(father.pattern, mother.pattern).toRegex()] = 0
        } catch (e: PatternSyntaxException) {
//            println("$father")
        }
    }
    return newPopulation
}

private fun mutatePop(population: MutableMap<Regex, Int>): MutableMap<Regex, Int> {
    // mutate pop
    val toKill = mutableSetOf<Regex>()
    val toAdd = mutableSetOf<Regex>()
    for ((regex, _) in population) {
        if (Random.nextFloat() < MUT_PROB) {
            toKill.add(regex)
            while (true) {
                try {
                    toAdd.add(mutateRegex(regex.pattern).toRegex())
                    break
                } catch (e: PatternSyntaxException) {
                }
            }
        }
    }
    for (regex in toKill) {
        population.remove(regex)
    }
    for (regex in toAdd) {
        population[regex] = 0
    }
    return population
}

private fun selectPop(population: MutableMap<Regex, Int>) =
    population.toList().sortedBy { it.second }.slice(0..POP_SIZE).associate { it }.toMutableMap()

fun main() {
    var population = generateRandomPop()
    repeat(2000) {
        if (it % 100 == 0) {
            println(it)
            println(population.toList().sortedBy { it1 -> -it1.second }.first())
        }
        population = reproducePop(population)
        mutatePop(population)
        population = selectPop(population)
    }
    (population.toList().sortedBy { -it.second }.slice(0..10).associate { it }.toMutableMap()).forEach {
        println("${it.key} : ${it.value}")
    }
}
