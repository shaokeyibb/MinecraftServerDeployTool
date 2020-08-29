import kim.minecraft.minecraftserverdeploytool.utils.ConsoleUtil

fun main() {
    println(
        ConsoleUtil.splitArguments(
            """
                java -Dfile.encoding=
                UTF8:\
                114514:\
                1919810 -jar spigot.jar "Hello MAN, Yes 'SIR'" 'Hey sir'
            """.trimIndent()
        ).joinToString("\n-> ", prefix = "-> ")
    )
}
