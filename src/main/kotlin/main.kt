import java.util.regex.PatternSyntaxException
import kotlin.random.Random

// goto 1
const val POP_SIZE = 100
const val REGEX_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#\$%&\'()*+,-./:;<=>?@[\\]^_`{|}~"
val regex_start_size_range = 5..15
const val MUT_PROB = 0.5

fun buildRegex(string: String, size: Int): Regex {
    while (true) {
        try {
            return (0 until size).map { string.random() }.joinToString("").toRegex()
        } catch (e: PatternSyntaxException) {}
    }
}

fun combineRegex(father: String, mother: String): String {
    val firstPart = father.substring((0 until father.length).random())
    val secondPart = mother.substring((0 until mother.length).random())
    return firstPart + secondPart
}

fun addRandom(string: String, addon: String): String {
    val splitIndex = (0 until string.length).random()
    return string.substring(0, splitIndex) + addon + string.substring(splitIndex, string.length)
}


fun mutateRegex(regex: String): String {
    return when((0 until 2).random()) {
        0 -> addRandom(regex, REGEX_CHARS.random().toString())
        else -> {
            val indexToRm = (1 until regex.length).random()
            regex.removeRange(indexToRm - 1, indexToRm)
        }
    }
}


val validMails = listOf(
    "louis.saglio@sfr.fr",
    "alainevrard@hotmail.fr",
    "alainsaillard@wanadoo.fr",
    "geralain08160@hotmail.fr",
    "aline.czuba@wanadoo.fr",
    "annette.ponsin@mairie-charlevillemezieres.fr",
    "annie.pigeot@cegetel.net",
    "cbourguignon@club-internet.fr",
    "jean-francois.lemasson119@orange.fr"
)

val invalidMails = listOf(
    "plainaddress",
    "#@%^%#\$@#\$@#.com",
    "@domain.com",
    "Joe Smith <email@domain.com>",
    "email.domain.com",
    "email@domain@domain.com",
    ".email@domain.com",
    "email.@domain.com"
)


fun main() {
    // generate random pop
    var population = mutableMapOf<Regex, Int>()
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

    // reproduce pop
    val newPopulation = mutableMapOf<Regex, Int>()
    while (newPopulation.size < POP_SIZE * 2) {
        val father = population.keys.random()
        val mother = population.keys.random()
        try {
            newPopulation[combineRegex(father.pattern, mother.pattern).toRegex()] = 0
        } catch (e: PatternSyntaxException) {}
    }
    population = newPopulation

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
                } catch (e: PatternSyntaxException) {}
            }
        }
    }

    // select
    population.toList().sortedBy { it.second }
}
