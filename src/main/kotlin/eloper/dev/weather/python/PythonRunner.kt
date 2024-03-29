package eloper.dev.weather.python

import com.google.gson.Gson
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.InputStreamReader

object PythonRunner {

    val gson = Gson()

    suspend inline fun <reified T> runPythonScript(args: PythonArgs): T {
        var output = ""
        var returnValue: T
        withTimeout(args.timeout) {
            val process = Runtime.getRuntime().exec("python3 ${args.scriptLoc} ${args.params?.joinToString(" ") ?: ""}")
            val stdInput = BufferedReader(InputStreamReader(process.inputStream))
            var outputLine: String? = ""
            while(stdInput.readLine().also { outputLine = it } != null) {
                output += if (output.isEmpty()) outputLine else "$outputLine"
            }
            val stdError = BufferedReader(InputStreamReader(process.errorStream))
            var errorOccurred = false
            while (stdError.readLine().also { outputLine = it } != null) {
                output += if (output.isEmpty()) outputLine else "$outputLine"
                errorOccurred = true
            }
            process.destroy()
            if (errorOccurred) {
                throw RuntimeException("Error running python script ${args.scriptLoc} with parameters ${args.params}. Output: $output")
            }
            returnValue = gson.fromJson(output, T::class.java)
        }
        return returnValue
    }

    data class PythonArgs(
        val scriptLoc: String,
        val timeout: Long, // In milliseconds
        val params: List<String>?
    )

}