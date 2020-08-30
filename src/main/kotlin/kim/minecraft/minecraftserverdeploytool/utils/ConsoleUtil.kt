package kim.minecraft.minecraftserverdeploytool.utils

import org.fusesource.jansi.Ansi
import org.jline.reader.*
import org.jline.terminal.TerminalBuilder
import java.nio.CharBuffer

object ConsoleUtil : Completer {
    val terminal by lazy {
        TerminalBuilder.builder()
            .dumb(
                System.getProperty("yop.idea") !== null ||
                        System.getProperty("java.class.path")
                            .contains("idea_rt.jar")
            )
            .build()
    }

    val lineReader by lazy {
        LineReaderBuilder.builder()
            .appName("MinecraftServerDeployTool")
            .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
            .completer(this)
            .terminal(terminal)
            .build()
    }
    private val RESET by lazy { Ansi().reset().toString() }
    private var candidates: Collection<Candidate>? = null
    fun readLine(prefix: String = "> ", vararg tab: String): String {
        return readLine(prefix = prefix) { tab.toList() }
    }

    fun printf(msg: Any?) {
        lineReader.printAbove("$msg$RESET")
    }

    fun printf(block: () -> Any?) {
        lineReader.printAbove("${block()}$RESET")
    }

    fun readLine(prefix: String = "", tabs: () -> Collection<String>): String {
        candidates = tabs().toList().map { Candidate(it) }
        return lineReader.readLine(prefix).also { candidates = null }.trimEnd()
    }

    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {
        this.candidates?.let { candidates.addAll(it) }
    }

    fun splitArguments(argument: String): Sequence<String> {
        /*class SQ<T> {
            lateinit var storage: MutableCollection<T>
            fun yield(v: T) {
                storage.add(v)
            }
        }

        fun <T> sequence(block: SQ<T>.() -> Unit): Sequence<T> {
            val sq = SQ<T>()
            sq.storage = ArrayList()
            block(sq)
            return sq.storage.asSequence()
        }*/
        return sequence {
            val builder = StringBuilder()
            val buffer = CharBuffer.wrap(argument)
            root@
            while (buffer.hasRemaining()) {
                when (val next = buffer.get()) {
                    ' ', '\n', '\t' -> {
                        // argument splitter
                        if (builder.isNotEmpty()) {
                            yield(builder.toString())
                            builder.clear()
                        }
                    }
                    '\'' -> {
                        if (builder.isNotEmpty()) {
                            yield(builder.toString())
                            builder.clear()
                        }
                        while (buffer.hasRemaining()) {
                            when (val nextNext = buffer.get()) {
                                '\'' -> {
                                    yield(builder.toString())
                                    builder.clear()
                                    continue@root
                                }
                                '\\' -> {
                                    if (buffer.hasRemaining()) {
                                        when (buffer.get()) {
                                            '\\' -> builder.append('\\')
                                            '\'' -> builder.append('\'')
                                            '\"' -> builder.append('"')
                                        }
                                    }
                                }
                                else -> {
                                    builder.append(nextNext)
                                }
                            }
                        }
                    }
                    '"' -> {
                        if (builder.isNotEmpty()) {
                            yield(builder.toString())
                            builder.clear()
                        }
                        while (buffer.hasRemaining()) {
                            when (val nextNext = buffer.get()) {
                                '\"' -> {
                                    yield(builder.toString())
                                    builder.clear()
                                    continue@root
                                }
                                '\\' -> {
                                    if (buffer.hasRemaining()) {
                                        when (buffer.get()) {
                                            '\\' -> builder.append('\\')
                                            '\'' -> builder.append('\'')
                                            '\"' -> builder.append('"')
                                        }
                                    }
                                }
                                else -> {
                                    builder.append(nextNext)
                                }
                            }
                        }
                    }
                    '\\' -> {
                        if (buffer.hasRemaining()) {
                            when (buffer.get()) {
                                '\\' -> builder.append('\\')
                                '\'' -> builder.append('\'')
                                '\"' -> builder.append('"')
                            }
                        }
                    }
                    else -> builder.append(next)
                }
            }
            if (builder.isNotEmpty()) yield(builder.toString())
        }
    }

    infix fun String.color(color: MyColor): String {
        return when(color.mode){
            ColorMode.FG->Ansi().fg(color.color).a(this).reset().toString()
            ColorMode.BG -> Ansi().bg(color.color).a(this).reset().toString()
            ColorMode.FGBRIGHT -> Ansi().fgBright(color.color).a(this).reset().toString()
            ColorMode.BGBRIGHT -> Ansi().bgBright(color.color).a(this).reset().toString()
        }
    }

    fun colorAfter(color: MyColor): String {
        return when(color.mode){
            ColorMode.FG->Ansi().fg(color.color).toString()
            ColorMode.BG -> Ansi().bg(color.color).toString()
            ColorMode.FGBRIGHT -> Ansi().fgBright(color.color).toString()
            ColorMode.BGBRIGHT -> Ansi().bgBright(color.color).toString()
        }
    }

    class MyColor(val mode: ColorMode, val color: Ansi.Color)

    enum class ColorMode {
        FG, BG, FGBRIGHT, BGBRIGHT
    }
}

